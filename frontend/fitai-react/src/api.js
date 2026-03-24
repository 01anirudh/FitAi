import { CONFIG } from './config';

export async function keycloakLogin(username, password) {
  const body = new URLSearchParams({
    grant_type: 'password',
    client_id: CONFIG.keycloakClientId,
    username,
    password,
  });

  const res = await fetch(
    `${CONFIG.keycloakUrl}/realms/${CONFIG.keycloakRealm}/protocol/openid-connect/token`,
    { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body }
  );

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error_description || 'Invalid credentials.');
  }

  return res.json(); // { access_token, ... }
}

export async function registerUser(name, email, password) {
  const res = await fetch(`${CONFIG.gatewayUrl}/api/users/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, password }),
  });

  if (!res.ok) {
    const txt = await res.text();
    throw new Error(txt || 'Registration failed.');
  }

  return res.json().catch(() => ({}));
}

export async function logActivity(token, payload) {
  const res = await fetch(`${CONFIG.gatewayUrl}/api/activities`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!res.ok) throw new Error('Failed to log activity.');
  return res.json();
}

export async function getActivities(token) {
  const res = await fetch(`${CONFIG.gatewayUrl}/api/activities`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error('Could not fetch activities.');
  return res.json();
}
