import React, { useState } from 'react';

const ACTIVITY_TYPES = [
  { value: 'RUNNING',       label: '🏃 Running' },
  { value: 'CYCLING',       label: '🚴 Cycling' },
  { value: 'SWIMMING',      label: '🏊 Swimming' },
  { value: 'WEIGHTLIFTING', label: '🏋️ Weight Lifting' },
  { value: 'YOGA',          label: '🧘 Yoga' },
  { value: 'WALKING',       label: '🚶 Walking' },
  { value: 'OTHER',         label: '⚡ Other' },
];

function nowLocalISO() {
  const d = new Date();
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  return d.toISOString().slice(0, 16);
}

export default function LogActivityForm({ onSubmit }) {
  const [type, setType]         = useState('');
  const [duration, setDuration] = useState('');

  const [startTime, setStart]   = useState(nowLocalISO);
  const [loading, setLoading]   = useState(false);
  const [msg, setMsg]           = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setMsg(null);
    setLoading(true);
    try {
      await onSubmit({
        type,
        duration: parseInt(duration),
        caloriesBurned: 0,
        startTime: startTime + ':00',
      });
      setMsg({ text: '✅ Activity logged!', kind: 'success' });
      setType(''); setDuration(''); setStart(nowLocalISO());
      setTimeout(() => setMsg(null), 3000);
    } catch (err) {
      setMsg({ text: '❌ ' + err.message, kind: 'error' });
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card">
      <div className="card-title"><span className="icon">➕</span> Log Activity</div>
      <p className="card-sub">Record your workout session</p>

      {msg && <div className={`alert ${msg.kind}`}>{msg.text}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">Activity Type</label>
          <select className="form-input" value={type} onChange={e => setType(e.target.value)} required>
            <option value="">Select type…</option>
            {ACTIVITY_TYPES.map(t => (
              <option key={t.value} value={t.value}>{t.label}</option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">Duration (min)</label>
          <input
            type="number" className="form-input" placeholder="30" min="1"
            value={duration} onChange={e => setDuration(e.target.value)} required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Start Time</label>
          <input
            type="datetime-local" className="form-input"
            value={startTime} onChange={e => setStart(e.target.value)} required
          />
        </div>

        <button type="submit" className={`btn btn-primary${loading ? ' loading' : ''}`} disabled={loading}>
          {loading ? 'Logging…' : 'Log Activity'}
        </button>
      </form>
    </div>
  );
}
