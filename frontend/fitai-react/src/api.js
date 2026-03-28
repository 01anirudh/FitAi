import { CONFIG } from './config';

export async function syncUser(token) {
  const res = await fetch(`${CONFIG.gatewayUrl}/api/users/sync`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` }
  });
  if (!res.ok) throw new Error('Failed to sync user');
  return res.json();
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
