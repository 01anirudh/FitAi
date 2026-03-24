import React, { useState } from 'react';
import LoginPage    from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';

export default function App() {
  const [page, setPage]         = useState('login'); // 'login' | 'register' | 'dashboard'
  const [token, setToken]       = useState(null);
  const [username, setUsername] = useState('');

  function handleLogin(accessToken, uname) {
    setToken(accessToken);
    setUsername(uname);
    setPage('dashboard');
  }

  function handleLogout() {
    setToken(null);
    setUsername('');
    setPage('login');
  }

  if (page === 'dashboard' && token) {
    return <DashboardPage token={token} username={username} onLogout={handleLogout} />;
  }
  if (page === 'register') {
    return <RegisterPage onGoLogin={() => setPage('login')} />;
  }
  return <LoginPage onLogin={handleLogin} onGoRegister={() => setPage('register')} />;
}
