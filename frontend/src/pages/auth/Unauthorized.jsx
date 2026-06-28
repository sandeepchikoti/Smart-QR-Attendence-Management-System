import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldAlert } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const Unauthorized = () => {
  const { user } = useAuth();

  const getDashboardPath = () => {
    if (!user) return '/login';
    switch (user.role) {
      case 'ADMIN': return '/admin';
      case 'FACULTY': return '/faculty';
      case 'STUDENT': return '/student';
      default: return '/login';
    }
  };

  return (
    <div className="d-flex align-items-center justify-content-center min-vh-100 bg-dark text-white">
      <div className="text-center glass-card p-5" style={{ maxWidth: '480px' }}>
        <div className="text-danger mb-4 d-flex justify-content-center">
          <ShieldAlert size={64} />
        </div>
        <h1 className="fw-bold mb-3">Access Denied</h1>
        <p className="text-secondary mb-4">
          You do not have the required security credentials to access this dashboard.
        </p>
        <Link to={getDashboardPath()} className="btn btn-glow px-4 py-2">
          Return to Dashboard
        </Link>
      </div>
    </div>
  );
};

export default Unauthorized;
