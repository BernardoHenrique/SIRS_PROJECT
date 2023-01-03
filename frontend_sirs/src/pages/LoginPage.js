import React, {useState} from "react";
import {HvButton, HvContainer, HvInput} from "@hitachivantara/uikit-react-core"
import {Link} from "react-router-dom";
import io from 'socket.io-client';

export const LoginPage = () => {

    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const socket = io('http://localhost:8000');
    const [verified, setVerified] = useState(false);



    const validateEntries = () => {
         socket.on('connect', () => {
            console.log('Connected to server');
        });
        socket.emit('message', {
            cmd: 'login',
            user: {userName},
            password: {password},
        });
        socket.on('message', data => {
            console.log(data.response); // 'Hello, client!'
            setVerified(data.response);
        });
    }

    
    const validationMessages = {
        error: "Wrong password",
        requiredError: "Your password is required",
        minCharError: "Your password has less than 6 characters",
        maxCharError: "Your password has more than 12 characters",
    }

    return (
        <HvContainer>
            <h1>Login</h1>
            <HvInput
                onChange={(event, value) => setUserName(value)}
                id="input-username"
                label="Username"
                description="Please enter your  Username"
                placeholder="Insert Username"
            />
        <br/>
            <HvInput
                onChange={(event, value) => setPassword(value)}
                id="input-password"
                label="Password"
                description="Enter your password"
                placeholder="Please enter your password"
                type="password"
                required
                maxCharQuantity={12}
                minCharQuantity={6}
                validationMessages={validationMessages}
            />
            <br/>
            {verified ? (
            <Link to="/Restaurants">
                <HvButton category="primary" onClick={() => validateEntries()}>
                    Confirm
                </HvButton>
            </Link>) : (
                <HvButton category="primary" onClick={() => validateEntries()}>
                    Confirm
                </HvButton>
            )}
        </HvContainer>
    );
}