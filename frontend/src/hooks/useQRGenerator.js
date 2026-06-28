import { useState, useEffect, useRef } from 'react';
import { sessionApi } from '../api/sessionApi';

export const useQRGenerator = (sessionId, active) => {
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [secondsLeft, setSecondsLeft] = useState(10);
  const intervalRef = useRef(null);
  const countdownRef = useRef(null);

  const fetchToken = async () => {
    try {
      const response = await sessionApi.getActiveToken(sessionId);
      if (response.success) {
        setToken(response.data.token);
        setError(null);
        setSecondsLeft(10); // reset countdown on successful fetch
      }
    } catch (err) {
      setError(err.response?.data?.error?.message || 'Failed to fetch QR token');
      setToken(null);
    }
  };

  useEffect(() => {
    if (!sessionId || !active) {
      setToken(null);
      return;
    }

    setLoading(true);
    fetchToken().finally(() => setLoading(false));

    // Setup 10-second API polling
    intervalRef.current = setInterval(() => {
      fetchToken();
    }, 10000);

    // Setup 1-second visual countdown tick
    countdownRef.current = setInterval(() => {
      setSecondsLeft((prev) => (prev <= 1 ? 10 : prev - 1));
    }, 10000 / 10); // 1 second

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
      if (countdownRef.current) clearInterval(countdownRef.current);
    };
  }, [sessionId, active]);

  return { token, loading, error, secondsLeft };
};
export default useQRGenerator;
