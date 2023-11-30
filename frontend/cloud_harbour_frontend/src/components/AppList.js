import React from 'react';
import {ListGroup, Table} from 'react-bootstrap';

const AppList = ({ apps, viewDetails }) => {
    return (
        <Table striped bordered hover>
            <thead>
            <tr>
                <th>App Name</th>
                <td>Image</td>
                <td>URL</td>
                <td>Status</td>
                <td>Internal Port</td>
                <td>External Port</td>
            </tr>
            </thead>
            <tbody>
            {apps.map((app) => (
                <tr key={app.id} onClick={() => viewDetails(app)}>
                    <td>{app.appName}</td>
                    <td>{app.dockerImage}</td>
                    <td>{app.url}</td>
                    <td>{app.status}</td>
                    <td>{app.internalPort}</td>
                    <td>{app.externalPort}</td>
                </tr>
            ))}
            </tbody>
        </Table>
    );
};

export default AppList;