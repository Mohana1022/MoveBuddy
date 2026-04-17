import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { MapPin, Navigation, Clock, ChevronRight, Search, ArrowRight } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { customerAPI, bookingAPI } from '../services/api';
import toast from 'react-hot-toast';

const VEHICLE_EMOJI = { Bike: '🏍️', Auto: '🛺', Car: '🚗', SUV: '🚙', Bus: '🚌' };

const BookRidePage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [destination, setDestination] = useState('');
  const [loading, setLoading] = useState(false);
  const [bookingLoading, setBookingLoading] = useState(false);
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [showVehicles, setShowVehicles] = useState(false);
  const [rideDist, setRideDist] = useState(0);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!destination) return;
    setLoading(true);
    try {
      const response = await customerAPI.getAvailableVehicles(user.mobileNo, destination);
      if (response.data.statuscode === 200) {
        const rideData = response.data.data;
        setVehicles(rideData.availableVehicles || []);
        setRideDist(rideData.distance || 0);
        setShowVehicles(true);
        if (!rideData.availableVehicles?.length) toast.error('No vehicles found in your area.');
      } else {
        toast.error(response.data.message);
      }
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to search for vehicles.');
    } finally {
      setLoading(false);
    }
  };

  const handleBook = async () => {
    if (!selectedVehicle) { toast.error('Please select a vehicle first.'); return; }
    setBookingLoading(true);
    try {
      const response = await bookingAPI.bookVehicle(user.mobileNo, {
        vehicleid: selectedVehicle.v.id,
        sourceLoc: 'Current Location',
        destinationLoc: destination,
        distanceTravelled: Math.round(rideDist),
        fare: Math.round(selectedVehicle.fare),
        estimatedTime: Math.round(selectedVehicle.estimatedTime)
      });
      if (response.data.statuscode === 200) {
        toast.success('Ride booked successfully! 🎉');
        navigate(`/track-ride/${response.data.data.id}`);
      } else {
        toast.error(response.data.message || 'Booking failed.');
      }
    } catch (err) {
      toast.error(err.response?.data?.message || 'Booking failed.');
    } finally {
      setBookingLoading(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: 760, paddingBottom: 60 }}>

      {/* ── Header ── */}
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}
        className="page-header" style={{ padding: 'clamp(2rem, 5vw, 4rem) 0 1.5rem' }}>
        <span className="section-label">Premium Rides</span>
        <h1 className="page-title" style={{ fontSize: 'clamp(1.8rem, 5vw, 2.5rem)', fontWeight: 800 }}>Book a Ride</h1>
        <p className="page-subtitle" style={{ fontSize: 'clamp(0.85rem, 1.2vw, 1rem)' }}>Enter your destination to see available vehicles nearby.</p>
      </motion.div>

      {/* ── Search Form ── */}
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5, delay: 0.1 }}
        className="glass-card glass-card-cyan" style={{ padding: 'clamp(20px, 5vw, 32px)', marginBottom: 28 }}>

        {/* Route visualiser */}
        <div style={{ display: 'flex', alignItems: 'stretch', gap: 16, marginBottom: 24 }}>
          {/* Left — icon column (hidden on very small screens if needed, but flex-shrink 0 keeps it) */}
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, paddingTop: 14, flexShrink: 0 }}>
            <div style={{ width: 10, height: 10, borderRadius: '50%', background: 'var(--accent-cyan)', boxShadow: '0 0 10px var(--accent-cyan)', animation: 'pinPulse 2s infinite' }} />
            <div style={{ width: 1, flex: 1, background: 'linear-gradient(to bottom, rgba(0,212,255,0.4), rgba(124,58,237,0.4))', borderLeft: '1px dashed rgba(255,255,255,0.15)' }} />
            <div style={{ width: 10, height: 10, borderRadius: '2px', background: 'var(--accent-rose)', boxShadow: '0 0 10px var(--accent-rose)' }} />
          </div>

          {/* Right — input column */}
          <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 18 }}>
            <div style={{ position: 'relative' }}>
              <Navigation size={15} color="var(--accent-cyan)" style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)' }} />
              <input className="form-input" style={{ paddingLeft: 40, opacity: 0.6, fontSize: '0.9rem' }}
                value="Current Location" readOnly />
            </div>

            <form onSubmit={handleSearch} style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
              <div style={{ flex: '1 1 240px', position: 'relative' }}>
                <MapPin size={15} color="var(--accent-rose)" style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)' }} />
                <input id="destination-input" className="form-input" style={{ paddingLeft: 40, fontSize: '1rem' }}
                  type="text" placeholder="Where to?" value={destination}
                  onChange={e => setDestination(e.target.value)}
                  required disabled={loading || showVehicles} />
              </div>
              {showVehicles ? (
                <button type="button" className="btn btn-secondary btn-sm-100" style={{ whiteSpace: 'nowrap', minHeight: '48px' }}
                  onClick={() => { setShowVehicles(false); setSelectedVehicle(null); setDestination(''); }}>
                  Change
                </button>
              ) : (
                <button type="submit" className="btn btn-primary btn-sm-100" disabled={loading} style={{ whiteSpace: 'nowrap', minHeight: '48px' }}>
                  {loading ? <div className="spinner spinner-sm" /> : <><Search size={15} /> Find Rides</>}
                </button>
              )}
            </form>
          </div>
        </div>

        {/* Distance badge */}
        {showVehicles && rideDist > 0 && (
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            <span className="badge badge-info"><MapPin size={11} /> {rideDist.toFixed(1)} km</span>
            <span className="badge badge-secondary" style={{ maxWidth: '100%', overflow: 'hidden', textOverflow: 'ellipsis' }}>
              <ArrowRight size={11} /> {destination}
            </span>
          </div>
        )}
      </motion.div>

      {/* ── Vehicle List ── */}
      <AnimatePresence>
        {showVehicles && (
          <motion.div key="vehicles"
            initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}
            transition={{ duration: 0.4 }}>
            <span className="section-label" style={{ display: 'block', marginBottom: 16 }}>
              {vehicles.length} Vehicle{vehicles.length !== 1 ? 's' : ''} Available
            </span>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 24 }}>
              {vehicles.map(v => {
                const isSelected = selectedVehicle?.v.id === v.v.id;
                return (
                  <motion.div key={v.v.id} whileHover={{ y: -2 }}
                    onClick={() => setSelectedVehicle(v)}
                    style={{
                      cursor: 'pointer',
                      padding: 'clamp(16px, 4vw, 24px)',
                      borderRadius: 'var(--radius-lg)',
                      background: isSelected ? 'rgba(0,212,255,0.06)' : 'var(--glass-bg)',
                      backdropFilter: 'blur(24px)',
                      border: isSelected ? '1px solid rgba(0,212,255,0.35)' : '1px solid var(--glass-border)',
                      boxShadow: isSelected ? 'var(--glass-glow-cyan)' : 'var(--glass-shadow)',
                      display: 'flex', 
                      flexWrap: 'wrap',
                      alignItems: 'center', 
                      justifyContent: 'space-between',
                      gap: 16,
                      transition: 'all 0.3s ease'
                    }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 16, minWidth: 'min(100%, 200px)' }}>
                      {/* Vehicle emoji in glowing circle */}
                      <div style={{
                        width: 52, height: 52, borderRadius: 'var(--radius-md)',
                        background: isSelected ? 'rgba(0,212,255,0.1)' : 'rgba(255,255,255,0.04)',
                        border: `1px solid ${isSelected ? 'rgba(0,212,255,0.25)' : 'rgba(255,255,255,0.06)'}`,
                        display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem', flexShrink: 0
                      }}>
                        {VEHICLE_EMOJI[v.v.type] || '🚗'}
                      </div>
                      <div>
                        <p style={{ fontFamily: 'Syne,sans-serif', fontWeight: 700, fontSize: '1rem', color: 'var(--text-primary)', marginBottom: 3 }}>
                          {v.v.vehicleName || v.v.name}
                        </p>
                        <p style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>
                          {v.v.model} · {v.v.type}
                        </p>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginTop: 4 }}>
                          <Clock size={12} color="var(--text-muted)" />
                          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontFamily: 'Rajdhani,sans-serif', fontWeight: 600 }}>
                            {Math.round(v.estimatedTime)} min away
                          </span>
                        </div>
                      </div>
                    </div>
                    <div style={{ textAlign: isSelected ? 'left' : 'right', flex: '1 1 auto', display: 'flex', flexDirection: 'column', alignItems: 'flex-end', minWidth: '100px' }}>
                      <p style={{
                        fontFamily: 'Syne,sans-serif', fontWeight: 800, fontSize: 'clamp(1.2rem, 3vw, 1.8rem)',
                        background: isSelected ? 'var(--gradient-text)' : 'none',
                        WebkitBackgroundClip: isSelected ? 'text' : 'unset',
                        WebkitTextFillColor: isSelected ? 'transparent' : 'var(--text-primary)',
                        backgroundClip: isSelected ? 'text' : 'unset',
                        lineHeight: 1
                      }}>
                        ₹{Math.round(v.fare)}
                      </p>
                      <p style={{ fontSize: '0.65rem', color: 'var(--text-muted)', fontFamily: 'Rajdhani,sans-serif', letterSpacing: '0.08em', marginTop: 4, textAlign: 'right' }}>
                        EST. FARE
                      </p>
                    </div>
                  </motion.div>
                );
              })}
            </div>

            {/* Confirm Button */}
            <motion.button
              whileTap={{ scale: 0.98 }}
              className="btn btn-primary btn-lg btn-block"
              disabled={!selectedVehicle || bookingLoading}
              onClick={handleBook}
              style={{ boxShadow: selectedVehicle ? '0 0 30px rgba(0,212,255,0.3)' : 'none' }}>
              {bookingLoading
                ? <><div className="spinner spinner-sm" /> Booking...</>
                : <>Confirm Booking <ChevronRight size={18} /></>}
            </motion.button>

            {selectedVehicle && (
              <p style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.78rem', marginTop: 12, fontFamily: 'Rajdhani,sans-serif', letterSpacing: '0.08em' }}>
                ₹{Math.round(selectedVehicle.fare)} · {Math.round(selectedVehicle.estimatedTime)} min ETA · {rideDist.toFixed(1)} km
              </p>
            )}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default BookRidePage;
