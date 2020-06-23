const tasklist = require("tasklist");
$ = require("jquery");
const apiActions = require("./apiActions");

// let spotifyTasks = await tasklist({
//   verbose: true,
//   filter: ["Imagename eq Spotify.exe"],
// })(async () => {
//   console.log();
//   /*
//     [{
//         imageName: 'taskhostex.exe',
//         pid: 1820,
//         sessionName: 'Console',
//         sessionNumber: 1,
//         memUsage: 4415488
//     }, …]
//     */
// })();

// function getSpecificTask(taksName) {
//   let spotifyTasks = [](async () => {
//     spotifyTasks = await tasklist({
//       verbose: true,
//       filter: ["Imagename eq Spotify.exe"],
//     });
//   });
//   return spotifyTasks;
// }

async function getSpecificTask(taskName) {
  let song = "";
  try {
    const taskList = await tasklist({
      verbose: true,
      filter: ["Imagename eq " + taskName],
    });

    console.log(taskList);

    if (!taskList || taskList.length == 0) {
      return "";
    }

    console.log(taskList[0].windowTitle);

    const songTask = taskList.find((t) => t.windowTitle.match(" - "));

    if (songTask != null) {
      song = songTask.windowTitle;
    }

    console.log("Song: ", song);
    return song;
  } catch (error) {
    console.log(error);
  }
  return song;
}

var artistName, songName;

function updateUI(artist, song) {
  if (!artistName || !songName) {
    // Not song found or loading...
    artistName = artist;
    songName = song;

    getLyrics(artistName, songName);
    getSong(artistName, songName);
    getArtist(artistName);
  } else {
    if (artistName != artist || songName != song) {
      artistName = artist;
      songName = song;

      getLyrics(artistName, songName);
      getSong(artistName, songName);
      getArtist(artistName);
    }
  }
}

async function getActiveSongByPlayer(playerName = "Spotify.exe") {
  await getSpecificTask(playerName)
    .then((response) => {
      if (response) {
        console.log("Response:", response);

        const songParts = response.split(" - ");
        const artistName = songParts[0];
        const songName = songParts[1];

        console.log("R:", response);
        console.log("Artistname:", artistName);
        console.log("Sognanme:", songName);

        if (response && artistName && songName) {
          updateUI(artistName, songName);
        }
      } else {
        console.log("Response:", " No Song Active");
      }
      return response;
    })
    .catch((err) => {
      console.log("Error:", err);
    });
}

async function getActiveSong() {
  let songFullTitle = await getActiveSongByPlayer("Spotify.exe");
  if (!songFullTitle || songFullTitle.length == 0) {
    songFullTitle = await getActiveSongByPlayer("vlc.exe");
    const songParts = songFullTitle.split(".")[0];
    const artistName = songParts[0];
    const songName = songParts[1];
    return songParts;
  } else {
    return songFullTitle.split(" - ");
  }
}

function getSongNameAndArtistName(song = "") {
  const songNameParts = song.split(" - ");
  return { artistName: songNameParts[0], songName: songNameParts[1] };
}

var activeTab;

function switchTab(navEl, tabId) {
  if (!activeTab) {
    activeTab = "lyricsSection";
  }

  if (activeTab !== tabId) {
    document.getElementsByClassName("active")[0].classList.remove("active");
    navEl.classList.add("active");
    fadeSwitchElements(activeTab, tabId);

    activeTab = tabId;
  }
}

function fadeSwitchElements(id1, id2) {
  var element1 = $("#" + id1);
  var element2 = $("#" + id2);

  if (element1.is(":visible")) {
    element1.fadeToggle(500, function () {
      element2.fadeToggle(500);
    });
  } else {
    switchDisplayVisibility(element1, element2);
  }
}

function switchDisplayVisibility(el1, el2) {
  el1.css("display", "none");
  el2.css("display", "block");
}

function getArtist(artistName) {
  apiActions.getArtistURI(artistName).then((data) => {
    console.log("Artist URI:", data);
    const artistURI = data;

    if (artistURI.length > 0) {
      apiActions.getArtistData(artistURI).then((data) => {
        console.log("Artist JSON:", data);

        document.getElementById("artistTopName").innerText = artistName;
        document.getElementById("artistShortDesc").innerText =
          data.shortDescription;

        document
          .getElementById("artistTopImage")
          .setAttribute("src", data.imageURL);

        fillWithWhiteSpaceIfNotPresent(data.longDescription, "artistLongDesc");
        if (!data.name || data.name.length == 0) {
          data.name = artistName;
        }
        fillWithWhiteSpaceIfNotPresent(data.name, "artistMembers");

        if (data.careerStartYear == -1) careerStartYear = "";

        fillWithWhiteSpaceIfNotPresent(
          data.careerStartYear,
          "artistCareerStart"
        );
        fillWithWhiteSpaceIfNotPresent(data.location, "artistCountry");

        addToAuthorsList("artistGenresList", data.genres);
        addToAuthorsList("artistRecordLabelsList", data.recordLabels);

        if (data.albums.length > 0) {
          document.getElementById("artistAlbums").style.display = "";

          var albumsNames = data.albums.map(function (item) {
            return item["name"];
          });

          addToAuthorsList(
            "artistAlbumsList",
            albumsNames,
            "btn btn-secondary btn-lg m-1"
          );
        } else {
          document.getElementById("artistAlbums").style.display = "none";
        }

        if (data.awards.length > 0) {
          document.getElementById("artistAwards").style.display = "";
          addItemsToAwardsTable("artistAwardsTable", data.awards);
        } else {
          document.getElementById("artistAwards").style.display = "none";
        }

        addSocialMediaAccounts(data.socialLinks);

        fadeSwitchElements("artistSectionLoadingMsg", "artistSectionContent");

        document.getElementById("artistURI").innerText = artistURI;
        document.getElementById("searchSimilarArtists").style.display = "";
        document.getElementById("loadSimilarArtists").style.display = "none";
        document.getElementById("rowTableSimilarArtists").style.display =
          "none";
      });
    }
  });
}

function fillWithWhiteSpaceIfNotPresent(dataString, elementId) {
  if (!dataString || dataString.length == 0) {
    document.getElementById(elementId).innerHTML = "&nbsp;";
  } else {
    document.getElementById(elementId).innerText = dataString;
  }
}

function getSong(artistName, songName) {
  apiActions.getSongURI(artistName, songName).then((data) => {
    console.log("Song URI:", data);
    const songURI = data;

    if (songURI.length > 0) {
      apiActions.getSongData(songURI).then((data) => {
        console.log("Song JSON:", data);

        document.getElementById("topSongTitle").innerText = songName;
        document.getElementById("topSongArtist").innerText = artistName;

        if (data.coverURL) {
          document
            .getElementById("topSongImage")
            .setAttribute("src", data.coverURL);
        } else {
          document
            .getElementById("topSongImage")
            .setAttribute("src", "./images/image_not_available.jpg");
        }

        document.getElementById("songDescription").innerText = data.description;
        document
          .getElementById("songLearnMoreLink")
          .setAttribute("href", data.DBPediaURI);

        fillWithWhiteSpaceIfNotPresent(data.runTime, "songRunTime");
        fillWithWhiteSpaceIfNotPresent(data.albumName, "songAlbumName");

        fillWithWhiteSpaceIfNotPresent(
          dateConverter(data.date),
          "songReleaseDate"
        );

        addToAuthorsList("songMusicAuthorsList", data.musicAuthors);
        addToAuthorsList("songLyricsAuthorsList", data.lyricsAuthors);

        if (!data.videoURL || data.videoURL.length == 0) {
          document.getElementById("musicVideo").style.display = "none";
          document.getElementById("musicVideoNotAvailable").style.display =
            "block";
        } else {
          document.getElementById("musicVideo").style.display = "block";
          document.getElementById("musicVideoNotAvailable").style.display =
            "none";

          document
            .getElementById("songYouTubeVideo")
            .setAttribute(
              "src",
              "https://www.youtube.com/embed/" + data.videoURL
            );
        }

        if (!data.award || data.award.length <= 1) {
          document.getElementById("songAward").style.display = "none";
        } else {
          document.getElementById("songAward").style.display = "";
          document.getElementById("songAwardTitle").innerText = data.award;
          if (data.award == "Gold") {
            document
              .getElementById("songAwardImage")
              .setAttribute("src", "./images/gold-award.png");
          } else if (data.award == "Platinum") {
            document
              .getElementById("songAwardImage")
              .setAttribute("src", "./images/platinum-award.png");
          } else {
            document
              .getElementById("songAwardImage")
              .setAttribute("src", "./images/image_not_available.jpg");
          }
        }

        fadeSwitchElements("songSectionLoadingMsg", "songSectionContent");
      });
    }
  });
}

function getLyrics(artistName, songName) {
  document.getElementById("fullSongTitle").innerText =
    artistName + " - " + songName;
  apiActions.getLyrics(artistName, songName).then((data) => {
    if (data) {
      console.log("Song Lyrics:", data);
      const songLyrics = data;

      document.getElementById("songLyrics").innerText = songLyrics;
    }
    fadeSwitchElements("lyricsSectionLoadingMsg", "lyricsContent");
  });
}

function addToAuthorsList(
  listId,
  listElements,
  listItemClass = "list-group-item d-flex justify-content-between align-items-center"
) {
  var authorsEl = document.getElementById(listId);
  authorsEl.innerHTML = "";
  const authorsList = listElements;
  for (var x in authorsList) {
    var node = document.createElement("li");
    node.setAttribute("class", listItemClass);
    node.innerText = authorsList[x];
    authorsEl.appendChild(node);
  }
}

function addItemsToAwardsTable(tableId, items) {
  var table = document.getElementById(tableId);
  var body = table.tBodies[0];
  body.innerHTML = "";
  //$("#" + tableId + " tbody").empty();

  if (items.length == 0) {
    var row = body.insertRow(body.rows.length);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    cell1.innerHTML = "/";
    cell2.innerHTML = "/";
    cell3.innerHTML = "/";
  } else {
    for (var x in items) {
      var row = body.insertRow(body.rows.length);
      var cell1 = row.insertCell(0);
      var cell2 = row.insertCell(1);
      var cell3 = row.insertCell(2);
      cell1.innerHTML = items[x].awardTitle;
      cell2.innerHTML = items[x].awardFor;
      cell3.innerHTML = items[x].year;
    }
  }
}

function removeTableRows(tableId) {
  var elmtTable = document.getElementById(tableId);
  var tableRows = elmtTable.getElementsByTagName("tr");
  var rowCount = tableRows.length;

  for (var x = rowCount - 1; x > 0; x--) {
    elmtTable.removeChild(tableRows[x]);
  }
}

function addSocialMediaLink(elementId, link, linkId) {
  if (!linkId || linkId.length == 0) {
    document.getElementById(elementId).style.display = "none";
  } else {
    document.getElementById(elementId).style.display = "";
    document.getElementById(elementId).setAttribute("href", link + linkId);
  }
}

function addSocialMediaAccounts(items) {
  addSocialMediaLink(
    "artistFacebook",
    "https://www.facebook.com/",
    items.facebook
  );
  addSocialMediaLink("artistTwitter", "https://twitter.com/", items.twitter);
  addSocialMediaLink(
    "artistYouTube",
    "https://youtube.com/channel/",
    items.youtube
  );
  addSocialMediaLink(
    "artistiTunes",
    "https://itunes.apple.com/artist/",
    items.itunes
  );
  addSocialMediaLink(
    "artistSpotify",
    "https://open.spotify.com/artist/",
    items.spotify
  );
  addSocialMediaLink(
    "artistMusicBrainz",
    "https://musicbrainz.org/artist/",
    items.musicbrainz
  );
  addSocialMediaLink(
    "artistInstagram",
    "https://www.instagram.com/",
    items.instagram
  );
  addSocialMediaLink(
    "artistSoundCloud",
    "https://soundcloud.com/",
    items.soundcfaloud
  );
  addSocialMediaLink(
    "artistGooglePlay",
    "https://play.google.com/store/music/artist?id=",
    items.googlemusic
  );
}

function findSimilarArtists() {
  var artistURI = document.getElementById("artistURI").innerText;

  if (artistURI.length > 0) {
    document.getElementById("searchSimilarArtists").style.display = "none";
    document.getElementById("loadSimilarArtists").style.display = "";

    apiActions.getSimilarArtists(artistURI).then((data) => {
      console.log("Similar Artists JSON:", data);
      document.getElementById("loadSimilarArtists").style.display = "none";

      if (data.length > 0) {
        document.getElementById("rowTableSimilarArtists").style.display = "";
        document.getElementById("");
        addItemsToSimilarArtistsTable("tableSimilarArtists", data);
      } else {
        document.getElementById("rowTableSimilarArtists").style.display =
          "none";
      }
    });
  }
}

function addItemsToSimilarArtistsTable(tableId, items) {
  var table = document.getElementById(tableId);
  var body = table.tBodies[0];
  body.innerHTML = "";

  if (items.length == 0) {
    var row = body.insertRow(body.rows.length);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    cell1.innerHTML = "/";
    cell2.innerHTML = "/";
    cell3.innerHTML = "/";
  } else {
    for (var x in items) {
      var row = body.insertRow(body.rows.length);
      var cell1 = row.insertCell(0);
      var cell2 = row.insertCell(1);
      var cell3 = row.insertCell(2);
      cell1.innerHTML = items[x].name;
      cell2.innerHTML = items[x].genre;
      cell3.innerHTML = "<a href='" + items[x].wikiDataURI + "'>Read More</a>";
    }
  }
}

// function addSocialMediaAccounts(items) {
//   document
//     .getElementById("artistFacebook")
//     .setAttribute("href", "https://www.facebook.com/" + items.facebook);
//   document
//     .getElementById("artistTwitter")
//     .setAttribute("href", "https://twitter.com/" + items.twitter);
//   document
//     .getElementById("artistYouTube")
//     .setAttribute("href", "https://youtube.com/channel/" + items.youtube);
//   document
//     .getElementById("artistiTunes")
//     .setAttribute("href", "https://itunes.apple.com/artist/" + items.itunes);
//   document
//     .getElementById("artistSpotify")
//     .setAttribute("href", "https://open.spotify.com/artist/" + items.spotify);
//   document
//     .getElementById("artistMusicBrainz")
//     .setAttribute(
//       "href",
//       "https://musicbrainz.org/artist/" + items.musicbrainz
//     );
//   document
//     .getElementById("artistInstagram")
//     .setAttribute("href", "https://www.instagram.com/" + items.instagram);
//   document
//     .getElementById("artistSoundCloud")
//     .setAttribute("href", "https://soundcloud.com/" + items.soundcfaloud);
//   document
//     .getElementById("artistGooglePlay")
//     .setAttribute(
//       "href",
//       "https://play.google.com/store/music/artist?id=" + items.googlemusic
//     );
// }

async function refresh() {
  console.log("Refresh");
  await getActiveSongByPlayer();
}

function dateConverter(date) {
  if (!date || date.length == 0) {
    return "";
  }

  let newDate = new Date(date);

  //console.log(dd.toLocaleDateString("en-GB"))
  return newDate.toLocaleDateString("en-GB");
}
