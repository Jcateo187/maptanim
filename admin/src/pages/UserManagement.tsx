import React, { useEffect, useState } from 'react';
import {
  Users,
  UserCheck,
  UserX,
  UserMinus,
  Search,
  Filter,
  RefreshCw,
  Eye,
  Mail,
  Phone,
  Radio,
  Send,
  Download,
  Plus,
  AlertTriangle,
  Clock,
  Smartphone,
  ShieldCheck,
  Activity,
  CheckCircle2,
  TrendingUp,
  X,
  SlidersHorizontal,
  ChevronRight,
} from 'lucide-react';
import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  AreaChart,
  Area,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  CartesianGrid,
} from 'recharts';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { Farmer, AccountStatus, UserRole, UserActivityLog, UserTrackingMetrics } from '../types';
import { apiService } from '../services/api';

interface UserManagementProps {
  initialTab?: 'directory' | 'tracking' | 'activity';
}

export const UserManagement: React.FC<UserManagementProps> = ({ initialTab = 'directory' }) => {
  const [farmers, setFarmers] = useState<Farmer[]>([]);
  const [metrics, setMetrics] = useState<UserTrackingMetrics | null>(null);
  const [activityLogs, setActivityLogs] = useState<UserActivityLog[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [activeSubTab, setActiveSubTab] = useState<'directory' | 'tracking' | 'activity'>(initialTab);

  // Filters and Search
  const [search, setSearch] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [roleFilter, setRoleFilter] = useState<string>('ALL');

  // Modals
  const [selectedFarmer, setSelectedFarmer] = useState<Farmer | null>(null);
  const [isAddModalOpen, setIsAddModalOpen] = useState<boolean>(false);
  const [isAdvisoryModalOpen, setIsAdvisoryModalOpen] = useState<boolean>(false);
  const [advisoryTarget, setAdvisoryTarget] = useState<Farmer | null>(null);
  const [advisoryTitle, setAdvisoryTitle] = useState<string>('');
  const [advisoryBody, setAdvisoryBody] = useState<string>('');
  const [isSubmittingAdvisory, setIsSubmittingAdvisory] = useState<boolean>(false);

  // New User Form State
  const [newFullName, setNewFullName] = useState<string>('');
  const [newEmail, setNewEmail] = useState<string>('');
  const [newPhone, setNewPhone] = useState<string>('');
  const [newRole, setNewRole] = useState<UserRole>('FARMER');
  const [newFarmName, setNewFarmName] = useState<string>('');

  const loadData = async () => {
    setLoading(true);
    const [usersList, trackingStats, logs] = await Promise.all([
      apiService.getFarmers(),
      apiService.getUserTrackingMetrics(),
      apiService.getUserActivityLogs(),
    ]);
    setFarmers(usersList);
    setMetrics(trackingStats);
    setActivityLogs(logs);
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleToggleStatus = async (farmer: Farmer, nextStatus?: AccountStatus) => {
    let targetStatus: AccountStatus;
    if (nextStatus) {
      targetStatus = nextStatus;
    } else {
      targetStatus = farmer.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    }
    await apiService.updateUserStatus(farmer.id, targetStatus);
    loadData();
    if (selectedFarmer && selectedFarmer.id === farmer.id) {
      setSelectedFarmer({ ...selectedFarmer, status: targetStatus });
    }
  };

  const handleRoleChange = async (farmer: Farmer, role: UserRole) => {
    await apiService.updateUserRole(farmer.id, role);
    loadData();
    if (selectedFarmer && selectedFarmer.id === farmer.id) {
      setSelectedFarmer({ ...selectedFarmer, role });
    }
  };

  const handleOpenAdvisoryModal = (farmer: Farmer) => {
    setAdvisoryTarget(farmer);
    setAdvisoryTitle(
      farmer.status === 'INACTIVE'
        ? `Field Check-in: We Miss You at ${farmer.farmName}`
        : `MapTanim Agronomic Advisory for ${farmer.farmName}`
    );
    setAdvisoryBody(
      farmer.status === 'INACTIVE'
        ? `Magandang araw ${farmer.fullName}! Notice: It has been ${farmer.daysInactive || 14} days since your last farm synchronization. Please check your active plots and verify DSS recommendations for upcoming rainfall.`
        : `Magandang araw ${farmer.fullName}! Your regional agricultural extension officer has dispatched updated seasonal guidelines for your cultivated zones.`
    );
    setIsAdvisoryModalOpen(true);
  };

  const handleSendAdvisory = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!advisoryTarget || !advisoryTitle.trim() || !advisoryBody.trim()) return;
    setIsSubmittingAdvisory(true);
    await apiService.sendUserAdvisory(advisoryTarget.id, advisoryTitle.trim(), advisoryBody.trim());
    setIsSubmittingAdvisory(false);
    setIsAdvisoryModalOpen(false);
    setAdvisoryTarget(null);
    loadData();
  };

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newFullName.trim() || !newEmail.trim()) return;

    await apiService.addFarmer({
      fullName: newFullName.trim(),
      email: newEmail.trim(),
      phoneNumber: newPhone.trim() || '+63 900 000 0000',
      role: newRole,
      status: 'ACTIVE',
      farmName: newFarmName.trim() || `${newFullName.trim()}'s Smallholder Farm`,
      activePlotsCount: 4,
      deviceInfo: 'Android Mobile (Pending Activation)',
    });

    setIsAddModalOpen(false);
    setNewFullName('');
    setNewEmail('');
    setNewPhone('');
    setNewFarmName('');
    loadData();
  };

  const handleExportCSV = () => {
    const headers = ['ID', 'Full Name', 'Email', 'Phone', 'Role', 'Status', 'Farm Name', 'Plots', 'Last Active', 'Days Inactive'];
    const rows = filteredFarmers.map((f) => [
      f.id,
      `"${f.fullName}"`,
      f.email,
      f.phoneNumber || 'N/A',
      f.role,
      f.status,
      `"${f.farmName}"`,
      f.activePlotsCount,
      `"${f.lastActiveAt || 'N/A'}"`,
      f.daysInactive ?? 0,
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map((e) => e.join(','))].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `maptanim_users_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Filter Logic
  const filteredFarmers = farmers.filter((f) => {
    const matchesSearch =
      f.fullName.toLowerCase().includes(search.toLowerCase()) ||
      f.email.toLowerCase().includes(search.toLowerCase()) ||
      f.farmName.toLowerCase().includes(search.toLowerCase()) ||
      (f.phoneNumber && f.phoneNumber.includes(search));

    const matchesStatus = statusFilter === 'ALL' || f.status === statusFilter;
    const matchesRole = roleFilter === 'ALL' || f.role === roleFilter;

    return matchesSearch && matchesStatus && matchesRole;
  });

  const inactiveFarmers = farmers.filter((f) => f.status === 'INACTIVE');
  const activeFarmersCount = farmers.filter((f) => f.status === 'ACTIVE').length;
  const inactiveFarmersCount = farmers.filter((f) => f.status === 'INACTIVE').length;
  const suspendedCount = farmers.filter((f) => f.status === 'SUSPENDED').length;
  const pendingCount = farmers.filter((f) => f.status === 'PENDING').length;
  const activeRate = farmers.length > 0 ? Math.round((activeFarmersCount / farmers.length) * 1000) / 10 : 0;

  // Real-time donut chart dataset
  const donutData = [
    { name: 'Active (Engaged)', value: activeFarmersCount || 1, color: '#4CAF50' },
    { name: 'Inactive / Dormant', value: inactiveFarmersCount || 0, color: '#F4A261' },
    { name: 'Pending Approval', value: pendingCount || 0, color: '#00BCD4' },
    { name: 'Suspended', value: suspendedCount || 0, color: '#E76F51' },
  ].filter((d) => d.value > 0);

  return (
    <div className="space-y-6 animate-fadeIn pb-12">
      {/* ─── 1. TOP HEADER & PRIMARY ACTIONS ─── */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold text-[#F4F4F4] tracking-tight flex items-center gap-2.5">
              <Users className="w-6 h-6 text-[#4CAF50]" />
              <span>User Management & Tracking</span>
            </h1>
            <span className="text-[11px] font-mono px-2 py-0.5 rounded-full bg-[#4CAF50]/15 text-[#4CAF50] font-bold border border-[#4CAF50]/30">
              Live Monitor
            </span>
          </div>
          <p className="text-xs text-[#C7D0D8] mt-1">
            Track smallholder farmer engagement, identify active vs dormant accounts, and manage system access.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          <button
            onClick={handleExportCSV}
            className="px-3.5 py-2 text-xs font-semibold rounded-xl bg-[#2B3136] border border-[#38434D] hover:bg-[#38434D] text-[#F4F4F4] shadow-xs flex items-center gap-2 transition cursor-pointer"
            title="Download CSV report"
          >
            <Download className="w-3.5 h-3.5 text-[#8A9BA8]" />
            <span>Export CSV</span>
          </button>

          <button
            onClick={() => setIsAddModalOpen(true)}
            className="px-4 py-2 text-xs font-semibold rounded-xl bg-[#4CAF50] hover:bg-[#388E3C] text-white shadow-xs flex items-center gap-2 transition cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Add User</span>
          </button>

          <button
            onClick={loadData}
            disabled={loading}
            className="p-2 text-xs rounded-xl bg-[#2B3136] border border-[#38434D] hover:bg-[#38434D] text-[#8A9BA8] transition cursor-pointer shadow-xs"
            title="Refresh database records"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-[#4CAF50]' : ''}`} />
          </button>
        </div>
      </div>

      {/* ─── 2. KPI METRIC CARDS (Active vs Inactive Tracking Focus) ─── */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Card 1: Total Smallholders */}
        <div className="bg-[#2B3136] p-5 rounded-2xl border border-[#38434D] shadow-xs relative overflow-hidden flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-[#8A9BA8] uppercase tracking-wider">
              Total Users
            </span>
            <div className="w-8 h-8 rounded-lg bg-[#00BCD4]/15 text-[#00BCD4] flex items-center justify-center">
              <Users className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-bold text-[#F4F4F4] font-mono">
              {farmers.length}
            </span>
            <span className="text-xs text-[#4CAF50] font-semibold flex items-center gap-0.5">
              <TrendingUp className="w-3 h-3" /> +8% mo
            </span>
          </div>
          <p className="text-[11px] text-[#8A9BA8] mt-1 truncate">
            Registered smallholders & field officers
          </p>
          <div className="h-1 w-full bg-[#1D2429] rounded-full mt-3 overflow-hidden">
            <div className="h-full bg-[#00BCD4] rounded-full" style={{ width: '100%' }} />
          </div>
        </div>

        {/* Card 2: Active Users (Tracking Active) */}
        <div className="bg-[#2B3136] p-5 rounded-2xl border border-[#4CAF50]/30 shadow-xs relative overflow-hidden flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-[#4CAF50] uppercase tracking-wider flex items-center gap-1.5">
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#4CAF50] opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-[#4CAF50]"></span>
              </span>
              Active Users
            </span>
            <div className="w-8 h-8 rounded-lg bg-[#4CAF50]/15 text-[#4CAF50] flex items-center justify-center">
              <UserCheck className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-bold text-[#F4F4F4] font-mono">
              {activeFarmersCount}
            </span>
            <span className="text-xs text-[#4CAF50] font-bold bg-[#4CAF50]/15 px-1.5 py-0.5 rounded">
              {activeRate}% of total
            </span>
          </div>
          <p className="text-[11px] text-[#C7D0D8] mt-1">
            Active within 7 days • Farm tasks in progress
          </p>
          <div className="h-1 w-full bg-[#1D2429] rounded-full mt-3 overflow-hidden">
            <div className="h-full bg-[#4CAF50] rounded-full" style={{ width: `${activeRate}%` }} />
          </div>
        </div>

        {/* Card 3: Inactive Users (Tracking Not Active) */}
        <div className="bg-[#2B3136] p-5 rounded-2xl border border-[#F4A261]/30 shadow-xs relative overflow-hidden flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-[#F4A261] uppercase tracking-wider flex items-center gap-1.5">
              <UserMinus className="w-3.5 h-3.5 text-[#F4A261]" />
              Inactive Users
            </span>
            <div className="w-8 h-8 rounded-lg bg-[#F4A261]/15 text-[#F4A261] flex items-center justify-center">
              <Clock className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-bold text-[#F4F4F4] font-mono">
              {inactiveFarmersCount}
            </span>
            <span className="text-xs text-[#F4A261] font-semibold bg-[#F4A261]/15 px-1.5 py-0.5 rounded">
              {farmers.length > 0 ? Math.round((inactiveFarmersCount / farmers.length) * 100) : 0}% dormant
            </span>
          </div>
          <p className="text-[11px] text-[#C7D0D8] mt-1">
            No activity &gt; 14 days • At risk of drop-off
          </p>
          <div className="h-1 w-full bg-[#1D2429] rounded-full mt-3 overflow-hidden">
            <div
              className="h-full bg-[#F4A261] rounded-full"
              style={{ width: `${farmers.length > 0 ? (inactiveFarmersCount / farmers.length) * 100 : 0}%` }}
            />
          </div>
        </div>

        {/* Card 4: Moderation / Pending / Suspended */}
        <div className="bg-[#2B3136] p-5 rounded-2xl border border-[#38434D] shadow-xs relative overflow-hidden flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-[#8A9BA8] uppercase tracking-wider">
              Pending & Suspended
            </span>
            <div className="w-8 h-8 rounded-lg bg-[#E76F51]/15 text-[#E76F51] flex items-center justify-center">
              <AlertTriangle className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-bold text-[#F4F4F4] font-mono">
              {suspendedCount + pendingCount}
            </span>
            <div className="flex items-center gap-1.5 text-[10px] font-bold">
              <span className="px-1.5 py-0.5 bg-[#00BCD4]/15 text-[#00BCD4] rounded">
                {pendingCount} Pending
              </span>
              <span className="px-1.5 py-0.5 bg-[#E76F51]/15 text-[#E76F51] rounded">
                {suspendedCount} Suspended
              </span>
            </div>
          </div>
          <p className="text-[11px] text-[#8A9BA8] mt-1">
            Requires admin moderation or identity review
          </p>
          <div className="h-1 w-full bg-[#1D2429] rounded-full mt-3 overflow-hidden">
            <div
              className="h-full bg-[#E76F51] rounded-full"
              style={{ width: `${farmers.length > 0 ? ((suspendedCount + pendingCount) / farmers.length) * 100 : 0}%` }}
            />
          </div>
        </div>
      </div>

      {/* ─── 3. CHART TRACKING SECTION (Active vs Inactive & Trend Analytics) ─── */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
        {/* Chart 1: Active vs Inactive Distribution Donut Chart */}
        <div className="bg-[#2B3136] p-5 rounded-2xl border border-[#38434D] shadow-xs flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-[#F4F4F4] flex items-center gap-2">
                <Activity className="w-4 h-4 text-[#4CAF50]" />
                <span>Account Status Distribution</span>
              </h3>
              <span className="text-[10px] text-[#C7D0D8] font-semibold bg-[#1D2429] px-2 py-0.5 rounded-md border border-[#38434D]">
                Real-time
              </span>
            </div>
            <p className="text-xs text-[#C7D0D8] mt-0.5">
              Active engagement vs dormant and restricted users
            </p>
          </div>

          <div className="h-56 w-full relative flex items-center justify-center my-2">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={donutData}
                  cx="50%"
                  cy="50%"
                  innerRadius={55}
                  outerRadius={80}
                  paddingAngle={4}
                  dataKey="value"
                >
                  {donutData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#183145',
                    borderColor: '#38434D',
                    borderRadius: '8px',
                    color: '#F4F4F4',
                    fontSize: '11px',
                  }}
                  formatter={(val: number, name: string) => [`${val} smallholders`, name]}
                />
              </PieChart>
            </ResponsiveContainer>

            {/* Centered Donut KPI Metric */}
            <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
              <span className="text-2xl font-black text-[#F4F4F4] font-mono leading-none">
                {activeRate}%
              </span>
              <span className="text-[10px] text-[#4CAF50] font-bold mt-1">
                Active Rate
              </span>
            </div>
          </div>

          {/* Donut Legend */}
          <div className="grid grid-cols-2 gap-2 pt-2 border-t border-[#38434D]">
            {donutData.map((item) => (
              <div key={item.name} className="flex items-center justify-between text-xs">
                <div className="flex items-center gap-1.5 truncate">
                  <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: item.color }} />
                  <span className="text-[#C7D0D8] truncate text-[11px]">{item.name}</span>
                </div>
                <span className="font-mono font-bold text-[#F4F4F4] text-xs shrink-0 pl-1">
                  {item.value}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Chart 2: Active vs Inactive Over Time (Area Chart) */}
        <div className="bg-[#2B3136] p-5 rounded-2xl border border-[#38434D] shadow-xs flex flex-col justify-between lg:col-span-2">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <div>
              <h3 className="text-sm font-bold text-[#F4F4F4] flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-[#00BCD4]" />
                <span>User Activity & Dormancy Trend (7-Day Tracking)</span>
              </h3>
              <p className="text-xs text-[#C7D0D8] mt-0.5">
                Comparison of daily engaged active users vs dormant smallholders
              </p>
            </div>
            <div className="flex items-center gap-3 text-[11px] text-[#C7D0D8]">
              <span className="flex items-center gap-1">
                <span className="w-2.5 h-2.5 rounded-full bg-[#4CAF50]" /> Active Users
              </span>
              <span className="flex items-center gap-1">
                <span className="w-2.5 h-2.5 rounded-full bg-[#F4A261]" /> Inactive / Dormant
              </span>
            </div>
          </div>

          <div className="h-64 w-full mt-4">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={metrics?.activityTrends || []} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="activeGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#4CAF50" stopOpacity={0.25} />
                    <stop offset="95%" stopColor="#4CAF50" stopOpacity={0} />
                  </linearGradient>
                  <linearGradient id="inactiveGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#F4A261" stopOpacity={0.2} />
                    <stop offset="95%" stopColor="#F4A261" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#38434D" />
                <XAxis dataKey="period" stroke="#8A9BA8" fontSize={11} tickLine={false} />
                <YAxis stroke="#8A9BA8" fontSize={11} tickLine={false} axisLine={false} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#183145',
                    borderColor: '#38434D',
                    borderRadius: '8px',
                    color: '#F4F4F4',
                    fontSize: '11px',
                  }}
                />
                <Area
                  type="monotone"
                  dataKey="active"
                  name="Active Users"
                  stroke="#4CAF50"
                  strokeWidth={2.5}
                  fillOpacity={1}
                  fill="url(#activeGradient)"
                />
                <Area
                  type="monotone"
                  dataKey="inactive"
                  name="Inactive / Dormant"
                  stroke="#F4A261"
                  strokeWidth={2}
                  strokeDasharray="4 4"
                  fillOpacity={1}
                  fill="url(#inactiveGradient)"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          <div className="pt-3 border-t border-[#38434D] flex items-center justify-between text-xs text-[#C7D0D8]">
            <span className="flex items-center gap-1.5">
              <CheckCircle2 className="w-3.5 h-3.5 text-[#4CAF50]" />
              Weekly retention rate: <strong className="text-[#F4F4F4]">86.2%</strong>
            </span>
            <span className="text-[11px] text-[#8A9BA8]">Updated hourly from Supabase synchronization</span>
          </div>
        </div>
      </div>

      {/* ─── 4. SUB-TAB NAVIGATION BAR ─── */}
      <div className="flex items-center justify-between border-b border-[#38434D] pb-1">
        <div className="flex items-center gap-2">
          <button
            onClick={() => setActiveSubTab('directory')}
            className={`px-4 py-2 text-xs font-bold rounded-t-xl transition cursor-pointer flex items-center gap-2 border-b-2 ${
              activeSubTab === 'directory'
                ? 'border-[#4CAF50] text-[#4CAF50] bg-[#4CAF50]/10'
                : 'border-transparent text-[#8A9BA8] hover:text-[#F4F4F4]'
            }`}
          >
            <Users className="w-4 h-4" />
            <span>User Directory & Management ({farmers.length})</span>
          </button>

          <button
            onClick={() => setActiveSubTab('tracking')}
            className={`px-4 py-2 text-xs font-bold rounded-t-xl transition cursor-pointer flex items-center gap-2 border-b-2 ${
              activeSubTab === 'tracking'
                ? 'border-[#F4A261] text-[#F4A261] bg-[#F4A261]/10'
                : 'border-transparent text-[#8A9BA8] hover:text-[#F4F4F4]'
            }`}
          >
            <Clock className="w-4 h-4" />
            <span>Inactive Smallholder Watchlist ({inactiveFarmers.length})</span>
          </button>

          <button
            onClick={() => setActiveSubTab('activity')}
            className={`px-4 py-2 text-xs font-bold rounded-t-xl transition cursor-pointer flex items-center gap-2 border-b-2 ${
              activeSubTab === 'activity'
                ? 'border-[#00BCD4] text-[#00BCD4] bg-[#00BCD4]/10'
                : 'border-transparent text-[#8A9BA8] hover:text-[#F4F4F4]'
            }`}
          >
            <Radio className="w-4 h-4 text-[#00BCD4]" />
            <span>Live Activity Audit Feed</span>
          </button>
        </div>
      </div>

      {/* ─── TAB 1: USER DIRECTORY & MANAGEMENT ─── */}
      {activeSubTab === 'directory' && (
        <div className="space-y-4">
          {/* Search & Multi-Filters */}
          <div className="bg-[#2B3136] p-4 rounded-2xl border border-[#38434D] shadow-xs flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between">
            <div className="relative flex-1">
              <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-[#8A9BA8]" />
              <input
                type="text"
                placeholder="Search by farmer name, email, farm name, or phone..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="w-full text-xs pl-10 pr-4 py-2.5 rounded-xl border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] placeholder:text-[#8A9BA8] focus:outline-none focus:border-[#4CAF50] transition"
              />
            </div>

            <div className="flex flex-wrap items-center gap-2.5">
              <div className="flex items-center gap-1.5 bg-[#1D2429] px-2.5 py-1.5 rounded-xl border border-[#38434D]">
                <Filter className="w-3.5 h-3.5 text-[#8A9BA8]" />
                <select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                  className="bg-transparent text-xs font-semibold text-[#F4F4F4] focus:outline-none cursor-pointer"
                >
                  <option value="ALL" className="bg-[#2B3136] text-[#F4F4F4]">All Status</option>
                  <option value="ACTIVE" className="bg-[#2B3136] text-[#F4F4F4]">🟢 Active Only</option>
                  <option value="INACTIVE" className="bg-[#2B3136] text-[#F4F4F4]">🟡 Inactive / Dormant</option>
                  <option value="PENDING" className="bg-[#2B3136] text-[#F4F4F4]">🔵 Pending Approval</option>
                  <option value="SUSPENDED" className="bg-[#2B3136] text-[#F4F4F4]">🔴 Suspended</option>
                </select>
              </div>

              <div className="flex items-center gap-1.5 bg-[#1D2429] px-2.5 py-1.5 rounded-xl border border-[#38434D]">
                <ShieldCheck className="w-3.5 h-3.5 text-[#8A9BA8]" />
                <select
                  value={roleFilter}
                  onChange={(e) => setRoleFilter(e.target.value)}
                  className="bg-transparent text-xs font-semibold text-[#F4F4F4] focus:outline-none cursor-pointer"
                >
                  <option value="ALL" className="bg-[#2B3136] text-[#F4F4F4]">All Roles</option>
                  <option value="FARMER" className="bg-[#2B3136] text-[#F4F4F4]">Smallholder Farmer</option>
                  <option value="GUEST" className="bg-[#2B3136] text-[#F4F4F4]">Guest / Anonymous</option>
                </select>
              </div>
            </div>
          </div>

          {/* Desktop Table View */}
          <div className="hidden md:block bg-[#2B3136] rounded-2xl border border-[#38434D] shadow-xs overflow-hidden">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#183145] border-b border-[#38434D] text-[#C7D0D8] font-bold">
                <tr>
                  <th className="py-3.5 px-4">User Profile</th>
                  <th className="py-3.5 px-4">Role</th>
                  <th className="py-3.5 px-4">Farm & Plots</th>
                  <th className="py-3.5 px-4">Tracking Status</th>
                  <th className="py-3.5 px-4">Last Activity</th>
                  <th className="py-3.5 px-4">Device / Client</th>
                  <th className="py-3.5 px-4 text-right">Account Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#38434D] text-[#F4F4F4]">
                {filteredFarmers.map((farmer) => {
                  const isUserActive = farmer.status === 'ACTIVE';
                  const isUserInactive = farmer.status === 'INACTIVE';

                  return (
                    <tr key={farmer.id} className="hover:bg-[#183145]/60 transition">
                      {/* User Profile */}
                      <td className="py-3.5 px-4">
                        <div className="flex items-center gap-3">
                          <div className="relative">
                            <div
                              className={`w-9 h-9 rounded-full font-bold flex items-center justify-center text-xs border ${
                                farmer.role === 'GUEST'
                                  ? 'bg-[#F4A261]/15 text-[#F4A261] border-[#F4A261]/35'
                                  : 'bg-[#4CAF50]/15 text-[#4CAF50] border-[#4CAF50]/30'
                              }`}
                            >
                              {farmer.fullName.charAt(0)}
                            </div>
                            {/* Online / Active indicator pulse */}
                            <span
                              className={`absolute bottom-0 right-0 w-2.5 h-2.5 rounded-full border-2 border-[#2B3136] ${
                                isUserActive ? 'bg-[#4CAF50]' : isUserInactive ? 'bg-[#F4A261]' : 'bg-[#E76F51]'
                              }`}
                            />
                          </div>
                          <div>
                            <p className="font-bold text-xs text-[#F4F4F4] flex items-center gap-1.5">
                              <span>{farmer.fullName}</span>
                              {farmer.isOnline && (
                                <span className="text-[9px] px-1.5 py-0.2 bg-[#4CAF50]/15 text-[#4CAF50] rounded font-semibold">
                                  Online
                                </span>
                              )}
                            </p>
                            <p className="text-[11px] text-[#8A9BA8] font-mono">{farmer.email}</p>
                          </div>
                        </div>
                      </td>

                      {/* Role */}
                      <td className="py-3.5 px-4">
                        <span
                          className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                            farmer.role === 'GUEST'
                              ? 'bg-[#F4A261]/20 text-[#F4A261] border border-[#F4A261]/30'
                              : 'bg-[#8A9BA8]/15 text-[#C7D0D8]'
                          }`}
                        >
                          {farmer.role === 'GUEST' ? 'GUEST / ANON' : 'FARMER'}
                        </span>
                      </td>

                      {/* Farm & Plots */}
                      <td className="py-3.5 px-4">
                        <p className="font-semibold text-xs text-[#F4F4F4]">{farmer.farmName}</p>
                        <p className="text-[11px] text-[#4CAF50] font-mono font-bold">
                          {farmer.activePlotsCount} active plots
                        </p>
                      </td>

                      {/* Tracking Status Badge */}
                      <td className="py-3.5 px-4">
                        <div className="flex items-center gap-1.5">
                          <span
                            className={`inline-flex items-center gap-1 text-[11px] font-bold px-2 py-0.5 rounded-full ${
                              farmer.status === 'ACTIVE'
                                ? 'bg-[#4CAF50]/15 text-[#4CAF50]'
                                : farmer.status === 'INACTIVE'
                                ? 'bg-[#F4A261]/15 text-[#F4A261]'
                                : farmer.status === 'PENDING'
                                ? 'bg-[#00BCD4]/15 text-[#00BCD4]'
                                : 'bg-[#E76F51]/15 text-[#E76F51]'
                            }`}
                          >
                            <span
                              className={`w-1.5 h-1.5 rounded-full ${
                                farmer.status === 'ACTIVE'
                                ? 'bg-[#4CAF50]'
                                : farmer.status === 'INACTIVE'
                                ? 'bg-[#F4A261]'
                                : farmer.status === 'PENDING'
                                ? 'bg-[#00BCD4]'
                                : 'bg-[#E76F51]'
                              }`}
                            />
                            {farmer.status}
                          </span>
                        </div>
                      </td>

                      {/* Last Activity */}
                      <td className="py-3.5 px-4">
                        <p className="text-xs font-medium text-[#F4F4F4]">
                          {farmer.lastActiveAt || 'Active Today'}
                        </p>
                        {farmer.daysInactive && farmer.daysInactive > 0 ? (
                          <p className="text-[10px] font-semibold text-[#F4A261]">
                            {farmer.daysInactive} days dormant
                          </p>
                        ) : (
                          <p className="text-[10px] text-[#4CAF50]">Recently synchronized</p>
                        )}
                      </td>

                      {/* Device / Client */}
                      <td className="py-3.5 px-4">
                        <p className="text-xs text-[#C7D0D8] flex items-center gap-1 truncate max-w-[150px]">
                          <Smartphone className="w-3.5 h-3.5 text-[#8A9BA8] shrink-0" />
                          <span className="truncate">{farmer.deviceInfo || 'Android Client'}</span>
                        </p>
                      </td>

                      {/* Actions */}
                      <td className="py-3.5 px-4 text-right">
                        <div className="flex items-center justify-end gap-1.5">
                          {/* View Details */}
                          <button
                            onClick={() => setSelectedFarmer(farmer)}
                            className="p-1.5 rounded-lg text-[#8A9BA8] hover:text-[#F4F4F4] hover:bg-[#1D2429] transition cursor-pointer"
                            title="Inspect User Profile & Plots"
                          >
                            <Eye className="w-4 h-4" />
                          </button>

                          {/* Quick Advisory / Push Message */}
                          <button
                            onClick={() => handleOpenAdvisoryModal(farmer)}
                            className="p-1.5 rounded-lg text-[#00BCD4] hover:bg-[#00BCD4]/15 transition cursor-pointer"
                            title="Send Push Advisory / SMS"
                          >
                            <Send className="w-3.5 h-3.5" />
                          </button>

                          {/* Activate / Inactivate Toggle */}
                          {farmer.status === 'ACTIVE' ? (
                            <button
                              onClick={() => handleToggleStatus(farmer, 'INACTIVE')}
                              className="p-1.5 rounded-lg text-[#F4A261] hover:bg-[#F4A261]/15 transition cursor-pointer"
                              title="Mark as Inactive / Dormant"
                            >
                              <UserMinus className="w-4 h-4" />
                            </button>
                          ) : (
                            <button
                              onClick={() => handleToggleStatus(farmer, 'ACTIVE')}
                              className="p-1.5 rounded-lg text-[#4CAF50] hover:bg-[#4CAF50]/15 transition cursor-pointer"
                              title="Re-activate Account"
                            >
                              <UserCheck className="w-4 h-4" />
                            </button>
                          )}

                          {/* Suspend Toggle */}
                          <button
                            onClick={() =>
                              handleToggleStatus(farmer, farmer.status === 'SUSPENDED' ? 'ACTIVE' : 'SUSPENDED')
                            }
                            className={`p-1.5 rounded-lg transition cursor-pointer ${
                              farmer.status === 'SUSPENDED'
                                ? 'text-[#4CAF50] hover:bg-[#4CAF50]/15'
                                : 'text-[#E76F51] hover:bg-[#E76F51]/15'
                            }`}
                            title={farmer.status === 'SUSPENDED' ? 'Unsuspend Account' : 'Suspend Account'}
                          >
                            <UserX className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {/* Mobile Card List (< md) */}
          <div className="grid grid-cols-1 gap-3 md:hidden">
            {filteredFarmers.map((farmer) => (
              <div key={farmer.id} className="bg-[#2B3136] p-4 rounded-2xl border border-[#38434D] shadow-xs space-y-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div
                      className={`w-10 h-10 rounded-full font-bold flex items-center justify-center text-xs border ${
                        farmer.role === 'GUEST'
                          ? 'bg-[#F4A261]/15 text-[#F4A261] border-[#F4A261]/35'
                          : 'bg-[#4CAF50]/15 text-[#4CAF50] border-[#4CAF50]/30'
                      }`}
                    >
                      {farmer.fullName.charAt(0)}
                    </div>
                    <div>
                      <div className="flex items-center gap-1.5">
                        <h4 className="font-bold text-[#F4F4F4] text-sm">{farmer.fullName}</h4>
                        <span
                          className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full ${
                            farmer.role === 'GUEST'
                              ? 'bg-[#F4A261]/20 text-[#F4A261] border border-[#F4A261]/30'
                              : 'bg-[#8A9BA8]/15 text-[#C7D0D8]'
                          }`}
                        >
                          {farmer.role === 'GUEST' ? 'GUEST' : 'FARMER'}
                        </span>
                      </div>
                      <p className="text-[11px] text-[#8A9BA8] font-mono">{farmer.email}</p>
                    </div>
                  </div>
                  <Badge
                    variant={
                      farmer.status === 'ACTIVE'
                        ? 'success'
                        : farmer.status === 'INACTIVE'
                        ? 'warning'
                        : farmer.status === 'PENDING'
                        ? 'neutral'
                        : 'danger'
                    }
                  >
                    {farmer.status}
                  </Badge>
                </div>

                <div className="p-3 rounded-xl bg-[#1D2429] text-xs space-y-1.5 border border-[#38434D]">
                  <div className="flex justify-between">
                    <span className="text-[#8A9BA8]">Farm:</span>
                    <span className="font-bold text-[#F4F4F4]">{farmer.farmName}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-[#8A9BA8]">Plots:</span>
                    <span className="font-mono text-[#4CAF50] font-bold">{farmer.activePlotsCount} plots</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-[#8A9BA8]">Last Active:</span>
                    <span className="font-medium text-[#F4F4F4]">{farmer.lastActiveAt || 'Active Today'}</span>
                  </div>
                </div>

                <div className="flex items-center justify-end gap-2 pt-1">
                  <button
                    onClick={() => handleOpenAdvisoryModal(farmer)}
                    className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-[#00BCD4]/15 text-[#00BCD4] flex items-center gap-1"
                  >
                    <Send className="w-3 h-3" />
                    <span>Advisory</span>
                  </button>
                  <button
                    onClick={() => setSelectedFarmer(farmer)}
                    className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-[#1D2429] text-[#F4F4F4] border border-[#38434D]"
                  >
                    Details
                  </button>
                  <button
                    onClick={() => handleToggleStatus(farmer)}
                    className={`px-3 py-1.5 text-xs font-semibold rounded-lg text-white ${
                      farmer.status === 'ACTIVE' ? 'bg-[#F4A261]' : 'bg-[#4CAF50]'
                    }`}
                  >
                    {farmer.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ─── TAB 2: INACTIVE SMALLHOLDER WATCHLIST ─── */}
      {activeSubTab === 'tracking' && (
        <div className="space-y-4">
          <div className="bg-[#2B3136] border border-[#F4A261]/40 rounded-2xl p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div className="flex items-start sm:items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#F4A261]/15 text-[#F4A261] flex items-center justify-center shrink-0">
                <AlertTriangle className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-sm font-bold text-[#F4A261]">
                  Inactive & Dormant Smallholder Watchlist ({inactiveFarmers.length} detected)
                </h4>
                <p className="text-xs text-[#C7D0D8] mt-0.5">
                  Smallholders who have not synchronized or logged crop activity for over 14 days. Re-engagement advisories can be dispatched directly.
                </p>
              </div>
            </div>
            <button
              onClick={() => {
                if (inactiveFarmers.length > 0) {
                  handleOpenAdvisoryModal(inactiveFarmers[0]);
                }
              }}
              className="px-4 py-2 text-xs font-bold rounded-xl bg-[#F4A261] hover:bg-[#E76F51] text-white transition shrink-0 cursor-pointer shadow-xs flex items-center gap-2"
            >
              <Send className="w-3.5 h-3.5" />
              <span>Broadcast Re-engagement Advisory</span>
            </button>
          </div>

          <div className="bg-[#2B3136] rounded-2xl border border-[#38434D] shadow-xs overflow-hidden">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#183145] border-b border-[#38434D] text-[#C7D0D8] font-bold">
                <tr>
                  <th className="py-3.5 px-4">Smallholder</th>
                  <th className="py-3.5 px-4">Farm & Location</th>
                  <th className="py-3.5 px-4">Dormancy Duration</th>
                  <th className="py-3.5 px-4">Last Event Recorded</th>
                  <th className="py-3.5 px-4 text-right">Re-engagement Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#38434D] text-[#F4F4F4]">
                {inactiveFarmers.map((f) => (
                  <tr key={f.id} className="hover:bg-[#183145]/60 transition">
                    <td className="py-3.5 px-4">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-[#F4A261]/15 text-[#F4A261] font-bold flex items-center justify-center text-xs">
                          {f.fullName.charAt(0)}
                        </div>
                        <div>
                          <p className="font-bold text-xs text-[#F4F4F4]">{f.fullName}</p>
                          <p className="text-[11px] text-[#8A9BA8] font-mono">{f.email}</p>
                        </div>
                      </div>
                    </td>
                    <td className="py-3.5 px-4">
                      <p className="font-semibold text-xs text-[#F4F4F4]">{f.farmName}</p>
                      <p className="text-[11px] text-[#C7D0D8]">{f.activePlotsCount} plots allocated</p>
                    </td>
                    <td className="py-3.5 px-4">
                      <div className="flex items-center gap-2">
                        <span className="px-2 py-0.5 rounded-full bg-[#F4A261]/15 text-[#F4A261] font-bold text-[11px]">
                          {f.daysInactive || 14} days inactive
                        </span>
                      </div>
                    </td>
                    <td className="py-3.5 px-4">
                      <p className="text-xs text-[#C7D0D8]">{f.activitySummary || 'Offline synchronization pending'}</p>
                      <p className="text-[10px] text-[#8A9BA8] mt-0.5">Last login: {new Date(f.lastLoginAt).toLocaleDateString()}</p>
                    </td>
                    <td className="py-3.5 px-4 text-right">
                      <button
                        onClick={() => handleOpenAdvisoryModal(f)}
                        className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-[#F4A261] hover:bg-[#E76F51] text-white flex items-center gap-1.5 ml-auto transition cursor-pointer shadow-xs"
                      >
                        <Send className="w-3 h-3" />
                        <span>Re-engage Smallholder</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ─── TAB 3: REAL-TIME USER ACTIVITY AUDIT STREAM ─── */}
      {activeSubTab === 'activity' && (
        <div className="bg-[#2B3136] rounded-2xl border border-[#38434D] shadow-xs p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#38434D] pb-3">
            <div>
              <h3 className="text-sm font-bold text-[#F4F4F4] flex items-center gap-2">
                <Radio className="w-4 h-4 text-[#00BCD4]" />
                <span>Live Smallholder Activity & Audit Feed</span>
              </h3>
              <p className="text-xs text-[#C7D0D8] mt-0.5">
                Real-time operational events from Android mobile app and Web admin
              </p>
            </div>
            <button
              onClick={loadData}
              className="text-xs font-semibold text-[#00BCD4] hover:underline cursor-pointer"
            >
              Refresh Feed
            </button>
          </div>

          <div className="divide-y divide-[#38434D]">
            {activityLogs.map((log) => (
              <div key={log.id} className="py-3.5 flex items-start justify-between gap-4">
                <div className="flex items-start gap-3">
                  <div
                    className={`w-8 h-8 rounded-xl flex items-center justify-center shrink-0 mt-0.5 ${
                      log.status === 'ONLINE'
                        ? 'bg-[#4CAF50]/15 text-[#4CAF50]'
                        : log.status === 'ACTIVE'
                        ? 'bg-[#00BCD4]/15 text-[#00BCD4]'
                        : 'bg-[#F4A261]/15 text-[#F4A261]'
                    }`}
                  >
                    <Activity className="w-4 h-4" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="font-bold text-xs text-[#F4F4F4]">{log.userName}</span>
                      <span className="text-[10px] font-mono font-bold px-1.5 py-0.5 rounded bg-[#1D2429] text-[#C7D0D8] border border-[#38434D]">
                        {log.module}
                      </span>
                    </div>
                    <p className="text-xs text-[#C7D0D8] mt-1">{log.details}</p>
                  </div>
                </div>
                <div className="text-right shrink-0">
                  <span className="text-[11px] font-mono text-[#8A9BA8]">{log.timestamp}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ─── MODAL 1: USER DETAILS & PROFILE MODAL ─── */}
      {selectedFarmer && (
        <Modal
          isOpen={!!selectedFarmer}
          onClose={() => setSelectedFarmer(null)}
          title="Smallholder Profile & Tracking Diagnostics"
          size="lg"
        >
          <div className="space-y-4 text-xs text-[#F4F4F4]">
            <div className="p-4 rounded-2xl bg-[#1D2429] border border-[#38434D] flex items-center gap-4">
              <div className="w-14 h-14 rounded-2xl bg-[#4CAF50]/15 text-[#4CAF50] border border-[#4CAF50]/30 flex items-center justify-center font-black text-2xl">
                {selectedFarmer.fullName.charAt(0)}
              </div>
              <div className="flex-1 min-w-0">
                <h4 className="text-base font-bold text-[#F4F4F4] truncate">{selectedFarmer.fullName}</h4>
                <p className="text-[#8A9BA8] font-mono mt-0.5 truncate">{selectedFarmer.email}</p>
                <div className="flex items-center gap-2 mt-2">
                  <Badge
                    variant={
                      selectedFarmer.status === 'ACTIVE'
                        ? 'success'
                        : selectedFarmer.status === 'INACTIVE'
                        ? 'warning'
                        : selectedFarmer.status === 'PENDING'
                        ? 'neutral'
                        : 'danger'
                    }
                  >
                    {selectedFarmer.status}
                  </Badge>
                  <span className="text-[10px] text-[#8A9BA8] font-mono">
                    ID: {selectedFarmer.id}
                  </span>
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div className="p-3.5 rounded-xl border border-[#38434D] bg-[#1D2429] space-y-1">
                <p className="text-[#8A9BA8] font-bold uppercase text-[10px]">Contact Information</p>
                <p className="font-semibold text-xs flex items-center gap-2 mt-1">
                  <Mail className="w-3.5 h-3.5 text-[#4CAF50]" /> {selectedFarmer.email}
                </p>
                <p className="font-semibold text-xs flex items-center gap-2">
                  <Phone className="w-3.5 h-3.5 text-[#4CAF50]" /> {selectedFarmer.phoneNumber || 'Unspecified'}
                </p>
              </div>

              <div className="p-3.5 rounded-xl border border-[#38434D] bg-[#1D2429] space-y-1">
                <p className="text-[#8A9BA8] font-bold uppercase text-[10px]">Farm Plots & Allocations</p>
                <p className="font-bold text-xs text-[#F4F4F4] mt-1">{selectedFarmer.farmName}</p>
                <p className="text-[#4CAF50] font-mono font-bold">{selectedFarmer.activePlotsCount} active plots</p>
              </div>
            </div>

            <div className="p-3.5 rounded-xl border border-[#38434D] bg-[#1D2429] space-y-2">
              <p className="text-[#8A9BA8] font-bold uppercase text-[10px]">Activity & Client Tracking</p>
              <div className="grid grid-cols-2 gap-2 text-xs">
                <div>
                  <span className="text-[#8A9BA8]">Last Activity:</span>
                  <p className="font-bold">{selectedFarmer.lastActiveAt || 'Active Today'}</p>
                </div>
                <div>
                  <span className="text-[#8A9BA8]">Client Device:</span>
                  <p className="font-mono text-[11px] truncate">{selectedFarmer.deviceInfo || 'Android MapTanim'}</p>
                </div>
              </div>
            </div>

            {/* Quick Status Modifiers */}
            <div className="p-3.5 rounded-xl border border-[#38434D] bg-[#1D2429] space-y-2">
              <p className="text-[#8A9BA8] font-bold uppercase text-[10px]">Administrative Controls</p>
              <div className="flex flex-wrap gap-2">
                <button
                  onClick={() => handleToggleStatus(selectedFarmer, 'ACTIVE')}
                  className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition cursor-pointer ${
                    selectedFarmer.status === 'ACTIVE'
                      ? 'bg-[#4CAF50] text-white'
                      : 'bg-[#2B3136] text-[#C7D0D8] hover:bg-[#38434D]'
                  }`}
                >
                  Set Active
                </button>
                <button
                  onClick={() => handleToggleStatus(selectedFarmer, 'INACTIVE')}
                  className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition cursor-pointer ${
                    selectedFarmer.status === 'INACTIVE'
                      ? 'bg-[#F4A261] text-white'
                      : 'bg-[#2B3136] text-[#C7D0D8] hover:bg-[#38434D]'
                  }`}
                >
                  Set Inactive / Dormant
                </button>
                <button
                  onClick={() => handleToggleStatus(selectedFarmer, 'SUSPENDED')}
                  className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition cursor-pointer ${
                    selectedFarmer.status === 'SUSPENDED'
                      ? 'bg-[#E76F51] text-white'
                      : 'bg-[#2B3136] text-[#C7D0D8] hover:bg-[#38434D]'
                  }`}
                >
                  Suspend Account
                </button>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => {
                  const target = selectedFarmer;
                  setSelectedFarmer(null);
                  handleOpenAdvisoryModal(target);
                }}
                className="px-4 py-2 text-xs font-semibold rounded-xl bg-[#00BCD4] text-white hover:bg-[#0097A7] cursor-pointer flex items-center gap-1.5"
              >
                <Send className="w-3.5 h-3.5" />
                <span>Send Direct Advisory</span>
              </button>
              <button
                onClick={() => setSelectedFarmer(null)}
                className="px-4 py-2 text-xs font-semibold rounded-xl bg-[#1D2429] text-[#C7D0D8] hover:bg-[#38434D] border border-[#38434D] cursor-pointer"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* ─── MODAL 2: ADD / INVITE USER MODAL ─── */}
      <Modal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        title="Register New Smallholder or Field Officer"
        size="md"
      >
        <form onSubmit={handleCreateUser} className="space-y-3.5 text-xs">
          <div>
            <label className="block text-xs font-semibold text-[#F4F4F4] mb-1">
              Full Name / Nickname *
            </label>
            <input
              type="text"
              required
              placeholder="e.g., Tatay Juan Dela Cruz"
              value={newFullName}
              onChange={(e) => setNewFullName(e.target.value)}
              className="w-full px-3 py-2 text-xs rounded-xl border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] focus:outline-none focus:border-[#4CAF50]"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#F4F4F4] mb-1">
              Registered Email *
            </label>
            <input
              type="email"
              required
              placeholder="e.g., juan.delacruz@gmail.com"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              className="w-full px-3 py-2 text-xs rounded-xl border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] focus:outline-none focus:border-[#4CAF50]"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#F4F4F4] mb-1">
              Mobile Contact Number
            </label>
            <input
              type="text"
              placeholder="+63 9XX XXX XXXX"
              value={newPhone}
              onChange={(e) => setNewPhone(e.target.value)}
              className="w-full px-3 py-2 text-xs rounded-xl border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] focus:outline-none focus:border-[#4CAF50]"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-[#F4F4F4] mb-1">
                Account Role
              </label>
              <select
                value={newRole}
                onChange={(e) => setNewRole(e.target.value as UserRole)}
                className="w-full px-3 py-2 text-xs rounded-xl border border-[#38434D] bg-[#1D2429] font-semibold text-[#F4F4F4]"
              >
                <option value="FARMER" className="bg-[#2B3136]">Smallholder Farmer</option>
                <option value="GUEST" className="bg-[#2B3136]">Guest / Trial Account</option>
              </select>
              <p className="text-[10px] text-[#8A9BA8] mt-1">
                Admin credentials are configured via Vercel Environment Variables.
              </p>
            </div>

            <div>
              <label className="block text-xs font-semibold text-[#F4F4F4] mb-1">
                Farm Name
              </label>
              <input
                type="text"
                placeholder="e.g., Murcia Organic Farm"
                value={newFarmName}
                onChange={(e) => setNewFarmName(e.target.value)}
                className="w-full px-3 py-2 text-xs rounded-xl border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] focus:outline-none focus:border-[#4CAF50]"
              />
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-3 border-t border-[#38434D]">
            <button
              type="button"
              onClick={() => setIsAddModalOpen(false)}
              className="px-4 py-2 text-xs font-semibold rounded-xl bg-[#1D2429] text-[#C7D0D8] hover:bg-[#38434D] border border-[#38434D] cursor-pointer"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-xs font-semibold rounded-xl bg-[#4CAF50] text-white hover:bg-[#388E3C] cursor-pointer flex items-center gap-1.5"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Confirm & Register</span>
            </button>
          </div>
        </form>
      </Modal>

      {/* ─── MODAL 3: DIRECT ADVISORY & RE-ENGAGEMENT MODAL ─── */}
      <Modal
        isOpen={isAdvisoryModalOpen}
        onClose={() => setIsAdvisoryModalOpen(false)}
        title={advisoryTarget ? `Dispatch Advisory: ${advisoryTarget.fullName}` : 'Dispatch Advisory'}
        size="md"
      >
        <form onSubmit={handleSendAdvisory} className="space-y-3.5 text-xs">
          <div>
            <label className="block text-xs font-semibold text-[#F4F4F4] mb-1">
              Advisory Subject / Title *
            </label>
            <input
              type="text"
              required
              value={advisoryTitle}
              onChange={(e) => setAdvisoryTitle(e.target.value)}
              className="w-full px-3 py-2 text-xs rounded-xl border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] focus:outline-none focus:border-[#00BCD4]"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#F4F4F4] mb-1">
              Message Content (Delivered to Mobile App Notifications) *
            </label>
            <textarea
              required
              rows={4}
              value={advisoryBody}
              onChange={(e) => setAdvisoryBody(e.target.value)}
              className="w-full px-3 py-2 text-xs rounded-xl border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] focus:outline-none focus:border-[#00BCD4]"
            />
          </div>

          <div className="flex justify-end gap-2 pt-3 border-t border-[#38434D]">
            <button
              type="button"
              onClick={() => setIsAdvisoryModalOpen(false)}
              className="px-4 py-2 text-xs font-semibold rounded-xl bg-[#1D2429] text-[#C7D0D8] hover:bg-[#38434D] border border-[#38434D] cursor-pointer"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmittingAdvisory}
              className="px-4 py-2 text-xs font-semibold rounded-xl bg-[#00BCD4] text-white hover:bg-[#0097A7] cursor-pointer flex items-center gap-1.5"
            >
              <Send className="w-3.5 h-3.5" />
              <span>{isSubmittingAdvisory ? 'Transmitting...' : 'Send Advisory'}</span>
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
