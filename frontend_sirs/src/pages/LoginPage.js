import React from "react";
import { HvContainer, HvInput } from "@hitachivantara/uikit-react-core"

export const LoginPage = () => {

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
                id="input-username"
                label="Username"
                description="Please enter your  Username"
                placeholder="Insert Username"
            />
        <br/>
            <HvInput
                id="input-password"
                label="Password"
                description="Enter your password"
                placeholder="Please enter your password"
                type="password"
                required
                maxCharQuantity={12}
                minCharQuantity={6}
                validation={(value) => value === "password"}
                validationMessages={validationMessages}
            />
        </HvContainer>
    );
}