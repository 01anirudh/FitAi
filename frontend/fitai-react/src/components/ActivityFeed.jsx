import React from 'react';

const ICONS = {
  RUNNING:'🏃', CYCLING:'🚴', SWIMMING:'🏊', WEIGHTLIFTING:'🏋️',
  YOGA:'🧘', WALKING:'🚶', OTHER:'⚡',
};
const NAMES = {
  RUNNING:'Running', CYCLING:'Cycling', SWIMMING:'Swimming', WEIGHTLIFTING:'Weight Lifting',
  YOGA:'Yoga', WALKING:'Walking', OTHER:'Other',
};

function formatDate(dt) {
  if (!dt) return '—';
  return new Intl.DateTimeFormat('en-US', {
    month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit', hour12: true,
  }).format(new Date(dt));
}

function ActivityCard({ activity, index }) {
  const type = (activity.type || 'OTHER').toUpperCase();
  return (
    <div className={`activity-card type-${type.toLowerCase()}`} style={{ animationDelay: `${index * 0.05}s` }}>
      <div className="activity-icon">{ICONS[type] || '⚡'}</div>
      <div className="activity-info">
        <div className="activity-type">{NAMES[type] || type}</div>
        <div className="activity-meta">{formatDate(activity.startTime || activity.createdAt)}</div>
      </div>
      <div className="activity-stats">
        <div className="activity-cal">{activity.caloriesBurned ?? '—'}</div>
        <div className="activity-cal-lbl">kcal</div>
        <div className="activity-dur">{activity.duration ?? '—'} min</div>
      </div>
    </div>
  );
}

export default function ActivityFeed({ activities, loading, error, onRefresh }) {
  return (
    <div>
      <div className="feed-title">
        <span>My Activities</span>
        <button className="refresh-btn" onClick={onRefresh}>↻ Refresh</button>
      </div>

      <div className="activity-list">
        {loading && <div className="spinner" />}

        {!loading && error && (
          <div className="empty-state">
            <div className="empty-icon">⚠️</div>
            <p>{error}</p>
          </div>
        )}

        {!loading && !error && activities.length === 0 && (
          <div className="empty-state">
            <div className="empty-icon">🏃</div>
            <p>No activities yet — log your first workout!</p>
          </div>
        )}

        {!loading && !error && activities.map((a, i) => (
          <ActivityCard key={a.id || i} activity={a} index={i} />
        ))}
      </div>
    </div>
  );
}
