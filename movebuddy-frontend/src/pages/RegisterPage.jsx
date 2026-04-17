import React, { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { User, Phone, Lock, Mail, Car, Shield, ChevronRight } from 'lucide-react';
import { authAPI } from '../services/api';
import toast from 'react-hot-toast';

const fadeUp = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.45, ease: 'easeOut' } },
  exit: { opacity: 0, y: -10, transition: { duration: 0.25 } }
};

/* Reusable labelled input */
function Field({ label, icon: Icon, type = 'text', name, value, onChange, placeholder, ...rest }) {
  return (
    <div className="form-group">
      <label className="form-label">{label}</label>
      <div style={{ position: 'relative' }}>
        {Icon && <Icon size={15} color="var(--text-muted)" style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)', pointerEvents: 'none' }} />}
        <input
          className="form-input"
          style={{ paddingLeft: Icon ? 42 : 16 }}
          type={type} name={name} value={value}
          onChange={onChange} placeholder={placeholder}
          {...rest}
        />
      </div>
    </div>
  );
}

function SelectField({ label, name, value, onChange, children }) {
  return (
    <div className="form-group">
      <label className="form-label">{label}</label>
      <div style={{ position: 'relative' }}>
        <select className="form-input" style={{ fontSize: '1rem' }} name={name} value={value} onChange={onChange}>{children}</select>
      </div>
    </div>
  );
}

const RegisterPage = () => {
  const query = new URLSearchParams(useLocation().search);
  const initialRole = query.get('role') === 'DRIVER' || query.get('type') === 'driver' ? 'DRIVER' : 'CUSTOMER';

  const [role, setRole] = useState(initialRole);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '', age: '', gender: 'Male',
    mobileNo: '', email: '', password: '',
    latitude: '12.9716', longitude: '77.5946',
    licenseNo: '', upiID: '',
    vehicleName: '', vehicleNo: '', vehicleType: 'Bike',
    model: '', vehicleCapacity: 1, pricePerKM: 15, averageSpeed: 40
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (role === 'CUSTOMER') {
        await authAPI.registerCustomer({
          name: formData.name, age: parseInt(formData.age),
          gender: formData.gender, mobileNo: parseInt(formData.mobileNo),
          emailId: formData.email, password: formData.password,
          latitude: formData.latitude, longitude: formData.longitude
        });
        toast.success('Rider account created! Please login.');
      } else {
        await authAPI.registerDriver({
          ...formData,
          driverName: formData.name, mailId: formData.email,
          age: parseInt(formData.age), mobileNo: parseInt(formData.mobileNo),
          licenseNo: parseInt(formData.licenseNo),
          vehicleCapacity: parseInt(formData.vehicleCapacity),
          pricePerKM: parseInt(formData.pricePerKM),
          averageSpeed: parseInt(formData.averageSpeed)
        });
        toast.success('Driver account created! Please login.');
      }
      navigate('/login');
    } catch (error) {
      const errorData = error.response?.data;
      const errorMsg = errorData?.message || 'Registration failed';
      if (errorData?.data && typeof errorData.data === 'object') {
        toast.error(Object.values(errorData.data)[0]);
      } else {
        toast.error(errorMsg);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'flex-start', justifyContent: 'center', padding: '100px 24px 60px' }}>
      {/* Background glow */}
      <div style={{ position: 'fixed', inset: 0, pointerEvents: 'none', background: 'radial-gradient(ellipse 60% 50% at 50% 30%, rgba(124,58,237,0.05) 0%, transparent 70%)' }} />

      <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, ease: 'easeOut' }} style={{ width: '100%', maxWidth: 600 }}>

        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: 36 }}>
          <Link to="/" style={{ textDecoration: 'none' }}>
            <div style={{ 
              fontFamily: 'Syne, sans-serif', fontWeight: 800, fontSize: 'clamp(1.5rem, 4vw, 2rem)', 
              background: 'var(--gradient-text)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text',
              textTransform: 'uppercase', letterSpacing: '0.05em'
            }}>
              MoveBuddy
            </div>
          </Link>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem', marginTop: 8, fontFamily: 'Rajdhani, sans-serif', letterSpacing: '0.12em', textTransform: 'uppercase' }}>
            Create Your Account
          </p>
        </div>

        <div className="glass-card" style={{ padding: 'clamp(24px, 5vw, 40px) clamp(20px, 4vw, 32px)' }}>
          {/* Role Toggle */}
          <div style={{ 
            display: 'flex', 
            flexDirection: 'row',
            background: 'rgba(255,255,255,0.03)', 
            borderRadius: 'var(--radius-pill)', 
            padding: 4, 
            marginBottom: 32, 
            border: '1px solid rgba(255,255,255,0.06)',
            flexWrap: 'wrap',
            gap: 4
          }}>
            {[
              { value: 'CUSTOMER', label: 'Rider', icon: User },
              { value: 'DRIVER', label: 'Driver', icon: Car },
            ].map(({ value, label }) => (
              <button
                key={value} type="button"
                onClick={() => setRole(value)}
                className={role === value ? 'btn btn-primary' : 'btn'}
                style={{
                  borderRadius: 'var(--radius-pill)',
                  flex: '1 1 120px',
                  background: role === value ? 'var(--gradient-cyan-violet)' : 'transparent',
                  boxShadow: role === value ? '0 0 20px rgba(0,212,255,0.2)' : 'none',
                  color: role === value ? 'white' : 'var(--text-secondary)',
                  border: 'none',
                  fontSize: '0.88rem',
                  transition: 'all 0.3s ease',
                  padding: '10px 16px'
                }}
              >
                {label}
              </button>
            ))}
          </div>

          <form onSubmit={handleSubmit}>
            {/* ── Shared fields ── */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16, marginBottom: 16 }}>
              <Field label="Full Name" icon={User} name="name" value={formData.name} onChange={handleChange} placeholder="Your full name" required />
              <Field label="Mobile Number" icon={Phone} type="number" name="mobileNo" value={formData.mobileNo} onChange={handleChange} placeholder="10-digit number" required />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16, marginBottom: 16 }}>
              <Field label="Age" type="number" name="age" value={formData.age} onChange={handleChange} placeholder="Age" required />
              <SelectField label="Gender" name="gender" value={formData.gender} onChange={handleChange}>
                <option>Male</option><option>Female</option><option>Other</option>
              </SelectField>
            </div>

            <div style={{ marginBottom: 16 }}>
              <Field label="Email Address" icon={Mail} type="email" name="email" value={formData.email} onChange={handleChange} placeholder="email@example.com" required />
            </div>

            <div style={{ marginBottom: 24 }}>
              <Field label="Password" icon={Lock} type="password" name="password" value={formData.password} onChange={handleChange} placeholder="Create a strong password" required />
            </div>

            {/* ── Driver-only fields ── */}
            <AnimatePresence>
              {role === 'DRIVER' && (
                <motion.div key="driver-fields" variants={fadeUp} initial="hidden" animate="visible" exit="exit">
                  <hr className="section-sep" style={{ margin: '0 0 24px' }} />
                  <span className="section-label" style={{ display: 'block', marginBottom: 20 }}>🚕 Vehicle Details</span>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16, marginBottom: 16 }}>
                    <Field label="License No" icon={Shield} type="number" name="licenseNo" value={formData.licenseNo} onChange={handleChange} placeholder="License number" required />
                    <Field label="UPI ID" name="upiID" value={formData.upiID} onChange={handleChange} placeholder="upi@bank" required />
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16, marginBottom: 16 }}>
                    <Field label="Vehicle Name" icon={Car} name="vehicleName" value={formData.vehicleName} onChange={handleChange} placeholder="e.g. Honda City" required />
                    <Field label="Vehicle Number" name="vehicleNo" value={formData.vehicleNo} onChange={handleChange} placeholder="MH 01 AB 1234" required />
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16, marginBottom: 16 }}>
                    <SelectField label="Vehicle Type" name="vehicleType" value={formData.vehicleType} onChange={handleChange}>
                      <option>Bike</option><option>Auto</option><option>Car</option><option>SUV</option><option>Bus</option>
                    </SelectField>
                    <Field label="Model" name="model" value={formData.model} onChange={handleChange} placeholder="e.g. 2022" required />
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: 16, marginBottom: 24 }}>
                    <Field label="Capacity" type="number" name="vehicleCapacity" value={formData.vehicleCapacity} onChange={handleChange} />
                    <Field label="Price/KM" type="number" name="pricePerKM" value={formData.pricePerKM} onChange={handleChange} />
                    <Field label="Avg Speed" type="number" name="averageSpeed" value={formData.averageSpeed} onChange={handleChange} />
                  </div>
                </motion.div>
              )}
            </AnimatePresence>

            <button type="submit" className="btn btn-primary btn-lg btn-block" disabled={loading}>
              {loading
                ? <><div className="spinner spinner-sm" /> Creating Account...</>
                : <>{role === 'CUSTOMER' ? 'Create Rider Account' : 'Register as Driver'} <ChevronRight size={16} /></>}
            </button>
          </form>

          <hr className="section-sep" style={{ margin: '28px 0' }} />

          <p style={{ textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.88rem' }}>
            Already have an account?{' '}
            <Link to="/login" style={{ color: 'var(--accent-cyan)', textDecoration: 'none', fontWeight: 500 }}>Sign in →</Link>
          </p>
        </div>
      </motion.div>
    </div>
  );
};

export default RegisterPage;
