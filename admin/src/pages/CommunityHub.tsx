import React, { useEffect, useState } from 'react';
import {
  MessageSquare,
  Pin,
  Trash2,
  Send,
  Search,
  Plus,
  RefreshCw,
  Heart,
  MessageCircle,
  ShieldCheck,
  CheckCircle2,
  Flag,
  ShieldAlert,
  Clock,
  X,
} from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { CommunityPost, CommunityComment, CommunityReport, ReportStatus } from '../types';
import { apiService } from '../services/api';

interface ChatChannel {
  id: string;
  name: string;
  subtitle: string;
  iconEmoji: string;
  unreadCount: number;
}

interface ChatMessage {
  id: string;
  sender: 'admin' | 'farmer';
  senderName: string;
  text: string;
  timestamp: string;
}

export const CommunityHub: React.FC = () => {
  const [posts, setPosts] = useState<CommunityPost[]>([]);
  const [reports, setReports] = useState<CommunityReport[]>([]);
  
  // Top-level Mode Switcher
  const [activeHubMode, setActiveHubMode] = useState<'FEED' | 'CHAT' | 'REPORTS'>('FEED');

  const [selectedPost, setSelectedPost] = useState<CommunityPost | null>(null);
  const [selectedReport, setSelectedReport] = useState<CommunityReport | null>(null);
  const [comments, setComments] = useState<CommunityComment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [refreshing, setRefreshing] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [sortBy, setSortBy] = useState<'NEWEST' | 'LIKES' | 'COMMENTS' | 'PINNED'>('PINNED');
  const [actionNotice, setActionNotice] = useState<string | null>(null);

  // Moderation filter state
  const [reportFilterStatus, setReportFilterStatus] = useState<ReportStatus | 'ALL'>('ALL');
  const [reportFilterType, setReportFilterType] = useState<string>('ALL');

  // New Post Modal State
  const [isCreateModalOpen, setIsCreateModalOpen] = useState<boolean>(false);
  const [newTitle, setNewTitle] = useState('');
  const [newContent, setNewContent] = useState('');
  const [newAuthorName, setNewAuthorName] = useState('MapTanim Agronomy Desk');
  const [newIsPinned, setNewIsPinned] = useState(false);

  // Reply State
  const [replyText, setReplyText] = useState('');
  const [replying, setReplying] = useState(false);

  // ─── LIVE CHAT STATE ───────────────────────────────────────────────────────
  const [chatChannels] = useState<ChatChannel[]>([
    { id: 'gen', name: 'General Farmers Chat', subtitle: 'Public Community Broadcast', iconEmoji: '🌾', unreadCount: 2 },
    { id: 'james', name: 'Farmer James Parreño', subtitle: 'Highland Veggie Grower • Online', iconEmoji: '👨‍🌾', unreadCount: 0 },
    { id: 'maria', name: 'Maria Santos', subtitle: 'Santos Family Garden • Active 5m ago', iconEmoji: '👩‍🌾', unreadCount: 1 },
    { id: 'pedro', name: 'Ka Pedring Reyes', subtitle: 'Reyes Eco Farm • Online', iconEmoji: '👨‍🌾', unreadCount: 0 },
  ]);
  const [selectedChannelId, setSelectedChannelId] = useState<string>('gen');
  const [chatSearchQuery, setChatSearchQuery] = useState<string>('');
  const [chatInputText, setChatInputText] = useState<string>('');

  const [channelMessages, setChannelMessages] = useState<Record<string, ChatMessage[]>>({
    gen: [
      { id: 'm1', sender: 'farmer', senderName: 'Mang Juan', text: 'Magandang araw mga kasama! Kamusta ang tanim nating talong ngayon?', timestamp: '10:15 AM' },
      { id: 'm2', sender: 'admin', senderName: 'MapTanim Agronomist (Admin)', text: 'Paalala sa lahat ng farmers sa Murcia: May DA advisories ukol sa mild fruit borer activity. Please check our tips.', timestamp: '10:18 AM' },
      { id: 'm3', sender: 'farmer', senderName: 'Farmer Elena', text: 'May tips ba kayo laban sa fruit borer sa ampalaya?', timestamp: '10:22 AM' },
      { id: 'm4', sender: 'farmer', senderName: 'Ka Pedring', text: 'Gumamit po kayo ng neem oil spray bawat linggo, epektibo po iyon.', timestamp: '10:25 AM' },
    ],
    james: [
      { id: 'j1', sender: 'farmer', senderName: 'Farmer James', text: 'Kumusta Boss Admin! May available ka bang update sa seed subsidy?', timestamp: '9:30 AM' },
      { id: 'j2', sender: 'admin', senderName: 'MapTanim Agronomist (Admin)', text: 'Nasa verification stage na ang listahan ng DA regional office, James. We will post once ready.', timestamp: '9:45 AM' },
    ],
    maria: [
      { id: 's1', sender: 'farmer', senderName: 'Maria Santos', text: 'Salamat po sa tip sa drip irrigation, napakaganda po ng daloy ng tubig sa plots!', timestamp: 'Yesterday' },
    ],
    pedro: [
      { id: 'p1', sender: 'farmer', senderName: 'Ka Pedring', text: 'Mag-aani po kami ng kamatis sa Sabado, baka may market matching kayo?', timestamp: '8:00 AM' },
    ],
  });

  const selectedChannel = chatChannels.find((c) => c.id === selectedChannelId) || chatChannels[0];
  const activeMessages = (channelMessages[selectedChannelId] || []).filter(
    (m) =>
      chatSearchQuery.trim() === '' ||
      m.text.toLowerCase().includes(chatSearchQuery.toLowerCase()) ||
      m.senderName.toLowerCase().includes(chatSearchQuery.toLowerCase())
  );

  const fetchPosts = async (preserveSelectedId?: string) => {
    const data = await apiService.getCommunityPosts();
    setPosts(data);

    if (data.length > 0) {
      if (preserveSelectedId) {
        const found = data.find((p) => p.id === preserveSelectedId);
        if (found) {
          setSelectedPost(found);
          const c = await apiService.getCommunityComments(found.id);
          setComments(c);
        } else {
          setSelectedPost(data[0]);
          const c = await apiService.getCommunityComments(data[0].id);
          setComments(c);
        }
      } else if (!selectedPost) {
        setSelectedPost(data[0]);
        const c = await apiService.getCommunityComments(data[0].id);
        setComments(c);
      }
    } else {
      setSelectedPost(null);
      setComments([]);
    }
  };

  const fetchReports = async () => {
    const data = await apiService.getCommunityReports();
    setReports(data);
    if (data.length > 0) {
      if (selectedReport) {
        const found = data.find((r) => r.id === selectedReport.id);
        setSelectedReport(found || data[0]);
      } else {
        setSelectedReport(data[0]);
      }
    } else {
      setSelectedReport(null);
    }
  };

  useEffect(() => {
    const init = async () => {
      setLoading(true);
      await Promise.all([fetchPosts(), fetchReports()]);
      setLoading(false);
    };
    init();
  }, []);

  const handleRefresh = async () => {
    setRefreshing(true);
    await Promise.all([fetchPosts(selectedPost?.id), fetchReports()]);
    setRefreshing(false);
    showNotice('Community records synchronized.');
  };

  const showNotice = (msg: string) => {
    setActionNotice(msg);
    setTimeout(() => setActionNotice(null), 3000);
  };

  const handleSelectPost = async (post: CommunityPost) => {
    setSelectedPost(post);
    const postComments = await apiService.getCommunityComments(post.id);
    setComments(postComments);
  };

  const handleTogglePin = async (postId: string, currentPin: boolean, e?: React.MouseEvent) => {
    if (e) e.stopPropagation();
    const nextPin = !currentPin;
    await apiService.togglePinCommunityPost(postId, nextPin);
    await fetchPosts(selectedPost?.id);
    showNotice(nextPin ? 'Post pinned to top' : 'Post unpinned');
  };

  const handleDeletePost = async (postId: string, e?: React.MouseEvent) => {
    if (e) e.stopPropagation();
    if (window.confirm('Are you sure you want to delete this community post?')) {
      await apiService.deleteCommunityPost(postId);
      showNotice('Community post deleted');
      await fetchPosts();
      await fetchReports();
    }
  };

  const handleAddComment = async () => {
    if (!selectedPost || !replyText.trim()) return;
    setReplying(true);
    await apiService.addCommunityComment({
      postId: selectedPost.id,
      content: replyText.trim(),
      authorName: 'MapTanim Agronomist (Admin)',
    });
    setReplyText('');
    setReplying(false);
    showNotice('Official reply published');
    const updatedComments = await apiService.getCommunityComments(selectedPost.id);
    setComments(updatedComments);
    await fetchPosts(selectedPost.id);
  };

  const handleDeleteComment = async (commentId: string) => {
    if (!selectedPost) return;
    if (window.confirm('Delete this comment from discussion thread?')) {
      await apiService.deleteCommunityComment(commentId, selectedPost.id);
      showNotice('Comment removed');
      const updatedComments = await apiService.getCommunityComments(selectedPost.id);
      setComments(updatedComments);
      await fetchPosts(selectedPost.id);
    }
  };

  // Moderation Action Handlers
  const handleUpdateReportStatus = async (reportId: string, status: ReportStatus, notes?: string) => {
    await apiService.updateCommunityReportStatus(reportId, status, notes);
    showNotice(`Report marked as ${status}`);
    await fetchReports();
  };

  const handleDeleteReportedContent = async (report: CommunityReport) => {
    if (report.targetType === 'POST') {
      if (window.confirm(`Delete reported post "${report.targetName}"?`)) {
        await apiService.deleteCommunityPost(report.targetId);
        await apiService.updateCommunityReportStatus(report.id, 'RESOLVED', 'Content removed by admin.');
        showNotice('Reported post deleted & report resolved');
        await fetchPosts();
        await fetchReports();
      }
    } else if (report.targetType === 'COMMENT') {
      if (window.confirm(`Delete reported comment by ${report.targetName}?`)) {
        await apiService.deleteCommunityComment(report.targetId, '');
        await apiService.updateCommunityReportStatus(report.id, 'RESOLVED', 'Comment removed by admin.');
        showNotice('Reported comment deleted & report resolved');
        await fetchPosts();
        await fetchReports();
      }
    } else if (report.targetType === 'USER') {
      if (window.confirm(`Resolve report for user "${report.targetName}"?`)) {
        await apiService.updateCommunityReportStatus(report.id, 'RESOLVED', 'User reviewed by admin.');
        showNotice('User report resolved');
        await fetchReports();
      }
    }
  };

  const handleDismissReport = async (reportId: string) => {
    await apiService.updateCommunityReportStatus(reportId, 'DISMISSED', 'Reviewed by moderation: no violation.');
    showNotice('Report dismissed');
    await fetchReports();
  };

  const handleCreatePost = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim() || !newContent.trim()) {
      alert('Please provide both a title and content.');
      return;
    }

    const created = await apiService.createCommunityPost({
      title: newTitle.trim(),
      category: 'GENERAL',
      content: newContent.trim(),
      authorName: newAuthorName.trim() || 'MapTanim Agronomy Desk',
      tags: ['Community'],
      isPinned: newIsPinned,
    });

    setIsCreateModalOpen(false);
    setNewTitle('');
    setNewContent('');
    showNotice('Post published to Community Hub');
    await fetchPosts(created?.id);
  };

  const handleSendChatMessage = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!chatInputText.trim()) return;

    const newMsg: ChatMessage = {
      id: 'm_' + Date.now(),
      sender: 'admin',
      senderName: 'MapTanim Agronomist (Admin)',
      text: chatInputText.trim(),
      timestamp: 'Just now',
    };

    setChannelMessages((prev) => ({
      ...prev,
      [selectedChannelId]: [...(prev[selectedChannelId] || []), newMsg],
    }));
    setChatInputText('');
    showNotice('Message broadcasted');
  };

  // Filtered post list
  const filteredPosts = posts
    .filter((post) => {
      return (
        searchQuery.trim() === '' ||
        post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        post.content.toLowerCase().includes(searchQuery.toLowerCase()) ||
        post.authorName.toLowerCase().includes(searchQuery.toLowerCase())
      );
    })
    .sort((a, b) => {
      if (sortBy === 'PINNED') {
        if (a.isPinned && !b.isPinned) return -1;
        if (!a.isPinned && b.isPinned) return 1;
        return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
      }
      if (sortBy === 'LIKES') return b.likesCount - a.likesCount;
      if (sortBy === 'COMMENTS') return b.commentsCount - a.commentsCount;
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    });

  // Filtered reports list
  const filteredReports = reports.filter((r) => {
    const matchesStatus = reportFilterStatus === 'ALL' || r.status === reportFilterStatus;
    const matchesType = reportFilterType === 'ALL' || r.targetType === reportFilterType;
    return matchesStatus && matchesType;
  });

  const pendingReportsCount = reports.filter((r) => r.status === 'PENDING').length;
  const totalPostsCount = posts.length;

  return (
    <div className="space-y-6 animate-fadeIn pb-12">
      {/* Toast Notification */}
      {actionNotice && (
        <div className="fixed top-24 right-8 z-50 flex items-center gap-3 px-5 py-3.5 bg-slate-900 text-white rounded-2xl shadow-xl text-sm font-bold animate-slideIn">
          <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />
          <span>{actionNotice}</span>
        </div>
      )}

      {/* ─── 1. TOP BAR ACTIONS & TABS ─── */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-4 sm:p-5 rounded-2xl border border-slate-100 shadow-sm">
        {/* Main Category Tabs */}
        <div className="inline-flex p-1 bg-slate-100 rounded-xl">
          <button
            onClick={() => setActiveHubMode('FEED')}
            className={`px-5 py-2.5 rounded-lg text-xs sm:text-sm font-bold transition cursor-pointer flex items-center gap-2 ${
              activeHubMode === 'FEED'
                ? 'bg-white text-slate-900 shadow-xs'
                : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            <MessageSquare className="w-4 h-4 text-emerald-600" />
            <span>Discussions ({totalPostsCount})</span>
          </button>

          <button
            onClick={() => setActiveHubMode('CHAT')}
            className={`px-5 py-2.5 rounded-lg text-xs sm:text-sm font-bold transition cursor-pointer flex items-center gap-2 ${
              activeHubMode === 'CHAT'
                ? 'bg-white text-slate-900 shadow-xs'
                : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            <span>💬</span>
            <span>Farmer Channels ({chatChannels.length})</span>
          </button>

          <button
            onClick={() => setActiveHubMode('REPORTS')}
            className={`px-5 py-2.5 rounded-lg text-xs sm:text-sm font-bold transition cursor-pointer flex items-center gap-2 ${
              activeHubMode === 'REPORTS'
                ? 'bg-white text-slate-900 shadow-xs'
                : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            <Flag className="w-4 h-4 text-rose-500" />
            <span>Reports</span>
            {pendingReportsCount > 0 && (
              <span className="ml-1 px-2 py-0.2 rounded-full text-xs font-bold bg-rose-500 text-white">
                {pendingReportsCount}
              </span>
            )}
          </button>
        </div>

        {/* Right Search, Actions & Sync Controls */}
        <div className="flex items-center gap-3">
          {activeHubMode === 'FEED' && (
            <>
              <div className="relative flex-1 sm:w-64">
                <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search discussions..."
                  className="w-full h-10 pl-9 pr-3 text-xs bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:ring-2 focus:ring-emerald-500 text-slate-900"
                />
              </div>

              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value as any)}
                className="h-10 px-3 text-xs font-semibold bg-slate-50 border border-slate-200 rounded-xl outline-none text-slate-700 cursor-pointer"
              >
                <option value="PINNED">Pinned First</option>
                <option value="NEWEST">Newest</option>
                <option value="LIKES">Most Liked</option>
                <option value="COMMENTS">Most Comments</option>
              </select>

              <button
                onClick={() => setIsCreateModalOpen(true)}
                className="h-10 px-4 text-xs font-bold rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white flex items-center gap-2 transition cursor-pointer shadow-xs"
              >
                <Plus className="w-4 h-4" />
                <span>Create Post</span>
              </button>
            </>
          )}

          {activeHubMode === 'REPORTS' && (
            <select
              value={reportFilterStatus}
              onChange={(e) => setReportFilterStatus(e.target.value as any)}
              className="h-10 px-3 text-xs font-semibold bg-slate-50 border border-slate-200 rounded-xl outline-none text-slate-700 cursor-pointer"
            >
              <option value="ALL">All Statuses</option>
              <option value="PENDING">Pending</option>
              <option value="INVESTIGATING">Investigating</option>
              <option value="RESOLVED">Resolved</option>
              <option value="DISMISSED">Dismissed</option>
            </select>
          )}

          <button
            onClick={handleRefresh}
            disabled={refreshing}
            className="h-10 px-3 text-xs font-semibold rounded-xl bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 transition flex items-center gap-1.5 shadow-xs cursor-pointer"
            title="Sync Database"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${refreshing ? 'animate-spin text-emerald-500' : 'text-slate-400'}`} />
            <span>Sync</span>
          </button>
        </div>
      </div>

      {/* ═══════════════════════════════════════════════════════════════════════ */}
      {/* 2. WORKSPACE GRID (Discussions vs Chat vs Reports)                      */}
      {/* ═══════════════════════════════════════════════════════════════════════ */}
      
      {/* MODE 1: DISCUSSIONS FEED */}
      {activeHubMode === 'FEED' && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Left Column: Post List */}
          <div className="lg:col-span-5 space-y-4 max-h-[800px] overflow-y-auto pr-1">
            {loading ? (
              <div className="p-10 bg-white border border-slate-100 rounded-2xl text-center text-slate-400 flex flex-col items-center justify-center gap-2 shadow-sm">
                <RefreshCw className="w-5 h-5 animate-spin text-emerald-500" />
                <p className="text-xs font-semibold">Loading discussions...</p>
              </div>
            ) : filteredPosts.length === 0 ? (
              <div className="p-10 bg-white border border-slate-100 rounded-2xl text-center text-slate-400 space-y-2 shadow-sm">
                <MessageSquare className="w-8 h-8 mx-auto text-slate-400 opacity-50" />
                <p className="text-sm font-bold text-slate-700">No discussions found</p>
                <p className="text-xs text-slate-400">Click "+ Create Post" to publish.</p>
              </div>
            ) : (
              filteredPosts.map((post) => {
                const isSelected = selectedPost?.id === post.id;
                const hasPendingReport = reports.some((r) => r.targetId === post.id && r.status === 'PENDING');

                return (
                  <div
                    key={post.id}
                    onClick={() => handleSelectPost(post)}
                    className={`p-5 rounded-2xl border transition-all duration-150 cursor-pointer text-left ${
                      isSelected
                        ? 'bg-emerald-50/80 border-emerald-500 ring-1 ring-emerald-500 shadow-sm'
                        : 'bg-white border-slate-100 hover:border-slate-200 shadow-sm hover:shadow-md'
                    }`}
                  >
                    {/* Header */}
                    <div className="flex items-center justify-between gap-3 pb-3 border-b border-slate-100 mb-3">
                      <div className="flex items-center gap-2.5 min-w-0">
                        <div className="w-7 h-7 rounded-full bg-emerald-100 text-emerald-700 font-bold text-xs flex items-center justify-center shrink-0">
                          {post.authorName.charAt(0)}
                        </div>
                        <div className="min-w-0">
                          <span className="font-bold text-slate-900 text-xs block truncate">
                            {post.authorName}
                          </span>
                          <span className="text-[11px] text-slate-400 font-mono">
                            {new Date(post.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}
                          </span>
                        </div>
                      </div>

                      <div className="flex items-center gap-1.5 shrink-0">
                        {hasPendingReport && (
                          <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-rose-100 text-rose-700">
                            Reported
                          </span>
                        )}
                        {post.isPinned && (
                          <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-amber-100 text-amber-800 flex items-center gap-1">
                            <Pin className="w-3 h-3" />
                            Pinned
                          </span>
                        )}
                      </div>
                    </div>

                    {/* Title */}
                    <h4 className="text-sm font-bold text-slate-900 leading-snug mb-1.5">
                      {post.title}
                    </h4>

                    {/* Excerpt */}
                    <p className="text-xs text-slate-600 line-clamp-2 leading-relaxed mb-3">
                      {post.content}
                    </p>

                    {/* Footer */}
                    <div className="flex items-center justify-between pt-3 border-t border-slate-100 text-xs text-slate-400 font-mono">
                      <span className="text-[11px] text-emerald-600 font-semibold">
                        Community Discussion
                      </span>

                      <div className="flex items-center gap-3">
                        <span className="flex items-center gap-1 text-rose-500">
                          <Heart className="w-3.5 h-3.5 fill-rose-500/20" />
                          {post.likesCount}
                        </span>
                        <span className="flex items-center gap-1 text-emerald-600">
                          <MessageCircle className="w-3.5 h-3.5" />
                          {post.commentsCount}
                        </span>
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>

          {/* Right Column: Selected Thread Viewer */}
          <div className="lg:col-span-7">
            {selectedPost ? (
              <div className="bg-white border border-slate-100 rounded-2xl p-6 sm:p-7 space-y-6 shadow-sm">
                {/* Header */}
                <div className="flex items-start justify-between gap-4 pb-4 border-b border-slate-100">
                  <div>
                    <h3 className="text-xl font-bold text-slate-900 leading-tight">
                      {selectedPost.title}
                    </h3>
                    <p className="text-xs text-slate-400 mt-1 font-mono">
                      Posted by <span className="font-semibold text-slate-700">{selectedPost.authorName}</span> • {new Date(selectedPost.createdAt).toLocaleString()}
                    </p>
                  </div>

                  <div className="flex items-center gap-2 shrink-0">
                    <button
                      onClick={(e) => handleTogglePin(selectedPost.id, selectedPost.isPinned, e)}
                      className={`p-2 rounded-xl border transition cursor-pointer ${
                        selectedPost.isPinned
                          ? 'bg-amber-50 text-amber-700 border-amber-200'
                          : 'bg-slate-50 text-slate-500 border-slate-200 hover:text-amber-600'
                      }`}
                      title={selectedPost.isPinned ? 'Unpin' : 'Pin'}
                    >
                      <Pin className="w-4 h-4" />
                    </button>
                    <button
                      onClick={(e) => handleDeletePost(selectedPost.id, e)}
                      className="p-2 rounded-xl bg-slate-50 text-slate-500 border border-slate-200 hover:text-rose-600 hover:bg-rose-50 transition cursor-pointer"
                      title="Delete Post"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* Content */}
                <div className="text-sm text-slate-800 leading-relaxed bg-slate-50 p-5 rounded-xl border border-slate-100">
                  <p className="whitespace-pre-line leading-relaxed">{selectedPost.content}</p>
                </div>

                {/* Comments */}
                <div className="space-y-4 pt-2 border-t border-slate-100">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                    Comments ({comments.length})
                  </h4>

                  <div className="space-y-3 max-h-[280px] overflow-y-auto pr-1">
                    {comments.length === 0 ? (
                      <div className="p-6 text-center text-xs text-slate-400 bg-slate-50 rounded-xl border border-dashed border-slate-200">
                        No responses yet. Write an official agronomy reply below.
                      </div>
                    ) : (
                      comments.map((comment) => (
                        <div
                          key={comment.id}
                          className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 text-xs space-y-1.5"
                        >
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <span className="font-bold text-slate-900">{comment.authorName}</span>
                              {comment.authorName.includes('Admin') && (
                                <Badge variant="success">ADMIN</Badge>
                              )}
                            </div>
                            <div className="flex items-center gap-2 text-slate-400 font-mono text-[10px]">
                              <span>{new Date(comment.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                              <button
                                onClick={() => handleDeleteComment(comment.id)}
                                className="hover:text-rose-500 cursor-pointer"
                                title="Delete comment"
                              >
                                <Trash2 className="w-3.5 h-3.5" />
                              </button>
                            </div>
                          </div>
                          <p className="text-slate-700 leading-relaxed pl-1">{comment.content}</p>
                        </div>
                      ))
                    )}
                  </div>

                  {/* Reply Input */}
                  <div className="pt-2 flex gap-2">
                    <input
                      type="text"
                      value={replyText}
                      onChange={(e) => setReplyText(e.target.value)}
                      placeholder="Write an official response..."
                      className="flex-1 h-10 px-3.5 text-xs bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:ring-2 focus:ring-emerald-500 text-slate-900"
                      onKeyDown={(e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); handleAddComment(); } }}
                    />
                    <button
                      onClick={handleAddComment}
                      disabled={replying || !replyText.trim()}
                      className="h-10 px-4 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl disabled:opacity-50 flex items-center gap-1.5 cursor-pointer shadow-xs"
                    >
                      <Send className="w-3.5 h-3.5" />
                      <span>Reply</span>
                    </button>
                  </div>
                </div>
              </div>
            ) : (
              <div className="p-16 bg-white border border-slate-100 rounded-2xl text-center text-slate-400 space-y-3 shadow-sm">
                <MessageSquare className="w-10 h-10 mx-auto text-slate-300 opacity-50" />
                <h4 className="text-base font-bold text-slate-700">Select a Discussion</h4>
                <p className="text-xs text-slate-400 max-w-sm mx-auto">
                  Click on any discussion post on the left to inspect conversation details and post replies.
                </p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* MODE 2: FARMER CHANNELS */}
      {activeHubMode === 'CHAT' && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Channels column */}
          <div className="lg:col-span-4 space-y-3">
            <div className="relative">
              <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                value={chatSearchQuery}
                onChange={(e) => setChatSearchQuery(e.target.value)}
                placeholder="Search channels..."
                className="w-full h-10 pl-9 pr-3 text-xs bg-white border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-emerald-500 text-slate-900"
              />
            </div>

            <div className="space-y-2">
              {chatChannels.map((channel) => {
                const isSelected = selectedChannelId === channel.id;
                return (
                  <div
                    key={channel.id}
                    onClick={() => setSelectedChannelId(channel.id)}
                    className={`p-3.5 rounded-xl border transition cursor-pointer flex items-center justify-between ${
                      isSelected
                        ? 'bg-emerald-50 border-emerald-500 text-slate-900 shadow-xs'
                        : 'bg-white border-slate-100 hover:bg-slate-50 shadow-xs'
                    }`}
                  >
                    <div className="flex items-center gap-3 min-w-0">
                      <span className="text-2xl shrink-0">{channel.iconEmoji}</span>
                      <div className="min-w-0">
                        <p className="text-xs font-bold text-slate-900 truncate">
                          {channel.name}
                        </p>
                        <p className="text-[11px] text-slate-400 truncate mt-0.5">
                          {channel.subtitle}
                        </p>
                      </div>
                    </div>
                    {channel.unreadCount > 0 && (
                      <span className="px-2 py-0.5 rounded-full text-xs font-bold bg-emerald-600 text-white">
                        {channel.unreadCount}
                      </span>
                    )}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Chat Stream */}
          <div className="lg:col-span-8 bg-white border border-slate-100 rounded-2xl p-6 flex flex-col h-[600px] shadow-sm">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100 mb-3">
              <div className="flex items-center gap-3">
                <span className="text-2xl">{selectedChannel.iconEmoji}</span>
                <div>
                  <h4 className="text-sm font-bold text-slate-900">
                    {selectedChannel.name}
                  </h4>
                  <p className="text-xs text-emerald-600 font-medium">Live Farmer Channel</p>
                </div>
              </div>
            </div>

            <div className="flex-1 overflow-y-auto space-y-3.5 p-2">
              {activeMessages.map((msg) => {
                const isAdmin = msg.sender === 'admin';
                return (
                  <div key={msg.id} className={`flex flex-col ${isAdmin ? 'items-end' : 'items-start'}`}>
                    <div className="flex items-center gap-2 text-[11px] text-slate-400 mb-1 font-mono">
                      <span className="font-bold text-slate-600">{msg.senderName}</span>
                      <span>•</span>
                      <span>{msg.timestamp}</span>
                    </div>
                    <div
                      className={`max-w-[75%] px-4 py-2.5 rounded-xl text-xs leading-relaxed ${
                        isAdmin
                          ? 'bg-emerald-600 text-white'
                          : 'bg-slate-100 text-slate-800 border border-slate-200'
                      }`}
                    >
                      {msg.text}
                    </div>
                  </div>
                );
              })}
            </div>

            <form onSubmit={handleSendChatMessage} className="pt-3 border-t border-slate-100 flex gap-2">
              <input
                type="text"
                value={chatInputText}
                onChange={(e) => setChatInputText(e.target.value)}
                placeholder={`Message ${selectedChannel.name}...`}
                className="flex-1 h-10 px-3 text-xs bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:ring-2 focus:ring-emerald-500 text-slate-900"
              />
              <button
                type="submit"
                disabled={!chatInputText.trim()}
                className="h-10 px-4 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl disabled:opacity-50 flex items-center gap-1.5 cursor-pointer shadow-xs"
              >
                <Send className="w-3.5 h-3.5" />
                <span>Send</span>
              </button>
            </form>
          </div>
        </div>
      )}

      {/* MODE 3: MODERATION REPORTS */}
      {activeHubMode === 'REPORTS' && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Left: Reports list */}
          <div className="lg:col-span-5 space-y-3 max-h-[800px] overflow-y-auto pr-1">
            {filteredReports.length === 0 ? (
              <div className="p-10 bg-white border border-slate-100 rounded-2xl text-center text-slate-400 space-y-2 shadow-sm">
                <ShieldCheck className="w-8 h-8 mx-auto text-emerald-500" />
                <p className="text-sm font-bold text-slate-700">No reports found</p>
                <p className="text-xs text-slate-400">All community guidelines are complied with.</p>
              </div>
            ) : (
              filteredReports.map((report) => {
                const isSelected = selectedReport?.id === report.id;
                const statusBadge =
                  report.status === 'PENDING'
                    ? 'bg-amber-100 text-amber-800'
                    : report.status === 'RESOLVED'
                    ? 'bg-emerald-100 text-emerald-800'
                    : report.status === 'DISMISSED'
                    ? 'bg-slate-100 text-slate-600'
                    : 'bg-rose-100 text-rose-800';

                return (
                  <div
                    key={report.id}
                    onClick={() => setSelectedReport(report)}
                    className={`p-4 rounded-xl border transition cursor-pointer text-left ${
                      isSelected
                        ? 'bg-rose-50 border-rose-500 shadow-sm'
                        : 'bg-white border-slate-100 hover:border-slate-200 shadow-sm'
                    }`}
                  >
                    <div className="flex items-center justify-between pb-2 border-b border-slate-100 mb-2">
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-slate-100 text-slate-600 uppercase font-mono">
                        {report.targetType}
                      </span>
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${statusBadge}`}>
                        {report.status}
                      </span>
                    </div>

                    <h4 className="text-xs font-bold text-slate-900 truncate">
                      {report.targetName}
                    </h4>

                    <p className="text-xs text-rose-600 font-semibold mt-1">
                      {report.reason}
                    </p>

                    <div className="flex items-center justify-between pt-2 mt-2 border-t border-slate-100 text-[10px] text-slate-400 font-mono">
                      <span>Reporter: {report.reporterName}</span>
                      <span>{new Date(report.createdAt).toLocaleDateString()}</span>
                    </div>
                  </div>
                );
              })
            )}
          </div>

          {/* Right: Report Detail */}
          <div className="lg:col-span-7">
            {selectedReport ? (
              <div className="bg-white border border-slate-100 rounded-2xl p-6 space-y-5 shadow-sm">
                <div className="flex items-start justify-between gap-4 pb-4 border-b border-slate-100">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <Badge variant={selectedReport.status === 'RESOLVED' ? 'success' : selectedReport.status === 'PENDING' ? 'warning' : 'danger'}>
                        {selectedReport.status}
                      </Badge>
                      <span className="text-xs font-mono text-slate-400">Report #{selectedReport.id}</span>
                    </div>
                    <h3 className="text-base font-bold text-slate-900 flex items-center gap-2">
                      <Flag className="w-4 h-4 text-rose-500" />
                      <span>{selectedReport.targetType} Violation Review</span>
                    </h3>
                  </div>

                  <div className="text-right text-xs text-slate-400 font-mono">
                    <p>{new Date(selectedReport.createdAt).toLocaleString()}</p>
                    <p className="text-slate-700 mt-0.5">By {selectedReport.reporterName}</p>
                  </div>
                </div>

                <div className="p-4 rounded-xl bg-rose-50 border border-rose-100 space-y-1.5">
                  <p className="text-[10px] font-bold text-rose-700 uppercase tracking-wider">Reported Target</p>
                  <h4 className="text-sm font-bold text-slate-900">{selectedReport.targetName}</h4>
                  {selectedReport.targetContent && (
                    <p className="text-xs text-slate-700 font-mono mt-1 bg-white p-3 rounded-lg border border-slate-200 leading-relaxed">
                      "{selectedReport.targetContent}"
                    </p>
                  )}
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                  <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                    <p className="text-slate-400 font-bold uppercase text-[10px]">Violation Reason</p>
                    <p className="text-xs font-bold text-rose-600 mt-0.5">{selectedReport.reason}</p>
                  </div>
                  <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                    <p className="text-slate-400 font-bold uppercase text-[10px]">Reporter Notes</p>
                    <p className="text-slate-700 mt-0.5">{selectedReport.details || 'None provided.'}</p>
                  </div>
                </div>

                <div className="pt-3 border-t border-slate-100 space-y-2">
                  <h4 className="text-[11px] font-bold uppercase tracking-wider text-slate-400">Moderation Actions</h4>
                  <div className="flex flex-wrap gap-2">
                    <button
                      onClick={() => handleDeleteReportedContent(selectedReport)}
                      className="h-9 px-4 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-lg flex items-center gap-1.5 cursor-pointer shadow-xs"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                      <span>Delete & Resolve</span>
                    </button>

                    <button
                      onClick={() => handleUpdateReportStatus(selectedReport.id, 'INVESTIGATING', 'Under inspection.')}
                      className="h-9 px-4 bg-white border border-slate-200 text-slate-700 text-xs font-bold rounded-lg hover:bg-slate-50 flex items-center gap-1.5 cursor-pointer shadow-xs"
                    >
                      <Clock className="w-3.5 h-3.5 text-amber-500" />
                      <span>Mark Investigating</span>
                    </button>

                    <button
                      onClick={() => handleDismissReport(selectedReport.id)}
                      className="h-9 px-4 bg-white border border-slate-200 text-slate-500 text-xs font-bold rounded-lg hover:text-slate-900 flex items-center gap-1.5 cursor-pointer shadow-xs"
                    >
                      <X className="w-3.5 h-3.5" />
                      <span>Dismiss</span>
                    </button>
                  </div>
                </div>
              </div>
            ) : (
              <div className="p-16 bg-white border border-slate-100 rounded-2xl text-center text-slate-400 space-y-3 shadow-sm">
                <ShieldAlert className="w-10 h-10 mx-auto text-slate-300 opacity-50" />
                <h4 className="text-base font-bold text-slate-700">Select a Moderation Report</h4>
                <p className="text-xs text-slate-400 max-w-sm mx-auto">
                  Click on any user-submitted report on the left desk to review offending content.
                </p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Modal: Create Post */}
      {isCreateModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white w-full max-w-lg p-6 rounded-2xl border border-slate-100 shadow-2xl space-y-4 animate-scaleUp">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">
                Create Community Post
              </h3>
              <button
                onClick={() => setIsCreateModalOpen(false)}
                className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreatePost} className="space-y-4 text-xs">
              <div className="space-y-1">
                <label className="font-bold text-slate-700 uppercase tracking-wider text-[11px]">Title</label>
                <input
                  type="text"
                  required
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  placeholder="Post title..."
                  className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:ring-2 focus:ring-emerald-500 text-slate-900"
                />
              </div>

              <div className="space-y-1">
                <label className="font-bold text-slate-700 uppercase tracking-wider text-[11px]">Author Name</label>
                <input
                  type="text"
                  value={newAuthorName}
                  onChange={(e) => setNewAuthorName(e.target.value)}
                  placeholder="MapTanim Agronomy Desk"
                  className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:ring-2 focus:ring-emerald-500 text-slate-900"
                />
              </div>

              <div className="space-y-1">
                <label className="font-bold text-slate-700 uppercase tracking-wider text-[11px]">Content</label>
                <textarea
                  rows={4}
                  required
                  value={newContent}
                  onChange={(e) => setNewContent(e.target.value)}
                  placeholder="Write your discussion content or guidance..."
                  className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:ring-2 focus:ring-emerald-500 text-slate-900 resize-none"
                />
              </div>

              <div className="flex items-center gap-2 pt-1">
                <input
                  type="checkbox"
                  id="pinCheck"
                  checked={newIsPinned}
                  onChange={(e) => setNewIsPinned(e.target.checked)}
                  className="w-4 h-4 rounded border-slate-300 text-emerald-600 focus:ring-emerald-500 cursor-pointer"
                />
                <label htmlFor="pinCheck" className="text-xs text-slate-700 font-semibold cursor-pointer">
                  Pin this post to the top of the forum
                </label>
              </div>

              <div className="flex justify-end gap-2.5 pt-3 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsCreateModalOpen(false)}
                  className="h-9 px-4 text-xs font-semibold rounded-lg bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="h-9 px-5 text-xs font-bold rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white flex items-center gap-1.5 cursor-pointer shadow-xs"
                >
                  <Send className="w-3.5 h-3.5" />
                  <span>Publish</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
