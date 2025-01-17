import React, {useEffect, useRef, useState} from 'react';
import AdminPage from "./admin-page";
import ClientPage from "./client-page";
import {Navigate} from "react-router-dom";
import {useUserRole} from "../common/user-context";
import NavbarComponent from "../components/navbar-component";
import {CompatClient, Stomp} from "@stomp/stompjs";
import SockJS from "sockjs-client";
import {Toast, ToastContainer} from "react-bootstrap";
import {axiosDevice, axiosUser} from "../common/axios";

function HomePage() {
    const [showNotificationDevice, setShowNotificationDevice] = useState(false);
    const [notificationMessageDevice, setNotificationMessageDevice] = useState("");
    const [showNotificationChat, setShowNotificationChat] = useState(false);
    const [notificationMessageChat, setNotificationMessageChat] = useState("");

    const {userRole, id} = useUserRole();

    let stompClientRefDevice = useRef<CompatClient | null>(null);
    let websocketRefDevice = useRef<WebSocket | null>(null);
    let stompClientRefChat = useRef<CompatClient | null>(null);
    let websocketRefChat = useRef<WebSocket | null>(null);

    useEffect(() => {
        let stompClientDevice = stompClientRefDevice.current;
        let websocketDevice = websocketRefDevice.current;
        if (stompClientDevice) stompClientDevice.disconnect();
        if (websocketDevice) {
            websocketDevice.close();
        }
        websocketDevice = new SockJS(process.env.REACT_APP_DEVICE_WEBSOCKET + 'monitoring/socket');
        stompClientDevice = Stomp.over(websocketDevice);
        stompClientDevice.connect({}, () => {
            console.log("Connected to WebSocket");
            stompClientDevice!.subscribe("/notifications/socket/user/" + id, notification => {
                const token = localStorage.getItem('token');
                axiosDevice.get(
                    "devices/" + JSON.parse(notification.body),
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }).then(response => {
                    setNotificationMessageDevice(response.data.description + " has exceeded the threshold of " + response.data.maxHourlyConsumption);
                    setShowNotificationDevice(true);
                });
            });
        }, () => {
            console.log("Failed to connect to WebSocket");
        }, () => {
            console.log("Disconnected from WebSocket");
        });
        stompClientRefDevice.current = stompClientDevice;
        websocketRefDevice.current = websocketDevice;


        let stompClientChat = stompClientRefChat.current;
        let websocketChat = websocketRefChat.current;
        if (stompClientChat) stompClientChat.disconnect();
        if (websocketChat) {
            websocketChat.close();
        }
        websocketChat = new SockJS(process.env.REACT_APP_CHAT_WEBSOCKET + 'chat/socket');
        stompClientChat = Stomp.over(websocketChat);
        stompClientChat.connect({}, () => {
            console.log("Connected to WebSocket");
            stompClientChat!.subscribe("/chat/seen/socket/user/" + id, notification => {
                const token = localStorage.getItem('token');
                axiosUser.get(
                    `users/${JSON.parse(notification.body)}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }).then(response => {
                    setNotificationMessageChat("Message to " + response.data.username + " has been seen");
                    setShowNotificationChat(true);
                });
            });
        }, () => {
            console.log("Failed to connect to WebSocket");
        }, () => {
            console.log("Disconnected from WebSocket");
        });
        stompClientRefChat.current = stompClientChat;
        websocketRefChat.current = websocketChat;
    }, [id]);

    const disconnect = () => {
        if (websocketRefDevice.current) {
            websocketRefDevice.current.close();
        }
        if (stompClientRefDevice.current) {
            stompClientRefDevice.current.disconnect();
        }
        if (websocketRefChat.current) {
            websocketRefChat.current.close();
        }
        if (stompClientRefChat.current) {
            stompClientRefChat.current.disconnect();
        }
    };

    if (userRole === undefined) {
        return <Navigate to="/login" replace/>;
    }

    return (
        <div>
            <NavbarComponent logoutCallback={disconnect}/>
            {userRole === 'admin' ? <AdminPage/> : <ClientPage/>}
            <ToastContainer position="top-end" className="p-3">
                <Toast onClose={() => setShowNotificationDevice(false)} show={showNotificationDevice} delay={3000}
                       autohide>
                    <Toast.Header>
                        <strong className="me-auto">Notification</strong>
                    </Toast.Header>
                    <Toast.Body>{notificationMessageDevice}</Toast.Body>
                </Toast>
            </ToastContainer>
            <ToastContainer position="bottom-end" className="p-3">
                <Toast onClose={() => setShowNotificationChat(false)} show={showNotificationChat} delay={3000} autohide>
                    <Toast.Header>
                        <strong className="me-auto">Message seen</strong>
                    </Toast.Header>
                    <Toast.Body>{notificationMessageChat}</Toast.Body>
                </Toast>
            </ToastContainer>
        </div>
    );
}

export default HomePage;
