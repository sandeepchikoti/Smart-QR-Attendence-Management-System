import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Layouts & Protected Guards
import ProtectedRoute from '../components/common/ProtectedRoute';
import DashboardLayout from '../layouts/DashboardLayout';

// Pages
import Login from '../pages/auth/Login';
import Unauthorized from '../pages/auth/Unauthorized';
import AdminDashboard from '../pages/admin/AdminDashboard';
import FacultyDashboard from '../pages/faculty/FacultyDashboard';
import SessionMgmt from '../pages/faculty/SessionMgmt';
import StudentDashboard from '../pages/student/StudentDashboard';
import ScanPage from '../pages/student/ScanPage';
import HistoryPage from '../pages/student/HistoryPage';

export const AppRoutes = () => {
  const { user } = useAuth();

  const getHomeRedirect = () => {
    if (!user) return <Navigate to="/login" replace />;
    switch (user.role) {
      case 'ADMIN': return <Navigate to="/admin" replace />;
      case 'FACULTY': return <Navigate to="/faculty" replace />;
      case 'STUDENT': return <Navigate to="/student" replace />;
      default: return <Navigate to="/login" replace />;
    }
  };

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/unauthorized" element={<Unauthorized />} />

      {/* Protected Routes inside DashboardLayout shell */}
      <Route element={<DashboardLayout />}>
        {/* Admin Section */}
        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route path="/admin" element={<AdminDashboard />} />
        </Route>

        {/* Faculty Section */}
        <Route element={<ProtectedRoute allowedRoles={['FACULTY']} />}>
          <Route path="/faculty" element={<FacultyDashboard />} />
          <Route path="/faculty/session/:sessionId" element={<SessionMgmt />} />
        </Route>

        {/* Student Section */}
        <Route element={<ProtectedRoute allowedRoles={['STUDENT']} />}>
          <Route path="/student" element={<StudentDashboard />} />
          <Route path="/student/scan" element={<ScanPage />} />
          <Route path="/student/history" element={<HistoryPage />} />
        </Route>
      </Route>

      {/* Fallback Redirects */}
      <Route path="/" element={getHomeRedirect()} />
      <Route path="*" element={getHomeRedirect()} />
    </Routes>
  );
};

export default AppRoutes;
