import axios from 'axios';

const axiosClient = axios.create({
  baseURL: '',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Crucial for passing HTTP-Only secure cookies (refresh token)
});

// Request Interceptor: Attach Access Token
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Handle Token Rotation (401 errors)
axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Avoid infinite loops on Auth login or double failures
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url.includes('/api/auth/login')
    ) {
      originalRequest._retry = true;

      try {
        // Request token refresh using cookie credentials
        const refreshResponse = await axios.post('/api/auth/refresh', {}, { withCredentials: true });
        
        if (refreshResponse.data && refreshResponse.data.success) {
          const newAccessToken = refreshResponse.data.data.accessToken;
          localStorage.setItem('accessToken', newAccessToken);
          
          // Re-inject token and repeat original request
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return axiosClient(originalRequest);
        }
      } catch (refreshError) {
        // Refresh token failed/expired -> log out user
        localStorage.removeItem('accessToken');
        window.dispatchEvent(new Event('auth-logout')); // Trigger global redirect
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosClient;
