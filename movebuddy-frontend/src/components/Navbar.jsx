import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout, isAuthenticated, isCustomer, isDriver, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const getDashboardLink = () => {
    if (isAdmin) return '/admin-dashboard';
    if (isDriver) return '/driver-dashboard';
    return '/rider-dashboard';
  };

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-logo">
        <div className="navbar-logo-icon">M</div>
        <div className="navbar-logo-text">Move<span>Buddy</span></div>
      </Link>

      <div className="navbar-nav">
        {!isAuthenticated ? (
          <>
            <Link to="/login" className={`nav-link ${location.pathname === '/login' ? 'active' : ''}`}>Login</Link>
            <Link to="/register" className="btn btn-primary btn-sm">Sign Up</Link>
          </>
        ) : (
          <>
            <Link to={getDashboardLink()} className={`nav-link ${location.pathname.includes('dashboard') ? 'active' : ''}`}>
              Dashboard
            </Link>
            
            {isCustomer && (
              <Link to="/book-ride" className={`nav-link ${location.pathname === '/book-ride' ? 'active' : ''}`}>
                Book Ride
              </Link>
            )}

            <Link to="/profile" className={`nav-link ${location.pathname === '/profile' ? 'active' : ''}`}>
              Profile
            </Link>

            <button onClick={handleLogout} className="btn btn-secondary btn-sm" style={{ marginLeft: '12px' }}>
              Logout
            </button>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
