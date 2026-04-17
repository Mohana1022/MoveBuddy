import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { motion, useInView } from 'framer-motion';
import { Shield, Zap, MapPin, Star, ChevronRight, Car, Clock, TrendingUp } from 'lucide-react';

/* ── Reusable animation variants ───────────────────────────── */
const fadeUp = {
  hidden: { opacity: 0, y: 40 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.7, ease: 'easeOut' } }
};

const stagger = {
  visible: { transition: { staggerChildren: 0.12 } }
};

/* ── Count-up hook ──────────────────────────────────────────── */
function useCountUp(target, duration = 2000) {
  const [count, setCount] = React.useState(0);
  const ref = useRef(null);
  const inView = useInView(ref, { once: true });

  useEffect(() => {
    if (!inView) return;
    let start = 0;
    const step = target / (duration / 16);
    const timer = setInterval(() => {
      start += step;
      if (start >= target) { setCount(target); clearInterval(timer); }
      else setCount(Math.floor(start));
    }, 16);
    return () => clearInterval(timer);
  }, [inView, target, duration]);

  return [count, ref];
}

/* ── Stat Card ────────────────────────────────────────────── */
function StatCard({ icon: Icon, value, label, suffix = '+' }) {
  const [count, ref] = useCountUp(value);
  return (
    <motion.div ref={ref} variants={fadeUp}
      className="glass-card"
      style={{
        display: 'flex', flexDirection: 'column', alignItems: 'center',
        textAlign: 'center', padding: '36px 20px', gap: 12
      }}>
      {/* Icon */}
      <div style={{
        width: 48, height: 48, borderRadius: '50%',
        background: 'rgba(0,212,255,0.08)',
        border: '1px solid rgba(0,212,255,0.2)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        flexShrink: 0,
        boxShadow: '0 0 16px rgba(0,212,255,0.15)'
      }}>
        <Icon size={20} color="var(--accent-cyan)" />
      </div>
      {/* Number */}
      <div style={{
        fontFamily: "'DM Sans', sans-serif",
        fontWeight: 300,
        fontSize: 'clamp(2.2rem, 4vw, 3.2rem)',
        lineHeight: 1,
        background: 'var(--gradient-text)',
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent',
        backgroundClip: 'text',
        letterSpacing: '-0.02em'
      }}>
        {count.toLocaleString()}{suffix}
      </div>
      {/* Label */}
      <div className="stat-label">{label}</div>
    </motion.div>
  );
}

/* ── Feature Card ─────────────────────────────────────────── */
function FeatureCard({ icon: Icon, title, desc, delay = 0, color = 'var(--accent-cyan)' }) {
  return (
    <motion.div variants={fadeUp} custom={delay}
      className="glass-card"
      style={{ padding: '32px 28px', cursor: 'default' }}>
      <div style={{
        width: 56, height: 56, borderRadius: 'var(--radius-md)',
        background: `rgba(${color === 'var(--accent-cyan)' ? '0,212,255' : '124,58,237'},0.08)`,
        border: `1px solid ${color === 'var(--accent-cyan)' ? 'rgba(0,212,255,0.2)' : 'rgba(124,58,237,0.2)'}`,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        marginBottom: 20,
        boxShadow: `0 0 20px ${color === 'var(--accent-cyan)' ? 'rgba(0,212,255,0.1)' : 'rgba(124,58,237,0.1)'}`
      }}>
        <Icon size={24} color={color} />
      </div>
      <h3 style={{ fontFamily: 'Syne,sans-serif', fontSize: '1.15rem', fontWeight: 700, marginBottom: 10, color: 'var(--text-primary)' }}>{title}</h3>
      <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.7 }}>{desc}</p>
    </motion.div>
  );
}

/* ── Hero Car SVG placeholder ─────────────────────────────── */
function HeroCar() {
  return (
    <div className="animate-float" style={{
      position: 'relative',
      filter: 'drop-shadow(0 20px 40px rgba(0,212,255,0.3))',
    }}>
      <svg viewBox="0 0 600 280" width="100%" xmlns="http://www.w3.org/2000/svg" style={{ maxWidth: 540 }}>
        <defs>
          <linearGradient id="bodyGrad" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stopColor="#1a2040" />
            <stop offset="100%" stopColor="#0a0f1e" />
          </linearGradient>
          <linearGradient id="glowGrad" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stopColor="#00D4FF" stopOpacity="0" />
            <stop offset="50%" stopColor="#00D4FF" stopOpacity="0.8" />
            <stop offset="100%" stopColor="#7C3AED" stopOpacity="0" />
          </linearGradient>
          <filter id="glow"><feGaussianBlur stdDeviation="3" result="blur" /><feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge></filter>
        </defs>
        {/* Car body */}
        <ellipse cx="300" cy="240" rx="260" ry="18" fill="rgba(0,212,255,0.05)" />
        <path d="M60 200 Q80 200 100 190 L180 150 Q220 120 300 118 Q380 120 420 150 L500 190 Q520 200 540 200 L540 215 Q300 230 60 215 Z" fill="url(#bodyGrad)" stroke="rgba(0,212,255,0.3)" strokeWidth="1" filter="url(#glow)" />
        {/* Roof */}
        <path d="M180 150 Q220 100 300 95 Q380 100 420 150" fill="url(#bodyGrad)" stroke="rgba(0,212,255,0.2)" strokeWidth="1" />
        {/* Windows */}
        <path d="M195 148 Q225 110 300 107 Q345 110 355 138 L320 140 Q300 115 200 148 Z" fill="rgba(0,212,255,0.06)" stroke="rgba(0,212,255,0.15)" strokeWidth="0.5" />
        <path d="M360 138 L405 148 Q395 110 360 110 Z" fill="rgba(0,212,255,0.06)" stroke="rgba(0,212,255,0.15)" strokeWidth="0.5" />
        {/* Wheels */}
        <circle cx="160" cy="215" r="32" fill="#080C17" stroke="rgba(0,212,255,0.4)" strokeWidth="2" filter="url(#glow)" />
        <circle cx="160" cy="215" r="18" fill="#0a0f1e" stroke="rgba(0,212,255,0.6)" strokeWidth="1.5" />
        <circle cx="440" cy="215" r="32" fill="#080C17" stroke="rgba(0,212,255,0.4)" strokeWidth="2" filter="url(#glow)" />
        <circle cx="440" cy="215" r="18" fill="#0a0f1e" stroke="rgba(0,212,255,0.6)" strokeWidth="1.5" />
        {/* Underglow */}
        <rect x="100" y="218" width="400" height="4" rx="2" fill="url(#glowGrad)" opacity="0.9" />
        {/* Headlights */}
        <ellipse cx="535" cy="198" rx="10" ry="5" fill="rgba(0,212,255,0.8)" filter="url(#glow)" />
        <ellipse cx="65" cy="198" rx="8" ry="4" fill="rgba(124,58,237,0.6)" filter="url(#glow)" />
        {/* Door lines */}
        <line x1="300" y1="148" x2="300" y2="210" stroke="rgba(0,212,255,0.1)" strokeWidth="1" />
        <line x1="220" y1="148" x2="215" y2="210" stroke="rgba(0,212,255,0.08)" strokeWidth="1" />
        <line x1="380" y1="148" x2="385" y2="210" stroke="rgba(0,212,255,0.08)" strokeWidth="1" />
      </svg>
    </div>
  );
}

/* ── Main Component ───────────────────────────────────────── */
const LandingPage = () => {
  return (
    <div style={{ overflowX: 'hidden' }}>

      {/* ══ HERO ══════════════════════════════════════════════ */}
      <section style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        position: 'relative',
        paddingTop: 80,
        overflow: 'hidden',
      }}>
        {/* Hero background gradient */}
        <div style={{
          position: 'absolute', inset: 0,
          background: 'radial-gradient(ellipse 80% 60% at 50% 0%, rgba(0,212,255,0.07) 0%, transparent 70%), radial-gradient(ellipse 60% 40% at 80% 50%, rgba(124,58,237,0.08) 0%, transparent 60%)',
          pointerEvents: 'none'
        }} />

        <div className="container" style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fit, minmax(min(100%, 500px), 1fr))', 
          gap: 'var(--grid-gap)', 
          alignItems: 'center', 
          paddingTop: 'clamp(2rem, 8vw, 6rem)',
          paddingBottom: 'clamp(2rem, 8vw, 6rem)'
        }}>
          {/* Left — Text */}
          <motion.div initial="hidden" animate="visible" variants={stagger}>
            <motion.span variants={fadeUp} className="section-label"> Premium Ride-Hailing</motion.span>

            <motion.h1 variants={fadeUp} style={{
              fontFamily: 'Syne, sans-serif',
              fontSize: 'var(--text-hero)',
              fontWeight: 700,
              lineHeight: 1.1,
              marginBottom: 24,
            }}>
              Ride{' '}
              <span className="text-gradient">Smarter,</span>
              <br />
              Arrive in{' '}
              <span style={{
                fontStyle: 'italic',
                background: 'var(--gradient-text)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text'
              }}>Style.</span>
            </motion.h1>

            <motion.p variants={fadeUp} style={{
              color: 'var(--text-secondary)',
              fontSize: '1.1rem',
              lineHeight: 1.8,
              marginBottom: 40,
              maxWidth: 460
            }}>
              MoveBuddy connects you to premium drivers in seconds. Experience seamless booking, real-time tracking, and transparent pricing — every single ride.
            </motion.p>

            <motion.div variants={fadeUp} style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
              <Link to="/register" className="btn btn-primary btn-lg btn-sm-100">
                Start Your Journey <ChevronRight size={18} />
              </Link>
              <Link to="/login" className="btn btn-secondary btn-lg btn-sm-100">
                Sign In
              </Link>
            </motion.div>

            {/* Trust badges */}
            <motion.div variants={fadeUp} style={{ display: 'flex', gap: 24, marginTop: 40, flexWrap: 'wrap' }}>
              {[
                { icon: Shield, text: 'Verified Drivers' },
                { icon: Zap, text: 'Instant Booking' },
                { icon: Star, text: '4.9★ Rated' },
              ].map(({ icon: Icon, text }) => (
                <div key={text} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <Icon size={14} color="var(--accent-cyan)" />
                  <span style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', fontFamily: 'Rajdhani, sans-serif', fontWeight: 600, letterSpacing: '0.05em' }}>{text}</span>
                </div>
              ))}
            </motion.div>
          </motion.div>

          {/* Right — Car Visual */}
          <motion.div
            initial={{ opacity: 0, x: 60 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.9, delay: 0.4, ease: 'easeOut' }}
            style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}
          >
            <HeroCar />
          </motion.div>
        </div>

        {/* Bottom fade */}
        <div style={{
          position: 'absolute', bottom: 0, left: 0, right: 0, height: 120,
          background: 'linear-gradient(to bottom, transparent, var(--bg-void))',
          pointerEvents: 'none'
        }} />
      </section>

      {/* ══ STATS ═════════════════════════════════════════════ */}
      <section className="container" style={{ padding: '80px 24px' }}>
        <motion.div initial="hidden" whileInView="visible" viewport={{ once: true }} variants={stagger}>
          <motion.div variants={fadeUp} style={{ textAlign: 'center', marginBottom: 48 }}>
            <span className="section-label">Our Impact</span>
            <h2 style={{ fontFamily: 'Syne,sans-serif', fontSize: 'var(--text-h2)', fontWeight: 700 }}>
              Numbers That <span className="text-gradient">Speak</span>
            </h2>
          </motion.div>

          <div className="responsive-grid">
            <StatCard icon={Car} value={10000} label="Rides Completed" />
            <StatCard icon={TrendingUp} value={200} label="Active Drivers" />
            <StatCard icon={MapPin} value={50} label="Cities Covered" />
            <StatCard icon={Star} value={49} label="Average Rating" suffix="/5★" />
          </div>
        </motion.div>
      </section>

      <hr className="section-sep" />

      {/* ══ FEATURES ══════════════════════════════════════════ */}
      <section className="container" style={{ padding: '80px 24px' }}>
        <motion.div initial="hidden" whileInView="visible" viewport={{ once: true }} variants={stagger}>
          <motion.div variants={fadeUp} style={{ textAlign: 'center', marginBottom: 56 }}>
            <span className="section-label">Why MoveBuddy</span>
            <h2 style={{ fontFamily: 'Syne,sans-serif', fontSize: 'var(--text-h2)', fontWeight: 700, maxWidth: 500, margin: '0 auto' }}>
              Everything You Need for a{' '}
              <span className="text-gradient">Perfect Ride</span>
            </h2>
          </motion.div>

          <div className="responsive-grid">
            <FeatureCard icon={Zap} title="Instant Matching" desc="Get paired with a nearby driver in under 60 seconds. Our smart algorithm finds the best match for your route." color="var(--accent-cyan)" />
            <FeatureCard icon={Shield} title="100% Verified" desc="Every driver passes rigorous background checks, license verification, and vehicle inspections before joining." color="var(--accent-violet-lt)" />
            <FeatureCard icon={MapPin} title="Live Tracking" desc="Watch your ride in real-time on an interactive map. Share your trip with family for complete peace of mind." color="var(--accent-cyan)" />
            <FeatureCard icon={Star} title="Rated Drivers" desc="Only 4.5★+ drivers stay active on our platform. Your comfort and safety is our highest priority." color="var(--accent-gold)" />
            <FeatureCard icon={Clock} title="OTP Security" desc="Each ride is secured with a unique one-time password. Only the right passenger starts the journey." color="var(--accent-violet-lt)" />
            <FeatureCard icon={TrendingUp} title="Fair Pricing" desc="Transparent, upfront fare estimates before you book. No surge surprises — just honest pricing." color="var(--accent-emerald)" />
          </div>
        </motion.div>
      </section>

      <hr className="section-sep" />

      {/* ══ CTA BANNER ════════════════════════════════════════ */}
      <section style={{ padding: '100px 24px', textAlign: 'center', position: 'relative', overflow: 'hidden' }}>
        <div style={{
          position: 'absolute', inset: 0,
          background: 'radial-gradient(ellipse 70% 80% at 50% 50%, rgba(0,212,255,0.06) 0%, transparent 70%)',
          pointerEvents: 'none'
        }} />

        <motion.div initial="hidden" whileInView="visible" viewport={{ once: true }} variants={stagger}
          className="container" style={{ maxWidth: 680, margin: '0 auto' }}>

          <motion.span variants={fadeUp} className="section-label">Ready to Roll?</motion.span>

          <motion.h2 variants={fadeUp} style={{
            fontFamily: 'Syne,sans-serif',
            fontSize: 'clamp(2rem,4vw,3.5rem)',
            fontWeight: 700,
            marginBottom: 20,
            lineHeight: 1.1
          }}>
            Start Your Journey <span className="text-gradient">Today</span>
          </motion.h2>

          <motion.p variants={fadeUp} style={{ color: 'var(--text-secondary)', fontSize: '1rem', marginBottom: 40, lineHeight: 1.8 }}>
            Join thousands of happy riders who trust MoveBuddy for every trip. Sign up in 30 seconds and book your first ride.
          </motion.p>

          <motion.div variants={fadeUp} style={{ display: 'flex', gap: 16, justifyContent: 'center', flexWrap: 'wrap' }}>
            <Link to="/register" className="btn btn-primary btn-lg btn-sm-100">
              Create Free Account <ChevronRight size={18} />
            </Link>
            <Link to="/register?type=driver" className="btn btn-secondary btn-lg btn-sm-100">
              Become a Driver
            </Link>
          </motion.div>
        </motion.div>
      </section>

      {/* Footer */}
      <footer style={{
        padding: '32px 24px',
        textAlign: 'center',
        borderTop: '1px solid rgba(255,255,255,0.05)',
        color: 'var(--text-muted)',
        fontSize: '0.82rem',
        fontFamily: 'Rajdhani, sans-serif',
        letterSpacing: '0.08em'
      }}>
        © 2026 MOVEBUDDY — ALL RIGHTS RESERVED &nbsp;•&nbsp; RIDE SMARTER
      </footer>
    </div>
  );
};

export default LandingPage;
