import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Phone, Lock, ChevronRight, LogIn } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import toast from 'react-hot-toast';

const LoginPage = () => {
  const [form, setForm] = useState({ mobileNo: '', password: '' });
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await authAPI.login({ mobileNo: parseInt(form.mobileNo), password: form.password });
      const { token, role, mobileNo } = res.data.data;
      login({ mobileNo, role }, token);
      toast.success('Welcome back!');
      if (role === 'CUSTOMER') navigate('/rider-dashboard');
      else if (role === 'DRIVER') navigate('/driver-dashboard');
      else navigate('/admin-dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '100px 24px 40px',
    }}>
      {/* Background glow */}
      <div style={{
        position: 'fixed', inset: 0, pointerEvents: 'none',
        background: 'radial-gradient(ellipse 60% 60% at 50% 50%, rgba(0,212,255,0.04) 0%, transparent 70%)'
      }} />

      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: 'easeOut' }}
        style={{ width: '100%', maxWidth: 440 }}
      >
        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: 40 }}>
          <Link to="/" style={{ textDecoration: 'none' }}>
            <div style={{
              fontFamily: 'Syne, sans-serif', fontWeight: 800, fontSize: '1.8rem',
              background: 'var(--gradient-text)',
              WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text'
            }}>MoveBuddy</div>
          </Link>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: 6, fontFamily: 'Rajdhani, sans-serif', letterSpacing: '0.1em', textTransform: 'uppercase' }}>Welcome Back</p>
        </div>

        <div className="glass-card glass-card-cyan" style={{ padding: '36px 32px' }}>
          <h1 style={{ fontFamily: 'Syne, sans-serif', fontSize: '1.6rem', fontWeight: 700, marginBottom: 8, color: 'var(--text-primary)' }}>Sign In</h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem', marginBottom: 32 }}>Enter your credentials to access your account.</p>

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
            <div className="form-group">
              <label className="form-label">Mobile Number</label>
              <div style={{ position: 'relative' }}>
                <Phone size={16} color="var(--text-muted)" style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)' }} />
                <input
                  id="login-mobile"
                  className="form-input"
                  style={{ paddingLeft: 42 }}
                  type="number"
                  placeholder="10-digit mobile number"
                  value={form.mobileNo}
                  onChange={e => setForm({ ...form, mobileNo: e.target.value })}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <div style={{ position: 'relative' }}>
                <Lock size={16} color="var(--text-muted)" style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)' }} />
                <input
                  id="login-password"
                  className="form-input"
                  style={{ paddingLeft: 42 }}
                  type="password"
                  placeholder="Your password"
                  value={form.password}
                  onChange={e => setForm({ ...form, password: e.target.value })}
                  required
                />
              </div>
            </div>

            <button type="submit" className="btn btn-primary btn-lg btn-block" disabled={loading} style={{ marginTop: 8 }}>
              {loading
                ? <><div className="spinner spinner-sm" /> Signing in...</>
                : <><LogIn size={18} /> Sign In <ChevronRight size={16} /></>}
            </button>
          </form>

          <hr className="section-sep" style={{ margin: '28px 0' }} />

          <p style={{ textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.88rem' }}>
            Don't have an account?{' '}
            <Link to="/register" style={{ color: 'var(--accent-cyan)', textDecoration: 'none', fontWeight: 500 }}>
              Create one →
            </Link>
          </p>
        </div>
      </motion.div>
    </div>
  );
};

export default LoginPage;
