import { useState, useCallback } from 'react';

export const useLocation = () => {
  const [coords, setCoords] = useState(null);
  const [accuracy, setAccuracy] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const getLocation = useCallback(() => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        const msg = 'Geolocation is not supported by this browser';
        setError(msg);
        reject(msg);
        return;
      }

      setLoading(true);
      setError(null);

      const options = {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      };

      navigator.geolocation.getCurrentPosition(
        (position) => {
          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;
          const gpsAccuracy = position.coords.accuracy;

          setCoords({ latitude, longitude });
          setAccuracy(gpsAccuracy);
          setLoading(false);
          resolve({ latitude, longitude, accuracy: gpsAccuracy });
        },
        (geoError) => {
          let msg = 'Failed to retrieve location coordinates';
          switch (geoError.code) {
            case geoError.PERMISSION_DENIED:
              msg = 'Location permission was denied. Please enable GPS access';
              break;
            case geoError.POSITION_UNAVAILABLE:
              msg = 'GPS coordinates are currently unavailable. Make sure your device has GPS active';
              break;
            case geoError.TIMEOUT:
              msg = 'Location request timed out. Please try scanning again';
              break;
          }
          setError(msg);
          setLoading(false);
          reject(msg);
        },
        options
      );
    });
  }, []);

  return { coords, accuracy, error, loading, getLocation };
};
export default useLocation;
