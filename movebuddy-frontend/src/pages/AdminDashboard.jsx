import React, { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import toast from 'react-hot-toast';

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [activeTab, setActiveTab] = useState('drivers'); // drivers, customers, rides
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
      setDrivers(driversRes.data.data);
      setCustomers(customersRes.data.data);
      setRides(ridesRes.data.data);
    } catch (err) {
      toast.error("Failed to load admin data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const handleSuspend = async (mobileNo, currentStatus) => {
    try {
      if (currentStatus === 'SUSPENDED') {
        await adminAPI.restoreDriver(mobileNo);
        toast.success("Driver restored.");
      } else {
        if (!window.confirm("Suspend this driver? They won't be able to accept rides.")) return;
        await adminAPI.suspendDriver(mobileNo);
        toast.success("Driver suspended.");
      }
      fetchDashboardData();
    } catch (err) {
      toast.error("Action failed.");
    }
  };

  const handleDeleteCustomer = async (mobileNo) => {
    if (!window.confirm("Permanently delete this customer account?")) return;
    try {
      await adminAPI.deleteCustomer(mobileNo);
      toast.success("Customer deleted.");
      fetchDashboardData();
    } catch (err) {
      toast.error("Delete failed.");
    }
  };

  if (loading && !stats) return <div className="loading-wrap"><div className="spinner"></div></div>;

  return (
    <div className="container">
      <div className="page-header">
        <h1 className="page-title">Admin Control Panel</h1>
        <p className="page-subtitle">Manage users, drivers and system-wide rides</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <span className="stat-label">Total Users</span>
          <span className="stat-value">{stats?.totalUsers || 0}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Total Drivers</span>
          <span className="stat-value">{stats?.totalDrivers || 0}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Total Rides</span>
          <span className="stat-value">{stats?.totalRides || 0}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Completed</span>
          <span className="stat-value">{stats?.completedRides || 0}</span>
        </div>
      </div>

      <div className="card mt-6">
        <div style={{ display: 'flex', gap: '8px', borderBottom: '1px solid var(--border)', marginBottom: '20px' }}>
          <button 
            className={`btn btn-sm ${activeTab === 'drivers' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('drivers')}
          >
            Drivers
          </button>
          <button 
            className={`btn btn-sm ${activeTab === 'customers' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('customers')}
          >
            Customers
          </button>
          <button 
            className={`btn btn-sm ${activeTab === 'rides' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('rides')}
          >
            Recent Rides
          </button>
        </div>

        {activeTab === 'drivers' && (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Driver Name</th>
                  <th>Vehicle</th>
                  <th>Mobile</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {drivers.map(d => (
                  <tr key={d.id}>
                    <td>{d.name}</td>
                    <td>{d.vehicle?.vehicleName} ({d.vehicle?.vehicleNo})</td>
                    <td>{d.mobileno}</td>
                    <td>
                      <span className={`badge ${d.status === 'SUSPENDED' ? 'badge-error' : 'badge-success'}`}>
                        {d.status}
                      </span>
                    </td>
                    <td>
                      <button 
                        className={`btn btn-sm ${d.status === 'SUSPENDED' ? 'btn-success' : 'btn-danger'}`}
                        onClick={() => handleSuspend(d.mobileno, d.status)}
                      >
                        {d.status === 'SUSPENDED' ? 'Restore' : 'Suspend'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {activeTab === 'customers' && (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Mobile</th>
                  <th>Email</th>
                  <th>Rides</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {customers.map(c => (
                  <tr key={c.id}>
                    <td>{c.name}</td>
                    <td>{c.mobileNo || c.mobileno}</td>
                    <td>{c.emailId || c.mailId}</td>
                    <td>{c.bookinglist?.length || 0}</td>
                    <td>
                      <button 
                        className="btn btn-sm btn-danger"
                        onClick={() => handleDeleteCustomer(c.mobileNo || c.mobileno)}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {activeTab === 'rides' && (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Customer</th>
                  <th>Route</th>
                  <th>Fare</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {rides.map(r => (
                  <tr key={r.id}>
                    <td>#{r.id}</td>
                    <td>{r.customer?.name}</td>
                    <td>{r.sourceLoc.substring(0,10)}... → {r.destinationLoc.substring(0,10)}...</td>
                    <td>₹{r.fare}</td>
                    <td>
                      <span className={`badge ${
                        r.bookingStatus === 'COMPLETED' ? 'badge-success' : 
                        r.bookingStatus === 'booked' ? 'badge-info' : 'badge-warning'
                      }`}>
                        {r.bookingStatus}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;
