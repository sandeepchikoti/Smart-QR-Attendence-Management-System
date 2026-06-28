import React, { useEffect, useState } from 'react';
import { attendanceApi } from '../../api/attendanceApi';
import { Clock, CheckCircle2, AlertCircle } from 'lucide-react';

export const HistoryPage = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await attendanceApi.getMyHistory();
        if (response.success) {
          setHistory(response.data);
        }
      } catch (err) {
        setError('Failed to fetch check-in history');
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading history...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="fade-in-up">
      <div className="glass-card p-4 mb-4">
        <h2 className="fw-bold tracking-tight mb-2">Check-in History</h2>
        <p className="text-secondary mb-0">Review all your historically registered attendance records.</p>
      </div>

      {error && (
        <div className="alert alert-danger border-0 bg-danger bg-opacity-15 text-danger rounded-3 mb-4">
          {error}
        </div>
      )}

      <div className="glass-card p-4">
        {history.length === 0 ? (
          <div className="text-center py-5 text-secondary">
            No attendance records found.
          </div>
        ) : (
          <div className="table-responsive">
            <table className="table table-dark table-hover border-0 align-middle mb-0">
              <thead>
                <tr className="border-bottom border-secondary text-secondary">
                  <th># ID</th>
                  <th>Timestamp</th>
                  <th>Attendance Status</th>
                  <th>Location Verified</th>
                </tr>
              </thead>
              <tbody>
                {history.map((record) => (
                  <tr key={record.attendanceId} className="border-bottom border-secondary bg-transparent">
                    <td className="fw-semibold text-secondary">#{record.attendanceId}</td>
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
                          <AlertCircle size={14} /> Manual Adjust
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

export default HistoryPage;
