import React, { useState } from 'react';
import { Table as BootstrapTable, Button } from 'react-bootstrap';

type Action = {
    text: string;
    variant: string; // Bootstrap variant (color)
    fn: (row: { [key: string]: string }) => void;
};

type TableProps = {
    cols: number;
    colsNames: string[];
    data: { [key: string]: string }[];
    actions?: Action[]; // Array of actions for each row
};

function formatColumnName(colName: string): string {
    return colName
        .toLowerCase()
        .split(' ')
        .map((word, index) =>
            index === 0 ? word : word.charAt(0).toUpperCase() + word.slice(1)
        )
        .join('');
}

const TableComponent: React.FC<TableProps> = ({ cols, colsNames, data, actions = [] }) => {
    const [sortColumn, setSortColumn] = useState<string>(formatColumnName(colsNames[0]));
    const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');

    const sortedData = [...data].sort((a, b) => {
        const valA = a[sortColumn] || '';
        const valB = b[sortColumn] || '';
        if (valA < valB) return sortOrder === 'asc' ? -1 : 1;
        if (valA > valB) return sortOrder === 'asc' ? 1 : -1;
        return 0;
    });

    const handleSort = (colName: string) => {
        const formattedColName = formatColumnName(colName);
        if (formattedColName === sortColumn) {
            setSortOrder((prevOrder) => (prevOrder === 'asc' ? 'desc' : 'asc'));
        } else {
            setSortColumn(formattedColName);
            setSortOrder('asc');
        }
    };

    return (
        <BootstrapTable striped bordered hover responsive className="mt-4">
            <thead>
            <tr>
                {colsNames.slice(0, cols).map((colName, index) => (
                    <th
                        key={index}
                        onClick={() => handleSort(colName)}
                        style={{ cursor: 'pointer', userSelect: 'none' }} // Disable text selection
                    >
                        {colName}
                        {sortColumn === formatColumnName(colName) && (
                            <span>{sortOrder === 'asc' ? ' ▲' : ' ▼'}</span>
                        )}
                    </th>
                ))}
                {actions.length > 0 && (
                    <th style={{ width: '1%', whiteSpace: 'nowrap' }}>Actions</th>
                )}
            </tr>
            </thead>
            <tbody>
            {sortedData.length > 0 ? (
                sortedData.map((row, rowIndex) => (
                    <tr key={rowIndex}>
                        {colsNames.slice(0, cols).map((colName, colIndex) => (
                            <td key={colIndex}>{row[formatColumnName(colName)]}</td>
                        ))}
                        {actions.length > 0 && (
                            <td style={{ width: '1%', whiteSpace: 'nowrap' }}>
                                {actions.map((action, actionIndex) => (
                                    <Button
                                        key={actionIndex}
                                        variant={action.variant}
                                        className="me-2"
                                        onClick={() => action.fn(row)}
                                    >
                                        {action.text}
                                    </Button>
                                ))}
                            </td>
                        )}
                    </tr>
                ))
            ) : (
                <tr>
                    <td colSpan={cols + (actions.length > 0 ? 1 : 0)} className="text-center">
                        No data available
                    </td>
                </tr>
            )}
            </tbody>
        </BootstrapTable>
    );
};

export default TableComponent;
