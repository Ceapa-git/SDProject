import React, {useEffect, useState} from 'react';
import {useUserRole} from '../common/user-context';
import {Container} from 'react-bootstrap';
import {ClientPageType} from '../common/types';
import {axiosDevice} from '../common/axios';
import TableComponent from '../components/table-component';
import {useNavigate} from "react-router-dom";
import ModalComponent from "../components/modal-component";
import ChatComponent from "../components/chat-component";

function ClientPage() {
    const navigate = useNavigate();
    const {username, id} = useUserRole();
    const [clientPageType, setClientPageType] = useState<ClientPageType>('home');
    const [devices, setDevices] = useState<{ [key: string]: string }[]>([]);

    const [selectedDevice, setSelectedDevice] = useState<string>("");
    const [showUsageModal, setShowUsageModal] = useState<boolean>(false);

    useEffect(() => {
        const fetchDevices = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await axiosDevice.get(
                    `/devices/user/${id}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                setDevices(response.data);
            } catch (error) {
                console.error("Failed to fetch devices:", error);
            }
        };

        const handleHashChange = async () => {
            if (window.location.hash === '#showDevices') {
                await fetchDevices();
                setClientPageType('showDevices');
            } else if (window.location.hash === '#chat') {
                setClientPageType('chat');
            } else {
                setClientPageType('home');
            }
        };

        handleHashChange().then();
        window.addEventListener('hashchange', handleHashChange);

        return () => {
            window.removeEventListener('hashchange', handleHashChange);
        };
    }, [id, navigate]);

    const handleViewUsage = (row: { [key: string]: string }) => {
        setSelectedDevice(row['id']);
        setShowUsageModal(true);
    }

    return (
        <Container className="mt-5">
            {clientPageType === 'home' && <h3>{`Hello, ${username || 'Guest'}`}</h3>}

            {(clientPageType === 'showDevices' || clientPageType === 'addDevice') && (
                <>
                    <TableComponent
                        cols={3}
                        colsNames={['Description', 'Address', 'Max Hourly Consumption']}
                        data={devices}
                        actions={[
                            {
                                text: 'View Usage',
                                variant: 'success',
                                fn: handleViewUsage
                            }]}
                    />
                </>
            )}

            {clientPageType === 'chat' && (
                <ChatComponent/>
            )}

            <ModalComponent
                deviceId={selectedDevice}
                isOpen={showUsageModal}
                onClose={() => setShowUsageModal(false)}
            />
        </Container>
    );
}

export default ClientPage;
