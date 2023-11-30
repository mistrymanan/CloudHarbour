import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';

const AppForm = ({ addApp }) => {
    const [appName, setAppName] = useState('');
    const [dockerImage, setDockerimage] = useState('');
    const [internalPort, setInternalPort] = useState(80);
    const [externalPort, setExternalPort] = useState(80);

    const handleSubmit = (e) => {
        e.preventDefault();
        const newApp = {
            appName,
            dockerImage,
            internalPort,
            externalPort
        };
        addApp(newApp);
        setAppName('');
    };

    return (
        <Form onSubmit={handleSubmit}>
            <Form.Group controlId="appName">
                <Form.Label>App Name</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Enter app name"
                    value={appName}
                    onChange={(e) => setAppName(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group controlId="appDockerImage">
                <Form.Label>Docker Image</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Enter Dockerimage"
                    value={dockerImage}
                    onChange={(e) => setDockerimage(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group controlId="appInternalPort">
                <Form.Label>Internal Port</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Enter Dockerimage"
                    value={internalPort}
                    onChange={(e) => setInternalPort(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group controlId="appInternalPort">
                <Form.Label>External Port</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Enter Dockerimage"
                    value={externalPort}
                    onChange={(e) => setExternalPort(e.target.value)}
                    required
                />
            </Form.Group>
            <Button variant="primary" type="submit" >
                Create App
            </Button>
        </Form>
    );
};

export default AppForm;