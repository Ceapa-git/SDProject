import React, {useState} from 'react';
import {Button, Container, Form, Modal, Nav, Navbar as BootstrapNavbar} from 'react-bootstrap';
import {useUserRole} from '../common/user-context';
import {axiosUser} from '../common/axios';
import {useNavigate} from "react-router-dom";

type NavbarComponentProps = {
    logoutCallback: () => void;
};

const NavbarComponent: React.FC<NavbarComponentProps> = ({logoutCallback}) => {
    const navigate = useNavigate();
    const {userRole, username, id, logout, login} = useUserRole();

    const [showModal, setShowModal] = useState(false);
    const [newUsername, setNewUsername] = useState(username || '');
    const [newPassword, setNewPassword] = useState('');

    const handleAccountInfoClick = () => setShowModal(true);

    const handleSaveChanges = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axiosUser.put(
                `/users/${id}`,
                {
                    username: newUsername,
                    password: newPassword,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
            if (response.status !== 200) {
                console.error("Error updating account info:", response);
                alert("Failed to update account information.");
                return;
            }
            const responseLogin = await axiosUser.post('/login', {
                username: newUsername,
                password: newPassword,
            });
            if (responseLogin.status !== 200) {
                console.error("Error updating account info:", responseLogin);
                alert("Failed to update account information.");
                return;
            }
            const jwt = responseLogin.data.jwt;
            const {role: responseRole, username: responseUsername, id: responseId} = responseLogin.data.loginData;
            setShowModal(false);
            login(responseRole.toLowerCase(), responseUsername, responseId, jwt);
            navigate('/');
        } catch (error) {
            console.error("Error updating account info:", error);
            alert("Failed to update account information.");
        }
    };

    const handleLogout = () => {
        logoutCallback();
        logout();
    };

    return (
        <>
            <BootstrapNavbar bg="light" expand="lg">
                <Container>
                    <BootstrapNavbar.Brand>
                        {userRole === 'admin' ? 'Admin App' : 'Client App'}
                    </BootstrapNavbar.Brand>
                    <BootstrapNavbar.Toggle aria-controls="basic-navbar-nav"/>
                    <BootstrapNavbar.Collapse id="basic-navbar-nav">
                        <Nav className="me-auto">
                            {userRole === 'client' ? (
                                <>
                                    <Nav.Link href="#showDevices">Show Devices</Nav.Link>
                                    <Nav.Link href="#chat">Chat with Admins</Nav.Link>
                                </>
                            ) : (
                                <>
                                    <Nav.Link href="#showUsers">Show Users</Nav.Link>
                                    <Nav.Link href="#addUser">Add User</Nav.Link>
                                    <Nav.Link href="#chat">Chat with Clients</Nav.Link>
                                </>
                            )}
                        </Nav>
                        <Nav>
                            <Button variant="outline-primary" className="me-2" onClick={handleAccountInfoClick}>
                                Change Account Info
                            </Button>
                            <Button variant="outline-danger" onClick={handleLogout}>
                                Logout
                            </Button>
                        </Nav>
                    </BootstrapNavbar.Collapse>
                </Container>
            </BootstrapNavbar>

            {/* Modal for Changing Account Info */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Change Account Info</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId="formNewUsername" className="mb-3">
                            <Form.Label>New Username</Form.Label>
                            <Form.Control
                                type="text"
                                value={newUsername}
                                onChange={(e) => setNewUsername(e.target.value)}
                            />
                        </Form.Group>
                        <Form.Group controlId="formNewPassword" className="mb-3">
                            <Form.Label>New Password</Form.Label>
                            <Form.Control
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                            />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={handleSaveChanges}>
                        Save Changes
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default NavbarComponent;
