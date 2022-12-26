import { React } from "react";
import { Link } from "react-router-dom";

export const Restaurants = () => {
        const restaurants = ["McDonald's", "Burger King", "Subway", "Italian Republic"]

        return (
            <>
                <h1>
                    Restaurants List:
                </h1>
                <div style={{ paddingLeft: 20, display: "flex", flexDirection: "row", gap: 10 }}>
                    <ul>
                    {restaurants.map((element) => (
                        <li>
                            <Link key={element} to={`/Restaurants/${element}`}>{element}</Link>
                        </li>
                            ))}
                    </ul>
                </div>
            </>
        );
    }