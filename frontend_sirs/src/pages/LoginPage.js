import React, {useState, useEffect} from "react";
import {
    HvButton,
    HvContainer,
    HvDialog, HvDialogActions,
    HvDialogContent,
    HvDialogTitle,
    HvInput
} from "@hitachivantara/uikit-react-core"
import {Link} from "react-router-dom";
import io from 'socket.io-client';

export const LoginPage = () => {

    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const socket = io.connect('https://localhost:3001');
    const [verified, setVerified] = useState(false);

    const validateEntries = () => {
        console.log(userName)
        localStorage.setItem("userName",  userName)
        localStorage.setItem("password",  password)
        socket.emit('login', {
            user: userName ,
            password: password ,
        });
        console.log(localStorage.getItem("userName"))
    }

    useEffect(() => {
        socket.on('receive_permission', (data) => {
            console.log(data);
            if(data.permission === "accept")
                setVerified(true)
            else
                setVerified(false)
        })
    }, [socket]);
    
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
                <HvButton category="primary" onClick={() => validateEntries()}>
                    Confirm
                </HvButton>
            <div>
                <HvDialog
                    disableBackdropClick
                    open={verified}
                    id="test"
                    firstFocusable="test-close"
                >
                    <HvDialogTitle variant="warning">SUCCESS</HvDialogTitle>
                    Lets book your reservation
                    <HvDialogActions>
                        <Link to="/Restaurants">
                        <HvButton id="apply" category="ghost">
                            Continue
                        </HvButton>
                        </Link>
                    </HvDialogActions>
                </HvDialog>
            </div>
        </HvContainer>
    );
}