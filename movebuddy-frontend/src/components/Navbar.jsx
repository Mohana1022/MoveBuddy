import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Menu, X, LogOut, LayoutDashboard, History, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout, isAuthenticated, isCustomer, isDriver, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [scrolled, setScrolled] = useState(false);
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 40);
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  // Close menu on route change
  useEffect(() => {
    setIsMenuOpen(false);
  }, [location.pathname]);

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

      {/* Mobile Toggle Button */}
      <button 
        className="mobile-menu-btn" 
        onClick={() => setIsMenuOpen(!isMenuOpen)}
        aria-label="Toggle Navigation"
        style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minWidth: '44px', minHeight: '44px' }}
      >
        {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
      </button>

      <div className={`navbar-nav ${isMenuOpen ? 'open' : ''}`}>
        {!isAuthenticated ? (
          <>
            <Link to="/login" className={`nav-link ${isActive('/login') ? 'active' : ''}`}>Login</Link>
            <Link to="/register" className="btn btn-primary btn-sm btn-sm-100">Get Started</Link>
          </>
        ) : (
          <>
            <Link to={getDashboardLink()} className={`nav-link ${isActive('dashboard') ? 'active' : ''}`}>
              <LayoutDashboard size={16} className="mobile-only-icon" /> Dashboard
            </Link>

            {isCustomer && (
              <>
                <Link to="/book-ride" className={`nav-link ${isActive('/book-ride') ? 'active' : ''}`}>Book Ride</Link>
                <Link to="/rider-history" className={`nav-link ${isActive('/rider-history') ? 'active' : ''}`}>
                  <History size={16} className="mobile-only-icon" /> History
                </Link>
              </>
            )}

            {isDriver && (
              <Link to="/driver-history" className={`nav-link ${isActive('/driver-history') ? 'active' : ''}`}>
                <History size={16} className="mobile-only-icon" /> History
              </Link>
            )}

            <Link to="/profile" className={`nav-link ${isActive('/profile') ? 'active' : ''}`}>
              <User size={16} className="mobile-only-icon" /> Profile
            </Link>

            <button onClick={handleLogout} className="btn btn-secondary btn-sm btn-sm-100" style={{ marginTop: isMenuOpen ? '12px' : '0' }}>
              <LogOut size={16} /> Logout
            </button>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
