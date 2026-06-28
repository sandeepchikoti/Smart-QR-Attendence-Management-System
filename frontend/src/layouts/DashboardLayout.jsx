import React from 'react';
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, Home, Calendar, Clock, BookOpen, Settings, ShieldAlert, Award } from 'lucide-react';

export const DashboardLayout = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const getNavLinks = () => {
    switch (user?.role) {
      case 'ADMIN':
        return [
          { to: '/admin', label: 'Dashboard', icon: Home },
          { to: '/admin/departments', label: 'Departments', icon: BookOpen },
          { to: '/admin/settings', label: 'Settings', icon: Settings },
        ];
      case 'FACULTY':
        return [
          { to: '/faculty', label: 'My Timetable', icon: Calendar },
          { to: '/faculty/sessions', label: 'Active Sessions', icon: Clock },
          { to: '/faculty/settings', label: 'Settings', icon: Settings },
        ];
      case 'STUDENT':
        return [
          { to: '/student', label: 'My Progress', icon: Award },
          { to: '/student/scan', label: 'Scan QR Code', icon: ShieldAlert },
          { to: '/student/history', label: 'Check-in History', icon: Clock },
        ];
      default:
        return [];
    }
  };

  const navLinks = getNavLinks();

  return (
    <div className="d-flex min-vh-100 bg-dark text-white">
      {/* Sidebar */}
      <aside className="d-none d-md-flex flex-column p-4 bg-black border-end border-secondary" style={{ width: '280px' }}>
        <div className="mb-5 d-flex align-items-center gap-2">
          <div className="bg-primary p-2 rounded-3 text-white fw-bold">QR</div>
          <span className="fs-5 fw-bold tracking-tight">Attendance System</span>
        </div>

        <nav className="nav flex-column gap-2 flex-grow-1">
          {navLinks.map((link) => {
            const Icon = link.icon;
            const isActive = location.pathname === link.to;
            return (
              <Link
                key={link.to}
                to={link.to}
                className={`nav-link d-flex align-items-center gap-3 px-3 py-2.5 rounded-3 transition-colors ${
                  isActive
                    ? 'bg-primary text-white shadow'
                    : 'text-secondary hover-text-white'
                }`}
              >
                <Icon size={20} />
                <span>{link.label}</span>
              </Link>
            );
          })}
        </nav>

        <button
          onClick={handleLogout}
          className="btn btn-outline-danger d-flex align-items-center justify-content-center gap-2 mt-auto"
        >
          <LogOut size={18} />
          <span>Sign Out</span>
        </button>
      </aside>

      {/* Main Area */}
      <div className="flex-grow-1 d-flex flex-column min-vh-100 overflow-y-auto">
        {/* Header */}
        <header className="navbar navbar-dark bg-black px-4 py-3 border-bottom border-secondary d-flex justify-content-between align-items-center">
          <div className="d-md-none d-flex align-items-center gap-2">
            <span className="fs-6 fw-bold text-primary">QR Attendance</span>
          </div>
          <div className="d-none d-md-block">
            <span className="text-secondary">Role: </span>
            <span className="badge bg-secondary text-light fw-bold">{user?.role}</span>
          </div>

          <div className="d-flex align-items-center gap-4">
            <div className="text-end d-none d-sm-block">
              <div className="fw-semibold">{user?.firstName} {user?.lastName}</div>
              <small className="text-secondary">{user?.email}</small>
            </div>
            <button
              onClick={handleLogout}
              className="btn btn-sm btn-outline-danger d-md-none"
            >
              <LogOut size={16} />
            </button>
          </div>
        </header>

        {/* Content Body */}
        <main className="p-4 flex-grow-1 d-flex flex-column container-fluid" style={{ maxWidth: '1200px' }}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;
