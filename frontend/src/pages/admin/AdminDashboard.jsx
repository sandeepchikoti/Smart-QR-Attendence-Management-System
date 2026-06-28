import React from 'react';
import { Shield, BookOpen, Settings, Users, Activity } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const AdminDashboard = () => {
  const { user } = useAuth();

  return (
    <div className="fade-in-up">
      <div className="glass-card p-4 mb-4">
        <h1 className="fw-bold tracking-tight mb-1 fs-3">Institutional Administration</h1>
        <p className="text-secondary mb-0">System configuration, logs auditing, and database master settings.</p>
      </div>

      <div className="row g-4 mb-4">
        <div className="col-12 col-md-3">
          <div className="glass-card p-4 text-center">
            <Users size={32} className="text-primary mb-2" />
            <h6 className="text-secondary fs-7 fw-bold">TOTAL USERS</h6>
            <div className="fs-3 fw-bold text-white">3 Active</div>
          </div>
        </div>

        <div className="col-12 col-md-3">
          <div className="glass-card p-4 text-center">
            <BookOpen size={32} className="text-accent mb-2" style={{ color: 'var(--accent-color)' }} />
            <h6 className="text-secondary fs-7 fw-bold">DEPARTMENTS</h6>
            <div className="fs-3 fw-bold text-white">0 Active</div>
          </div>
        </div>

        <div className="col-12 col-md-6">
          <div className="glass-card p-4 h-100 d-flex align-items-center justify-content-between">
            <div>
              <h6 className="text-secondary fs-7 fw-bold mb-1">SYSTEM INSTANCE</h6>
              <div className="text-white fw-bold">Spring Boot 3.3.1 + React + MySQL</div>
            </div>
            <Activity size={32} className="text-success" />
          </div>
        </div>
      </div>

      {/* Audits Panel */}
      <div className="glass-card p-4">
        <h5 className="fw-bold tracking-tight mb-3 d-flex align-items-center gap-2">
          <Shield size={20} className="text-primary" /> Active System Audit Logs
        </h5>
        <div className="text-center py-5 text-secondary border-dashed border-secondary rounded-3">
          Audit Logging pipeline active. Trigger operations (login, session starts, check-ins) to view records.
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
