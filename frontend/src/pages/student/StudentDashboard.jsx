import React, { useEffect, useState } from 'react';
import { Award, Clock, CheckCircle2, AlertCircle } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { attendanceApi as api } from '../../api/attendanceApi';

export const StudentDashboard = () => {
  const { user } = useAuth();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await api.getMyHistory();
        if (response.success) {
          setHistory(response.data);
        }
      } catch (err) {
        setError('Failed to fetch attendance summary');
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);

  const calculateStats = () => {
    if (history.length === 0) return { percent: 0, presentCount: 0, lateCount: 0, total: 0 };
    const total = history.length;
    const presentCount = history.filter((a) => a.status === 'PRESENT').length;
    const lateCount = history.filter((a) => a.status === 'LATE').length;
    const percent = Math.round(((presentCount + lateCount) / total) * 100);
    return { percent, presentCount, lateCount, total };
  };

  const stats = calculateStats();

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading data...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="fade-in-up">
      {/* Welcome Banner */}
      <div className="glass-card p-4 mb-4 d-flex justify-content-between align-items-center">
        <div>
          <h1 className="fw-bold tracking-tight mb-1 fs-3">Welcome Back, {user?.firstName}!</h1>
          <p className="text-secondary mb-0">Here is your attendance progress overview.</p>
        </div>
        <div className="bg-primary bg-opacity-20 text-primary p-3 rounded-3 d-none d-sm-block">
          <Award size={32} />
        </div>
      </div>

      {error && (
        <div className="alert alert-danger border-0 bg-danger bg-opacity-15 text-danger rounded-3 mb-4">
          {error}
        </div>
      )}

      {/* Stats Summary Cards */}
      <div className="row g-4 mb-4">
        {/* Progress Card */}
        <div className="col-12 col-md-4">
          <div className="glass-card p-4 h-100 text-center">
            <h5 className="text-secondary fs-7 fw-bold mb-3">OVERALL PERCENTAGE</h5>
            <div className="position-relative d-inline-block mb-3">
              {/* Simple HSL progress visual */}
              <div className="fs-1 fw-extrabold text-white">{stats.percent}%</div>
            </div>
            <div className="progress bg-dark rounded-pill" style={{ height: '8px' }}>
              <div
                className={`progress-bar rounded-pill ${stats.percent >= 75 ? 'bg-success' : 'bg-danger'}`}
                style={{ width: `${stats.percent}%` }}
              ></div>
            </div>
            <small className="text-secondary mt-2 d-block">
              {stats.percent >= 75 ? 'Meets attendance criteria (>=75%)' : 'Below attendance criteria (<75%)'}
            </small>
          </div>
        </div>

        {/* Count Card */}
        <div className="col-12 col-sm-6 col-md-4">
          <div className="glass-card p-4 h-100 d-flex align-items-center gap-3">
            <div className="bg-success bg-opacity-15 text-success p-3 rounded-3">
              <CheckCircle2 size={24} />
            </div>
            <div>
              <h6 className="text-secondary fs-7 fw-bold mb-1">CLASSES ATTENDED</h6>
              <div className="fs-3 fw-bold text-white">{stats.presentCount + stats.lateCount}</div>
              <small className="text-secondary">out of {stats.total} scheduled classes</small>
            </div>
          </div>
        </div>

        {/* Lateness Card */}
        <div className="col-12 col-sm-6 col-md-4">
          <div className="glass-card p-4 h-100 d-flex align-items-center gap-3">
            <div className="bg-warning bg-opacity-15 text-warning p-3 rounded-3">
              <Clock size={24} />
            </div>
            <div>
              <h6 className="text-secondary fs-7 fw-bold mb-1">LATE CHECK-INS</h6>
              <div className="fs-3 fw-bold text-white">{stats.lateCount}</div>
              <small className="text-secondary">counted with lateness penalty</small>
            </div>
          </div>
        </div>
      </div>

      {/* Check-In History logs */}
      <div className="glass-card p-4">
        <h5 className="fw-bold tracking-tight mb-4">Recent Attendance Records</h5>
        
        {history.length === 0 ? (
          <div className="text-center py-5">
            <p className="text-secondary mb-0">No attendance registered yet.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="table table-dark table-hover border-0 align-middle mb-0">
              <thead>
                <tr className="border-bottom border-secondary text-secondary">
                  <th>Check-In Time</th>
                  <th>Status</th>
                  <th>Location Verified</th>
                </tr>
              </thead>
              <tbody>
                {history.map((record) => (
                  <tr key={record.attendanceId} className="border-bottom border-secondary bg-transparent">
                    <td>{new Date(record.checkInTime).toLocaleString()}</td>
                    <td>
                      <span
                        className={`badge ${
                          record.status === 'PRESENT'
                            ? 'bg-success bg-opacity-15 text-success'
                            : record.status === 'LATE'
                            ? 'bg-warning bg-opacity-15 text-warning'
                            : 'bg-danger bg-opacity-15 text-danger'
                        } border-0`}
                      >
                        {record.status}
                      </span>
                    </td>
                    <td>
                      {record.isGpsVerified ? (
                        <span className="text-success fs-7 d-flex align-items-center gap-1">
                          <CheckCircle2 size={14} /> GPS Verified
                        </span>
                      ) : (
                        <span className="text-secondary fs-7 d-flex align-items-center gap-1">
                          <AlertCircle size={14} /> Manual Override
                        </span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentDashboard;
