import React, { useState, useRef, useCallback } from 'react';
import Keycloak from 'keycloak-js';
import { CONFIG } from './config';
import LoginPage    from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';

export default function App() {
  const [view, setView]       = useState('login'); // 'login' | 'register' | 'dashboard'
  const [keycloak, setKeycloak] = useState(null);
  const kcRef = useRef(null);

  // Called from LoginPage after successful Direct Access Grants token fetch
  const handleLogin = useCallback(async (accessToken, refreshToken, idToken) => {
    const kc = new Keycloak({
      url:      CONFIG.keycloakUrl,
      realm:    CONFIG.keycloakRealm,
      clientId: CONFIG.keycloakClientId,
    });
    // Initialize with existing tokens — no redirect, no login-required
    await kc.init({
      token:        accessToken,
      refreshToken: refreshToken,
      idToken:      idToken,
      checkLoginIframe: false,
    });
    kcRef.current = kc;
    setKeycloak(kc);
    setView('dashboard');
  }, []);

  // Called from DashboardPage logout button
  const handleLogout = useCallback(async () => {
    const kc = kcRef.current;
    if (kc?.refreshToken) {
      try {
        await fetch(
          `${CONFIG.keycloakUrl}/realms/${CONFIG.keycloakRealm}/protocol/openid-connect/logout`,
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({
              client_id:     CONFIG.keycloakClientId,
              refresh_token: kc.refreshToken,
            }),
          }
        );
      } catch (_) { /* best-effort revoke */ }
    }
    kcRef.current = null;
    setKeycloak(null);
    setView('login');
  }, []);

  if (view === 'register') {
    return (
      <RegisterPage
        onBack={() => setView('login')}
        onSuccess={() => setView('login')}
      />
    );
  }

  if (view === 'login' || !keycloak) {
    return (
      <LoginPage
        onLogin={handleLogin}
        onRegister={() => setView('register')}
      />
    );
  }

  return <DashboardPage keycloak={keycloak} onLogout={handleLogout} />;
}
