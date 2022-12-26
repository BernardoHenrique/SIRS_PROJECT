import { React, useState } from "react";
import { useParams } from "react-router-dom";
import {HvCheckBox, HvContainer, HvInput} from "@hitachivantara/uikit-react-core";

export const RestaurantDetails = () => {
    const { name } = useParams()
    const [numberPeople, setPeople] = useState(0)

    const validationMessages = {
        requiredError: "The number is required",
        maxCharError: "Number is too big",
        typeMismatchError: "Value is not a number",
    };
    return (
        <HvContainer>
            <h1>{ name } </h1>
            <br />
            <h3> Do reservation:</h3>
            <br />
            <HvInput
                id="numeric-required-input"
                type="number"
                label="Number of Persons"
                description="Enter number of persons"
                placeholder="Insert a number"
                validationMessages={validationMessages}
                required
                maxCharQuantity={5}
                onChange={(event, newValue) => setPeople(newValue)}
                showValidationIcon
            />
            <br />
            <HvCheckBox label="Use Card points" />
            <h3>Price {numberPeople * 2}€</h3>
        </HvContainer>
    );
}