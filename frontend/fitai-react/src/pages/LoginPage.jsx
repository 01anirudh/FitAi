import React, { useState } from 'react';
import { keycloakLogin } from '../api';

export default function LoginPage({ onLogin, onGoRegister }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError]       = useState('');
  const [loading, setLoading]   = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const data = await keycloakLogin(username, password);
      onLogin(data.access_token, username.split('@')[0]);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="logo">
          <div className="logo-icon">🏋️</div>
          <span className="logo-text">FitAI</span>
        </div>
        <h1 className="auth-title">Welcome back</h1>
        <p className="auth-sub">Sign in to your fitness dashboard</p>

        {error && <div className="alert error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Username or Email</label>
            <input
              type="text" className="form-input"
              placeholder="testuser"
              value={username} onChange={e => setUsername(e.target.value)} required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password" className="form-input"
              placeholder="••••••••"
              value={password} onChange={e => setPassword(e.target.value)} required
            />
          </div>
          <button type="submit" className={`btn btn-primary${loading ? ' loading' : ''}`} disabled={loading}>
            {loading ? 'Signing in…' : 'Sign In'}
          </button>
        </form>

        <p className="auth-switch">
          Don&apos;t have an account?{' '}
          <button className="link-btn" onClick={onGoRegister}>Create one</button>
        </p>
      </div>
    </div>
  );
}
