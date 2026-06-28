import React, { useState } from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useAuth } from '../../context/AuthContext';
import { Shield, KeyRound, AlertCircle } from 'lucide-react';

export const Login = () => {
  const { login, isAuthenticated, user } = useAuth();
  const navigate = useNavigate();
  const [errorMsg, setErrorMsg] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();

  // If already authenticated, redirect to role-specific dashboard immediately
  if (isAuthenticated && user) {
    switch (user.role) {
      case 'ADMIN':
        return <Navigate to="/admin" replace />;
      case 'FACULTY':
        return <Navigate to="/faculty" replace />;
      case 'STUDENT':
        return <Navigate to="/student" replace />;
    }
  }

  const onSubmit = async (data) => {
    setSubmitting(true);
    setErrorMsg(null);
    const result = await login(data.email, data.password);
    
    if (result.success) {
      // Recovery state metadata logic
      const cachedUser = JSON.parse(localStorage.getItem('userMetadata'));
      if (cachedUser) {
        switch (cachedUser.role) {
          case 'ADMIN':
            navigate('/admin');
            break;
          case 'FACULTY':
            navigate('/faculty');
            break;
          case 'STUDENT':
            navigate('/student');
            break;
        }
      }
    } else {
      setErrorMsg(result.message);
    }
    setSubmitting(false);
  };

  return (
    <div className="d-flex align-items-center justify-content-center min-vh-100 bg-dark" style={{ backgroundImage: 'radial-gradient(circle at top right, rgba(99, 102, 241, 0.15), transparent 40%)' }}>
      <div className="container" style={{ maxWidth: '450px' }}>
        <div className="glass-card p-4 p-sm-5 fade-in-up text-center">
          <div className="d-flex justify-content-center align-items-center mb-4">
            <div className="bg-primary p-3 rounded-circle text-white shadow-lg">
              <Shield size={36} />
            </div>
          </div>
          
          <h2 className="fw-bold tracking-tight mb-2">Institution Login</h2>
          <p className="text-secondary mb-4">Smart QR Attendance System</p>

          {errorMsg && (
            <div className="alert alert-danger d-flex align-items-center gap-2 text-start p-3 border-0 bg-danger bg-opacity-15 text-danger rounded-3 mb-4" role="alert">
              <AlertCircle size={20} className="flex-shrink-0" />
              <div>{errorMsg}</div>
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="text-start">
            <div className="mb-3">
              <label htmlFor="email" className="form-label text-secondary fs-7">Institutional Email</label>
              <input
                id="email"
                type="email"
                className={`form-control form-input-custom ${errors.email ? 'is-invalid border-danger' : ''}`}
                placeholder="email@university.edu"
                {...register('email', {
                  required: 'Email is required',
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: 'Invalid email address format'
                  }
                })}
                name="email"
              />
              {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
            </div>

            <div className="mb-4">
              <label htmlFor="password" className="form-label text-secondary fs-7">Account Password</label>
              <div className="position-relative">
                <input
                  id="password"
                  type="password"
                  className={`form-control form-input-custom ${errors.password ? 'is-invalid border-danger' : ''}`}
                  placeholder="••••••••"
                  {...register('password', {
                    required: 'Password is required',
                    minLength: {
                      value: 8,
                      message: 'Password must be at least 8 characters'
                    }
                  })}
                  name="password"
                />
                <KeyRound size={18} className="position-absolute end-0 top-50 translate-middle-y me-3 text-secondary" />
              </div>
              {errors.password && <div className="invalid-feedback d-block">{errors.password.message}</div>}
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="btn btn-glow w-100 py-3 d-flex align-items-center justify-content-center gap-2"
            >
              {submitting ? (
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              ) : (
                'Sign In Securely'
              )}
            </button>
          </form>

          <div className="mt-4 text-center">
            <small className="text-secondary">Seeded credentials: admin@university.edu / Admin123!</small>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
