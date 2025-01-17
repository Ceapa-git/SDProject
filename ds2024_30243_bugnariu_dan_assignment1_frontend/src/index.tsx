import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './app';
import {UserRoleProvider} from "./common/user-context";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <React.StrictMode>
        <UserRoleProvider>
            <App/>
        </UserRoleProvider>
    </React.StrictMode>
);