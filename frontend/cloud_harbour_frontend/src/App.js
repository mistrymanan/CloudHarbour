import React, { useState, useEffect } from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import AppService from "./services/AppService";
import AppForm from "./components/AppForm";
import AppList from "./components/AppList";
import AppDetails from "./components/AppDetails";

import {Router} from 'react-router-dom';
import axios from "axios";

const App = () => {

  // const [IP,setIP] = useState();

  const [apps, setApps] = useState([]);
  const [selectedApp, setSelectedApp] = useState(null);
  // console.log(location.pathname);
  const getIp = async () => {
    const res = await axios.get("https://api.ipify.org/?format=json");
    return res.data.ip
  };
  useEffect(() => {
    // getIp();
    const fetchApps = async () => {
      const ip = await getIp();
      const allApps = await AppService.getAllApps(ip);
      setApps(allApps);
    };

    fetchApps();
  }, []);

  const addApp = async (newApp) => {
    const ip = await getIp();
    const addedApp = await AppService.addApp(ip,newApp);
    setApps([...apps, addedApp]);
  };

  const deleteApp = async (appId) => {
    const ip = await getIp();
    await AppService.deleteApp(ip,appId);
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