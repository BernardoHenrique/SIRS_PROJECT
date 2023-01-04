const express = require('express')
const app = express();
const https = require("https");
const { Server } = require("socket.io");
const cors = require("cors")
app.use(cors());
const server = https.createServer(app);

const io = new Server(server, {
    cors: {
        origin: "https://localhost:3000",
        methods: ["GET", "POST"],
    },
});

io.on("connection", (socket) => {
    console.log(`User Connected: ${socket.id}`);
    socket.on("login", (data) => {
        if(data.user === "aaaa" && data.password === "1234567"){
            socket.emit("receive_permission", {
                permission: "accept",
            })
        }
        else
            socket.emit("receive_permission", {
                permission: "deny",
            })
    })
})

server.listen(3001, () => {
    console.log("SERVER IS RUNNING");
})