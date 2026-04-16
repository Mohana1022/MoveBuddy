import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const LandingPage = () => {
  const { isAuthenticated, isDriver, isCustomer } = useAuth();

  const getStartedLink = () => {
    if (!isAuthenticated) return '/register';
    if (isDriver) return '/driver-dashboard';
    return '/rider-dashboard';
  };

  return (
    <div className="container">
      <div className="animate-slideUp" style={{ padding: '80px 0', textAlign: 'center' }}>
        <h1 style={{ fontSize: '4rem', marginBottom: '24px' }}>
          Move smarter with <span style={{ color: 'var(--primary)' }}>MoveBuddy</span>
        </h1>
        <p className="page-subtitle" style={{ fontSize: '1.25rem', maxWidth: '700px', margin: '0 auto 40px' }}>
          The fastest and most affordable way to commute in your city. 
          Book bike taxis, autos, and cabs at the tap of a button.
        </p>
        
        <div style={{ display: 'flex', gap: '16px', justifyContent: 'center' }}>
          <Link to={getStartedLink()} className="btn btn-primary btn-lg">
            Get Started
          </Link>
          {!isAuthenticated && (
            <Link to="/login" className="btn btn-secondary btn-lg">
              Login to Account
            </Link>
          )}
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '32px', marginTop: '60px' }}>
        <div className="card card-hover">
          <div style={{ fontSize: '2.5rem', marginBottom: '16px' }}>⚡</div>
          <h3>Quick Booking</h3>
          <p className="text-secondary mt-2">Find the nearest driver in seconds. No more waiting on the street.</p>
        </div>
        <div className="card card-hover">
          <div style={{ fontSize: '2.5rem', marginBottom: '16px' }}>💰</div>
          <h3>Best Prices</h3>
          <p className="text-secondary mt-2">Transparent, distance-based pricing with no hidden surprises.</p>
        </div>
        <div className="card card-hover">
          <div style={{ fontSize: '2.5rem', marginBottom: '16px' }}>🛡️</div>
          <h3>Safe Rides</h3>
          <p className="text-secondary mt-2">Verified drivers and real-time ride tracking for your peace of mind.</p>
        </div>
      </div>

      <div style={{ marginTop: '100px', background: 'var(--surface)', padding: '60px', borderRadius: 'var(--radius-lg)', textAlign: 'center' }}>
        <h2 style={{ fontSize: '2.5rem', marginBottom: '16px' }}>Earn more with MoveBuddy</h2>
        <p className="text-secondary mb-6">Own a vehicle? Become a MoveBuddy Driver and start earning today.</p>
        <Link to="/register?role=DRIVER" className="btn btn-outline btn-lg">
          Register as Driver
        </Link>
      </div>
    </div>
  );
};

export default LandingPage;
