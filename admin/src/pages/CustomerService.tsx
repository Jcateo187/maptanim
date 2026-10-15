import React, { useEffect, useState, useMemo } from 'react';
import {
  Headphones,
  Search,
  RefreshCw,
  Send,
  CheckCircle2,
  Clock,
  User,
  AlertCircle,
  HelpCircle,
  Sprout,
  Bug,
  Key,
  ShieldAlert,
  MessageCircle,
  ChevronRight,
  ExternalLink,
  Sparkles,
} from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { FeedbackItem } from '../types';
import { apiService } from '../services/api';

const CANNED_RESPONSES = [
  {
    title: 'Password Reset',
    category: 'ACCOUNT_SUPPORT',
    icon: Key,
    text: 'Maaari mong i-reset ang iyong password gamit ang "Forgot Password" sa login screen. I-check ang spam o junk folder sa iyong Gmail kung hindi dumarating ang link.',
  },
  {
    title: 'Pest Advisory',
    category: 'PEST_DISEASE',
    icon: Bug,
    text: 'Para sa pest control, inirerekomenda ng DA-BPI ang pag-spray ng organic neem oil solution tuwing hapon at pag-isolate sa apektadong dahon upang mapigilan ang pagkalat.',
  },
  {
    title: 'Planting Calendar',
    category: 'AGRONOMIC_QUERY',
    icon: Sprout,
    text: 'Ayon sa MapTanim Crop Calendar, angkop itanim ang pananim na ito sa kasalukuyang panahon. Siguraduhing may sapat na drainage ang kamang taniman at regular na diligan.',
  },
  {
    title: 'App Sync',
    category: 'BUG',
    icon: AlertCircle,
    text: 'Pakisigurado na may aktibong internet connection at naka-update sa pinakabagong bersyon ang iyong MapTanim mobile app upang ma-sync nang maayos ang iyong crop plots.',
  },
  {
    title: 'Issue Resolved',
    category: 'GENERAL',
    icon: CheckCircle2,
    text: 'Salamat sa pakikipag-ugnayan sa MapTanim Customer Support! Ang iyong concern ay minarkahan nang resolved. Mag-iwan lamang muli ng mensahe kung may karagdagang tanong.',
  },
];

export const CustomerService: React.FC = () => {
  const [tickets, setTickets] = useState<FeedbackItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedTicketId, setSelectedTicketId] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'PENDING' | 'IN_PROGRESS' | 'RESOLVED'>('ALL');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [replyText, setReplyText] = useState<string>('');
  const [replyStatus, setReplyStatus] = useState<'PENDING' | 'IN_PROGRESS' | 'RESOLVED'>('RESOLVED');
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [lastRefreshed, setLastRefreshed] = useState<string>('');

  const loadTickets = async () => {
    setLoading(true);
    try {
      const data = await apiService.getFeedback();
      setTickets(data);
      if (data.length > 0 && !selectedTicketId) {
        setSelectedTicketId(data[0].id);
        setReplyText(data[0].adminReply || '');
        setReplyStatus(data[0].status === 'PENDING' ? 'RESOLVED' : data[0].status);
      }
      setLastRefreshed(new Date().toLocaleTimeString('en-PH', { hour: '2-digit', minute: '2-digit' }));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTickets();

    // Subscribe to live Supabase updates for incoming tickets
    const unsubscribe = apiService.subscribeToFeedback(() => {
      loadTickets();
      showToast('New customer service activity received from mobile!');
    });

    return () => {
      unsubscribe();
    };
  }, []);

  const selectedTicket = useMemo(() => {
    return tickets.find((t) => t.id === selectedTicketId) || null;
  }, [tickets, selectedTicketId]);

  const handleSelectTicket = (ticket: FeedbackItem) => {
    setSelectedTicketId(ticket.id);
    setReplyText(ticket.adminReply || '');
    setReplyStatus(ticket.status === 'PENDING' ? 'RESOLVED' : ticket.status);
  };

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 4000);
  };

  const handleSendReply = async () => {
    if (!selectedTicket || !replyText.trim()) return;
    setSubmitting(true);
    try {
      await apiService.updateFeedbackStatus(
        selectedTicket.id,
        replyStatus,
        replyText.trim(),
        selectedTicket.farmerId,
        selectedTicket.subject
      );
      showToast('Response successfully dispatched to farmer mobile app!');
      loadTickets();
    } finally {
      setSubmitting(false);
    }
  };

  const handleQuickStatusChange = async (newStatus: 'PENDING' | 'IN_PROGRESS' | 'RESOLVED') => {
    if (!selectedTicket) return;
    await apiService.updateFeedbackStatus(selectedTicket.id, newStatus);
    setReplyStatus(newStatus);
    showToast(`Ticket status updated to ${newStatus}`);
    loadTickets();
  };

  const filteredTickets = useMemo(() => {
    return tickets.filter((t) => {
      const matchesSearch =
        t.farmerName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        t.subject.toLowerCase().includes(searchQuery.toLowerCase()) ||
        t.message.toLowerCase().includes(searchQuery.toLowerCase()) ||
        t.id.toLowerCase().includes(searchQuery.toLowerCase());

      const matchesStatus = statusFilter === 'ALL' || t.status === statusFilter;
      const matchesCategory = categoryFilter === 'ALL' || t.category === categoryFilter;

      return matchesSearch && matchesStatus && matchesCategory;
    });
  }, [tickets, searchQuery, statusFilter, categoryFilter]);

  // Counts
  const totalCount = tickets.length;
  const pendingCount = tickets.filter((t) => t.status === 'PENDING').length;
  const inProgressCount = tickets.filter((t) => t.status === 'IN_PROGRESS').length;
  const resolvedCount = tickets.filter((t) => t.status === 'RESOLVED').length;

  const formatRelativeTime = (dateStr: string) => {
    if (!dateStr) return '';
    const diff = Date.now() - new Date(dateStr).getTime();
    if (diff < 60000) return 'Just now';
    if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`;
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`;
    return new Date(dateStr).toLocaleDateString('en-PH', { month: 'short', day: 'numeric' });
  };

  const getCategoryBadgeVariant = (cat: string) => {
    switch (cat) {
      case 'ACCOUNT_SUPPORT':
        return 'purple';
      case 'PEST_DISEASE':
      case 'BUG':
        return 'danger';
      case 'AGRONOMIC_QUERY':
        return 'success';
      default:
        return 'info';
    }
  };

  return (
    <div className="space-y-5 animate-fadeIn pb-12 select-none">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-20 right-6 z-50 flex items-center gap-2.5 px-4 py-3 rounded-xl bg-[#111813] text-white text-xs font-semibold shadow-xl border border-[#2E7D32] animate-bounce">
          <CheckCircle2 className="w-4 h-4 text-[#2E7D32]" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Top Header Card */}
      <div className="bg-white p-5 sm:p-6 rounded-2xl border border-[#E5E7EB] shadow-xs flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
        <div className="flex items-center gap-3.5">
          <div className="w-12 h-12 rounded-xl bg-[#E8F5E9] text-[#2E7D32] flex items-center justify-center font-bold shrink-0 border border-[#A5D6A7]">
            <Headphones className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-2.5 flex-wrap">
              <h2 className="font-extrabold text-lg text-[#111813] tracking-tight">
                Customer Service Live Desk
              </h2>
              <span className="flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-[#E8F5E9] text-[#2E7D32] border border-[#A5D6A7]">
                <span className="w-2 h-2 rounded-full bg-[#2E7D32] animate-pulse" />
                Live Supabase Channel
              </span>
            </div>
            <p className="text-xs text-[#6B7280] mt-0.5">
              Real-time mobile inquiries, live farmer communication, and push advisory notifications
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3 self-stretch sm:self-auto justify-between sm:justify-end">
          {lastRefreshed && (
            <span className="text-[11px] text-[#6B7280] font-mono hidden sm:inline">
              Synced: {lastRefreshed}
            </span>
          )}
          <button
            onClick={loadTickets}
            disabled={loading}
            className="h-10 px-4 rounded-xl bg-white border border-[#E5E7EB] text-[#374151] text-xs font-semibold hover:border-[#2E7D32] hover:text-[#2E7D32] transition flex items-center gap-2 shadow-xs cursor-pointer disabled:opacity-50"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-[#2E7D32]' : 'text-[#6B7280]'}`} />
            <span>{loading ? 'Syncing...' : 'Refresh Live'}</span>
          </button>
        </div>
      </div>

      {/* KPI Status Strip */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
        <button
          onClick={() => setStatusFilter('ALL')}
          className={`p-4 rounded-2xl border text-left transition-all ${
            statusFilter === 'ALL'
              ? 'bg-white border-[#2E7D32] shadow-sm ring-1 ring-[#2E7D32]'
              : 'bg-white border-[#E5E7EB] hover:border-[#CBD5E1]'
          }`}
        >
          <p className="text-xs font-semibold text-[#6B7280]">Total Inquiries</p>
          <p className="text-2xl font-black text-[#111813] mt-1">{totalCount}</p>
          <span className="text-[10px] text-[#6B7280] mt-0.5 block">From all mobile farmers</span>
        </button>

        <button
          onClick={() => setStatusFilter('PENDING')}
          className={`p-4 rounded-2xl border text-left transition-all ${
            statusFilter === 'PENDING'
              ? 'bg-white border-[#F59E0B] shadow-sm ring-1 ring-[#F59E0B]'
              : 'bg-white border-[#E5E7EB] hover:border-[#CBD5E1]'
          }`}
        >
          <div className="flex items-center justify-between">
            <p className="text-xs font-semibold text-[#D97706]">Needs Reply</p>
            {pendingCount > 0 && (
              <span className="w-2 h-2 rounded-full bg-[#F59E0B] animate-ping" />
            )}
          </div>
          <p className="text-2xl font-black text-[#D97706] mt-1">{pendingCount}</p>
          <span className="text-[10px] text-[#D97706] mt-0.5 block">Awaiting admin review</span>
        </button>

        <button
          onClick={() => setStatusFilter('IN_PROGRESS')}
          className={`p-4 rounded-2xl border text-left transition-all ${
            statusFilter === 'IN_PROGRESS'
              ? 'bg-white border-[#0284C7] shadow-sm ring-1 ring-[#0284C7]'
              : 'bg-white border-[#E5E7EB] hover:border-[#CBD5E1]'
          }`}
        >
          <p className="text-xs font-semibold text-[#0284C7]">In Progress</p>
          <p className="text-2xl font-black text-[#0284C7] mt-1">{inProgressCount}</p>
          <span className="text-[10px] text-[#0284C7] mt-0.5 block">Under technical investigation</span>
        </button>

        <button
          onClick={() => setStatusFilter('RESOLVED')}
          className={`p-4 rounded-2xl border text-left transition-all ${
            statusFilter === 'RESOLVED'
              ? 'bg-white border-[#2E7D32] shadow-sm ring-1 ring-[#2E7D32]'
              : 'bg-white border-[#E5E7EB] hover:border-[#CBD5E1]'
          }`}
        >
          <p className="text-xs font-semibold text-[#2E7D32]">Resolved</p>
          <p className="text-2xl font-black text-[#2E7D32] mt-1">{resolvedCount}</p>
          <span className="text-[10px] text-[#2E7D32] mt-0.5 block">Response pushed to user</span>
        </button>
      </div>

      {/* Main Two-Column Service Desk Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5 items-start">
        {/* LEFT COLUMN: Queue & Filters (5 cols) */}
        <div className="lg:col-span-5 bg-white rounded-2xl border border-[#E5E7EB] shadow-xs flex flex-col overflow-hidden" style={{ minHeight: '680px' }}>
          {/* Search & Category Filter Header */}
          <div className="p-4 border-b border-[#E5E7EB] space-y-3 bg-[#F9FAFB]">
            <div className="relative">
              <Search className="w-4 h-4 absolute left-3 top-3 text-[#9CA3AF]" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search by farmer name, ticket ID, or issue..."
                className="w-full pl-9 pr-4 py-2 text-xs bg-white border border-[#E5E7EB] rounded-xl outline-none focus:border-[#2E7D32] text-[#111813] placeholder-[#9CA3AF] transition"
              />
            </div>

            {/* Category Filter Pills */}
            <div className="flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar">
              {[
                { id: 'ALL', label: 'All' },
                { id: 'ACCOUNT_SUPPORT', label: 'Account' },
                { id: 'AGRONOMIC_QUERY', label: 'Agronomic' },
                { id: 'PEST_DISEASE', label: 'Pests' },
                { id: 'BUG', label: 'Bugs' },
                { id: 'GENERAL', label: 'General' },
              ].map((c) => (
                <button
                  key={c.id}
                  onClick={() => setCategoryFilter(c.id)}
                  className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold shrink-0 transition-colors ${
                    categoryFilter === c.id
                      ? 'bg-[#E8F5E9] text-[#2E7D32] border border-[#A5D6A7]'
                      : 'bg-white border border-[#E5E7EB] text-[#6B7280] hover:text-[#111813]'
                  }`}
                >
                  {c.label}
                </button>
              ))}
            </div>
          </div>

          {/* Ticket List */}
          <div className="flex-1 overflow-y-auto divide-y divide-[#E5E7EB] max-h-[560px]">
            {loading ? (
              <div className="p-6 space-y-4">
                {[1, 2, 3, 4].map((i) => (
                  <div key={i} className="space-y-2">
                    <div className="h-4 bg-[#F3F4F6] rounded w-2/3 animate-pulse" />
                    <div className="h-3 bg-[#F3F4F6] rounded w-1/2 animate-pulse" />
                  </div>
                ))}
              </div>
            ) : filteredTickets.length === 0 ? (
              <div className="p-8 text-center flex flex-col items-center justify-center h-64 gap-2 text-[#6B7280]">
                <MessageCircle className="w-8 h-8 text-[#9CA3AF]" />
                <p className="text-xs font-semibold">No inquiries found</p>
                <p className="text-[11px] text-[#9CA3AF]">
                  Try adjusting search or status filters
                </p>
              </div>
            ) : (
              filteredTickets.map((ticket) => {
                const isSelected = ticket.id === selectedTicketId;
                return (
                  <div
                    key={ticket.id}
                    onClick={() => handleSelectTicket(ticket)}
                    className={`p-4 transition-all cursor-pointer flex flex-col gap-2 ${
                      isSelected
                        ? 'bg-[#F0FDF4] border-l-4 border-l-[#2E7D32]'
                        : 'hover:bg-[#F9FAFB]'
                    }`}
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex items-center gap-2.5 min-w-0">
                        <div className="w-8 h-8 rounded-full bg-[#E8F5E9] text-[#2E7D32] font-black text-xs flex items-center justify-center shrink-0 border border-[#A5D6A7]">
                          {ticket.farmerName.charAt(0) || 'F'}
                        </div>
                        <div className="truncate">
                          <p className="text-xs font-bold text-[#111813] truncate">
                            {ticket.farmerName}
                          </p>
                          {ticket.farmName && (
                            <p className="text-[10px] text-[#6B7280] truncate">
                              {ticket.farmName}
                            </p>
                          )}
                        </div>
                      </div>

                      <div className="flex items-center gap-1.5 shrink-0">
                        <span className="text-[10px] text-[#6B7280]">
                          {formatRelativeTime(ticket.createdAt)}
                        </span>
                        <Badge variant={getCategoryBadgeVariant(ticket.category)}>
                          {ticket.category.replace('_', ' ')}
                        </Badge>
                      </div>
                    </div>

                    <div>
                      <p className="text-xs font-bold text-[#111813] line-clamp-1">
                        {ticket.subject}
                      </p>
                      <p className="text-[11px] text-[#4B5563] line-clamp-2 mt-0.5 leading-relaxed">
                        {ticket.message}
                      </p>
                    </div>

                    <div className="flex items-center justify-between pt-1 text-[10px]">
                      <span className="font-mono text-[#9CA3AF]">#{ticket.id}</span>
                      <div className="flex items-center gap-2">
                        {ticket.adminReply && (
                          <span className="flex items-center gap-1 text-[#2E7D32] font-semibold">
                            <CheckCircle2 className="w-3 h-3" /> Replied
                          </span>
                        )}
                        <span
                          className={`px-2 py-0.5 rounded-full font-bold border ${
                            ticket.status === 'RESOLVED'
                              ? 'bg-[#E8F5E9] text-[#2E7D32] border-[#A5D6A7]'
                              : ticket.status === 'IN_PROGRESS'
                              ? 'bg-[#E0F2FE] text-[#0284C7] border-[#BAE6FD]'
                              : 'bg-[#FEF3C7] text-[#D97706] border-[#FDE68A]'
                          }`}
                        >
                          {ticket.status}
                        </span>
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* RIGHT COLUMN: Live Chat & Response Console (7 cols) */}
        <div className="lg:col-span-7 bg-white rounded-2xl border border-[#E5E7EB] shadow-xs flex flex-col overflow-hidden" style={{ minHeight: '680px' }}>
          {selectedTicket ? (
            <div className="flex flex-col h-full">
              {/* Selected Ticket Top Bar */}
              <div className="p-4 sm:p-5 border-b border-[#E5E7EB] bg-[#F9FAFB] flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-[#E8F5E9] text-[#2E7D32] font-black text-sm flex items-center justify-center border border-[#A5D6A7] shrink-0">
                    {selectedTicket.farmerName.charAt(0)}
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="font-extrabold text-sm text-[#111813]">
                        {selectedTicket.farmerName}
                      </h3>
                      {selectedTicket.farmName && (
                        <span className="text-[11px] text-[#6B7280]">
                          ({selectedTicket.farmName})
                        </span>
                      )}
                    </div>
                    <p className="text-[10px] text-[#6B7280] font-mono mt-0.5">
                      ID: {selectedTicket.farmerId || 'Anonymous Farmer'} · Ticket #{selectedTicket.id}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2.5">
                  <span className="text-xs text-[#6B7280] font-semibold">Status:</span>
                  <select
                    value={selectedTicket.status}
                    onChange={(e) => handleQuickStatusChange(e.target.value as any)}
                    className="h-8 px-2.5 text-xs font-bold rounded-lg border border-[#E5E7EB] bg-white text-[#111813] outline-none cursor-pointer focus:border-[#2E7D32]"
                  >
                    <option value="PENDING">PENDING</option>
                    <option value="IN_PROGRESS">IN PROGRESS</option>
                    <option value="RESOLVED">RESOLVED</option>
                  </select>
                </div>
              </div>

              {/* Chat Thread Area */}
              <div className="p-5 flex-1 overflow-y-auto space-y-4 max-h-[340px]">
                {/* Farmer Message Bubble */}
                <div className="flex items-start gap-3">
                  <div className="w-8 h-8 rounded-full bg-[#E5E7EB] text-[#374151] flex items-center justify-center shrink-0 text-xs font-bold">
                    <User className="w-4 h-4" />
                  </div>
                  <div className="flex-1 max-w-xl bg-[#F9FAFB] border border-[#E5E7EB] rounded-2xl rounded-tl-none p-4 space-y-1.5 shadow-2xs">
                    <div className="flex items-center justify-between text-[11px]">
                      <span className="font-bold text-[#111813]">
                        {selectedTicket.farmerName} (Farmer)
                      </span>
                      <span className="text-[#6B7280]">
                        {new Date(selectedTicket.createdAt).toLocaleString()}
                      </span>
                    </div>
                    <p className="font-bold text-xs text-[#111813]">
                      {selectedTicket.subject}
                    </p>
                    <p className="text-xs text-[#374151] leading-relaxed whitespace-pre-wrap">
                      {selectedTicket.message}
                    </p>
                    <div className="pt-1">
                      <Badge variant={getCategoryBadgeVariant(selectedTicket.category)}>
                        {selectedTicket.category.replace('_', ' ')}
                      </Badge>
                    </div>
                  </div>
                </div>

                {/* Admin Reply Bubble (if exists) */}
                {selectedTicket.adminReply && (
                  <div className="flex items-start gap-3 justify-end">
                    <div className="flex-1 max-w-xl bg-[#F0FDF4] border border-[#A5D6A7] rounded-2xl rounded-tr-none p-4 space-y-1.5 shadow-2xs text-right">
                      <div className="flex items-center justify-between text-[11px]">
                        <span className="text-[#6B7280]">
                          {selectedTicket.resolvedAt
                            ? new Date(selectedTicket.resolvedAt).toLocaleString()
                            : 'Dispatched to mobile app'}
                        </span>
                        <span className="font-bold text-[#2E7D32]">
                          MapTanim Admin Support Desk
                        </span>
                      </div>
                      <p className="text-xs text-[#111813] leading-relaxed whitespace-pre-wrap text-left font-medium">
                        {selectedTicket.adminReply}
                      </p>
                      <div className="flex items-center justify-end gap-1 text-[10px] text-[#2E7D32] font-semibold pt-1">
                        <CheckCircle2 className="w-3 h-3" />
                        <span>Pushed to Mobile App Notification</span>
                      </div>
                    </div>
                    <div className="w-8 h-8 rounded-full bg-[#E8F5E9] text-[#2E7D32] flex items-center justify-center shrink-0 border border-[#A5D6A7]">
                      <Headphones className="w-4 h-4" />
                    </div>
                  </div>
                )}
              </div>

              {/* Fast Canned Responses Bar */}
              <div className="p-3 bg-[#F9FAFB] border-t border-[#E5E7EB] space-y-1.5">
                <div className="flex items-center gap-1.5 text-[11px] font-bold text-[#6B7280]">
                  <Sparkles className="w-3.5 h-3.5 text-[#2E7D32]" />
                  <span>Quick Canned Agricultural Advice:</span>
                </div>
                <div className="flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar">
                  {CANNED_RESPONSES.map((cr) => {
                    const Icon = cr.icon;
                    return (
                      <button
                        key={cr.title}
                        onClick={() => setReplyText(cr.text)}
                        className="px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-white border border-[#E5E7EB] hover:border-[#2E7D32] hover:text-[#2E7D32] text-[#374151] transition shrink-0 flex items-center gap-1.5 shadow-2xs cursor-pointer"
                      >
                        <Icon className="w-3 h-3 text-[#2E7D32]" />
                        <span>{cr.title}</span>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Response Composer Box */}
              <div className="p-4 border-t border-[#E5E7EB] space-y-3 bg-white">
                <div className="space-y-1">
                  <label className="text-[11px] font-bold text-[#111813] uppercase tracking-wider">
                    {selectedTicket.adminReply ? 'Update Response:' : 'Write Support Response:'}
                  </label>
                  <textarea
                    rows={3}
                    value={replyText}
                    onChange={(e) => setReplyText(e.target.value)}
                    placeholder="Type customized advice, troubleshooting steps, or recommendations for the farmer..."
                    className="w-full p-3 text-xs bg-white border border-[#E5E7EB] rounded-xl outline-none focus:border-[#2E7D32] text-[#111813] resize-none placeholder-[#9CA3AF]"
                  />
                </div>

                <div className="flex items-center justify-between flex-wrap gap-2">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-semibold text-[#6B7280]">Mark as:</span>
                    <select
                      value={replyStatus}
                      onChange={(e) => setReplyStatus(e.target.value as any)}
                      className="h-8 px-2.5 text-xs font-bold rounded-lg border border-[#E5E7EB] bg-white text-[#111813] outline-none cursor-pointer"
                    >
                      <option value="RESOLVED">RESOLVED (Ticket Closed)</option>
                      <option value="IN_PROGRESS">IN PROGRESS (Under Review)</option>
                      <option value="PENDING">PENDING</option>
                    </select>
                  </div>

                  <button
                    onClick={handleSendReply}
                    disabled={submitting || !replyText.trim()}
                    className="h-9 px-4 bg-[#2E7D32] hover:bg-[#1B5E20] text-white text-xs font-bold rounded-xl flex items-center gap-1.5 shadow-sm cursor-pointer disabled:opacity-50 transition"
                  >
                    <Send className="w-3.5 h-3.5" />
                    <span>{submitting ? 'Sending...' : 'Send Response & Push to Mobile'}</span>
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <div className="p-12 flex flex-col items-center justify-center h-full gap-3 text-center my-auto">
              <div className="w-16 h-16 rounded-2xl bg-[#E8F5E9] border border-[#A5D6A7] text-[#2E7D32] flex items-center justify-center">
                <Headphones className="w-8 h-8" />
              </div>
              <h3 className="font-extrabold text-base text-[#111813]">
                Customer Service Communication Console
              </h3>
              <p className="text-xs text-[#6B7280] max-w-sm leading-relaxed">
                Select an inquiry ticket from the queue on the left to view the farmer's concern and dispatch official replies.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
