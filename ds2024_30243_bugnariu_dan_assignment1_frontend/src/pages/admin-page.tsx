import React, {useEffect, useState} from 'react';
import {Button, Container, Modal} from 'react-bootstrap';
import {useUserRole} from '../common/user-context';
import {AdminPageType} from '../common/types';
import {axiosDevice, axiosUser} from '../common/axios';
import TableComponent from '../components/table-component';
import FormComponent, {FieldType} from '../components/form-component';
import {useNavigate} from "react-router-dom";
import ModalComponent from "../components/modal-component";
import ChatComponent from "../components/chat-component";

function AdminPage() {
    const navigate = useNavigate();
    const {username} = useUserRole();
    const [adminPageType, setAdminPageType] = useState<AdminPageType>('home');
    const [users, setUsers] = useState<{ [key: string]: string }[]>([]);
    const [devices, setDevices] = useState<{ [key: string]: string }[]>([]);
    const [selectedUser, setSelectedUser] = useState<{ [key: string]: string } | null>(null);
    const [selectedDevice, setSelectedDevice] = useState<{ [key: string]: string } | null>(null);

    const [showEditModal, setShowEditModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showAddModal, setShowAddModal] = useState(false);
    const [showDevicesModal, setShowDevicesModal] = useState(false);
    const [showDeviceEditModal, setShowDeviceEditModal] = useState(false);
    const [showAddDeviceModal, setShowAddDeviceModal] = useState(false);

    const [selectedDeviceId, setSelectedDeviceId] = useState<string>("");
    const [showUsageModal, setShowUsageModal] = useState<boolean>(false);

    const addUserFields: { name: string, type: FieldType, options?: string[] }[] = [
        {name: 'Username', type: 'text'},
        {name: 'Role', type: 'dropdown', options: ['ADMIN', 'CLIENT']},
        {name: 'Password', type: 'text'},
    ];

    const editDeviceFields: { name: string, type: FieldType }[] = [
        {name: 'Description', type: 'text'},
        {name: 'Address', type: 'text'},
        {name: 'Max Hourly Consumption', type: 'text'},
    ];

    const addDeviceFields: { name: string, type: FieldType }[] = [
        {name: 'Description', type: 'text'},
        {name: 'Address', type: 'text'},
        {name: 'Max Hourly Consumption', type: 'text'},
    ];

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await axiosUser.get(
                    '/users',
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                setUsers(response.data);
            } catch (error) {
                console.error("Failed to fetch users:", error);
            }
        };

        const handleHashChange = async () => {
            if (window.location.hash === '#showUsers') {
                await fetchUsers();
                setAdminPageType('showUsers');
            } else if (window.location.hash === '#addUser') {
                navigate('#showUsers');
                setShowAddModal(true);
            } else if (window.location.hash === '#chat') {
                setAdminPageType('chat');
            } else {
                setAdminPageType('home');
            }
        };

        handleHashChange().then();
        window.addEventListener('hashchange', handleHashChange);

        return () => {
            window.removeEventListener('hashchange', handleHashChange);
        };
    }, [navigate]);

    const handleAddUserSubmit = async (data: { [key: string]: any }) => {
        try {
            const token = localStorage.getItem('token');
            const response = await axiosUser.post(
                '/users',
                {
                    username: data.Username,
                    role: data.Role.toUpperCase(),
                    password: data.Password,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
            setUsers((prevUsers) => [...prevUsers, response.data]);
            setShowAddModal(false);
            setAdminPageType('showUsers');
        } catch (error) {
            console.error("Error adding user:", error);
        }
    };

    const handleEdit = (user: { [key: string]: string }) => {
        setSelectedUser(user);
        setShowEditModal(true);
    };

    const handleSaveEdit = async (data: { [key: string]: any }) => {
        if (selectedUser) {
            try {
                const token = localStorage.getItem('token');
                await axiosUser.put(
                    `/users/${selectedUser.id}`,
                    {
                        username: data.Username,
                        role: data.Role,
                        password: data.Password,
                    },
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                setUsers((prevUsers) =>
                    prevUsers.map((user) =>
                        user.id === selectedUser.id
                            ? {...user, username: data.Username, role: data.Role}
                            : user
                    )
                );
                setShowEditModal(false);
            } catch (error) {
                console.error("Error updating user:", error);
            }
        }
    };

    const handleDelete = (user: { [key: string]: string }) => {
        setSelectedUser(user);
        setShowDeleteModal(true);
    };

    const handleConfirmDelete = async () => {
        if (selectedUser) {
            try {
                const token = localStorage.getItem('token');
                await axiosUser.delete(
                    `/users/${selectedUser.id}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                setUsers((prevUsers) => prevUsers.filter((user) => user.id !== selectedUser.id));
                setShowDeleteModal(false);
            } catch (error) {
                console.error("Error deleting user:", error);
            }
        }
    };

    const handleViewDevices = async (user: { [key: string]: string }) => {
        setSelectedUser(user);
        try {
            const token = localStorage.getItem('token');
            const response = await axiosDevice.get(
                `/devices/user/${user.id}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
            setDevices(response.data);
            setShowDevicesModal(true);
        } catch (error) {
            console.error("Failed to fetch devices:", error);
        }
    };

    const handleEditDevice = (device: { [key: string]: string }) => {
        setSelectedDevice(device);
        setShowDeviceEditModal(true);
    };

    const handleSaveDeviceEdit = async (data: { [key: string]: any }) => {
        if (selectedDevice) {
            try {
                const token = localStorage.getItem('token');
                await axiosDevice.put(
                    `/devices/${selectedDevice.id}`,
                    {
                        description: data.Description,
                        address: data.Address,
                        maxHourlyConsumption: data['Max Hourly Consumption'],
                    },
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                setDevices((prevDevices) =>
                    prevDevices.map((device) =>
                        device.id === selectedDevice.id
                            ? {
                                ...device,
                                description: data.Description,
                                address: data.Address,
                                maxHourlyConsumption: data['Max Hourly Consumption']
                            }
                            : device
                    )
                );
                setShowDeviceEditModal(false);
            } catch (error) {
                console.error("Error updating device:", error);
            }
        }
    };

    const handleDeleteDevice = async (device: { [key: string]: string }) => {
        try {
            const token = localStorage.getItem('token');
            await axiosDevice.delete(
                `/devices/${device.id}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
            setDevices((prevDevices) => prevDevices.filter((d) => d.id !== device.id));
        } catch (error) {
            console.error("Error deleting device:", error);
        }
    };

    const handleAddDevice = (user: { [key: string]: string }) => {
        setSelectedUser(user); // Set the user for whom the device is being added
        setShowAddDeviceModal(true); // Open the Add Device modal
    };

    const handleSaveNewDevice = async (data: { [key: string]: any }) => {
        try {
            const token = localStorage.getItem('token');
            const response = await axiosDevice.post(
                '/devices',
                {
                    description: data.Description,
                    address: data.Address,
                    maxHourlyConsumption: data['Max Hourly Consumption'],
                    userId: selectedUser?.id,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
            setDevices((prevDevices) => [...prevDevices, response.data]);
            setShowAddDeviceModal(false);
        } catch (error) {
            console.error("Error adding device:", error);
        }
    };

    const handleViewUsage = (device: { [key: string]: string }) => {
        setSelectedDeviceId(device['id']);
        setShowUsageModal(true);
    }


    return (
        <Container className="mt-5">
            {adminPageType === 'home' && <h3>{`Hello, ${username || 'Admin'}`}</h3>}

            {adminPageType === 'showUsers' && (
                <TableComponent
                    cols={2}
                    colsNames={['Username', 'Role']}
                    data={users}
                    actions={[
                        {
                            text: 'Add Device',
                            variant: 'primary',
                            fn: handleAddDevice,
                        },
                        {
                            text: 'View Devices',
                            variant: 'info',
                            fn: handleViewDevices
                        },
                        {
                            text: 'Edit',
                            variant: 'warning',
                            fn: handleEdit
                        },
                        {
                            text: 'Delete',
                            variant: 'danger',
                            fn: handleDelete
                        }
                    ]}
                />
            )}

            {adminPageType === 'chat' && (
                <ChatComponent/>
            )}

            {/* Edit User Modal */}
            <Modal show={showEditModal} onHide={() => setShowEditModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit User</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormComponent
                        fields={addUserFields}
                        onSubmit={handleSaveEdit}
                        initialValues={{
                            Username: selectedUser?.username,
                            Role: selectedUser?.role,
                        }}
                    />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowEditModal(false)}>
                        Cancel
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Edit Device Modal */}
            <Modal show={showDeviceEditModal} onHide={() => setShowDeviceEditModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit Device</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormComponent
                        fields={editDeviceFields}
                        onSubmit={handleSaveDeviceEdit}
                        initialValues={{
                            Description: selectedDevice?.description,
                            Address: selectedDevice?.address,
                            'Max Hourly Consumption': selectedDevice?.maxHourlyConsumption,
                        }}
                    />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeviceEditModal(false)}>
                        Cancel
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Delete Confirmation Modal */}
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Confirm Delete</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Are you sure you want to delete this user?
                    <ul>
                        <li><strong>Username:</strong> {selectedUser?.username}</li>
                        <li><strong>Role:</strong> {selectedUser?.role}</li>
                    </ul>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
                        Cancel
                    </Button>
                    <Button variant="danger" onClick={handleConfirmDelete}>
                        Delete
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* View Devices Modal */}
            <Modal show={showDevicesModal} onHide={() => setShowDevicesModal(false)} size={"lg"}>
                <Modal.Header closeButton>
                    <Modal.Title>Devices for {selectedUser?.username}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <TableComponent
                        cols={3}
                        colsNames={['Description', 'Address', 'Max Hourly Consumption']}
                        data={devices}
                        actions={[
                            {
                                text: 'View Usage',
                                variant: 'success',
                                fn: handleViewUsage,
                            },
                            {
                                text: 'Edit',
                                variant: 'warning',
                                fn: handleEditDevice,
                            },
                            {
                                text: 'Delete',
                                variant: 'danger',
                                fn: handleDeleteDevice,
                            }
                        ]}
                    />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDevicesModal(false)}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Add User Modal */}
            <Modal show={showAddModal} onHide={() => setShowAddModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Add User</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormComponent fields={addUserFields} onSubmit={handleAddUserSubmit} initialValues={{
                        Role: 'CLIENT'
                    }}/>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowAddModal(false)}>
                        Cancel
                    </Button>
                </Modal.Footer>
            </Modal>
            <Modal show={showAddDeviceModal} onHide={() => setShowAddDeviceModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Add Device</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormComponent fields={addDeviceFields} onSubmit={handleSaveNewDevice} initialValues={{}}/>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowAddDeviceModal(false)}>
                        Cancel
                    </Button>
                </Modal.Footer>
            </Modal>

            <ModalComponent
                deviceId={selectedDeviceId}
                isOpen={showUsageModal}
                onClose={() => setShowUsageModal(false)}
            />

        </Container>
    );
}

export default AdminPage;
