import React, {useEffect, useRef, useState} from 'react';
import {Button, Card, Col, Container, Form, ListGroup, Row} from 'react-bootstrap';
import {axiosChat, axiosUser} from "../common/axios";
import {useUserRole} from "../common/user-context";
import {CompatClient, Stomp} from "@stomp/stompjs";
import SockJS from "sockjs-client";

type ChatProps = {};

type Message = {
    text: string;
    senderId: string;
    receiverId: string;
    timestamp: number;
    seen: boolean;
};

const ChatComponent: React.FC<ChatProps> = () => {
    const [selectedId, setSelectedId] = useState<string | null>(null);
    const [selectedUser, setSelectedUser] = useState<string | null>(null);
    const [users, setUsers] = useState<{ id: string, username: string }[]>([]);
    const [messages, setMessages] = useState<Message[]>([]);
    const [newMessage, setNewMessage] = useState<string>('');
    const [writingStatuses, setWritingStatuses] = useState<string[]>([]);
    const {id, userRole} = useUserRole();

    let stompClientRefChatReceive = useRef<CompatClient | null>(null);
    let websocketRefChatReceive = useRef<WebSocket | null>(null);
    let stompClientRefChatSend = useRef<CompatClient | null>(null);
    let websocketRefChatSend = useRef<WebSocket | null>(null);

    useEffect(() => {
        let stompClientChatReceive = stompClientRefChatReceive.current;
        let websocketChatReceive = websocketRefChatReceive.current;
        if (stompClientChatReceive) stompClientChatReceive.disconnect();
        if (websocketChatReceive) websocketChatReceive.close();

        const URL_RECEIVE = process.env.REACT_APP_CHAT_WEBSOCKET + 'chat/socket';
        websocketChatReceive = new SockJS(URL_RECEIVE);
        stompClientChatReceive = Stomp.over(websocketChatReceive);
        stompClientChatReceive.connect({}, () => {
            console.log("Connected to WebSocket for receiving messages");
            stompClientChatReceive!.subscribe("/chat/receive/socket/user/" + id, notification => {
                const token = localStorage.getItem('token');
                axiosChat.post(
                    `message/seen`,
                    JSON.parse(notification.body),
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                ).then(() => {
                });
                setMessages((prevMessages) => {
                    const newMessage = JSON.parse(notification.body);
                    return [...prevMessages, newMessage];
                });
            });
        }, () => {
            console.log("Failed to connect to WebSocket for receiving messages");
        });

        stompClientRefChatReceive.current = stompClientChatReceive;
        websocketRefChatReceive.current = websocketChatReceive;

        return () => {
            stompClientChatReceive?.disconnect();
            websocketChatReceive?.close();
        };
    }, [id]);

    useEffect(() => {
        let stompClientChatSend = stompClientRefChatSend.current;
        let websocketChatSend = websocketRefChatSend.current;
        if (stompClientChatSend) stompClientChatSend.disconnect();
        if (websocketChatSend) websocketChatSend.close();

        const URL_SEND = process.env.REACT_APP_CHAT_WEBSOCKET + 'chat/socket';
        websocketChatSend = new SockJS(URL_SEND);
        stompClientChatSend = Stomp.over(websocketChatSend);
        stompClientChatSend.connect({}, () => {
            console.log("Connected to WebSocket for sending messages");
        }, () => {
            console.log("Failed to connect to WebSocket for sending messages");
        });

        stompClientRefChatSend.current = stompClientChatSend;
        websocketRefChatSend.current = websocketChatSend;

        return () => {
            stompClientChatSend?.disconnect();
            websocketChatSend?.close();
        };
    }, []);
    let stompClientRefChat = useRef<CompatClient | null>(null);
    let websocketRefChat = useRef<WebSocket | null>(null);

    useEffect(() => {
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
                setMessages((prevMessages) => {
                    const userId = JSON.parse(notification.body);
                    return prevMessages.map((msg) => {
                        if (msg.receiverId === userId) {
                            return {...msg, seen: true};
                        }
                        return msg;
                    });
                });
            });
        }, () => {
            console.log("Failed to connect to WebSocket");
        }, () => {
            console.log("Disconnected from WebSocket");
        });
        stompClientRefChat.current = stompClientChat;
        websocketRefChat.current = websocketChat;
        return () => {
            stompClientChat?.disconnect();
            websocketChat?.close();
        }
    }, [id]);

    let stompClientRefWriting = useRef<CompatClient | null>(null);
    let websocketRefWriting = useRef<WebSocket | null>(null);

    useEffect(() => {
        let stompClientWriting = stompClientRefWriting.current;
        let websocketWriting = websocketRefWriting.current;

        if (stompClientWriting) stompClientWriting.disconnect();
        if (websocketWriting) websocketWriting.close();

        const websocket = new SockJS(process.env.REACT_APP_CHAT_WEBSOCKET + 'chat/socket');
        stompClientWriting = Stomp.over(websocket);

        stompClientWriting.connect({}, () => {
            console.log("Connected to WebSocket for writing status");

            stompClientWriting!.subscribe(`/chat/writing/start/socket/user/${id}`, (message) => {
                const senderId = JSON.parse(message.body);
                setWritingStatuses((prevStatuses) => [...prevStatuses, senderId]);
            });

            stompClientWriting!.subscribe(`/chat/writing/stop/socket/user/${id}`, (message) => {
                const senderId = JSON.parse(message.body);
                setWritingStatuses((prevStatuses) => prevStatuses.filter((status) => status !== senderId));
            });
        }, () => {
            console.log("Failed to connect to WebSocket for writing status");
        });

        stompClientRefWriting.current = stompClientWriting;
        websocketRefWriting.current = websocket;

        return () => {
            stompClientWriting?.disconnect();
            websocketWriting?.close();
        };
    }, [id]);

    useEffect(() => {
        const token = localStorage.getItem('token');
        axiosUser.get(
            'users',
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }).then((response) => {
            const responseUsers = response.data
                .filter((user: { id: string, username: string, role: string }) => user.role.toLowerCase() !== userRole)
                .map((user: { id: string, username: string }) => ({
                    id: user.id,
                    username: user.username,
                }));
            setUsers(responseUsers);

            axiosChat.get(
                `message/${id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }).then((response) => {
                setMessages(response.data);
            });
        });
        axiosChat.get(
            `writing/${id}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }).then((response) => {
                console.log(response.data.map((writingStatus: { senderId: string }) => writingStatus.senderId));
                setWritingStatuses(response.data.map((writingStatus: { senderId: string }) => writingStatus.senderId));
            }
        )
    }, [id, userRole]);

    const handleSelectId = (id: string) => {
        setSelectedId(id);
        const user = users.find((user) => user.id === id);
        setSelectedUser(user?.username || null);
    };

    const handleSendMessage = () => {
        if (stompClientRefChatSend.current && selectedId && newMessage.trim()) {
            const messagePayload: Message = {
                text: newMessage,
                senderId: id!,
                receiverId: selectedId,
                timestamp: new Date().getTime(),
                seen: false,
            };
            stompClientRefChatSend.current.send(
                `/chat/send/socket/user/${selectedId}`,
                {},
                JSON.stringify(messagePayload)
            );
            setMessages((prevMessages) => [...prevMessages, messagePayload]);
            setNewMessage('');
            handleTyping(false);
        }
    };

    const handleTyping = (isTyping: boolean) => {
        if (stompClientRefChatSend.current && selectedId) {
            const endpoint = isTyping ? `/writing/start` : `/writing/stop`;
            const token = localStorage.getItem('token');
            axiosChat.post(
                endpoint,
                {
                    senderId: id,
                    receiverId: selectedId,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            ).then(() => {
                console.log("Typing status updated");
                console.log(endpoint);
            });
        }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setNewMessage(e.target.value);
        handleTyping(!!e.target.value);
    };

    return (
        <Container className="mt-4">
            <Row>
                <Col md={4}>
                    <ListGroup>
                        {users.map((user) => (
                            <ListGroup.Item
                                key={user.id}
                                action
                                active={selectedId === user.id}
                                onClick={() => handleSelectId(user.id)}
                            >
                                {user.username}
                                {writingStatuses.filter(
                                    (writingStatus) => writingStatus === user.id
                                ).length > 0 && (
                                    " (writing...)"
                                )}
                            </ListGroup.Item>
                        ))}
                    </ListGroup>

                </Col>
                <Col md={8}>
                    <Card>
                        <Card.Header>
                            {selectedId ? `Chat with ${selectedUser}` : 'Select a person to chat with'}
                        </Card.Header>
                        <Card.Body style={{height: '400px', overflowY: 'auto'}}>
                            {selectedId && messages.filter(
                                (msg) => (msg.senderId === selectedId || msg.receiverId === selectedId)
                            ) ? (
                                messages
                                    .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()) // Parse and compare timestamps
                                    .filter((msg) => (msg.senderId === selectedId || msg.receiverId === selectedId))
                                    .map((msg, index) => {
                                        const formattedTimestamp = new Date(msg.timestamp).toLocaleString(); // Format the timestamp
                                        return (
                                            <div
                                                key={index}
                                                className={`mb-2 ${msg.senderId === id ? 'text-end' : 'text-start'}`}
                                            >
                                                <div>
                                                    <strong>{msg.senderId === id ? 'You' : selectedUser}:</strong>
                                                    <span> {msg.text}</span>
                                                </div>
                                                <div className="text-muted" style={{fontSize: '0.8rem'}}>
                                                    {formattedTimestamp}
                                                </div>
                                                {msg.senderId === id && (
                                                    <div className="text-muted" style={{fontSize: '0.7rem'}}>
                                                        {msg.seen ? 'Seen' : 'Delivered'}
                                                    </div>
                                                )}
                                            </div>
                                        );
                                    })

                            ) : (
                                <div className="text-center text-muted">
                                    No messages to display
                                </div>
                            )}
                        </Card.Body>
                        {selectedId && (
                            <Card.Footer>
                                <Form onSubmit={(e) => {
                                    e.preventDefault();
                                    handleSendMessage();
                                }}>
                                    <Row>
                                        <Col xs={9}>
                                            <Form.Control
                                                type="text"
                                                value={newMessage}
                                                onChange={handleInputChange}
                                                placeholder="Type a message..."
                                            />
                                        </Col>
                                        <Col xs={3} className="text-end">
                                            <Button
                                                variant="primary"
                                                type="submit"
                                                disabled={!newMessage.trim()}
                                            >
                                                Send
                                            </Button>
                                        </Col>
                                    </Row>
                                </Form>
                            </Card.Footer>
                        )}
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default ChatComponent;
