import { useEffect, useMemo, useState } from 'react';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'https://cloudcost-sentinel.onrender.com';
const USER_ID = import.meta.env.VITE_DEMO_USER_ID || '1';

function formatCurrency(value) {
  const number = Number(value || 0);
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(number);
}

function App() {
  const [summary, setSummary] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [billingRecords, setBillingRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  async function loadDashboard() {
    setLoading(true);
    setError('');

    try {
      const [summaryRes, recommendationsRes, recordsRes] = await Promise.all([
        fetch(`${API_BASE}/api/dashboard/summary?userId=${USER_ID}`),
        fetch(`${API_BASE}/api/billing/recommendations?userId=${USER_ID}`),
        fetch(`${API_BASE}/api/billing?userId=${USER_ID}`)
      ]);

      if (!summaryRes.ok) {
        throw new Error('Dashboard summary API failed. Make sure demo data exists in Render.');
      }

      const summaryData = await summaryRes.json();
      const recommendationsData = recommendationsRes.ok ? await recommendationsRes.json() : [];
      const recordsData = recordsRes.ok ? await recordsRes.json() : [];

      setSummary(summaryData);
      setRecommendations(Array.isArray(recommendationsData) ? recommendationsData : []);
      setBillingRecords(Array.isArray(recordsData) ? recordsData : []);
    } catch (err) {
      setError(err.message || 'Unable to load dashboard data.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadDashboard();
  }, []);

  const costRows = useMemo(() => {
    if (!summary?.costByService) return [];
    return Object.entries(summary.costByService).sort((a, b) => Number(b[1]) - Number(a[1]));
  }, [summary]);

  const maxCost = useMemo(() => {
    if (!costRows.length) return 1;
    return Math.max(...costRows.map(([, value]) => Number(value)));
  }, [costRows]);

  return (
    <main className="page-shell">
      <section className="hero">
        <div>
          <p className="eyebrow">Cloud FinOps Dashboard</p>
          <h1>CloudCost Sentinel</h1>
          <p className="hero-text">
            Monitor cloud spend, detect idle resources, and surface optimization recommendations from billing data.
          </p>
        </div>
        <div className="hero-actions">
          <button onClick={loadDashboard} disabled={loading}>
            {loading ? 'Loading...' : 'Refresh Dashboard'}
          </button>
          <a href={`${API_BASE}/actuator/health`} target="_blank" rel="noreferrer">
            API Health
          </a>
        </div>
      </section>

      {error && (
        <section className="alert">
          <strong>Unable to load demo data.</strong>
          <span>{error}</span>
          <small>
            First create a demo user and upload sample-billing.csv to the Render backend.
          </small>
        </section>
      )}

      <section className="cards-grid">
        <MetricCard label="Total Cloud Cost" value={formatCurrency(summary?.totalCost)} helper="Current uploaded billing data" />
        <MetricCard label="Estimated Savings" value={formatCurrency(summary?.estimatedMonthlySavings)} helper="Potential savings from recommendations" />
        <MetricCard label="Idle Resources" value={summary?.idleResourceCount ?? '--'} helper="Low utilization resources" />
        <MetricCard label="Recommendations" value={summary?.recommendationCount ?? '--'} helper="Optimization actions generated" />
      </section>

      <section className="dashboard-grid">
        <div className="panel">
          <div className="panel-header">
            <div>
              <h2>Cost by Service</h2>
              <p>Cloud spend grouped across services.</p>
            </div>
          </div>

          <div className="bar-list">
            {costRows.length === 0 && <p className="empty">No cost data yet.</p>}
            {costRows.map(([service, cost]) => (
              <div className="bar-row" key={service}>
                <div className="bar-label">
                  <span>{service}</span>
                  <strong>{formatCurrency(cost)}</strong>
                </div>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${Math.max((Number(cost) / maxCost) * 100, 8)}%` }} />
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="panel">
          <div className="panel-header">
            <div>
              <h2>Top Recommendations</h2>
              <p>Rule-based optimization insights.</p>
            </div>
          </div>

          <div className="recommendation-list">
            {recommendations.length === 0 && <p className="empty">No recommendations yet.</p>}
            {recommendations.slice(0, 5).map((item) => (
              <article className="recommendation-card" key={item.billingRecordId}>
                <div>
                  <span className="pill">{item.service}</span>
                  <h3>{item.resourceId}</h3>
                  <p>{item.recommendation}</p>
                </div>
                <div className="savings">
                  <span>Save</span>
                  <strong>{formatCurrency(item.estimatedSavings)}</strong>
                </div>
              </article>
            ))}
          </div>
        </div>
      </section>

      <section className="panel full-width">
        <div className="panel-header">
          <div>
            <h2>Recent Billing Records</h2>
            <p>Sample cloud billing data stored in PostgreSQL.</p>
          </div>
        </div>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Service</th>
                <th>Resource</th>
                <th>Region</th>
                <th>Usage Hours</th>
                <th>CPU</th>
                <th>Cost</th>
              </tr>
            </thead>
            <tbody>
              {billingRecords.slice(0, 8).map((record) => (
                <tr key={record.id}>
                  <td>{record.service}</td>
                  <td>{record.resourceId}</td>
                  <td>{record.region}</td>
                  <td>{record.usageHours}</td>
                  <td>{record.cpuUtilization}%</td>
                  <td>{formatCurrency(record.cost)}</td>
                </tr>
              ))}
              {billingRecords.length === 0 && (
                <tr>
                  <td colSpan="6" className="empty-table">No billing records found.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
}

function MetricCard({ label, value, helper }) {
  return (
    <article className="metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
      <p>{helper}</p>
    </article>
  );
}

export default App;
