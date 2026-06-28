import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { sessionApi } from '../../api/sessionApi';
import { Calendar, Play, Clock, ArrowRight } from 'lucide-react';

export const FacultyDashboard = () => {
  const navigate = useNavigate();
  const [starting, setStarting] = useState(false);
  const [error, setError] = useState(null);

  // Default lectures schedule for quick seeder testing
  const mockSchedules = [
    { id: 1, subject: 'Advanced Software Engineering', code: 'CS-402', room: 'Room 402', time: '10:00 AM - 11:30 AM' },
    { id: 2, subject: 'Cloud Infrastructure Architecture', code: 'CS-408', room: 'Room 105', time: '01:30 PM - 03:00 PM' }
  ];

  const handleStartAttendance = async (timetableId) => {
    setStarting(true);
    setError(null);
    try {
      const response = await sessionApi.startSession(timetableId);
      if (response.success) {
        // Redirect to live session tracking view
        navigate(`/faculty/session/${response.data.sessionId}`);
      }
    } catch (err) {
      setError(err.response?.data?.error?.message || 'Failed to initiate attendance session. Make sure DB seeder ran');
    } finally {
      setStarting(false);
    }
  };

  return (
    <div className="fade-in-up">
      <div className="glass-card p-4 mb-4">
        <h2 className="fw-bold tracking-tight mb-2">My Lecture Schedule</h2>
        <p className="text-secondary mb-0">Select a class below to generate a dynamic attendance QR code.</p>
      </div>

      {error && (
        <div className="alert alert-danger border-0 bg-danger bg-opacity-15 text-danger rounded-3 mb-4">
          {error}
        </div>
      )}

      <div className="row g-4">
        {mockSchedules.map((lecture) => (
          <div key={lecture.id} className="col-12 col-lg-6">
            <div className="glass-card p-4 h-100 d-flex flex-column justify-content-between">
              <div>
                <div className="d-flex justify-content-between align-items-start mb-3">
                  <span className="badge bg-primary bg-opacity-20 text-primary">{lecture.code}</span>
                  <span className="text-secondary fs-7 d-flex align-items-center gap-1">
                    <Clock size={14} /> {lecture.time}
                  </span>
                </div>
                <h4 className="fw-bold text-white mb-2">{lecture.subject}</h4>
                <p className="text-secondary mb-4">{lecture.room}</p>
              </div>

              <button
                onClick={() => handleStartAttendance(lecture.id)}
                disabled={starting}
                className="btn btn-glow w-100 d-flex align-items-center justify-content-center gap-2 py-3"
              >
                {starting ? (
                  <span className="spinner-border spinner-border-sm" role="status"></span>
                ) : (
                  <>
                    <Play size={18} /> Start Attendance Session
                  </>
                )}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default FacultyDashboard;
