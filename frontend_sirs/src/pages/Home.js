import React from "react";
import logo from "../logo.svg";
import {HvButton, HvContainer} from "@hitachivantara/uikit-react-core";

export const Home = () => {
    return (
    <div>
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo" />
                <HvContainer>
                    <p>
                        Edit <code>src/App.js</code> and save to reload.
                    </p>
                    <HvButton category="primary">Login</HvButton>
                </HvContainer>
                <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Learn React
                </a>
            </header>
        </div>
    </div>
    )
}