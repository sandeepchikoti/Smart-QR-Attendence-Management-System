import React, { createContext, useState, useEffect, useContext } from 'react';
import { authApi } from '../api/authApi';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Recover user metadata from localStorage if exists (for instantaneous UI loading)
  useEffect(() => {
    const cachedUser = localStorage.getItem('userMetadata');
    const token = localStorage.getItem('accessToken');
    if (cachedUser && token) {
      setUser(JSON.parse(cachedUser));
    }
    setLoading(false);

    // Global listener for API logout redirects
    const handleLogoutEvent = () => {
      clearAuthentication();
    };

    window.addEventListener('auth-logout', handleLogoutEvent);
    return () => window.removeEventListener('auth-logout', handleLogoutEvent);
  }, []);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const response = await authApi.login(email, password);
      if (response.success) {
        const { accessToken, role, firstName, lastName, email: userEmail } = response.data;
        const profile = { role, firstName, lastName, email: userEmail };
        
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('userMetadata', JSON.stringify(profile));
        setUser(profile);
        return { success: true };
      }
    } catch (error) {
      clearAuthentication();
      const message = error.response?.data?.error?.message || 'Login failed. Please check credentials';
      return { success: false, message };
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    setLoading(true);
    try {
      await authApi.logout();
    } catch (e) {
      console.error('Logout request failed on server', e);
    } finally {
      clearAuthentication();
      setLoading(false);
    }
  };

  const clearAuthentication = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('userMetadata');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
