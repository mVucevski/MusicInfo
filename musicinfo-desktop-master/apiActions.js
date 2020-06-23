const axios = require("axios");

function getArtistURI(artistName) {
  return axios
    .get("http://localhost:8080/artist/" + artistName)
    .then((response) => {
      console.log("Response:", response);
      return response.data;
    })
    .catch((err) => {
      console.log("Error:", err);
    });
}

function getSongURI(artistName, songName) {
  return axios
    .get("http://localhost:8080/song/" + artistName + "/" + songName)
    .then((response) => {
      console.log("Response:", response);
      return response.data;
    })
    .catch((err) => {
      console.log("Error:", err);
    });
}

function getArtistData(artistURI) {
  return axios
    .get("http://localhost:8080/artist/" + artistURI + "/all")
    .then((response) => {
      console.log("Response:", response);
      return response.data;
    })
    .catch((err) => {
      console.log("Error:", err);
    });
}

function getSongData(songURI) {
  return axios
    .get("http://localhost:8080/song/" + songURI + "/all")
    .then((response) => {
      console.log("Response:", response);
      return response.data;
    })
    .catch((err) => {
      console.log("Error:", err);
    });
}

function getLyrics(artistName, songName) {
  return axios
    .get("http://localhost:8080/lyrics/" + artistName + "/" + songName)
    .then((response) => {
      console.log("getLyrics Response:", response);
      return response.data;
    })
    .catch((err) => {
      console.log("Error:", err);
    });
}

function getSimilarArtists(artistURI) {
  return axios
    .get("http://localhost:8080/artist/" + artistURI + "/similarArtists")
    .then((response) => {
      console.log("Response:", response);
      return response.data;
    })
    .catch((err) => {
      console.log("Error:", err);
    });
}

module.exports = {
  getArtistURI: getArtistURI,
  getSongURI: getSongURI,
  getArtistData: getArtistData,
  getSongData: getSongData,
  getLyrics: getLyrics,
  getSimilarArtists: getSimilarArtists,
};
