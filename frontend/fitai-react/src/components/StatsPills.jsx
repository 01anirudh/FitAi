import React from 'react';

export default function StatsPills({ activities }) {
  const total    = activities.length;
  const calories = activities.reduce((s, a) => s + (a.caloriesBurned || 0), 0);
  const minutes  = activities.reduce((s, a) => s + (a.duration || 0), 0);

  return (
    <div className="stats-row">
      <div className="stat-pill">
        <div className="stat-num">{total}</div>
        <div className="stat-lbl">Activities</div>
      </div>
      <div className="stat-pill">
        <div className="stat-num">{calories.toLocaleString()}</div>
        <div className="stat-lbl">Total Kcal</div>
      </div>
      <div className="stat-pill">
        <div className="stat-num">{minutes.toLocaleString()}</div>
        <div className="stat-lbl">Minutes</div>
      </div>
    </div>
  );
}
