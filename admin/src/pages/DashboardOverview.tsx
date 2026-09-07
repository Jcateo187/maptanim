import React, { useEffect, useState } from 'react';
import {
  Users, Sprout, MessageSquare, Flag,
  Activity, RefreshCw, AlertTriangle,
  CheckCircle2, Clock, BarChart3,
  ChevronRight, Smartphone, Heart, BookOpen, Pin, Calendar,
} from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Cell,
  PieChart,
  Pie,
} from 'recharts';
import { apiService } from '../services/api';
import { DashboardStats, FeedbackItem, CommunityReport, CommunityPost } from '../types';
import { MOCK_STATS } from '../services/mockData';

// ─────────────────────────────────────────────────────────────────────────────
// Stat Card
// ─────────────────────────────────────────────────────────────────────────────
interface StatCardProps {
  icon: React.ReactNode;
  label: string;
  value: string | number;
  sub?: string;
  accent: string;
  badge?: { text: string; color: string };
  isLoading?: boolean;
}
const StatCard: React.FC<StatCardProps> = ({ icon, label, value, sub, accent, badge, isLoading }) => (
  <div
    className="rounded-2xl p-5 flex items-center gap-4 border transition-all duration-200 hover:scale-[1.02] shadow-lg"
    style={{ background: 'linear-gradient(135deg,#182933 0%,#1a2d3a 100%)', borderColor: accent + '33' }}
  >
    <div
      className="w-12 h-12 rounded-xl flex items-center justify-center shrink-0"
      style={{ background: accent + '22', border: '1px solid ' + accent + '44' }}
    >
      <div style={{ color: accent }}>{icon}</div>
    </div>
    <div className="flex-1 min-w-0">
      <p className="text-xs font-semibold text-[#8A9BA8] truncate">{label}</p>
      {isLoading
        ? <div className="h-8 w-16 bg-[#223B49] rounded-lg animate-pulse mt-1" />
        : <h3 className="text-2xl font-black text-white tracking-tight leading-tight">{value}</h3>
      }
      {sub && <p className="text-[11px] text-[#5F7586] mt-0.5 truncate">{sub}</p>}
    </div>
    {badge && (
      <span
        className="px-2.5 py-0.5 rounded-full text-[11px] font-extrabold shrink-0"
        style={{ background: badge.color + '22', color: badge.color, border: '1px solid ' + badge.color + '44' }}
      >
        {badge.text}
      </span>
    )}
  </div>
);

// ─────────────────────────────────────────────────────────────────────────────
// Status Badge helper
// ─────────────────────────────────────────────────────────────────────────────
const statusBadge = (status: string) => {
  const map: Record<string, { bg: string; text: string; label: string }> = {
    PENDING: { bg: '#f59e0b22', text: '#f59e0b', label: 'Pending' },
    IN_PROGRESS: { bg: '#06b6d422', text: '#06b6d4', label: 'In Progress' },
    RESOLVED: { bg: '#00E67622', text: '#00E676', label: 'Resolved' },
    INVESTIGATING: { bg: '#8b5cf622', text: '#8b5cf6', label: 'Investigating' },
    DISMISSED: { bg: '#5F758622', text: '#5F7586', label: 'Dismissed' },
  };
  const s = map[status] || map['PENDING'];
  return (
    <span className="px-2 py-0.5 rounded-full text-[10px] font-bold" style={{ background: s.bg, color: s.text }}>
      {s.label}
    </span>
  );
};

// ─────────────────────────────────────────────────────────────────────────────
// Main Dashboard
// ─────────────────────────────────────────────────────────────────────────────
export const DashboardOverview: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>(MOCK_STATS);
  const [loading, setLoading] = useState(true);
  const [lastRefreshed, setLastRefreshed] = useState('');
  const [feedbackList, setFeedbackList] = useState<FeedbackItem[]>([]);
  const [reportList, setReportList] = useState<CommunityReport[]>([]);
  const [communityPosts, setCommunityPosts] = useState<CommunityPost[]>([]);
  const [feedLoading, setFeedLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'feedback' | 'reports' | 'community'>('feedback');
  const [plantedView, setPlantedView] = useState<'varieties' | 'crops'>('varieties');

  const loadAll = async () => {
    setLoading(true);
    setFeedLoading(true);
    try {
      const [data, feedback, reports, posts] = await Promise.all([
        apiService.getDashboardStats(),
        apiService.getFeedback(),
        apiService.getCommunityReports(),
        apiService.getCommunityPosts(),
      ]);
      setStats(data);
      setFeedbackList(feedback.slice(0, 10));
      setReportList(reports.slice(0, 10));
      setCommunityPosts(posts.slice(0, 10));
      setLastRefreshed(new Date().toLocaleTimeString('en-PH', { hour: '2-digit', minute: '2-digit' }));
    } catch (err) {
      console.error('Dashboard load error', err);
    } finally {
      setLoading(false);
      setFeedLoading(false);
    }
  };

  useEffect(() => { loadAll(); }, []);

  const tooltipStyle = {
    backgroundColor: '#14232C',
    border: '1px solid #223B49',
    borderRadius: '12px',
    color: '#F4F4F4',
    fontSize: '12px',
    boxShadow: '0 8px 24px rgba(0,0,0,0.5)',
  };

  const formatDate = (s: string) => {
    if (!s) return '';
    const d = new Date(s);
    const now = Date.now();
    const diff = now - d.getTime();
    if (diff < 60000) return 'just now';
    if (diff < 3600000) return Math.floor(diff / 60000) + 'm ago';
    if (diff < 86400000) return Math.floor(diff / 3600000) + 'h ago';
    return d.toLocaleDateString('en-PH', { month: 'short', day: 'numeric' });
  };

  const catColor: Record<string, string> = {
    BUG: '#ef4444', FEATURE_REQUEST: '#8b5cf6',
    AGRONOMIC_QUERY: '#22c55e', GENERAL: '#06b6d4',
  };

  return (
    <div className="flex flex-col gap-6 w-full animate-fadeIn select-none">

      {/* ── Header ──────────────────────────────────────────────────────────── */}
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-xl font-black text-white">MapTanim Analytics Dashboard</h1>
          <p className="text-xs text-[#5F7586] mt-0.5">Live data from Supabase · Philippine Agricultural Intelligence System</p>
        </div>
        <button
          onClick={loadAll}
          disabled={loading}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-[#182933] border border-[#223B49] text-xs font-semibold text-[#C7D0D8] hover:border-emerald-500/50 hover:text-emerald-400 transition-all disabled:opacity-50"
        >
          <RefreshCw className={'w-3.5 h-3.5' + (loading ? ' animate-spin' : '')} />
          {loading ? 'Loading...' : ('Refresh' + (lastRefreshed ? ' · ' + lastRefreshed : ''))}
        </button>
      </div>

      {/* ── ROW 1: 4 Stat Cards ─────────────────────────────────────────────── */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          icon={<Users className="w-6 h-6" />}
          label="Total Users"
          value={loading ? '–' : stats.totalFarmers.toLocaleString()}
          sub={stats.activeUsersToday + ' active today'}
          accent="#00E676"
          badge={{ text: 'LIVE', color: '#00E676' }}
          isLoading={loading}
        />
        <StatCard
          icon={<Sprout className="w-6 h-6" />}
          label="Total Crops"
          value={loading ? '–' : stats.totalCrops}
          sub="15 Philippine crops"
          accent="#4CAF50"
          isLoading={loading}
        />
        <StatCard
          icon={<MessageSquare className="w-6 h-6" />}
          label="Feedback"
          value={loading ? '–' : stats.totalFeedback}
          sub="System & app support"
          accent="#06b6d4"
          badge={stats.totalFeedback > 0 ? { text: 'Review', color: '#06b6d4' } : undefined}
          isLoading={loading}
        />
        <StatCard
          icon={<Flag className="w-6 h-6" />}
          label="Post Reports"
          value={loading ? '–' : stats.totalPostReports}
          sub={stats.pendingReports + ' pending review'}
          accent="#f59e0b"
          badge={stats.pendingReports > 0 ? { text: stats.pendingReports + ' Pending', color: '#f59e0b' } : undefined}
          isLoading={loading}
        />
      </div>

      {/* ── MAIN CONTENT: Left Charts | Right Feed ───────────────────────────── */}
      <div className="grid grid-cols-1 xl:grid-cols-12 gap-5">

        {/* ── LEFT COLUMN: Analytics Charts ─────────────────────────────────── */}
        <div className="xl:col-span-8 flex flex-col gap-5">

          {/* User Registration Dual Chart */}
          <div className="bg-[#182933] border border-[#223B49] rounded-2xl p-5 sm:p-6 flex flex-col gap-4 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <h4 className="text-base font-bold text-white">User Registration Analytics</h4>
                <p className="text-xs text-[#5F7586] mt-0.5">New registrations vs returning active users — last 7 days</p>
              </div>
              <div className="flex items-center gap-3 text-[11px]">
                <span className="flex items-center gap-1.5"><span className="w-2 h-2 rounded-full bg-[#00E676]" /> New</span>
                <span className="flex items-center gap-1.5 text-[#8A9BA8]"><span className="w-2 h-2 rounded-full bg-[#FFA726]" /> Returning</span>
              </div>
            </div>
            <div className="h-[200px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={stats.weeklyRegistrations} margin={{ top: 10, right: 10, left: -18, bottom: 0 }}>
                  <defs>
                    <filter id="glw-g" x="-20%" y="-20%" width="140%" height="140%">
                      <feGaussianBlur stdDeviation="3" result="blur" />
                      <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
                    </filter>
                    <filter id="glw-a" x="-20%" y="-20%" width="140%" height="140%">
                      <feGaussianBlur stdDeviation="3" result="blur" />
                      <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
                    </filter>
                    <linearGradient id="gG" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor="#00E676" stopOpacity={0.25} />
                      <stop offset="95%" stopColor="#00E676" stopOpacity={0} />
                    </linearGradient>
                    <linearGradient id="gA" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor="#FFA726" stopOpacity={0.2} />
                      <stop offset="95%" stopColor="#FFA726" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#223B49" vertical={false} />
                  <XAxis dataKey="day" stroke="#5F7586" fontSize={11} tickLine={false} axisLine={false} />
                  <YAxis stroke="#5F7586" fontSize={11} tickLine={false} axisLine={false} allowDecimals={false} />
                  <Tooltip contentStyle={tooltipStyle}
                    formatter={(val: any, name: any) => [val, name === 'newUsers' ? 'New Users' : 'Returning']} />
                  <Area type="monotone" dataKey="newUsers" stroke="#00E676" strokeWidth={2.5} fill="url(#gG)" filter="url(#glw-g)" dot={false} />
                  <Area type="monotone" dataKey="returningUsers" stroke="#FFA726" strokeWidth={2.5} fill="url(#gA)" filter="url(#glw-a)" dot={false} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
            <div className="grid grid-cols-3 gap-3 pt-2 border-t border-[#223B49]">
              <div className="text-center">
                <p className="text-[10px] text-[#5F7586] uppercase font-bold">Total Users</p>
                <p className="text-lg font-black text-white">{stats.totalFarmers.toLocaleString()}</p>
              </div>
              <div className="text-center border-x border-[#223B49]">
                <p className="text-[10px] text-[#5F7586] uppercase font-bold">Active Today</p>
                <p className="text-lg font-black text-emerald-400">{stats.activeUsersToday}</p>
              </div>
              <div className="text-center">
                <p className="text-[10px] text-[#5F7586] uppercase font-bold">Community Posts</p>
                <p className="text-lg font-black text-white">{stats.totalCommunityPosts}</p>
              </div>
            </div>
          </div>

          {/* Most Planted Varieties / Crops */}
          <div className="bg-[#182933] border border-[#223B49] rounded-2xl p-5 sm:p-6 flex flex-col gap-4 shadow-sm">
            <div className="flex items-center justify-between flex-wrap gap-2">
              <div>
                <div className="flex items-center gap-2">
                  <h4 className="text-base font-bold text-white">
                    {plantedView === 'varieties' ? 'Most Planted Varieties' : 'Most Planted Crops'}
                  </h4>
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                    Philippine Cultivars
                  </span>
                </div>
                <p className="text-xs text-[#5F7586] mt-0.5">
                  {plantedView === 'varieties'
                    ? 'Top cultivated crop seed varieties across active farmer crop plots'
                    : 'From active crop plots across all farmer farms'}
                </p>
              </div>
              <div className="flex items-center bg-[#14232C] p-1 rounded-xl border border-[#223B49] text-[11px] font-semibold">
                <button
                  onClick={() => setPlantedView('varieties')}
                  className={`px-2.5 py-1 rounded-lg transition-all ${plantedView === 'varieties' ? 'bg-[#182933] text-emerald-400 font-bold shadow-sm' : 'text-[#5F7586] hover:text-[#C7D0D8]'}`}
                >
                  By Variety
                </button>
                <button
                  onClick={() => setPlantedView('crops')}
                  className={`px-2.5 py-1 rounded-lg transition-all ${plantedView === 'crops' ? 'bg-[#182933] text-emerald-400 font-bold shadow-sm' : 'text-[#5F7586] hover:text-[#C7D0D8]'}`}
                >
                  By Crop
                </button>
              </div>
            </div>

            {loading
              ? <div className="flex flex-col gap-3">{[1,2,3,4,5].map(i => <div key={i} className="h-6 bg-[#223B49] rounded-lg animate-pulse" />)}</div>
              : (plantedView === 'varieties' ? (stats.topPlantedVarieties?.length || 0) === 0 : stats.topPlantedCrops.length === 0)
                ? (
                  <div className="flex flex-col items-center justify-center py-8 text-center">
                    <BarChart3 className="w-8 h-8 text-[#223B49] mb-2" />
                    <p className="text-xs text-[#5F7586]">No planting data yet</p>
                    <p className="text-[11px] text-[#3B5266] mt-1">Appears once farmers start planting crops in the app</p>
                  </div>
                )
                : plantedView === 'varieties'
                  ? (
                    <div className="flex flex-col gap-2.5">
                      {(stats.topPlantedVarieties || []).map((variety, i) => {
                        const max = stats.topPlantedVarieties?.[0]?.plantCount || 1;
                        const pct = Math.round((variety.plantCount / max) * 100);
                        return (
                          <div key={variety.varietyName + '-' + i} className="flex items-center gap-3">
                            <span className="text-[11px] font-bold text-[#5F7586] w-4 shrink-0">{i + 1}</span>
                            <div className="w-44 shrink-0 flex flex-col min-w-0">
                              <span className="text-xs font-bold text-[#F4F4F4] truncate">{variety.varietyName}</span>
                              <span className="text-[10px] text-[#5F7586] truncate">{variety.cropName}</span>
                            </div>
                            <div className="flex-1 h-2 bg-[#14232C] rounded-full overflow-hidden">
                              <div className="h-full rounded-full transition-all duration-700"
                                style={{ width: pct + '%', backgroundColor: variety.color, boxShadow: '0 0 8px ' + variety.color + '60' }} />
                            </div>
                            <div className="w-16 text-right shrink-0">
                              <span className="text-[11px] font-mono font-bold" style={{ color: variety.color }}>
                                {variety.plantCount} <span className="text-[9px] text-[#5F7586] font-normal">plots</span>
                              </span>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )
                  : (
                    <div className="flex flex-col gap-2.5">
                      {stats.topPlantedCrops.map((crop, i) => {
                        const max = stats.topPlantedCrops[0]?.plantCount || 1;
                        const pct = Math.round((crop.plantCount / max) * 100);
                        return (
                          <div key={crop.cropName} className="flex items-center gap-3">
                            <span className="text-[11px] font-bold text-[#5F7586] w-4 shrink-0">{i + 1}</span>
                            <div className="w-44 shrink-0 flex flex-col min-w-0">
                              <span className="text-xs font-semibold text-[#C7D0D8] truncate">{crop.cropName}</span>
                              {crop.varietyName && (
                                <span className="text-[10px] text-[#5F7586] truncate">{crop.varietyName}</span>
                              )}
                            </div>
                            <div className="flex-1 h-2 bg-[#14232C] rounded-full overflow-hidden">
                              <div className="h-full rounded-full transition-all duration-700"
                                style={{ width: pct + '%', backgroundColor: crop.color, boxShadow: '0 0 8px ' + crop.color + '60' }} />
                            </div>
                            <div className="w-16 text-right shrink-0">
                              <span className="text-[11px] font-mono font-bold" style={{ color: crop.color }}>
                                {crop.plantCount} <span className="text-[9px] text-[#5F7586] font-normal">plots</span>
                              </span>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )
            }
          </div>

          {/* Harvest Dates + Crop Pie Row */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-5">

            {/* Most Harvest Dates Chart */}
            <div className="md:col-span-8 bg-[#182933] border border-[#223B49] rounded-2xl p-5 flex flex-col gap-4 shadow-sm">
              <div className="flex items-center justify-between flex-wrap gap-2">
                <div>
                  <div className="flex items-center gap-2">
                    <h4 className="text-sm font-bold text-white">Most Harvest Dates</h4>
                    <span className="px-2 py-0.5 rounded-full text-[10px] font-black bg-[#00E67622] text-[#00E676] border border-[#00E67644]">
                      PEAK HARVEST DATES
                    </span>
                  </div>
                  <p className="text-xs text-[#5F7586] mt-0.5">Harvest yield (kg) and frequency by date across all farms</p>
                </div>
                {stats.harvestDateAnalytics?.peakHarvestDate && (
                  <div className="flex items-center gap-2 px-3 py-1 rounded-xl bg-[#14232C] border border-emerald-500/30 text-xs">
                    <Calendar className="w-3.5 h-3.5 text-emerald-400" />
                    <span className="text-[#8A9BA8]">Peak:</span>
                    <strong className="text-white font-mono">
                      {new Date(stats.harvestDateAnalytics.peakHarvestDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                    </strong>
                    <span className="px-1.5 py-0.2 rounded text-[10px] font-black bg-emerald-500/20 text-emerald-400">
                      {stats.harvestDateAnalytics.peakHarvestYieldKg.toLocaleString()} kg
                    </span>
                  </div>
                )}
              </div>
              <div className="h-[160px] w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={stats.harvestDateAnalytics?.dateRecords || []} margin={{ top: 8, right: 8, left: -18, bottom: 0 }}>
                    <defs>
                      <linearGradient id="gHarv" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#00E676" stopOpacity={0.35} />
                        <stop offset="95%" stopColor="#00E676" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" stroke="#223B49" vertical={false} />
                    <XAxis dataKey="displayDate" stroke="#5F7586" fontSize={10} tickLine={false} axisLine={false} />
                    <YAxis stroke="#5F7586" fontSize={10} tickLine={false} axisLine={false} tickFormatter={(v: number) => v + 'kg'} />
                    <Tooltip contentStyle={tooltipStyle}
                      formatter={(val: any) => [val + ' kg', 'Harvest Yield']}
                      labelFormatter={(label: any, payload: any) => {
                        const item = payload?.[0]?.payload;
                        return item ? `${item.fullDate || label} · ${item.harvestCount} plots (${item.topCrop})` : label;
                      }}
                    />
                    <Area type="monotone" dataKey="yieldKg" stroke="#00E676" strokeWidth={2.5} fill="url(#gHarv)" dot={{ r: 3, fill: '#00E676' }} activeDot={{ r: 5, fill: '#00E676' }} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
              <div className="flex items-center gap-4 text-[11px] text-[#C7D0D8] border-t border-[#223B49] pt-2 flex-wrap">
                <span className="flex items-center gap-1.5">
                  <span className="w-3 h-0.5 bg-[#00E676] rounded-full inline-block" /> Daily Harvest Yield (kg)
                </span>
                <span className="flex items-center gap-1.5 text-[#8A9BA8]">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 inline-block" /> Top Crop: <strong className="text-white">{stats.harvestDateAnalytics?.peakHarvestCrop || 'Mixed'}</strong>
                </span>
                <span className="ml-auto text-[#5F7586]">
                  Peak Day: <strong className="text-emerald-400 font-bold">{stats.harvestDateAnalytics?.peakHarvestYieldKg?.toLocaleString() || 0} kg</strong>
                </span>
              </div>
            </div>

            {/* Crop Category Pie */}
            <div className="md:col-span-4 bg-[#182933] border border-[#223B49] rounded-2xl p-5 flex flex-col gap-3 shadow-sm">
              <div>
                <h4 className="text-sm font-bold text-white">Crop Categories</h4>
                <p className="text-xs text-[#5F7586] mt-0.5">By type</p>
              </div>
              <div className="h-[120px] w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie data={stats.cropDistribution} dataKey="value" nameKey="category"
                      cx="50%" cy="50%" innerRadius={35} outerRadius={55} paddingAngle={3}>
                      {stats.cropDistribution.map((entry, i) => (
                        <Cell key={i} fill={entry.color} stroke="transparent" />
                      ))}
                    </Pie>
                    <Tooltip contentStyle={tooltipStyle} formatter={(val: any) => [val + '%', 'Share']} />
                  </PieChart>
                </ResponsiveContainer>
              </div>
              <div className="flex flex-col gap-1">
                {stats.cropDistribution.map((d) => (
                  <div key={d.category} className="flex items-center gap-2 text-[10px]">
                    <span className="w-2 h-2 rounded-full shrink-0" style={{ backgroundColor: d.color }} />
                    <span className="text-[#8A9BA8] truncate flex-1">{d.category}</span>
                    <span className="font-bold text-white">{d.value}%</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Peak Harvest Dates Breakdown */}
          <div className="bg-[#182933] border border-[#223B49] rounded-2xl p-5 flex flex-col gap-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-emerald-400" />
                <h4 className="text-sm font-bold text-white">Peak Harvest Dates Breakdown</h4>
              </div>
              <span className="text-xs text-[#5F7586]">Ranked by harvest volume & frequency</span>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              {(stats.harvestDateAnalytics?.dateRecords || [])
                .slice()
                .sort((a, b) => b.yieldKg - a.yieldKg)
                .slice(0, 4)
                .map((item, idx) => {
                  const peak = stats.harvestDateAnalytics?.peakHarvestYieldKg || 1;
                  const pct = Math.min(100, Math.round((item.yieldKg / peak) * 100));
                  const color = idx === 0 ? '#00E676' : idx === 1 ? '#4CAF50' : idx === 2 ? '#FFA726' : '#06b6d4';
                  return (
                    <div key={item.date} className="flex flex-col gap-2 p-3 rounded-xl bg-[#14232C] border border-[#223B49]">
                      <div className="flex items-center justify-between text-xs">
                        <span className="font-semibold text-white">{item.displayDate}</span>
                        <span className="font-bold font-mono text-[11px]" style={{ color }}>{item.yieldKg} kg</span>
                      </div>
                      <div className="h-1.5 w-full bg-[#182933] rounded-full overflow-hidden">
                        <div className="h-full rounded-full transition-all duration-700"
                          style={{ width: pct + '%', backgroundColor: color, boxShadow: '0 0 8px ' + color + '60' }} />
                      </div>
                      <div className="flex items-center justify-between text-[10px] text-[#5F7586]">
                        <span className="truncate max-w-[100px]">{item.topCrop}</span>
                        <span className="font-mono text-white/80">{item.harvestCount} plots</span>
                      </div>
                    </div>
                  );
                })}
            </div>
          </div>

          {/* About Card + Live Summary Row */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div className="bg-[#182933] border border-[#223B49] rounded-2xl p-5 flex flex-col gap-3">
              <div className="flex items-center gap-2">
                <Smartphone className="w-4 h-4 text-emerald-400" />
                <h4 className="text-sm font-bold text-white">About MapTanim Admin</h4>
              </div>
              <p className="text-xs text-[#8A9BA8] leading-relaxed">
                MapTanim Admin is the zero-code control panel for the mobile knowledge base.
                Admins manage the <strong className="text-white">15 core Philippine crops</strong> in Supabase —
                the mobile app automatically downloads updates as a <strong className="text-emerald-400">System Update</strong> notification. No app code changes needed.
              </p>
              <div className="flex flex-wrap gap-2 mt-auto pt-2">
                {['Crops', 'Varieties', 'Growth Stages', 'NPK & pH', 'Companions', 'Pest Guides'].map(tag => (
                  <span key={tag} className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                    {tag}
                  </span>
                ))}
              </div>
            </div>
            <div className="bg-[#182933] border border-[#223B49] rounded-2xl p-5 flex flex-col gap-3">
              <div className="flex items-center gap-2">
                <Activity className="w-4 h-4 text-[#00E676]" />
                <h4 className="text-sm font-bold text-white">Live Platform Summary</h4>
              </div>
              <div className="flex flex-col gap-2.5">
                {[
                  { label: 'Registered Farmers', value: stats.totalFarmers, color: '#00E676' },
                  { label: 'Active Farms', value: stats.activeFarms, color: '#4CAF50' },
                  { label: 'Crops in Library', value: stats.totalCrops, color: '#06b6d4' },
                  { label: 'Community Posts', value: stats.totalCommunityPosts, color: '#8b5cf6' },
                  { label: 'Pending Reports', value: stats.pendingReports, color: stats.pendingReports > 0 ? '#f59e0b' : '#00E676' },
                  { label: 'Total Feedback', value: stats.totalFeedback, color: '#06b6d4' },
                ].map(item => (
                  <div key={item.label} className="flex items-center justify-between">
                    <span className="text-xs text-[#8A9BA8]">{item.label}</span>
                    <span className="text-xs font-black" style={{ color: loading ? '#5F7586' : item.color }}>
                      {loading ? '–' : item.value.toLocaleString()}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>

        </div>{/* end left column */}

        {/* ── RIGHT COLUMN: Feedback & Reports Live Feed ───────────────────── */}
        <div className="xl:col-span-4 flex flex-col gap-0">
          <div className="bg-[#182933] border border-[#223B49] rounded-2xl flex flex-col overflow-hidden shadow-md" style={{ minHeight: '600px' }}>

            {/* Tab Header */}
            <div className="flex items-center border-b border-[#223B49]">
              <button
                onClick={() => setActiveTab('feedback')}
                className={'flex-1 flex items-center justify-center gap-2 py-3 text-[11px] font-bold transition-all ' +
                  (activeTab === 'feedback'
                    ? 'bg-[#14232C] text-emerald-400 border-b-2 border-emerald-400'
                    : 'text-[#5F7586] hover:text-[#C7D0D8]')}
              >
                <MessageSquare className="w-3 h-3" />
                Feedback
                {stats.totalFeedback > 0 && (
                  <span className="px-1.5 py-0.5 rounded-full text-[9px] font-black bg-[#06b6d422] text-[#06b6d4]">
                    {stats.totalFeedback}
                  </span>
                )}
              </button>
              <div className="w-px h-7 bg-[#223B49]" />
              <button
                onClick={() => setActiveTab('reports')}
                className={'flex-1 flex items-center justify-center gap-2 py-3 text-[11px] font-bold transition-all ' +
                  (activeTab === 'reports'
                    ? 'bg-[#14232C] text-[#f59e0b] border-b-2 border-[#f59e0b]'
                    : 'text-[#5F7586] hover:text-[#C7D0D8]')}
              >
                <Flag className="w-3 h-3" />
                Reports
                {stats.pendingReports > 0 && (
                  <span className="px-1.5 py-0.5 rounded-full text-[9px] font-black bg-[#f59e0b22] text-[#f59e0b]">
                    {stats.pendingReports}
                  </span>
                )}
              </button>
              <div className="w-px h-7 bg-[#223B49]" />
              <button
                onClick={() => setActiveTab('community')}
                className={'flex-1 flex items-center justify-center gap-2 py-3 text-[11px] font-bold transition-all ' +
                  (activeTab === 'community'
                    ? 'bg-[#14232C] text-[#8b5cf6] border-b-2 border-[#8b5cf6]'
                    : 'text-[#5F7586] hover:text-[#C7D0D8]')}
              >
                <BookOpen className="w-3 h-3" />
                Community
                {stats.totalCommunityPosts > 0 && (
                  <span className="px-1.5 py-0.5 rounded-full text-[9px] font-black bg-[#8b5cf622] text-[#8b5cf6]">
                    {stats.totalCommunityPosts}
                  </span>
                )}
              </button>
            </div>

            {/* Feed Items */}
            <div className="flex-1 overflow-y-auto">
              {feedLoading
                ? (
                  <div className="flex flex-col gap-0">
                    {[1,2,3,4,5].map(i => (
                      <div key={i} className="p-4 border-b border-[#1E2F3A]">
                        <div className="h-3 bg-[#223B49] rounded w-3/4 mb-2 animate-pulse" />
                        <div className="h-3 bg-[#1a2d3a] rounded w-1/2 animate-pulse" />
                      </div>
                    ))}
                  </div>
                )
                : activeTab === 'feedback'
                  ? feedbackList.length === 0
                    ? (
                      <div className="flex flex-col items-center justify-center h-48 gap-2">
                        <MessageSquare className="w-8 h-8 text-[#223B49]" />
                        <p className="text-xs text-[#5F7586]">No feedback yet</p>
                      </div>
                    )
                    : feedbackList.map((item) => (
                      <div key={item.id} className="p-4 border-b border-[#1E2F3A] hover:bg-[#14232C] transition-colors">
                        <div className="flex items-start justify-between gap-2 mb-1.5">
                          <div className="flex items-center gap-2 min-w-0">
                            <div className="w-6 h-6 rounded-lg shrink-0 flex items-center justify-center text-[10px] font-black text-white"
                              style={{ backgroundColor: catColor[item.category] || '#5F7586' }}>
                              {item.farmerName?.charAt(0) || '?'}
                            </div>
                            <span className="text-xs font-semibold text-[#C7D0D8] truncate">{item.farmerName}</span>
                          </div>
                          {statusBadge(item.status)}
                        </div>
                        <p className="text-xs font-bold text-white mb-1 truncate">{item.subject}</p>
                        <p className="text-[11px] text-[#8A9BA8] leading-relaxed line-clamp-2">{item.message}</p>
                        <div className="flex items-center justify-between mt-2">
                          <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-[#14232C] border border-[#223B49] text-[#5F7586]">
                            {item.category.replace('_', ' ')}
                          </span>
                          <span className="flex items-center gap-1 text-[10px] text-[#5F7586]">
                            <Clock className="w-2.5 h-2.5" />
                            {formatDate(item.createdAt)}
                          </span>
                        </div>
                        {item.adminReply && (
                          <div className="mt-2 pl-3 border-l-2 border-emerald-500/40">
                            <p className="text-[10px] text-emerald-400 font-semibold mb-0.5">Admin Reply</p>
                            <p className="text-[10px] text-[#8A9BA8] line-clamp-2">{item.adminReply}</p>
                          </div>
                        )}
                      </div>
                    ))
                  : reportList.length === 0
                    ? (
                      <div className="flex flex-col items-center justify-center h-48 gap-2">
                        <CheckCircle2 className="w-8 h-8 text-[#223B49]" />
                        <p className="text-xs text-[#5F7586]">No post reports</p>
                      </div>
                    )
                    : reportList.map((item) => (
                      <div key={item.id} className="p-4 border-b border-[#1E2F3A] hover:bg-[#14232C] transition-colors">
                        <div className="flex items-start justify-between gap-2 mb-1.5">
                          <div className="flex items-center gap-2 min-w-0">
                            <div className="w-6 h-6 rounded-lg shrink-0 flex items-center justify-center bg-[#f59e0b22] border border-[#f59e0b44]">
                              <Flag className="w-3 h-3 text-[#f59e0b]" />
                            </div>
                            <span className="text-xs font-semibold text-[#C7D0D8] truncate">{item.reporterName}</span>
                          </div>
                          {statusBadge(item.status)}
                        </div>
                        <p className="text-xs font-bold text-white mb-1">
                          <span className="text-[#5F7586] font-normal text-[10px]">{item.targetType}: </span>
                          {item.targetName}
                        </p>
                        <p className="text-[11px] text-[#8A9BA8] leading-relaxed line-clamp-2">{item.reason}</p>
                        {item.targetContent && (
                          <p className="text-[10px] text-[#5F7586] mt-1 line-clamp-1 italic">"{item.targetContent}"</p>
                        )}
                        <div className="flex items-center justify-between mt-2">
                          <div className="flex items-center gap-1">
                            {item.status === 'PENDING' && (
                              <span className="flex items-center gap-1 text-[10px] text-[#f59e0b] font-bold">
                                <AlertTriangle className="w-2.5 h-2.5" /> Needs Review
                              </span>
                            )}
                          </div>
                          <span className="flex items-center gap-1 text-[10px] text-[#5F7586]">
                            <Clock className="w-2.5 h-2.5" />
                            {formatDate(item.createdAt)}
                          </span>
                        </div>
                      </div>
                    ))
              }
              {/* ── Community Hub Feed ─────────────────────────────────────── */}
              {activeTab === 'community' && (
                communityPosts.length === 0
                  ? (
                    <div className="flex flex-col items-center justify-center h-48 gap-2">
                      <BookOpen className="w-8 h-8 text-[#223B49]" />
                      <p className="text-xs text-[#5F7586]">No community posts yet</p>
                    </div>
                  )
                  : communityPosts.map((post) => {
                    const catColors: Record<string, string> = {
                      PEST_ALERT: '#ef4444',
                      FARMING_TIP: '#22c55e',
                      EQUIPMENT: '#06b6d4',
                      GENERAL: '#8b5cf6',
                      OFFICIAL_ADVISORY: '#f59e0b',
                    };
                    const catLabels: Record<string, string> = {
                      PEST_ALERT: 'Pest Alert',
                      FARMING_TIP: 'Farming Tip',
                      EQUIPMENT: 'Equipment',
                      GENERAL: 'General',
                      OFFICIAL_ADVISORY: 'Advisory',
                    };
                    const accent = catColors[post.category] || '#8b5cf6';
                    return (
                      <div key={post.id} className="p-4 border-b border-[#1E2F3A] hover:bg-[#14232C] transition-colors">
                        <div className="flex items-start justify-between gap-2 mb-1.5">
                          <div className="flex items-center gap-2 min-w-0">
                            <div className="w-6 h-6 rounded-lg shrink-0 flex items-center justify-center text-[10px] font-black text-white"
                              style={{ backgroundColor: accent + 'cc' }}>
                              {post.authorName?.charAt(0) || 'F'}
                            </div>
                            <span className="text-xs font-semibold text-[#C7D0D8] truncate">{post.authorName}</span>
                          </div>
                          <div className="flex items-center gap-1 shrink-0">
                            {post.isPinned && (
                              <span className="flex items-center gap-0.5 text-[9px] font-bold text-[#f59e0b]">
                                <Pin className="w-2.5 h-2.5" /> Pinned
                              </span>
                            )}
                            <span className="px-1.5 py-0.5 rounded-full text-[9px] font-bold"
                              style={{ background: accent + '22', color: accent }}>
                              {catLabels[post.category] || post.category}
                            </span>
                          </div>
                        </div>
                        <p className="text-xs font-bold text-white mb-1 truncate">{post.title}</p>
                        <p className="text-[11px] text-[#8A9BA8] leading-relaxed line-clamp-2">{post.content}</p>
                        {post.tags && post.tags.length > 0 && (
                          <div className="flex gap-1 mt-1.5 flex-wrap">
                            {post.tags.slice(0, 3).map((tag: string) => (
                              <span key={tag} className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-[#14232C] border border-[#223B49] text-[#5F7586]">
                                #{tag}
                              </span>
                            ))}
                          </div>
                        )}
                        <div className="flex items-center justify-between mt-2">
                          <div className="flex items-center gap-3 text-[10px] text-[#5F7586]">
                            <span className="flex items-center gap-1">
                              <Heart className="w-2.5 h-2.5" /> {post.likesCount}
                            </span>
                            <span className="flex items-center gap-1">
                              <MessageSquare className="w-2.5 h-2.5" /> {post.commentsCount}
                            </span>
                          </div>
                          <span className="flex items-center gap-1 text-[10px] text-[#5F7586]">
                            <Clock className="w-2.5 h-2.5" />
                            {formatDate(post.createdAt)}
                          </span>
                        </div>
                      </div>
                    );
                  })
              )}
            </div>

            {/* Footer — view all link */}
            <div className="border-t border-[#223B49] p-3">
              <button className="w-full flex items-center justify-center gap-1.5 text-xs font-semibold text-[#5F7586] hover:text-emerald-400 transition-colors py-1">
                View all in{' '}
                {activeTab === 'feedback' ? 'Feedback Management' : activeTab === 'reports' ? 'Community Hub → Reports' : 'Community Hub'}
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>

      </div>{/* end main content grid */}
    </div>
  );
};
