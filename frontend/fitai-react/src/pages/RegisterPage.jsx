import React, { useState } from 'react';
import { registerUser } from '../api';

export default function RegisterPage({ onGoLogin }) {
  const [name, setName]         = useState('');
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [msg, setMsg]           = useState(null); // { text, type }
  const [loading, setLoading]   = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setMsg(null);
    setLoading(true);
    try {
      await registerUser(name, email, password);
      setMsg({ text: '✅ Account created! Redirecting to login…', type: 'success' });
      setTimeout(onGoLogin, 1800);
    } catch (err) {
      setMsg({ text: err.message, type: 'error' });
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
        <h1 className="auth-title">Create account</h1>
        <p className="auth-sub">Start your fitness journey today</p>

        {msg && <div className={`alert ${msg.type}`}>{msg.text}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input
              type="text" className="form-input"
              placeholder="Jane Smith"
              value={name} onChange={e => setName(e.target.value)} required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              type="email" className="form-input"
              placeholder="you@example.com"
              value={email} onChange={e => setEmail(e.target.value)} required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password" className="form-input"
              placeholder="Min. 8 characters"
              value={password} onChange={e => setPassword(e.target.value)}
              required minLength={8}
            />
          </div>
          <button type="submit" className={`btn btn-primary${loading ? ' loading' : ''}`} disabled={loading}>
            {loading ? 'Creating…' : 'Create Account'}
          </button>
          <button type="button" className="btn btn-outline" onClick={onGoLogin} style={{ marginTop: 10 }}>
            Back to Login
          </button>
        </form>

        <p className="auth-switch">
          Already have an account?{' '}
          <button className="link-btn" onClick={onGoLogin}>Sign in</button>
        </p>
      </div>
    </div>
  );
}
