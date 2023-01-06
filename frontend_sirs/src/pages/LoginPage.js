import React, {useState} from "react";
import {
    HvButton,
    HvContainer,
    HvDialog, HvDialogActions,
    HvDialogTitle,
    HvInput
} from "@hitachivantara/uikit-react-core"
import axios from "axios";
import {Link} from "react-router-dom";

export const LoginPage = () => {

    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const [verified, setVerified] = useState(false);

    const validateEntries = () => {
        localStorage.setItem("userName",  userName)
        localStorage.setItem("password",  password)
        axios.get("http://localhost:8080/login", {
            params: {
                user: userName,
                password: password
            }
        })
            .then(response => {
                console.log(response.data);
                setVerified(response.data);
            })
            .catch(error => console.log(error))
        console.log(localStorage.getItem("userName"))
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
                <HvButton category="primary" onClick={validateEntries}>
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