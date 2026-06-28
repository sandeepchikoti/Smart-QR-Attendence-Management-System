import axiosClient from './axiosClient';

export const sessionApi = {
  startSession: async (timetableId) => {
    const response = await axiosClient.post('/api/sessions/start', { timetableId });
    return response.data;
  },

  getActiveToken: async (sessionId) => {
    const response = await axiosClient.get(`/api/sessions/${sessionId}/token`);
    return response.data;
  },

  endSession: async (sessionId) => {
    const response = await axiosClient.post(`/api/sessions/${sessionId}/end`);
    return response.data;
  }
};
