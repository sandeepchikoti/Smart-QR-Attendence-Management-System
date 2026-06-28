import axiosClient from './axiosClient';

export const attendanceApi = {
  checkIn: async (payload) => {
    const response = await axiosClient.post('/api/attendance/check-in', payload);
    return response.data;
  },

  overrideAttendance: async (attendanceId, status, reason) => {
    const response = await axiosClient.put(`/api/attendance/${attendanceId}/override`, { status, reason });
    return response.data;
  },

  getSessionAttendance: async (sessionId) => {
    const response = await axiosClient.get(`/api/attendance/session/${sessionId}`);
    return response.data;
  },

  getMyHistory: async () => {
    const response = await axiosClient.get('/api/attendance/my-history');
    return response.data;
  }
};
