import React, { useEffect, useState, useCallback } from 'react';
import { logActivity, getActivities } from '../api';
import LogActivityForm from '../components/LogActivityForm';
import ActivityFeed from '../components/ActivityFeed';
import StatsPills from '../components/StatsPills';

export default function DashboardPage({ token, username, onLogout }) {
  const [activities, setActivities] = useState([]);
  const [feedLoading, setFeedLoading] = useState(true);
  const [feedError, setFeedError]     = useState('');

  const fetchActivities = useCallback(async () => {
    setFeedLoading(true);
    setFeedError('');
    try {
      const data = await getActivities(token);
      data.sort((a, b) => new Date(b.startTime || b.createdAt) - new Date(a.startTime || a.createdAt));
      setActivities(data);
    } catch (err) {
      setFeedError(err.message);
    } finally {
      setFeedLoading(false);
    }
  }, [token]);

  useEffect(() => { fetchActivities(); }, [fetchActivities]);

  async function handleLog(payload) {
    await logActivity(token, payload);
    fetchActivities();
  }

  return (
    <div className="dashboard">
      {/* Navbar */}
      <nav className="navbar">
        <div className="nav-logo">
          <div className="logo-icon" style={{ width: 34, height: 34, fontSize: 17 }}>🏋️</div>
          <span className="logo-text">FitAI</span>
        </div>
        <div className="nav-user">
          <span className="nav-greeting">Hello, <strong>{username}</strong> 👋</span>
          <button className="btn-logout" onClick={onLogout}>Sign Out</button>
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
