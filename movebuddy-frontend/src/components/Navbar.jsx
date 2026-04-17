import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout, isAuthenticated, isCustomer, isDriver, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 60);
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const handleLogout = () => { logout(); navigate('/'); };

  const getDashboardLink = () => {
    if (isAdmin) return '/admin-dashboard';
    if (isDriver) return '/driver-dashboard';
    return '/rider-dashboard';
  };

  const isActive = (path) => location.pathname === path || location.pathname.startsWith(path);

  return (
    <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
      <Link to="/" className="navbar-logo">
        <div className="navbar-logo-dot" />
        <div className="navbar-logo-text">MoveBuddy</div>
      </Link>

      <div className="navbar-nav">
        {!isAuthenticated ? (
          <>
            <Link to="/login" className={`nav-link ${isActive('/login') ? 'active' : ''}`}>Login</Link>
            <Link to="/register" className="btn btn-primary btn-sm">Get Started</Link>
          </>
        ) : (
          <>
            <Link to={getDashboardLink()} className={`nav-link ${isActive('dashboard') ? 'active' : ''}`}>
              Dashboard
            </Link>

            {isCustomer && (
              <>
                <Link to="/book-ride" className={`nav-link ${isActive('/book-ride') ? 'active' : ''}`}>Book Ride</Link>
                <Link to="/rider-history" className={`nav-link ${isActive('/rider-history') ? 'active' : ''}`}>History</Link>
              </>
            )}

            {isDriver && (
              <Link to="/driver-history" className={`nav-link ${isActive('/driver-history') ? 'active' : ''}`}>History</Link>
            )}

            <Link to="/profile" className={`nav-link ${isActive('/profile') ? 'active' : ''}`}>Profile</Link>

            <button onClick={handleLogout} className="btn btn-secondary btn-sm">
              Logout
            </button>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
