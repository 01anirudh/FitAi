import React, { useEffect, useState, useCallback } from 'react';
import { syncUser, logActivity, getActivities } from '../api';
import LogActivityForm from '../components/LogActivityForm';
import ActivityFeed from '../components/ActivityFeed';
import StatsPills from '../components/StatsPills';

export default function DashboardPage({ keycloak }) {
  const [activities, setActivities] = useState([]);
  const [feedLoading, setFeedLoading] = useState(true);
  const [feedError, setFeedError]     = useState('');

  const fetchActivities = useCallback(async () => {
    if (!keycloak.token) return;
    setFeedLoading(true);
    setFeedError('');
    try {
      await syncUser(keycloak.token); // Synchronize Keycloak user to local Neon DB automatically
      const data = await getActivities(keycloak.token);
      data.sort((a, b) => new Date(b.startTime || b.createdAt) - new Date(a.startTime || a.createdAt));
      setActivities(data);
    } catch (err) {
      setFeedError(err.message);
    } finally {
      setFeedLoading(false);
    }
  }, [keycloak.token]);

  useEffect(() => { fetchActivities(); }, [fetchActivities]);

  async function handleLog(payload) {
    if (keycloak.isTokenExpired()) {
      await keycloak.updateToken(30);
    }
    await logActivity(keycloak.token, payload);
    fetchActivities();
  }

  return (
    <div className="dashboard">
      <nav className="navbar">
        <div className="nav-logo">
          <img src="/favicon.svg" alt="FitAI Logo" style={{ width: 34, height: 34, borderRadius: 8 }} />
          <span className="logo-text">FitAI</span>
        </div>
        <div className="nav-user">
          <span className="nav-greeting">Hello, <strong>{keycloak.tokenParsed?.preferred_username || 'Athlete'}</strong> 👋</span>
          <button className="btn-logout" onClick={() => keycloak.logout()}>Sign Out</button>
        </div>
      </nav>

      <div className="dashboard-body">
        <StatsPills activities={activities} />

        <div className="dashboard-grid">
          <LogActivityForm onSubmit={handleLog} />
          <ActivityFeed
            activities={activities}
            loading={feedLoading}
            error={feedError}
            onRefresh={fetchActivities}
          />
        </div>
      </div>
    </div>
  );
}
