import React, { useEffect, useState } from 'react';
import { Users, Sprout, Layers, Scale, RefreshCw, MoreHorizontal, Calendar, Activity, AlertTriangle, CheckCircle2, Clock } from 'lucide-react';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { StatCard } from '../components/common/StatCard';
import { YieldTrendsChart } from '../components/charts/YieldTrendsChart';
import { CropDistributionChart } from '../components/charts/CropDistributionChart';
import { DashboardStats } from '../types';
import { apiService } from '../services/api';

export const DashboardOverview: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const growthMonitoringData = [
    { name: 'Aug 12', val: 2400 },
    { name: 'Aug 13', val: 1800 },
    { name: 'Aug 14', val: 3200 },
    { name: 'Aug 15', val: 3800 },
    { name: 'Aug 16', val: 4900 },
  ];

  // Recent activities data (system management focus)
  const recentActivities = [
    { id: 'ACT-001', description: 'Farmer registration approved', actor: 'John Sina (Murcia Farm)', date: '12 Aug 2026', status: 'Completed' as const },
    { id: 'ACT-002', description: 'DSS rule updated', actor: 'Admin — Companion pairing', date: '13 Aug 2026', status: 'Completed' as const },
    { id: 'ACT-003', description: 'Pest alert published', actor: 'System — Highland Bed 2', date: '13 Aug 2026', status: 'Alert' as const },
    { id: 'ACT-004', description: 'Harvest record submitted', actor: 'Aline Gomez (Santos Garden)', date: '15 Aug 2026', status: 'Pending' as const },
  ];

  // Pending tasks / alerts
  const pendingTasks = [
    { id: 'TSK-001', title: 'Feedback Ticket #7', subtitle: 'From: Ka Ryan Vasquez', detail: 'Overdue by 3 days', priority: 'high' as const },
    { id: 'TSK-002', title: 'Community Report', subtitle: 'Spam — Seed Swap post', detail: 'Pending review', priority: 'medium' as const },
    { id: 'TSK-003', title: 'DSS Conflict Warning', subtitle: 'Tomato ↔ Fennel pairing', detail: 'Needs validation', priority: 'low' as const },
    { id: 'TSK-004', title: 'Farmer Account', subtitle: 'Maria Santos — pending', detail: 'Awaiting approval', priority: 'medium' as const },
  ];

  const loadData = async () => {
    setLoading(true);
    const res = await apiService.getDashboardStats();
    setStats(res);
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading || !stats) {
    return (
      <div className="flex items-center justify-center h-64 bg-white border border-slate-200 rounded-xl text-slate-500 gap-3">
        <RefreshCw className="w-5 h-5 animate-spin text-emerald-600" />
        <span className="text-sm font-semibold text-slate-600">Loading Dashboard...</span>
      </div>
    );
  }

  const getStatusBadge = (status: 'Completed' | 'Alert' | 'Pending') => {
    switch (status) {
      case 'Completed':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-50 text-emerald-600 border border-emerald-100">
            <CheckCircle2 className="w-3 h-3" />
            Completed
          </span>
        );
      case 'Alert':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-rose-50 text-rose-500 border border-rose-100">
            <AlertTriangle className="w-3 h-3" />
            Alert
          </span>
        );
      case 'Pending':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-amber-50 text-amber-600 border border-amber-100">
            <Clock className="w-3 h-3" />
            Pending
          </span>
        );
    }
  };

  const getPriorityDot = (priority: 'high' | 'medium' | 'low') => {
    const colors = {
      high: 'bg-rose-500',
      medium: 'bg-amber-500',
      low: 'bg-slate-400',
    };
    return <span className={`w-2 h-2 rounded-full shrink-0 ${colors[priority]}`} />;
  };

  return (
    <div className="space-y-6 animate-fadeIn pb-8">
      {/* Top Controls Row */}
      <div className="flex items-center justify-between">
        <div></div>
        <div className="flex items-center gap-2 bg-white px-3.5 py-2 rounded-lg border border-slate-200 text-xs font-medium text-slate-600 cursor-pointer hover:bg-slate-50 transition">
          <Calendar className="w-3.5 h-3.5 text-slate-400" />
          <span>12 August'26 to 18 August'26</span>
        </div>
      </div>

      {/* ─── ROW 1: 4 TOP METRIC CARDS ─── */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <StatCard
          title="Total Farmers"
          value={stats.totalFarmers.toLocaleString()}
          subtitle="Registered smallholders"
          icon={Users}
          iconBgColor="bg-[#2563EB]"
        />
        <StatCard
          title="Active Farms"
          value={stats.activeFarms.toLocaleString()}
          subtitle="Managed agricultural zones"
          icon={Sprout}
          iconBgColor="bg-[#7C3AED]"
        />
        <StatCard
          title="Cultivated Plots"
          value={stats.totalPlots.toLocaleString()}
          subtitle="Active crop beds"
          icon={Layers}
          iconBgColor="bg-[#DB2777]"
        />
        <StatCard
          title="Monthly Harvest"
          value={`${stats.totalHarvestKgThisMonth.toLocaleString()} kg`}
          subtitle="Combined vegetable yield"
          icon={Scale}
          iconBgColor="bg-[#0891B2]"
        />
      </div>

      {/* ─── ROW 2: 3 CHART CARDS ─── */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 items-stretch">
        {/* Chart 1: Yield vs Target */}
        <div className="h-full">
          <YieldTrendsChart data={stats.monthlyYield} />
        </div>

        {/* Chart 2: Growth Monitoring (Line Chart) */}
        <div className="bg-white p-6 sm:p-7 rounded-2xl border border-slate-200/90 shadow-sm space-y-4 h-full flex flex-col box-border">
          <div className="flex items-center justify-between">
            <h4 className="font-bold text-sm text-slate-800">
              Growth Monitoring
            </h4>
            <button className="text-slate-400 hover:text-slate-600">
              <MoreHorizontal className="w-4 h-4" />
            </button>
          </div>

          <div className="flex-1 min-h-[220px] w-full pt-1">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={growthMonitoringData} margin={{ top: 10, right: 10, left: -25, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" vertical={false} />
                <XAxis dataKey="name" stroke="#64748B" fontSize={11} tickLine={false} axisLine={false} />
                <YAxis stroke="#64748B" fontSize={11} tickLine={false} axisLine={false} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#1E293B',
                    border: 'none',
                    borderRadius: '8px',
                    color: '#FFF',
                    fontSize: '12px',
                  }}
                />
                <Line
                  type="monotone"
                  dataKey="val"
                  stroke="#06B6D4"
                  strokeWidth={2.5}
                  dot={{ r: 4, fill: '#FFFFFF', stroke: '#06B6D4', strokeWidth: 2 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 3: Crops Cultivated (Bar Chart) */}
        <div className="h-full">
          <CropDistributionChart data={stats.cropDistribution} />
        </div>
      </div>

      {/* ─── ROW 3: 2 DATA TABLES (Activities + Pending Tasks) ─── */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Left Card (8 Cols): Recent Activities */}
        <div className="lg:col-span-8 bg-white rounded-2xl border border-slate-200/90 shadow-sm p-6 sm:p-7 space-y-4 box-border">
          <div className="flex items-center justify-between pb-1">
            <h4 className="font-bold text-sm text-slate-800">
              Recent Activities
            </h4>
            <button className="text-slate-400 hover:text-slate-600">
              <MoreHorizontal className="w-4 h-4" />
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-slate-100">
                  <th className="pb-3 px-2 text-slate-500 font-semibold">Activity ID</th>
                  <th className="pb-3 px-2 text-slate-500 font-semibold">Description</th>
                  <th className="pb-3 px-2 text-slate-500 font-semibold">Date</th>
                  <th className="pb-3 px-2 text-slate-500 font-semibold">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {recentActivities.map((activity) => (
                  <tr key={activity.id} className="hover:bg-slate-50/70 transition">
                    <td className="py-3.5 px-2 font-bold text-slate-800">{activity.id}</td>
                    <td className="py-3.5 px-2">
                      <div>
                        <p className="font-medium text-slate-700">{activity.description}</p>
                        <p className="text-[11px] text-slate-400">{activity.actor}</p>
                      </div>
                    </td>
                    <td className="py-3.5 px-2 text-slate-500">{activity.date}</td>
                    <td className="py-3.5 px-2">
                      {getStatusBadge(activity.status)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Right Card (4 Cols): Pending Tasks */}
        <div className="lg:col-span-4 bg-white rounded-2xl border border-slate-200/90 shadow-sm p-6 sm:p-7 space-y-4 box-border">
          <div className="flex items-center justify-between pb-1">
            <h4 className="font-bold text-sm text-slate-800">
              Pending Tasks
            </h4>
            <button className="text-slate-400 hover:text-slate-600">
              <MoreHorizontal className="w-4 h-4" />
            </button>
          </div>

          <div className="space-y-4 text-xs">
            {pendingTasks.map((task, i) => (
              <div
                key={task.id}
                className={`flex items-start justify-between gap-3 ${i < pendingTasks.length - 1 ? 'pb-3.5 border-b border-slate-50' : ''}`}
              >
                <div className="flex items-start gap-2.5 min-w-0">
                  <div className="mt-1">
                    {getPriorityDot(task.priority)}
                  </div>
                  <div className="min-w-0">
                    <p className="font-bold text-slate-800 truncate">{task.title}</p>
                    <p className="text-slate-400 text-[11px] truncate mt-0.5">{task.subtitle}</p>
                  </div>
                </div>
                <p className="text-rose-500 text-[11px] font-semibold whitespace-nowrap shrink-0">{task.detail}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
