const express = require('express')
const app = express();
const https = require("https");
const { Server } = require("socket.io");
const cors = require("cors")
const fs = require('fs')
app.use(cors());
const server = https.createServer({
    key: fs.readFileSync('../frontend_sirs/reactcert/key.pem'),
    cert: fs.readFileSync('../frontend_sirs/reactcert/cert.pem')
  },app);

const io = new Server(server, {
    cors: {
        origin: "https://localhost:3000",
        methods: ["GET", "POST"],
    },
});

io.on('connection', (socket) => {
    socket.on('login', (data) => {
        console.log(data.user)
        console.log(data.password)
        if (data.user === "aaaa") {
            console.log("deu certo")
            socket.emit("receive_permission", {
                permission: "accept"
            })
        } else
            socket.emit("receive_permission", {
                permission: "accept"
            })
    })
    socket.on('getCardPoints', () => {
        socket.emit('setCardPoints', {
            points: "15"
        })
    })
})

server.listen(3001, () => {
    console.log("SERVER IS RUNNING");
})