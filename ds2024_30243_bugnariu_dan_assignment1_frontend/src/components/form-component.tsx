import React, { useState, useEffect } from 'react';
import { Form, Button } from 'react-bootstrap';

export type FieldType = 'text' | 'number' | 'dropdown';

interface Field {
    name: string;
    type: FieldType;
    options?: string[]; // Options for dropdown fields
}

interface FormComponentProps {
    fields: Field[];
    onSubmit: (data: { [key: string]: any }) => void;
    initialValues?: { [key: string]: any }; // Optional initial values
}

const FormComponent: React.FC<FormComponentProps> = ({ fields, onSubmit, initialValues = {} }) => {
    const [formData, setFormData] = useState<{ [key: string]: any }>({});
    const [errors, setErrors] = useState<{ [key: string]: string }>({}); // Track field errors

    // Set initial form values if provided
    useEffect(() => {
        setFormData(initialValues);
    }, [initialValues]);

    const handleChange = (name: string, value: any) => {
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const validateForm = () => {
        const newErrors: { [key: string]: string } = {};
        fields.forEach((field) => {
            if (!formData[field.name] || formData[field.name].toString().trim() === '') {
                newErrors[field.name] = `${field.name} cannot be empty`;
            }
        });
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (validateForm()) {
            onSubmit(formData);
        }
    };

    return (
        <Form onSubmit={handleSubmit}>
            {fields.map((field, index) => (
                <Form.Group controlId={`form${field.name}`} className="mb-3" key={index}>
                    <Form.Label>{field.name}</Form.Label>
                    {field.type === 'text' && (
                        <Form.Control
                            type="text"
                            value={formData[field.name] || ''}
                            onChange={(e) => handleChange(field.name, e.target.value)}
                            isInvalid={!!errors[field.name]}
                        />
                    )}
                    {field.type === 'number' && (
                        <Form.Control
                            type="number"
                            value={formData[field.name] || ''}
                            onChange={(e) => handleChange(field.name, parseFloat(e.target.value))}
                            isInvalid={!!errors[field.name]}
                        />
                    )}
                    {field.type === 'dropdown' && field.options && (
                        <Form.Select
                            value={formData[field.name] || ''}
                            onChange={(e) => handleChange(field.name, e.target.value)}
                            isInvalid={!!errors[field.name]}
                        >
                            <option value="" disabled>Select an option</option>
                            {field.options.map((option, i) => (
                                <option key={i} value={option}>
                                    {option}
                                </option>
                            ))}
                        </Form.Select>
                    )}
                    <Form.Control.Feedback type="invalid">
                        {errors[field.name]}
                    </Form.Control.Feedback>
                </Form.Group>
            ))}
            <Button variant="primary" type="submit">
                Submit
            </Button>
        </Form>
    );
};

export default FormComponent;
