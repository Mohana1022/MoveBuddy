import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Users, Car, TrendingUp, Activity, Shield, Trash2, RotateCcw } from 'lucide-react';
import { adminAPI } from '../services/api';
import toast from 'react-hot-toast';

const fadeUp = {
  hidden: { opacity: 0, y: 20 },
  visible: (i = 0) => ({ opacity: 1, y: 0, transition: { duration: 0.4, delay: i * 0.08 } })
};

const TABS = [
  { id: 'drivers',   label: 'Drivers',   icon: Car },
  { id: 'customers', label: 'Customers', icon: Users },
  { id: 'rides',     label: 'All Rides', icon: Activity },
];

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [activeTab, setActiveTab] = useState('drivers');
  const [drivers, setDrivers] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [statsRes, driversRes, customersRes, ridesRes] = await Promise.all([
        adminAPI.getDashboard(),
        adminAPI.getAllDrivers(),
        adminAPI.getAllCustomers(),
        adminAPI.getAllRides()
      ]);
      setStats(statsRes.data.data);
      setDrivers(driversRes.data.data || []);
      setCustomers(customersRes.data.data || []);
      setRides(ridesRes.data.data || []);
    } catch (err) {
      toast.error('Failed to load admin data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchDashboardData(); }, []);

  const handleSuspend = async (mobileNo, currentStatus) => {
    try {
      if (currentStatus === 'SUSPENDED') {
        await adminAPI.restoreDriver(mobileNo);
        toast.success('Driver restored.');
      } else {
        if (!window.confirm('Suspend this driver? They won\'t be able to accept rides.')) return;
        await adminAPI.suspendDriver(mobileNo);
        toast.success('Driver suspended.');
      }
      fetchDashboardData();
    } catch { toast.error('Action failed.'); }
  };

  const handleDeleteCustomer = async (mobileNo) => {
    if (!window.confirm('Permanently delete this customer account?')) return;
    try {
      await adminAPI.deleteCustomer(mobileNo);
      toast.success('Customer deleted.');
      fetchDashboardData();
    } catch { toast.error('Deletion failed.'); }
  };

  if (loading) return <div className="loading-wrap"><div className="spinner" /></div>;

  const statCards = [
    { icon: Car,         label: 'Total Drivers',    value: stats?.totalDrivers   ?? 0, color: 'var(--accent-cyan)' },
    { icon: Users,       label: 'Total Customers',  value: stats?.totalCustomers ?? 0, color: 'var(--accent-violet-lt)' },
    { icon: TrendingUp,  label: 'Total Rides',      value: stats?.totalRides     ?? 0, color: 'var(--accent-emerald)' },
    { icon: Activity,    label: 'Active Rides',     value: stats?.activeRides    ?? 0, color: 'var(--accent-gold)' },
  ];

  return (
    <div className="container" style={{ paddingBottom: 60 }}>

      {/* ── Header ── */}
      <motion.div initial="hidden" animate="visible" variants={fadeUp} className="page-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{
            width: 44, height: 44, borderRadius: 'var(--radius-md)',
            background: 'rgba(0,212,255,0.08)', border: '1px solid rgba(0,212,255,0.2)',
            display: 'flex', alignItems: 'center', justifyContent: 'center'
          }}>
            <Shield size={20} color="var(--accent-cyan)" />
          </div>
          <div>
            <span className="section-label" style={{ marginBottom: 2 }}>System</span>
            <h1 className="page-title" style={{ fontSize: '2rem' }}>Admin Panel</h1>
          </div>
        </div>
      </motion.div>

      {/* ── Stats ── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: 16, marginBottom: 36 }}>
        {statCards.map(({ icon: Icon, label, value, color }, i) => (
          <motion.div key={label} initial="hidden" animate="visible" variants={fadeUp} custom={i}
            className="glass-card" style={{ padding: '22px 20px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
              <div style={{ width: 42, height: 42, borderRadius: 'var(--radius-md)', background: `rgba(${color === 'var(--accent-cyan)' ? '0,212,255' : color === 'var(--accent-violet-lt)' ? '168,85,247' : color === 'var(--accent-emerald)' ? '16,185,129' : '245,158,11'},0.08)`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                <Icon size={18} color={color} />
              </div>
              <div>
                <div style={{ fontFamily: "'DM Sans', sans-serif", fontWeight: 300, fontSize: '1.8rem', color: 'var(--text-primary)', lineHeight: 1, letterSpacing: '-0.02em' }}>{value.toLocaleString()}</div>
                <div className="stat-label" style={{ marginTop: 4 }}>{label}</div>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* ── Tab Navigation ── */}
      <motion.div initial="hidden" animate="visible" variants={fadeUp} custom={4}
        style={{ display: 'flex', gap: 4, background: 'rgba(255,255,255,0.03)', borderRadius: 'var(--radius-pill)', padding: 4, border: '1px solid rgba(255,255,255,0.06)', marginBottom: 24, width: 'fit-content' }}>
        {TABS.map(({ id, label, icon: Icon }) => (
          <button key={id} onClick={() => setActiveTab(id)}
            style={{
              display: 'flex', alignItems: 'center', gap: 8, padding: '10px 20px',
              borderRadius: 'var(--radius-pill)', border: 'none', cursor: 'pointer',
              fontFamily: 'Rajdhani,sans-serif', fontWeight: 600, fontSize: '0.82rem', letterSpacing: '0.08em', textTransform: 'uppercase',
              background: activeTab === id ? 'var(--gradient-cyan-violet)' : 'transparent',
              color: activeTab === id ? 'white' : 'var(--text-secondary)',
              boxShadow: activeTab === id ? '0 0 16px rgba(0,212,255,0.2)' : 'none',
              transition: 'all 0.3s ease'
            }}>
            <Icon size={14} /> {label}
          </button>
        ))}
      </motion.div>

      {/* ── Table Content ── */}
      <motion.div key={activeTab} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}
        className="glass-card" style={{ padding: '8px 0' }}>

        {/* DRIVERS TAB */}
        {activeTab === 'drivers' && (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Driver</th><th>Mobile</th><th>Vehicle</th><th>City</th><th>Status</th><th>Action</th></tr></thead>
              <tbody>
                {drivers.map(d => (
                  <tr key={d.mobileno || d.mobileNo}>
                    <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{d.name}</td>
                    <td>{d.mobileno || d.mobileNo}</td>
                    <td style={{ fontSize: '0.82rem' }}>{d.vehicle?.vehicleName} · {d.vehicle?.vehicleNo}</td>
                    <td>{d.vehicle?.currentCity}</td>
                    <td>
                      <span className={`badge ${d.status === 'SUSPENDED' ? 'badge-error' : d.status === 'Available' ? 'badge-success' : 'badge-secondary'}`}>
                        {d.status}
                      </span>
                    </td>
                    <td>
                      <button
                        onClick={() => handleSuspend(d.mobileno || d.mobileNo, d.status)}
                        className={`btn btn-sm ${d.status === 'SUSPENDED' ? 'btn-success' : 'btn-danger'}`}>
                        {d.status === 'SUSPENDED'
                          ? <><RotateCcw size={12} /> Restore</>
                          : <><Shield size={12} /> Suspend</>}
                      </button>
                    </td>
                  </tr>
                ))}
                {drivers.length === 0 && <tr><td colSpan={6} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>No drivers found</td></tr>}
              </tbody>
            </table>
          </div>
        )}

        {/* CUSTOMERS TAB */}
        {activeTab === 'customers' && (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Customer</th><th>Mobile</th><th>Email</th><th>Rides</th><th>Penalty</th><th>Action</th></tr></thead>
              <tbody>
                {customers.map(c => (
                  <tr key={c.mobileNo}>
                    <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{c.name}</td>
                    <td>{c.mobileNo}</td>
                    <td style={{ fontSize: '0.82rem' }}>{c.emailId}</td>
                    <td style={{ fontFamily: "'DM Sans', sans-serif", fontWeight: 300, color: 'var(--accent-cyan)', letterSpacing: '-0.01em' }}>
                      {c.bookinglist?.length ?? 0}
                    </td>
                    <td>
                      {c.penality > 0
                        ? <span className="badge badge-warning">{c.penality} marks</span>
                        : <span className="badge badge-success">Clean</span>}
                    </td>
                    <td>
                      <button onClick={() => handleDeleteCustomer(c.mobileNo)} className="btn btn-sm btn-danger">
                        <Trash2 size={12} /> Delete
                      </button>
                    </td>
                  </tr>
                ))}
                {customers.length === 0 && <tr><td colSpan={6} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>No customers found</td></tr>}
              </tbody>
            </table>
          </div>
        )}

        {/* RIDES TAB */}
        {activeTab === 'rides' && (
          <div className="table-wrap">
            <table>
              <thead><tr><th>#</th><th>Customer</th><th>Driver</th><th>Route</th><th>Fare</th><th>Status</th></tr></thead>
              <tbody>
                {rides.map(r => (
                  <tr key={r.id}>
                    <td style={{ color: 'var(--text-muted)', fontFamily: 'Rajdhani,sans-serif', fontWeight: 600 }}>#{r.id}</td>
                    <td style={{ color: 'var(--text-primary)' }}>{r.customer?.name || r.customerName || '—'}</td>
                    <td>{r.vehicle?.driver?.name || r.driverName || '—'}</td>
                    <td style={{ fontSize: '0.8rem', maxWidth: 200 }}>
                      <span style={{ color: 'var(--accent-emerald)' }}>●</span> {(r.sourceLoc || '').substring(0, 16)}...
                      <br />
                      <span style={{ color: 'var(--accent-rose)' }}>●</span> {(r.destinationLoc || '').substring(0, 16)}...
                    </td>
                    <td style={{ fontFamily: "'DM Sans', sans-serif", fontWeight: 300, color: 'var(--accent-cyan)', letterSpacing: '-0.01em' }}>₹{r.fare}</td>
                    <td>
                      <span className={`badge ${r.bookingStatus === 'COMPLETED' ? 'badge-success' : r.bookingStatus === 'IN_PROGRESS' ? 'badge-primary' : r.bookingStatus?.includes('cancel') ? 'badge-error' : 'badge-secondary'}`}>
                        {r.bookingStatus}
                      </span>
                    </td>
                  </tr>
                ))}
                {rides.length === 0 && <tr><td colSpan={6} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>No rides found</td></tr>}
              </tbody>
            </table>
          </div>
        )}
      </motion.div>
    </div>
  );
};

export default AdminDashboard;
