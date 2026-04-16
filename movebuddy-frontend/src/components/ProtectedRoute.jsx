import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ allowedRoles = [] }) => {
  const { user, loading, isAuthenticated } = useAuth();

  if (loading) {
    return (
      <div className="loading-wrap">
        <div className="spinner"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
    // Redirect to their respective dashboard if they try to access a route not meant for them
    const path = user.role === 'DRIVER' ? '/driver-dashboard' : 
                 user.role === 'ADMIN' ? '/admin-dashboard' : 
                 '/rider-dashboard';
    return <Navigate to={path} replace />;
  }

  return <Outlet />;
};

export default ProtectedRoute;
