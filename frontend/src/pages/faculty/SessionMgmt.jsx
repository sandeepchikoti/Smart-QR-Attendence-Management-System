import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQRGenerator } from '../../hooks/useQRGenerator';
import { sessionApi } from '../../api/sessionApi';
import { attendanceApi } from '../../api/attendanceApi';
import { Users, Clock, ShieldAlert, Award, XCircle } from 'lucide-react';

export const SessionMgmt = () => {
  const { sessionId } = useParams();
  const navigate = useNavigate();
  const [sessionActive, setSessionActive] = useState(true);
  const [students, setStudents] = useState([]);
  const [ending, setEnding] = useState(false);

  // Hook to fetch and rotate dynamic tokens every 10s
  const { token, loading: qrLoading, error: qrError, secondsLeft } = useQRGenerator(sessionId, sessionActive);

  // Query checked-in students list
  const fetchStudents = async () => {
    try {
      const response = await attendanceApi.getSessionAttendance(sessionId);
      if (response.success) {
        setStudents(response.data);
      }
    } catch (e) {
      console.warn('Failed to query session check-ins:', e);
    }
  };

  // Poll check-in database list every 4 seconds to show real-time updates
  useEffect(() => {
    fetchStudents();
    const studentPoller = setInterval(fetchStudents, 4000);
    return () => clearInterval(studentPoller);
  }, [sessionId]);

  const handleEndSession = async () => {
    setEnding(true);
    try {
      const response = await sessionApi.endSession(sessionId);
      if (response.success) {
        setSessionActive(false);
        navigate('/faculty');
      }
    } catch (e) {
      console.error('Error ending session:', e);
    } finally {
      setEnding(false);
    }
  };

  return (
    <div className="fade-in-up">
      {/* Banner */}
      <div className="glass-card p-4 mb-4 d-flex justify-content-between align-items-center flex-wrap gap-3">
        <div>
          <span className="badge bg-success mb-2">Live Session ID: #{sessionId}</span>
          <h2 className="fw-bold text-white mb-0">Active Lecture Check-In</h2>
        </div>
        <button
          onClick={handleEndSession}
          disabled={ending}
          className="btn btn-danger px-4 py-2.5 d-flex align-items-center gap-2 border-0"
        >
          <XCircle size={18} />
          {ending ? 'Closing...' : 'Close Lecture Session'}
        </button>
      </div>

      <div className="row g-4">
        {/* Dynamic QR Display Panel */}
        <div className="col-12 col-md-5">
          <div className="glass-card p-4 text-center h-100 d-flex flex-column justify-content-between align-items-center">
            <h5 className="text-secondary fs-7 fw-bold mb-4">DYNAMIC CLASSROOM QR</h5>

            {qrError ? (
              <div className="text-danger py-4">{qrError}</div>
            ) : qrLoading && !token ? (
              <div className="spinner-border text-primary my-5" role="status"></div>
            ) : (
              <div className="my-3">
                <div className="qr-pulse-container mb-3">
                  {/* Simulated QR Code containing Token info */}
                  <div
                    className="bg-black p-4 d-flex flex-column justify-content-center align-items-center rounded-3 shadow"
                    style={{ width: '220px', height: '220px', border: '1px solid rgba(255,255,255,0.1)' }}
                  >
                    <div className="text-primary fw-mono fs-8 mb-2">DYNAMIC CODE</div>
                    <div className="text-white fw-bold tracking-wider fs-6 text-break">{token}</div>
                    <ShieldAlert size={40} className="text-primary mt-3 opacity-50" />
                  </div>
                </div>

                {/* Countdown progress visual */}
                <div className="d-flex align-items-center justify-content-center gap-3">
                  <div className="spinner-grow spinner-grow-sm text-primary" role="status"></div>
                  <span className="text-secondary fs-7">
                    Token rotates in <strong className="text-white">{secondsLeft}</strong> seconds
                  </span>
                </div>
              </div>
            )}

            <div className="w-100 bg-black bg-opacity-35 p-3 rounded-3 mt-4 text-start">
              <small className="text-secondary d-block mb-1">SESSION CONTROL PARAMETERS</small>
              <div className="d-flex justify-content-between fs-7">
                <span className="text-secondary">Token Interval:</span>
                <span className="text-white font-monospace">10 Seconds</span>
              </div>
              <div className="d-flex justify-content-between fs-7 mt-1">
                <span className="text-secondary">Geofence Radius:</span>
                <span className="text-white font-monospace">50 Meters</span>
              </div>
            </div>
          </div>
        </div>

        {/* Real-time Student Logs List */}
        <div className="col-12 col-md-7">
          <div className="glass-card p-4 h-100 d-flex flex-column justify-content-between">
            <div>
              <div className="d-flex justify-content-between align-items-center mb-4">
                <h5 className="fw-bold tracking-tight mb-0">Checked-in Students</h5>
                <div className="badge bg-primary bg-opacity-15 text-primary p-2 d-flex align-items-center gap-2">
                  <Users size={16} />
                  <span>{students.length} Present</span>
                </div>
              </div>

              {students.length === 0 ? (
                <div className="text-center py-5 text-secondary">
                  <Clock size={36} className="mb-3 opacity-40" />
                  <p className="mb-0">Waiting for student check-ins...</p>
                  <small className="text-muted">Display the QR code on screen for students to scan.</small>
                </div>
              ) : (
                <div className="table-responsive" style={{ maxHeight: '350px' }}>
                  <table className="table table-dark table-hover border-0 align-middle mb-0">
                    <thead>
                      <tr className="border-bottom border-secondary text-secondary">
                        <th>Record ID</th>
                        <th>Timestamp</th>
                        <th>Status</th>
                        <th>GPS Check</th>
                      </tr>
                    </thead>
                    <tbody>
                      {students.map((student) => (
                        <tr key={student.attendanceId} className="border-bottom border-secondary bg-transparent">
                          <td className="fw-bold text-secondary">#{student.attendanceId}</td>
                          <td>{new Date(student.checkInTime).toLocaleTimeString()}</td>
                          <td>
                            <span
                              className={`badge ${
                                student.status === 'PRESENT'
                                  ? 'bg-success bg-opacity-15 text-success'
                                  : student.status === 'LATE'
                                  ? 'bg-warning bg-opacity-15 text-warning'
                                  : 'bg-danger bg-opacity-15 text-danger'
                              } border-0`}
                            >
                              {student.status}
                            </span>
                          </td>
                          <td>
                            {student.isGpsVerified ? (
                              <span className="text-success fs-8">GPS Verified</span>
                            ) : (
                              <span className="text-secondary fs-8">Manual Override</span>
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
        </div>
      </div>
    </div>
  );
};

export default SessionMgmt;
