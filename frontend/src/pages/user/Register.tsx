import {FormEvent, useReducer, useState} from "react";
import {attemptRegister, RegisterRequest} from "../../lib/User";
import {Link} from "react-router-dom";
import {formReducer} from "../../lib/utils";

const initialFormData: RegisterRequest = {
    username: "",
    email: "",
    password: ""
}

export default function Register() {
    const [formData, setFormData] = useReducer(formReducer<RegisterRequest>, initialFormData);
    const [isRegistered, setIsRegistered] = useState(false);

    function handleFormSubmit(event: FormEvent) {
        event.preventDefault();
        console.log(formData);
        attemptRegister(formData)
            .then(() => setIsRegistered(true));
    }

    if (isRegistered) {
        return <>
            <h1>Rejestracja ukończona</h1>
            <p>Potwierdź adres mailowy przed zalogowaniem</p>
            <Link to="/user/login">Login</Link>
        </>;
    }

    return <>
        <h1>Register new account</h1>

        <form onSubmit={handleFormSubmit}>
            <label>
                <p>Nazwa użytkownika:</p>
                <input name="username" type="text" onChange={setFormData}/>
            </label>
            <label>
                <p>Email:</p>
                <input name="email" type="email" onChange={setFormData}/>
            </label>
            <label>
                <p>Hasło:</p>
                <input name="password" type="password" onChange={setFormData}/>
            </label>
            <input type="submit" value="Rejestracja"/>
        </form>
    </>
}
