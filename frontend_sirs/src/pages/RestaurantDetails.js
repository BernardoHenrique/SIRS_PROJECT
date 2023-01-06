import { React, useEffect, useState} from "react";
import { useParams } from "react-router-dom";
import {
    HvBanner,
    HvButton,
    HvCheckBox,
    HvContainer,
    HvDatePicker,
    HvDialog,
    HvDialogActions,
    HvDialogContent,
    HvDialogTitle,
    HvInput,
    HvTimePicker
} from "@hitachivantara/uikit-react-core";
import axios from "axios";

export const RestaurantDetails = () => {
    const { name } = useParams();
    const [numberPeople, setPeople] = useState();
    const [showSuccess, setSuccess] = useState(false);
    const [cardPoints, setCardPoints] = useState(25);
    const [useDiscount, setDiscount] = useState(false);
    const [date, setDate] = useState(false);
    const [time, setTime] = useState(false);
    const [cardNumber, setCardNumber] = useState("");
    const [ownersName, setOwnersName] = useState("");
    const [validity, setValidity] = useState("");
    const [code, setCode] = useState("");
    const [hasCard, setHasCard] = useState(false);
    const [openBanner, setOpenBanner] = useState(false);

    const validationMessages = {
        requiredError: "The number is required",
        maxCharError: "Number is too big",
        typeMismatchError: "Value is not a number",
    };

     useEffect(() => {
         axios.get("http://localhost:8080/getCard", {
             params: {
                 name: localStorage.getItem("userName"),
             }
         })
             .then(response => {
                 console.log(response.data);
                 setHasCard(response.data);
             })
             .catch(error => console.log(error))
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
        if(useDiscount && cardPoints >= 10) {
            setCardPoints(cardPoints - 10);
            setDiscount(false);
        }
    }

    const updateCard = () => {
        if(!hasCard){
            axios.get("http://localhost:8080/updateCard", {
                params: {
                    card: cardNumber,
                    name: ownersName,
                    validity: validity,
                    code: code
                }
            })
                .then(response => {
                console.log(response.data);
            })
                .catch(error => console.log(error))
            setHasCard(true);
        }
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
                    <HvDialogContent style={{ width: 500 }}>
                        {!hasCard ?
                            (
                                <form id="form" >
                                    <HvDialogTitle>Payment details</HvDialogTitle>
                                    <h2>You don't have any card associated</h2>
                                    <HvInput required name="CardNumber" label="Card number" placeholder="Insert card number" onChange={(event, value) => setCardNumber(value)} />
                                    <br />
                                    <HvInput required name="Validity" label="Validity date" placeholder="Insert validity date" onChange={(event, value) => setValidity(value)} />
                                    <br />
                                    <HvInput required name="Owner Name" label="Owner Name" placeholder="Insert owner's name" onChange={(event, value) => setOwnersName(value)} />
                                    <br />
                                    <HvInput required name="3 digits code" label="3 digits code" placeholder="Insert 3 digits code" onChange={(event, value) => setCode(value)} />
                                    <br />
                            </form>) : (
                                <HvDialogTitle>Do you want to pay with your registered card?</HvDialogTitle>
                            )}
                    </HvDialogContent>
                    <HvDialogActions>
                        <HvButton type="submit" form="dialog-form" category="ghost" onClick={() => {setSuccess(false); updateCardPoints(); updateCard(); setOpenBanner(true)}}>
                            Submit
                        </HvButton>
                        <HvButton id="cancel" category="ghost" onClick={() => setSuccess(false)}>
                            Cancel
                        </HvButton>
                    </HvDialogActions>
                </HvDialog>
            </div>
            <HvBanner open={openBanner} variant="success" showIcon label="Reservation completed" onClose={() => setOpenBanner(false)}/>
        </HvContainer>
    );
}