import React from "react";
import {Link} from "react-router-dom";

export const Home = () => {
    return (
    <div>
        <div className="App">
            <header className="App-header">
                <img src="//cdn.shopify.com/s/files/1/0398/4549/4945/files/new_year_1.png?v=1640392869"
                     alt="The Cork Collection"
                     width="350" height="160.0" />
                <br/>
                <Link className="App-link" to="/LoginPage"
                >
                    Login
                </Link>
            </header>
        </div>
    </div>
    )
}