import React, { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useUserRole } from '../common/user-context';
import { axiosUser } from '../common/axios';
import { Alert } from 'react-bootstrap';

function LoginPage() {
    const navigate = useNavigate();
    const { userRole, login } = useUserRole();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    if (userRole !== undefined && username !== '' && password !== '') {
        return <Navigate to="/" replace />;
    }

    async function handleSubmit(event: React.FormEvent) {
        event.preventDefault();
        try {
            const response = await axiosUser.post('/login', {
                username,
                password,
            });
            if (response.status === 200) {
                const jwt = response.data.jwt;
                const { role: responseRole, username: responseUsername, id: responseId } = response.data.loginData;
                login(responseRole.toLowerCase(), responseUsername, responseId, jwt);
                navigate('/');
            } else {
                setErrorMessage('Login failed. Please check your credentials.');
            }
        } catch (error) {
            console.error('Error logging in:', error);
            setErrorMessage('An error occurred during login. Please try again.');
        }
    }

    return (
        <div className="d-flex justify-content-center align-items-center vh-100">
            <div className="card p-4" style={{ width: '100%', maxWidth: '400px' }}>
                <h3 className="card-title text-center">Login</h3>

                {errorMessage && (
                    <Alert variant="danger" onClose={() => setErrorMessage(null)} dismissible>
                        {errorMessage}
                    </Alert>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Username</label>
                        <input
                            type="text"
                            className="form-control"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Password</label>
                        <input
                            type="password"
                            className="form-control"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100">Login</button>
                </form>
            </div>
        </div>
    );
}

export default LoginPage;
