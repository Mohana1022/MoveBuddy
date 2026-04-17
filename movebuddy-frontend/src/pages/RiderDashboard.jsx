import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { MapPin, Clock, History, ChevronRight, Navigation, AlertTriangle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { bookingAPI, customerAPI } from '../services/api';

const fadeUp = {
  hidden: { opacity: 0, y: 24 },
  visible: (i = 0) => ({ opacity: 1, y: 0, transition: { duration: 0.5, delay: i * 0.1, ease: 'easeOut' } })
};

const RiderDashboard = () => {
  const { user } = useAuth();
  const [activeRide, setActiveRide] = useState(null);
  const [loading, setLoading] = useState(true);
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [profileRes, activeRes] = await Promise.allSettled([
          customerAPI.getProfile(user.mobileNo),
          bookingAPI.getActiveBooking(user.mobileNo)
        ]);
        if (profileRes.status === 'fulfilled') setProfile(profileRes.value.data.data);
        if (activeRes.status === 'fulfilled' && activeRes.value.data.statuscode === 200)
          setActiveRide(activeRes.value.data.data);
      } catch (err) {
        console.error('Dashboard error', err);
      } finally {
        setLoading(false);
      }
    };
    if (user?.mobileNo) fetchData();
  }, [user]);

  if (loading) return <div className="loading-wrap"><div className="spinner" /></div>;

  const firstName = profile?.name?.split(' ')[0] || 'Rider';
  const hour = new Date().getHours();
  const greeting = hour < 12 ? 'Good Morning' : hour < 17 ? 'Good Afternoon' : 'Good Evening';

  return (
    <div className="container" style={{ paddingBottom: 60 }}>

      {/* ── Header ── */}
      <motion.div initial="hidden" animate="visible" variants={fadeUp} className="page-header">
        <span className="section-label">{greeting} 👋</span>
        <h1 className="page-title">{firstName}</h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: 4, display: 'flex', alignItems: 'center', gap: 6 }}>
          <MapPin size={14} color="var(--accent-cyan)" />
          {profile?.currentLoc || 'Detecting location...'}
        </p>
      </motion.div>

      {/* ── Active Ride Banner ── */}
      {activeRide && (
        <motion.div initial="hidden" animate="visible" variants={fadeUp} custom={1}
          className="glass-card glass-card-cyan"
          style={{ padding: '24px 28px', marginBottom: 28, display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 16 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <div style={{
              width: 48, height: 48, borderRadius: '50%',
              background: 'rgba(0,212,255,0.1)', border: '1px solid rgba(0,212,255,0.3)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              boxShadow: '0 0 16px rgba(0,212,255,0.2)',
              animation: 'activePulse 2s ease-in-out infinite'
            }}>
              <Navigation size={20} color="var(--accent-cyan)" />
            </div>
            <div>
              <span className="badge badge-primary" style={{ marginBottom: 6 }}>Ride in Progress</span>
              <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>
                <span style={{ color: 'var(--accent-emerald)' }}>●</span> {activeRide.sourceLoc}
                &nbsp;→&nbsp;
                <span style={{ color: 'var(--accent-rose)' }}>●</span> {activeRide.destinationLoc}
              </p>
            </div>
          </div>
          <Link to={`/track-ride/${activeRide.id}`} className="btn btn-primary btn-sm" style={{ whiteSpace: 'nowrap' }}>
            Track Live <ChevronRight size={14} />
          </Link>
        </motion.div>
      )}

      {/* ── Quick Actions ── */}
      <motion.div initial="hidden" animate="visible" variants={fadeUp} custom={2} style={{ marginBottom: 28 }}>
        <span className="section-label" style={{ display: 'block', marginBottom: 16 }}>Quick Actions</span>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 16 }}>

          <Link to="/book-ride" style={{ textDecoration: 'none' }}>
            <motion.div whileHover={{ y: -4, scale: 1.01 }} className="glass-card"
              style={{ padding: '28px 24px', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: 12 }}>
              <div style={{
                width: 52, height: 52, borderRadius: 'var(--radius-md)',
                background: 'rgba(0,212,255,0.08)', border: '1px solid rgba(0,212,255,0.2)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                boxShadow: '0 0 16px rgba(0,212,255,0.1)'
              }}>
                <Navigation size={22} color="var(--accent-cyan)" />
              </div>
              <div>
                <p style={{ fontFamily: 'Syne, sans-serif', fontWeight: 700, fontSize: '1.05rem', color: 'var(--text-primary)', marginBottom: 4 }}>Book a Ride</p>
                <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>Find nearby drivers instantly</p>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: 'var(--accent-cyan)', fontSize: '0.8rem', fontFamily: 'Rajdhani, sans-serif', fontWeight: 600, letterSpacing: '0.05em' }}>
                BOOK NOW <ChevronRight size={14} />
              </div>
            </motion.div>
          </Link>

          <Link to="/rider-history" style={{ textDecoration: 'none' }}>
            <motion.div whileHover={{ y: -4, scale: 1.01 }} className="glass-card"
              style={{ padding: '28px 24px', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: 12 }}>
              <div style={{
                width: 52, height: 52, borderRadius: 'var(--radius-md)',
                background: 'rgba(124,58,237,0.08)', border: '1px solid rgba(124,58,237,0.2)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                boxShadow: '0 0 16px rgba(124,58,237,0.1)'
              }}>
                <History size={22} color="var(--accent-violet-lt)" />
              </div>
              <div>
                <p style={{ fontFamily: 'Syne, sans-serif', fontWeight: 700, fontSize: '1.05rem', color: 'var(--text-primary)', marginBottom: 4 }}>Ride History</p>
                <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>View all your past trips</p>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: 'var(--accent-violet-lt)', fontSize: '0.8rem', fontFamily: 'Rajdhani, sans-serif', fontWeight: 600, letterSpacing: '0.05em' }}>
                VIEW ALL <ChevronRight size={14} />
              </div>
            </motion.div>
          </Link>
        </div>
      </motion.div>

      {/* ── Status Card ── */}
      {!activeRide && (
        <motion.div initial="hidden" animate="visible" variants={fadeUp} custom={3} className="glass-card"
          style={{ padding: '32px', textAlign: 'center' }}>
          <div style={{ fontSize: '3rem', marginBottom: 12 }}>🚕</div>
          <p style={{ fontFamily: 'Syne, sans-serif', fontWeight: 700, fontSize: '1.1rem', marginBottom: 8, color: 'var(--text-primary)' }}>
            No Active Ride
          </p>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem', marginBottom: 20 }}>
            Ready for your next journey? Book a premium ride in seconds.
          </p>
          <Link to="/book-ride" className="btn btn-primary">
            Find a Driver <ChevronRight size={16} />
          </Link>
        </motion.div>
      )}

      {/* ── Penalty Warning ── */}
      {profile?.penality > 0 && (
        <motion.div initial="hidden" animate="visible" variants={fadeUp} custom={4}
          className="alert alert-warning" style={{ marginTop: 24, display: 'flex', gap: 12, alignItems: 'flex-start' }}>
          <AlertTriangle size={18} style={{ flexShrink: 0, marginTop: 2 }} />
          <div>
            <strong>Account Notice:</strong> You have {profile.penality} cancellation mark{profile.penality > 1 ? 's' : ''} on your profile.
            Frequent cancellations may lead to account suspension.
          </div>
        </motion.div>
      )}
    </div>
  );
};

export default RiderDashboard;
