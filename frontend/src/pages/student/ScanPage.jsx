import React, { useState, useEffect } from 'react';
import { useLocation } from '../../hooks/useLocation';
import { attendanceApi } from '../../api/attendanceApi';
import { Camera, MapPin, CheckCircle, AlertTriangle } from 'lucide-react';

export const ScanPage = () => {
  const { coords, accuracy, error: gpsError, loading: gpsLoading, getLocation } = useLocation();
  const [tokenInput, setTokenInput] = useState('');
  const [sessionIdInput, setSessionIdInput] = useState('');
  const [checkingIn, setCheckingIn] = useState(false);
  const [result, setResult] = useState(null);

  // Auto-acquire GPS coordinates on mount
  useEffect(() => {
    getLocation().catch((err) => console.warn('Initial GPS query failed:', err));
  }, [getLocation]);

  // Generate unique canvas device fingerprint
  const getDeviceFingerprint = () => {
    try {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      ctx.textBaseline = 'top';
      ctx.font = '14px Arial';
      ctx.fillText('QR-Attendance-Fingerprint', 2, 2);
      const dataUrl = canvas.toDataURL();
      let hash = 0;
      for (let i = 0; i < dataUrl.length; i++) {
        hash = (hash << 5) - hash + dataUrl.charCodeAt(i);
        hash |= 0;
      }
      return 'fp_' + Math.abs(hash);
    } catch (e) {
      return 'fp_fallback_std';
    }
  };

  const handleCheckIn = async (e) => {
    e.preventDefault();
    setResult(null);
    
    if (!sessionIdInput || !tokenInput) {
      setResult({ success: false, message: 'Please enter both Session ID and Scanned Token' });
      return;
    }

    setCheckingIn(true);
    try {
      // 1. Refresh GPS coordinates to guarantee fresh location values
      const currentLoc = await getLocation();

      // 2. Submit payload to backend
      const payload = {
        sessionId: parseInt(sessionIdInput, 10),
        scannedToken: tokenInput,
        latitude: currentLoc.latitude,
        longitude: currentLoc.longitude,
        accuracy: currentLoc.accuracy,
        deviceFingerprint: getDeviceFingerprint(),
      };

      const response = await attendanceApi.checkIn(payload);
      if (response.success) {
        setResult({
          success: true,
          message: `Attendance marked successfully as ${response.data.status}!`,
          gpsVerified: response.data.isGpsVerified,
        });
        setTokenInput(''); // clear inputs
      }
    } catch (error) {
      const message = error.response?.data?.error?.message || 'Check-in failed. Please verify token and location';
      setResult({ success: false, message });
    } finally {
      setCheckingIn(false);
    }
  };

  return (
    <div className="fade-in-up">
      <div className="glass-card p-4 mb-4">
        <h2 className="fw-bold tracking-tight mb-2">Scan Classroom QR</h2>
        <p className="text-secondary mb-0">Record your presence securely inside the classroom.</p>
      </div>

      <div className="row g-4">
        {/* Scanner Simulation View */}
        <div className="col-12 col-md-6">
          <div className="glass-card p-4 text-center h-100 d-flex flex-column justify-content-center align-items-center">
            <div className="scanner-frame mb-3 position-relative">
              <div className="scanner-beam"></div>
              <div className="d-flex justify-content-center align-items-center h-100 bg-black bg-opacity-40 text-secondary">
                <Camera size={48} className="text-secondary opacity-30" />
              </div>
            </div>
            <p className="text-secondary fs-7 mt-2">Point your camera at the screen displaying the attendance QR code</p>
          </div>
        </div>

        {/* Check-In Submission Form & Location Panel */}
        <div className="col-12 col-md-6">
          <div className="glass-card p-4 h-100 d-flex flex-column justify-content-between">
            {/* GPS Panel */}
            <div className="bg-black bg-opacity-40 p-3 rounded-3 mb-4">
              <h6 className="text-secondary fs-7 fw-bold mb-3 d-flex align-items-center gap-2">
                <MapPin size={16} className="text-primary" /> GEOLOCATION FEED
              </h6>
              
              {gpsLoading ? (
                <div className="text-primary fs-7">Acquiring high-accuracy GPS coordinates...</div>
              ) : gpsError ? (
                <div className="text-danger fs-7 d-flex align-items-center gap-2">
                  <AlertTriangle size={14} /> {gpsError}
                </div>
              ) : coords ? (
                <div>
                  <div className="d-flex justify-content-between fs-7 mb-1">
                    <span className="text-secondary">Latitude:</span>
                    <span className="text-white fw-mono">{coords.latitude.toFixed(6)}</span>
                  </div>
                  <div className="d-flex justify-content-between fs-7 mb-1">
                    <span className="text-secondary">Longitude:</span>
                    <span className="text-white fw-mono">{coords.longitude.toFixed(6)}</span>
                  </div>
                  <div className="d-flex justify-content-between fs-7">
                    <span className="text-secondary">Accuracy Radius:</span>
                    <span className="text-success fw-semibold">± {accuracy ? accuracy.toFixed(1) : 0} meters</span>
                  </div>
                </div>
              ) : (
                <div className="text-secondary fs-7">No GPS coordinates locked yet</div>
              )}

              <button
                type="button"
                onClick={() => getLocation()}
                className="btn btn-sm btn-outline-primary w-100 mt-3"
              >
                Refresh Geolocation
              </button>
            </div>

            {/* Check-In Result alerts */}
            {result && (
              <div
                className={`alert ${
                  result.success ? 'alert-success bg-success bg-opacity-15 text-success' : 'alert-danger bg-danger bg-opacity-15 text-danger'
                } border-0 rounded-3 p-3 mb-4 d-flex align-items-center gap-2`}
              >
                {result.success ? <CheckCircle size={20} /> : <AlertTriangle size={20} />}
                <div>{result.message}</div>
              </div>
            )}

            {/* Simulated Payload Form */}
            <form onSubmit={handleCheckIn}>
              <div className="mb-3">
                <label className="form-label text-secondary fs-7">Class Session ID</label>
                <input
                  type="number"
                  className="form-control form-input-custom"
                  placeholder="e.g. 45"
                  value={sessionIdInput}
                  onChange={(e) => setSessionIdInput(e.target.value)}
                  required
                />
              </div>

              <div className="mb-4">
                <label className="form-label text-secondary fs-7">Scanned QR Security Token</label>
                <input
                  type="text"
                  className="form-control form-input-custom"
                  placeholder="Paste or type security token from board"
                  value={tokenInput}
                  onChange={(e) => setTokenInput(e.target.value)}
                  required
                />
              </div>

              <button
                type="submit"
                disabled={checkingIn || gpsLoading || !coords}
                className="btn btn-glow w-100 py-3"
              >
                {checkingIn ? (
                  <span className="spinner-border spinner-border-sm" role="status"></span>
                ) : (
                  'Mark Attendance Presence'
                )}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ScanPage;
