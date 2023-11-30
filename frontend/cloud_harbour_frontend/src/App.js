import React, { useState, useEffect } from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import AppService from "./services/AppService";
import AppForm from "./components/AppForm";
import AppList from "./components/AppList";
import AppDetails from "./components/AppDetails";
const App = () => {
  const [apps, setApps] = useState([]);
  const [selectedApp, setSelectedApp] = useState(null);

  useEffect(() => {
    const fetchApps = async () => {
      const allApps = await AppService.getAllApps();
      setApps(allApps);
    };

    fetchApps();
  }, []);

  const addApp = async (newApp) => {
    const addedApp = await AppService.addApp(newApp);
    setApps([...apps, addedApp]);
  };

  const deleteApp = async (appId) => {
    await AppService.deleteApp(appId);
    setApps(apps.filter((app) => app.id !== appId));
    setSelectedApp(null);
  };

  const viewDetails = (app) => {
    setSelectedApp(app);
  };

  return (
      <Container>
        <Row>
          <Col md={12}>
            <h2>Create App</h2>
            <AppForm addApp={addApp} />
          </Col>
        </Row>
        <Row>
          <Col md={12}>
            <h2>App List</h2>
            <AppList apps={apps} viewDetails={viewDetails} />
          </Col>
        </Row>
        <Row>
          <Col md={12}>
            {selectedApp ? (
                <AppDetails app={selectedApp} deleteApp={deleteApp} />
            ) : (
                <p>Select an app to view details</p>
            )}
          </Col>
        </Row>
      </Container>
  );
};

export default App;