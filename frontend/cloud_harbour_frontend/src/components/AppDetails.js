import React from 'react';
import { Button,Card } from 'react-bootstrap';

const AppDetails = ({ app, deleteApp }) => {
    return (
        <Card style={{ width: '18rem' }} >
            <Card.Body>
                <Card.Title>{app.appName} Details</Card.Title>
                <Card.Subtitle className="mb-2 text-muted">
                    <strong>App ID:</strong> {app.id}
                    <strong>Name:</strong> {app.appName}
                    <strong>Docker Image:</strong> {app.dockerImage}
                    <strong>URL:</strong> {app.url}
                    <strong>Status:</strong> {app.status}
                    <strong>Internal Port:</strong> {app.internalPort}
                    <strong>External Port:</strong> {app.externalPort}
                </Card.Subtitle>
                <Button variant="danger" onClick={() => deleteApp(app.id)}>
                    Delete
                </Button>
            </Card.Body>
        </Card>
    );
};

export default AppDetails;
