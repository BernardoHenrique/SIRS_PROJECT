import React from "react";
import { Route, Routes, Link} from "react-router-dom";
import { LoginPage } from "./pages/LoginPage";
import { Restaurants } from "./pages/Restaurants";
import { RestaurantDetails } from "./pages/RestaurantDetails";
import { Home } from "./pages/Home"
import './App.css';
import { HvProvider } from "@hitachivantara/uikit-react-core";

function App() {
  return (
      <HvProvider>
          <ul>
            <li>
            <Link to="/">
                Home
            </Link>
                </li>
              <li>
              <Link to="/LoginPage">
              Login
            </Link>
            </li>
              <li>
                  <Link to="/Restaurants">
                      Restaurants List
                  </Link>
              </li>
          </ul>
          <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/LoginPage" element={<LoginPage />}/>
              <Route path="/Restaurants">
                <Route index element={<Restaurants />} />
                <Route path=":name" element={<RestaurantDetails />} />
              </Route>
          </Routes>
      </HvProvider>
  );
}

export default App;
