import React, { useState, useEffect, useRef } from 'react';
import Keycloak from 'keycloak-js';
import { CONFIG } from './config';
import DashboardPage from './pages/DashboardPage';

export default function App() {
  const [keycloak, setKeycloak] = useState(null);
  const [authenticated, setAuthenticated] = useState(false);
  const isRun = useRef(false);

  useEffect(() => {
    if (isRun.current) return;
    isRun.current = true;

    const kc = new Keycloak({
      url: CONFIG.keycloakUrl,
      realm: CONFIG.keycloakRealm,
      clientId: CONFIG.keycloakClientId,
    });

    kc.init({ onLoad: 'login-required', checkLoginIframe: false })
      .then((auth) => {
        setKeycloak(kc);
        setAuthenticated(auth);
      })
      .catch(() => console.error("Keycloak initialization failed!"));
  }, []);

  if (!keycloak) {
    return <div className="loading-screen" style={{color: 'white', textAlign: 'center', marginTop: '20vh'}}><h2>Loading Secure FitAI Login...</h2></div>;
  }

  if (authenticated) {
    return <DashboardPage keycloak={keycloak} />;
  }

  return <div className="loading-screen" style={{color: 'white', textAlign: 'center', marginTop: '20vh'}}><h2>Unable to authenticate!</h2></div>;
}
