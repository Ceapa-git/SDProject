import React from 'react';
import {BrowserRouter, Routes, Route} from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';

import HomePage from "./pages/home-page";
import ErrorPage from "./common/errorhandling/error-page";

import './app.css';
import LoginPage from "./pages/login-page";

function App() {
    return (
        <div className="App">
                <BrowserRouter>
                    <Routes>
                        <Route path='/' element={<HomePage/>}/>
                        <Route path='/login' element={<LoginPage/>}/>

                        {/*Error*/}
                        <Route path='/error' element={<ErrorPage/>}/>
                        <Route element={<ErrorPage/>}/>
                    </Routes>
                </BrowserRouter>
        </div>
    );
}

export default App;
