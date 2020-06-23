// Modules to control application life and create native browser window
const { app, BrowserWindow, Menu } = require("electron");

let win;

function createWindow() {
  // Create the browser window.
  win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true,
    },
  });

  // and load the index.html of the app.
  win.loadFile("index.html");

  // Set custom menu
  Menu.setApplicationMenu(menu);
  win.setMenuBarVisibility(false);

  // Disable menu bar
  //win.setMenu(null);

  // Emitted when the window is closed.
  win.on("closed", () => {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    win = null;
  });

  win.webContents.on("new-window", function (e, url) {
    e.preventDefault();
    require("electron").shell.openExternal(url);
  });
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.whenReady().then(() => {
  createWindow();

  app.on("activate", function () {
    // On macOS it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

// Quit when all windows are closed.
app.on("window-all-closed", function () {
  // On macOS it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== "darwin") app.quit();
});

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.

// Custom Menu

var menu = Menu.buildFromTemplate([
  {
    label: "Menu",
    submenu: [
      {
        label: "Settings",
      },
      {
        label: "Exit",
        click() {
          app.quit();
        },
      },
    ],
  },
  {
    label: "DevTools",
    click() {
      //Open the DevTools
      win.webContents.openDevTools();
    },
  },
]);
