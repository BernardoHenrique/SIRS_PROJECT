import {React, useEffect, useState} from "react";
import { useParams } from "react-router-dom";
import {
    HvButton,
    HvCheckBox,
    HvContainer,
    HvDatePicker, HvDialog, HvDialogActions, HvDialogContent, HvDialogTitle,
    HvInput,
    HvTimePicker
} from "@hitachivantara/uikit-react-core";
import io from "socket.io-client";

export const RestaurantDetails = () => {
    const { name } = useParams();
    const [numberPeople, setPeople] = useState();
    const [showSuccess, setSuccess] = useState(false);
    const [cardPoints, setCardPoints] = useState(0);
    const [useDiscount, setDiscount] = useState(false);
    const [date, setDate] = useState(false);
    const [time, setTime] = useState(false);
    const [card, setCard] = useState("");
    const socket = io.connect('https://localhost:3001');

    const validationMessages = {
        requiredError: "The number is required",
        maxCharError: "Number is too big",
        typeMismatchError: "Value is not a number",
    };

    useEffect(() => {
        socket.emit('getCardPoints');
        socket.on('setCardPoints', (data) => {
            setCardPoints(data.points);
        });
    }, []);

    const validateEntry = () => {
        return !isNaN(numberPeople) && numberPeople > 0 && date && time;
    }

    const calcPrice = () => {
      if(useDiscount)
          return numberPeople * 2 * 0.9
      else
          return numberPeople * 2
    }

    const updateCardPoints = () => {
        if(useDiscount && cardPoints >= 10)
            setCardPoints(cardPoints - 10)
    }

    const sendBooking = () => {
        socket.emit('sendBooking', {
            user: localStorage.getItem("user"),
            restaurant: name,
            numberPeople: numberPeople,
            date: date,
            time: time,
            card: card,
        })
    }

        return (
        <HvContainer>
            <h1>{ name } </h1>
            <br />
            <h3>Your card Points -> {cardPoints}</h3>
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
            <HvDatePicker onChange={() => setDate(true)} label="Pick Date" required id="DatePicker" placeholder="Select date" aria-label="Date" />
            <br />
            <HvTimePicker onChange={() => setTime(true)} required id="main" label="Time" placeholder="Select time" locale="pt-pt" />
            <br />
            <HvCheckBox disabled={cardPoints < 10} checked={useDiscount} onClick={() => setDiscount(!useDiscount)} label="Use Card points" />
            <h3>Price {calcPrice()}€</h3>
            <HvButton onClick={() => setSuccess(true)} disabled={!validateEntry()} category="primary">
                Accept
            </HvButton>
            <div>
                <HvDialog open={showSuccess} onClose={() => setSuccess(false)} aria-label="Create a new post">
                    <HvDialogTitle>Payment details</HvDialogTitle>
                    <HvDialogContent style={{ width: 500 }}>
                        <form id="dialog-form" >
                            <HvInput required name="title" label="Title" placeholder="Enter text" onChange={(event, value) => setCard(value)} />
                            <br />
                        </form>
                    </HvDialogContent>
                    <HvDialogActions>
                        <HvButton type="submit" form="dialog-form" category="ghost" onClose={() => {setSuccess(false); sendBooking(); updateCardPoints()}}>
                            Submit
                        </HvButton>
                        <HvButton id="cancel" category="ghost" onClick={() => setSuccess(false)}>
                            Cancel
                        </HvButton>
                    </HvDialogActions>
                </HvDialog>
            </div>
        </HvContainer>
    );
}