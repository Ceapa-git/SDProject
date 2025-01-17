import React, {useEffect, useRef} from 'react';
import {Button, Modal} from 'react-bootstrap'
import {StaticDatePicker} from '@mui/x-date-pickers/StaticDatePicker';
import {LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFnsV3";
import {axiosDevice, axiosMonitoring} from "../common/axios";
import {LineChart} from "@mui/x-charts";

// Component Props
interface ModalComponentProps {
    deviceId: string;
    isOpen: boolean;
    onClose: () => void;
}

type Device = {
    description: string;
    address: string;
    maxHourlyConsumption: number;
}

const ModalComponent: React.FC<ModalComponentProps> = ({deviceId, isOpen, onClose}) => {
    const [device, setDevice] = React.useState<Device | null>(null);
    const [data, setData] = React.useState<{ x: number, y: number }[]>([]);
    const timestamp = useRef(0);

    useEffect(() => {
        if (deviceId !== "") {
            const token = localStorage.getItem('token');
            axiosDevice.get(
                `/devices/${deviceId}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }).then(response => {
                setDevice(response.data);
            });
        }
    }, [deviceId]);

    useEffect(() => {
        timestamp.current = 0;
        setData([]);
    }, [isOpen]);

    const handleDateChange = (date: Date | null) => {
        timestamp.current = date ? date.getTime() : 0;
        if (timestamp.current > 0) {
            const token = localStorage.getItem('token');
            axiosMonitoring.get(
                `/monitoring/${deviceId}/${timestamp.current}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            ).then(response => {
                if (response.status !== 200 || !response.data || response.data.length === 0) {
                    setData([]);
                    return;
                }
                const rawData = response.data.map((entry: { measurementValue: number; timestamp: number }) => ({
                    x: entry.timestamp,
                    y: entry.measurementValue,
                })).sort((a: { x: number; }, b: { x: number; }) => a.x - b.x);
                console.log(rawData);
                const reducedData: { x: number, y: number }[] = [];
                for (let i = 0; i < rawData.length; i += 6) {
                    const chunk = rawData.slice(i, i + 6);
                    const x = chunk[0].x;
                    const y = chunk.reduce((sum: number, entry: { x: number, y: number }) => sum + entry.y, 0);
                    reducedData.push({x, y});
                }
                setData(reducedData);
            });
        }
    }

    return (
        <Modal show={isOpen} onHide={onClose} size={'xl'}>
            <Modal.Header closeButton>
                <Modal.Title>Usage</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <LocalizationProvider dateAdapter={AdapterDateFns}>
                    <StaticDatePicker
                        displayStaticWrapperAs="desktop"
                        onChange={handleDateChange}
                    />
                </LocalizationProvider>
                <LineChart
                    width={1100}
                    height={350}
                    grid={{horizontal: true, vertical: true}}
                    xAxis={[{
                        data: data.map(entry => entry.x),
                        valueFormatter: (value: number) => new Date(value).toLocaleTimeString()
                    }]}
                    series={[{
                        type: 'line',
                        data: data.map(entry => entry.y),
                        disableHighlight: true
                    }, {
                        type: 'line',
                        color: 'red',
                        data: device ? data.map(_ => device.maxHourlyConsumption) : [],
                        disableHighlight: true
                    }]}
                />
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onClose}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ModalComponent;