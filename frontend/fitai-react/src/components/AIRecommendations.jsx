import React, { useState } from 'react';
import { getRecommendationForActivity } from '../api';

export default function AIRecommendations({ activities, token }) {
  const [expanded, setExpanded] = useState(null);       // activityId
  const [recs, setRecs]         = useState({});         // { [activityId]: recommendation | null }
  const [loading, setLoading]   = useState({});         // { [activityId]: bool }

  async function loadRec(activityId) {
    if (recs[activityId] !== undefined) {
      // Toggle: collapse if already open
      setExpanded(prev => prev === activityId ? null : activityId);
      return;
    }
    setLoading(l => ({ ...l, [activityId]: true }));
    setExpanded(activityId);
    try {
      const data = await getRecommendationForActivity(token, activityId);
      setRecs(r => ({ ...r, [activityId]: data }));
    } catch {
      setRecs(r => ({ ...r, [activityId]: null }));
    } finally {
      setLoading(l => ({ ...l, [activityId]: false }));
    }
  }

  if (!activities || activities.length === 0) return null;

  return (
    <div className="card ai-panel">
      <div className="card-title"><span className="icon">🤖</span> AI Insights</div>
      <p className="card-sub">Gemini-powered recommendations for your workouts</p>

      <div className="ai-list">
        {activities.slice(0, 5).map(a => {
          const id   = a.id;
          const type = (a.type || 'OTHER').toUpperCase();
          const isOpen = expanded === id;
          const rec    = recs[id];
          const busy   = loading[id];

          return (
            <div key={id} className="ai-item">
              <button className="ai-header" onClick={() => loadRec(id)}>
                <span className="ai-activity-name">{type.replace('WEIGHTLIFTING', 'WEIGHT LIFTING')} — {a.duration ?? '?'} min</span>
                <span className={`ai-chevron ${isOpen ? 'open' : ''}`}>›</span>
              </button>

              {isOpen && (
                <div className="ai-body">
                  {busy && <div className="spinner" style={{ margin: '12px auto' }} />}

                  {!busy && rec === null && (
                    <p className="ai-pending">⏳ AI is still processing this workout. Check back in a moment.</p>
                  )}

                  {!busy && rec && (
                    <>
                      <p className="ai-summary">{rec.recommendation}</p>

                      {rec.improvements?.length > 0 && (
                        <div className="ai-section">
                          <div className="ai-section-title">💪 Improvements</div>
                          <ul>{rec.improvements.map((t, i) => <li key={i}>{t}</li>)}</ul>
                        </div>
                      )}
                      {rec.suggestions?.length > 0 && (
                        <div className="ai-section">
                          <div className="ai-section-title">🎯 Next Session</div>
                          <ul>{rec.suggestions.map((t, i) => <li key={i}>{t}</li>)}</ul>
                        </div>
                      )}
                      {rec.safety?.length > 0 && (
                        <div className="ai-section">
                          <div className="ai-section-title">🛡️ Safety Tips</div>
                          <ul>{rec.safety.map((t, i) => <li key={i}>{t}</li>)}</ul>
                        </div>
                      )}
                    </>
                  )}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
