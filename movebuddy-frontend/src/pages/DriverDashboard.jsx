import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { TrendingUp, CheckCircle, Star, Power, MapPin, ChevronRight, Key } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { driverAPI } from '../services/api';
import toast from 'react-hot-toast';

const fadeUp = {
  hidden: { opacity: 0, y: 24 },
  visible: (i = 0) => ({ opacity: 1, y: 0, transition: { duration: 0.5, delay: i * 0.1, ease: 'easeOut' } })
};

const DriverDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [incomingRides, setIncomingRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isOnline, setIsOnline] = useState(false);
  const [activeRide, setActiveRide] = useState(null);
  const [otpInput, setOtpInput] = useState('');
  const [stats, setStats] = useState({ earnings: 0, rides: 0 });
  const [history, setHistory] = useState([]);

  const fetchData = async () => {
    try {
      const profileRes = await driverAPI.getProfile(user.mobileNo);
      const data = profileRes.data.data;
      setProfile(data);
      setIsOnline(data.status === 'Available');

      const historyRes = await driverAPI.getBookingHistory(user.mobileNo);
      if (historyRes.data.statuscode === 200) {
        const histData = historyRes.data.data;
        const histList = histData.history || [];
        setHistory(histList);
        setStats({
          earnings: histData.totalAmount || 0,
          rides: histList.filter(r => r.status === 'COMPLETED').length || 0
        });
      }

      const ridesRes = await driverAPI.getIncomingRides(user.mobileNo);
      const rides = ridesRes.data.data;
      const ongoing = rides.find(r => r.bookingStatus === 'IN_PROGRESS' || (r.bookingStatus === 'booked' && r.vehicle?.id === data.vehicle?.id));
      if (ongoing) { setActiveRide(ongoing); }
      else { setIncomingRides(rides.filter(r => r.bookingStatus === 'booked')); }
    } catch (err) {
      console.error('Driver data error', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 10000);
    return () => clearInterval(interval);
  }, [user.mobileNo]);

  const handleToggleOnline = async () => {
    try {
      await driverAPI.toggleAvailability(user.mobileNo);
      setIsOnline(p => !p);
      toast.success(!isOnline ? 'You are now Online!' : 'You are now Offline');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to toggle status.');
    }
  };

  const handleStartRide = async (bookingId) => {
    if (!otpInput) { toast.error('Enter the OTP from customer.'); return; }
    try {
      await driverAPI.validateOtp(bookingId, otpInput);
      toast.success('OTP Verified! Drive safe. 🚗');
      setOtpInput('');
      fetchData();
    } catch { toast.error('Invalid OTP. Please check with customer.'); }
  };

  const handleCompleteRide = async (bookingId) => {
    try {
      await driverAPI.completeRide(bookingId, 'CASH');
      toast.success('Ride Completed! Payment processed. 💰');
      setActiveRide(null);
      fetchData();
    } catch { toast.error('Failed to complete ride.'); }
  };

  if (loading) return <div className="loading-wrap"><div className="spinner" /></div>;

  return (
    <div className="container" style={{ paddingBottom: 60 }}>

      {/* ── Header ── */}
      <motion.div initial="hidden" animate="visible" variants={fadeUp}
        className="page-header flex justify-between items-center" style={{ flexWrap: 'wrap', gap: 16 }}>
        <div>
          <span className="section-label">Driver Console</span>
          <h1 className="page-title">{profile?.name}</h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem', display: 'flex', alignItems: 'center', gap: 6 }}>
            <MapPin size={13} color="var(--accent-cyan)" />
            {profile?.vehicle?.vehicleName} · {profile?.vehicle?.vehicleNo}
          </p>
        </div>

        {/* Online Toggle */}
        <motion.button whileTap={{ scale: 0.96 }} onClick={handleToggleOnline}
          className={`btn ${isOnline ? 'btn-success' : 'btn-secondary'}`}
          style={{ gap: 10, minWidth: 140, position: 'relative', overflow: 'hidden' }}>
          <Power size={16} />
          {isOnline ? 'ONLINE' : 'OFFLINE'}
          {isOnline && (
            <span style={{
              position: 'absolute', top: 6, right: 10,
              width: 8, height: 8, borderRadius: '50%',
              background: 'var(--accent-emerald)',
              boxShadow: '0 0 8px var(--accent-emerald)',
              animation: 'activePulse 2s infinite'
            }} />
          )}
        </motion.button>
      </motion.div>

      {/* ── Stats Grid ── */}
      <motion.div initial="hidden" animate="visible" variants={fadeUp} custom={1}
        style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 16, marginBottom: 28 }}>
        {[
          { icon: TrendingUp, label: 'Total Earnings', value: `₹${stats.earnings.toLocaleString()}`, color: 'var(--accent-cyan)' },
          { icon: CheckCircle, label: 'Rides Done', value: stats.rides, color: 'var(--accent-emerald)' },
          { icon: Star, label: 'Rating', value: '5.0 ★', color: 'var(--accent-gold)' },
        ].map(({ icon: Icon, label, value, color }) => (
          <div key={label} className="glass-card stat-card" style={{ flexDirection: 'row', alignItems: 'center', gap: 16, padding: '20px 20px' }}>
            <div style={{ width: 44, height: 44, borderRadius: 'var(--radius-md)', background: `rgba(${color === 'var(--accent-cyan)' ? '0,212,255' : color === 'var(--accent-emerald)' ? '16,185,129' : '245,158,11'},0.08)`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
              <Icon size={20} color={color} />
            </div>
            <div>
              <div style={{ fontFamily: "'DM Sans', sans-serif", fontWeight: 300, fontSize: '1.6rem', color: 'var(--text-primary)', lineHeight: 1, letterSpacing: '-0.02em' }}>{value}</div>
              <div className="stat-label" style={{ marginTop: 4 }}>{label}</div>
            </div>
          </div>
        ))}
      </motion.div>

      {/* ── Active Ride / Incoming Rides ── */}
      <motion.div initial="hidden" animate="visible" variants={fadeUp} custom={2} style={{ marginBottom: 28 }}>
        {activeRide ? (
          <div className="glass-card glass-card-cyan" style={{ padding: '28px 32px' }}>
            <span className="badge badge-primary" style={{ marginBottom: 16 }}>● Active Ride</span>
            <div className="flex justify-between items-start" style={{ flexWrap: 'wrap', gap: 20 }}>
              <div>
                <h2 style={{ fontFamily: 'Syne,sans-serif', fontWeight: 700, fontSize: '1.3rem', color: 'var(--text-primary)', marginBottom: 4 }}>
                  {activeRide.customer?.name}
                </h2>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginBottom: 16 }}>{activeRide.customer?.mobileNo}</p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <span style={{ color: 'var(--accent-emerald)', fontSize: '0.7rem' }}>⬤ FROM</span>
                    <span style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>{activeRide.sourceLoc}</span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <span style={{ color: 'var(--accent-rose)', fontSize: '0.7rem' }}>⬤ TO</span>
                    <span style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>{activeRide.destinationLoc}</span>
                  </div>
                </div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <p style={{ fontSize: '0.72rem', fontFamily: 'Rajdhani,sans-serif', letterSpacing: '0.1em', color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: 4 }}>Expected Fare</p>
                <p style={{ fontFamily: "'DM Sans', sans-serif", fontWeight: 300, fontSize: '2.2rem', background: 'var(--gradient-text)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text', letterSpacing: '-0.02em', lineHeight: 1 }}>₹{activeRide.fare}</p>
              </div>
            </div>

            <div className="border-top pt-6 mt-6">
              {activeRide.bookingStatus === 'booked' ? (
                <div className="flex gap-4" style={{ flexWrap: 'wrap' }}>
                  <div style={{ position: 'relative', flex: 1, minWidth: 160 }}>
                    <Key size={15} color="var(--text-muted)" style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)' }} />
                    <input className="form-input" style={{ paddingLeft: 40 }} type="text"
                      placeholder="Enter Customer OTP" value={otpInput}
                      onChange={e => setOtpInput(e.target.value)} />
                  </div>
                  <button className="btn btn-primary" onClick={() => handleStartRide(activeRide.id)}>
                    Verify & Start Ride <ChevronRight size={16} />
                  </button>
                </div>
              ) : (
                <button className="btn btn-success btn-block btn-lg" onClick={() => handleCompleteRide(activeRide.id)}>
                  <CheckCircle size={18} /> Finish Ride & Collect Cash
                </button>
              )}
            </div>
          </div>
        ) : (
          <div className="glass-card" style={{ padding: '24px 28px' }}>
            <div className="flex justify-between items-center" style={{ marginBottom: 20 }}>
              <h3 style={{ fontFamily: 'Syne,sans-serif', fontWeight: 700, color: 'var(--text-primary)' }}>
                Incoming Requests <span className="badge badge-primary" style={{ marginLeft: 8 }}>{incomingRides.length}</span>
              </h3>
            </div>
            {!isOnline ? (
              <div style={{ textAlign: 'center', padding: '32px 0' }}>
                <div style={{ fontSize: '2.5rem', marginBottom: 12 }}>⚡</div>
                <p style={{ fontFamily: 'Syne,sans-serif', fontWeight: 700, color: 'var(--text-primary)', marginBottom: 8 }}>Go Online to Accept Rides</p>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem' }}>Toggle the switch above to start receiving ride requests in {profile?.vehicle?.currentCity}.</p>
              </div>
            ) : incomingRides.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '32px 0' }}>
                <div className="spinner" style={{ margin: '0 auto 16px' }} />
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem' }}>Searching for rides in {profile?.vehicle?.currentCity}...</p>
              </div>
            ) : (
              <div className="table-wrap">
                <table>
                  <thead><tr><th>Customer</th><th>Route</th><th>Dist</th><th>Fare</th><th>Action</th></tr></thead>
                  <tbody>
                    {incomingRides.map(ride => (
                      <tr key={ride.id}>
                        <td style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{ride.customer?.name}</td>
                        <td>
                          <div style={{ fontSize: '0.8rem' }}>
                            <span style={{ color: 'var(--accent-emerald)' }}>●</span> {ride.sourceLoc?.substring(0, 18)}...<br />
                            <span style={{ color: 'var(--accent-rose)' }}>●</span> {ride.destinationLoc?.substring(0, 18)}...
                          </div>
                        </td>
                        <td>{ride.distanceTravelled} km</td>
                        <td style={{ fontFamily: "'DM Sans', sans-serif", fontWeight: 300, color: 'var(--accent-cyan)', letterSpacing: '-0.01em' }}>₹{ride.fare}</td>
                        <td><button className="btn btn-primary btn-sm" onClick={() => setActiveRide(ride)}>Accept</button></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </motion.div>

      {/* ── Recent Rides History ── */}
      <motion.div initial="hidden" animate="visible" variants={fadeUp} custom={3}>
        <div className="flex justify-between items-center" style={{ marginBottom: 16 }}>
          <span className="section-label" style={{ marginBottom: 0 }}>Recent Rides</span>
          <button className="btn-link" onClick={() => navigate('/driver-history')}>View All →</button>
        </div>

        <div className="glass-card" style={{ padding: '8px 0' }}>
          {history.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '32px', color: 'var(--text-secondary)', fontSize: '0.88rem' }}>
              No ride history yet. Complete your first ride to see it here.
            </div>
          ) : (
            <div className="table-wrap">
              <table>
                <thead><tr><th>Customer</th><th>Route</th><th>Fare</th><th>Status</th></tr></thead>
                <tbody>
                  {history.slice(0, 5).map((ride, idx) => (
                    <tr key={ride.id || idx}>
                      <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{ride.customerName || '—'}</td>
                      <td style={{ fontSize: '0.8rem' }}>{ride.fromLoc} → {ride.toLoc}</td>
                      <td style={{ fontFamily: 'Syne,sans-serif', fontWeight: 700, color: 'var(--accent-cyan)' }}>₹{ride.fare}</td>
                      <td><span className={`badge ${ride.status === 'COMPLETED' ? 'badge-success' : 'badge-info'}`}>{ride.status}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </motion.div>
    </div>
  );
};

export default DriverDashboard;
