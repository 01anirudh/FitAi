import React, { useState } from 'react';
import { CONFIG } from '../config';

export default function RegisterPage({ onBack, onSuccess }) {
  const [form, setForm] = useState({
    username: '', email: '', firstName: '', lastName: '', password: '', confirm: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');
  const [success, setSuccess] = useState(false);

  const set = field => e => setForm(f => ({ ...f, [field]: e.target.value }));

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    if (form.password !== form.confirm) {
      setError('Passwords do not match');
      return;
    }
    if (form.password.length < 8) {
      setError('Password must be at least 8 characters');
      return;
    }
    setLoading(true);
    try {
      const res = await fetch(`${CONFIG.gatewayUrl}/api/users/keycloak-register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username:  form.username,
          email:     form.email,
          firstName: form.firstName,
          lastName:  form.lastName,
          password:  form.password,
        }),
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Registration failed. Please try again.');
      setSuccess(true);
      setTimeout(() => onSuccess(), 2000);
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
          <div className="logo-icon">⚡</div>
          <span className="logo-text">FitAI</span>
        </div>
        <h1 className="auth-title">Create account</h1>
        <p className="auth-sub">Start tracking your fitness journey today</p>

        {error   && <div className="alert error">{error}</div>}
        {success && <div className="alert success">Account created! Taking you to login…</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">First Name</label>
              <input id="reg-firstname" className="form-input" type="text" placeholder="First name"
                value={form.firstName} onChange={set('firstName')} required />
            </div>
            <div className="form-group">
              <label className="form-label">Last Name</label>
              <input id="reg-lastname" className="form-input" type="text" placeholder="Last name"
                value={form.lastName} onChange={set('lastName')} required />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input id="reg-username" className="form-input" type="text" placeholder="Choose a username"
              value={form.username} onChange={set('username')} required />
          </div>
          <div className="form-group">
            <label className="form-label">Email</label>
            <input id="reg-email" className="form-input" type="email" placeholder="your@email.com"
              value={form.email} onChange={set('email')} required />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input id="reg-password" className="form-input" type="password" placeholder="Min. 8 characters"
              value={form.password} onChange={set('password')} required />
          </div>
          <div className="form-group">
            <label className="form-label">Confirm Password</label>
            <input id="reg-confirm" className="form-input" type="password" placeholder="Repeat your password"
              value={form.confirm} onChange={set('confirm')} required />
          </div>
          <button id="reg-submit" type="submit"
            className={`btn btn-primary${loading ? ' loading' : ''}`}
            disabled={loading || success}>
            {loading ? 'Creating account…' : 'Create Account'}
          </button>
        </form>

        <div className="auth-switch">
          Already have an account?{' '}
          <button className="link-btn" onClick={onBack}>Sign in</button>
        </div>
      </div>
    </div>
  );
}
