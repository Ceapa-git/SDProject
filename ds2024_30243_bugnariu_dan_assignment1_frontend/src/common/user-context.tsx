import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import {UserRole} from './types';
import {axiosUser} from "./axios";

type UserRoleContextType = {
    userRole: UserRole | undefined;
    username: string | undefined;
    id: string | undefined;
    token: string | undefined;
    login: (role: UserRole, username: string, id: string, jwt: string) => void;
    logout: () => void;
};

const UserRoleContext = createContext<UserRoleContextType | undefined>(undefined);

export const UserRoleProvider = ({children}: { children: ReactNode }) => {
    const [userRole, setUserRole] = useState<UserRole | undefined>(undefined);
    const [username, setUsername] = useState<string | undefined>(undefined);
    const [id, setId] = useState<string | undefined>(undefined);
    const [token, setToken] = useState<string | undefined>(undefined);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const checkStoredUser = async () => {
            const storedRole = localStorage.getItem('userRole') as UserRole | null;
            const storedUsername = localStorage.getItem('username');
            const storedId = localStorage.getItem('id');
            const storedJwt = localStorage.getItem('token');

            if (storedRole && storedUsername && storedId && storedJwt) {
                try {
                    const response = await axiosUser.post(
                        '/checkToken',
                        {},
                        {
                            headers: {
                                Authorization: `Bearer ${storedJwt}`,
                            },
                        });

                    if (response.status === 200) {
                        setUserRole(storedRole);
                        setUsername(storedUsername);
                        setId(storedId);
                        setToken(storedJwt);
                    } else {
                        clearLocalStorage();
                    }
                } catch {
                    clearLocalStorage();
                }
            }
            setIsLoading(false);
        };

        checkStoredUser().then();
    }, []);

    const clearLocalStorage = () => {
        localStorage.removeItem('userRole');
        localStorage.removeItem('username');
        localStorage.removeItem('id');
        localStorage.removeItem('token');
    };

    if (isLoading) {
        return <div className="loading-widget">Loading...</div>; // Replace this with your spinner or loading component
    }

    function login(role: UserRole, username: string, id: string, jwt: string) {
        setUserRole(role);
        setUsername(username);
        setId(id);
        setToken(jwt);

        localStorage.setItem('userRole', role);
        localStorage.setItem('username', username);
        localStorage.setItem('id', id);
        localStorage.setItem('token', jwt);
    }

    function logout() {
        setUserRole(undefined);
        setUsername(undefined);
        setId(undefined);
        setToken(undefined);

        clearLocalStorage();
    }

    return (
        <UserRoleContext.Provider value={{userRole, username, id, token, login, logout}}>
            {children}
        </UserRoleContext.Provider>
    );
};

export const useUserRole = () => {
    const context = useContext(UserRoleContext);
    if (!context) {
        throw new Error("useUserRole must be used within a UserRoleProvider");
    }
    return context;
};
