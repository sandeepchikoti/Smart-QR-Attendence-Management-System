import axiosClient from './axiosClient';

export const authApi = {
  login: async (email, password) => {
    const response = await axiosClient.post('/api/auth/login', { email, password });
    return response.data;
  },

  logout: async () => {
    const response = await axiosClient.post('/api/auth/logout');
    return response.data;
  },

  refresh: async () => {
    const response = await axiosClient.post('/api/auth/refresh');
    return response.data;
  }
};
