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
  UserPlus,
  AlertCircle,
  Users,
} from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { CommunityPost, CommunityComment, CommunityReport, ReportStatus, Farmer } from '../types';
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
  const [farmers, setFarmers] = useState<Farmer[]>([]);
  
  // Top-level Mode Switcher
  const [activeHubMode, setActiveHubMode] = useState<'FEED' | 'CHAT' | 'REPORTS'>('FEED');

  const [selectedPost, setSelectedPost] = useState<CommunityPost | null>(null);
  const [selectedReport, setSelectedReport] = useState<CommunityReport | null>(null);
  const [comments, setComments] = useState<CommunityComment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [refreshing, setRefreshing] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [sortBy, setSortBy] = useState<'NEWEST' | 'LIKES' | 'COMMENTS' | 'PINNED'>('PINNED');
  const [notice, setNotice] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  // Moderation filter state
  const [reportFilterStatus, setReportFilterStatus] = useState<ReportStatus | 'ALL'>('ALL');
  const [reportFilterType, setReportFilterType] = useState<string>('ALL');

  // New Post Modal State
  const [isCreateModalOpen, setIsCreateModalOpen] = useState<boolean>(false);
  const [isPublishingPost, setIsPublishingPost] = useState<boolean>(false);
  const [newTitle, setNewTitle] = useState('');
  const [newContent, setNewContent] = useState('');
  const [newAuthorName, setNewAuthorName] = useState('');
  const [newIsPinned, setNewIsPinned] = useState(false);

  // Reply State
  const [replyText, setReplyText] = useState('');
  const [replying, setReplying] = useState(false);

  // ─── LIVE CHAT STATE (Farmer & Agronomist Direct Conversations) ─────────────
  const [activeFarmerIds, setActiveFarmerIds] = useState<string[]>(() => {
    try {
      const saved = localStorage.getItem('maptanim_admin_active_chats');
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  });

  const saveActiveFarmerIds = (ids: string[]) => {
    setActiveFarmerIds(ids);
    try {
      localStorage.setItem('maptanim_admin_active_chats', JSON.stringify(ids));
    } catch (e) {
      console.warn('Failed to save active chat IDs', e);
    }
  };

  const [isAddFarmerModalOpen, setIsAddFarmerModalOpen] = useState<boolean>(false);
  const [farmerSearchQuery, setFarmerSearchQuery] = useState<string>('');

  const [selectedChannelId, setSelectedChannelId] = useState<string | null>(null);
  const [chatSearchQuery, setChatSearchQuery] = useState<string>('');
  const [chatInputText, setChatInputText] = useState<string>('');

  const [channelMessages, setChannelMessages] = useState<Record<string, ChatMessage[]>>({});

  // Active conversations derived strictly from added farmers (No mock "gen" channel)
  const chatChannels: ChatChannel[] = farmers
    .filter((f) => activeFarmerIds.includes(f.id))
    .map((f) => ({
      id: f.id,
      name: f.fullName,
      subtitle: `${f.farmName} • ${f.isOnline ? 'Online' : f.lastActiveAt || 'Active'}`,
      iconEmoji: '👨‍🌾',
      unreadCount: 0,
    }));

  useEffect(() => {
    if (chatChannels.length > 0) {
      if (!selectedChannelId || !chatChannels.some((c) => c.id === selectedChannelId)) {
        setSelectedChannelId(chatChannels[0].id);
      }
    } else {
      setSelectedChannelId(null);
    }
  }, [chatChannels, selectedChannelId]);

  const selectedChannel = chatChannels.find((c) => c.id === selectedChannelId) || null;

  // Filter channels by farmer name AND message content
  const filteredChannels = chatChannels.filter((channel) => {
    if (!chatSearchQuery.trim()) return true;
    const query = chatSearchQuery.toLowerCase();
    const nameMatches = channel.name.toLowerCase().includes(query);
    const msgs = channelMessages[channel.id] || [];
    const messageMatches = msgs.some((m) => m.text.toLowerCase().includes(query));
    return nameMatches || messageMatches;
  });

  const activeMessages = (selectedChannelId && channelMessages[selectedChannelId]) || [];

  const availableFarmers = farmers.filter((f) => {
    if (!farmerSearchQuery.trim()) return true;
    const q = farmerSearchQuery.toLowerCase();
    return (
      f.fullName.toLowerCase().includes(q) ||
      f.farmName.toLowerCase().includes(q)
    );
  });

  const fetchFarmers = async () => {
    try {
      const data = await apiService.getFarmers();
      setFarmers(data);
    } catch (err) {
      console.warn('Failed to fetch farmers', err);
    }
  };

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
      await Promise.all([fetchPosts(), fetchReports(), fetchFarmers()]);
      setLoading(false);
    };
    init();
  }, []);

  const handleRefresh = async () => {
    setRefreshing(true);
    await Promise.all([fetchPosts(selectedPost?.id), fetchReports(), fetchFarmers()]);
    setRefreshing(false);
    showNotice('Community records synchronized.');
  };

  const showNotice = (msg: string, type: 'success' | 'error' = 'success') => {
    setNotice({ message: msg, type });
    setTimeout(() => setNotice(null), 3500);
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
      showNotice('Please provide both a title and content.', 'error');
      return;
    }

    setIsPublishingPost(true);
    try {
      const created = await apiService.createCommunityPost({
        title: newTitle.trim(),
        category: 'GENERAL',
        content: newContent.trim(),
        authorName: newAuthorName.trim() || 'MapTanim Agronomy Desk',
        tags: ['CropCare', 'Vegetables', 'Community'],
        isPinned: newIsPinned,
      });

      setIsCreateModalOpen(false);
      setNewTitle('');
      setNewContent('');
      setNewAuthorName('');
      setNewIsPinned(false);
      showNotice('Post published successfully to Community Hub! 🌾', 'success');
      await fetchPosts(created?.id);
    } catch (err: any) {
      console.error('Failed to create post:', err);
      showNotice(err?.message || 'Failed to publish post. Please check your connection.', 'error');
    } finally {
      setIsPublishingPost(false);
    }
  };

  const handleSendChatMessage = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!chatInputText.trim() || !selectedChannelId) return;

    const newMsg: ChatMessage = {
      id: 'm_' + Date.now(),
      sender: 'admin',
      senderName: 'MapTanim Agronomist (Admin)',
      text: chatInputText.trim(),
      timestamp: 'Just now',
    };

    const currentList = channelMessages[selectedChannelId] || [];

    setChannelMessages((prev) => ({
      ...prev,
      [selectedChannelId]: [...currentList, newMsg],
    }));
    setChatInputText('');
    showNotice('Message sent', 'success');
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
      {notice && (
        <div
          className={`fixed top-24 right-8 z-50 flex items-center gap-3 px-5 py-3.5 bg-[#183145] text-[#F4F4F4] border rounded-2xl shadow-xl text-sm font-bold animate-slideIn ${
            notice.type === 'error' ? 'border-[#E76F51]' : 'border-[#38434D]'
          }`}
        >
          {notice.type === 'error' ? (
            <AlertCircle className="w-5 h-5 text-[#E76F51] shrink-0" />
          ) : (
            <CheckCircle2 className="w-5 h-5 text-[#4CAF50] shrink-0" />
          )}
          <span>{notice.message}</span>
          <button
            type="button"
            onClick={() => setNotice(null)}
            className="ml-2 text-[#8A9BA8] hover:text-white transition cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* ─── 1. TOP BAR ACTIONS & TABS ─── */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-[#2B3136] p-4 sm:p-5 rounded-2xl border border-[#38434D] shadow-sm">
        {/* Main Category Tabs */}
        <div className="inline-flex p-1 bg-[#1D2429] border border-[#38434D] rounded-xl">
          <button
            onClick={() => setActiveHubMode('FEED')}
            className={`px-5 py-2.5 rounded-lg text-xs sm:text-sm font-bold transition cursor-pointer flex items-center gap-2 ${
              activeHubMode === 'FEED'
                ? 'bg-[#4CAF50] text-white shadow-xs'
                : 'text-[#C7D0D8] hover:text-[#F4F4F4]'
            }`}
          >
            <MessageSquare className="w-4 h-4" />
            <span>Discussions ({totalPostsCount})</span>
          </button>

          <button
            onClick={() => setActiveHubMode('CHAT')}
            className={`px-5 py-2.5 rounded-lg text-xs sm:text-sm font-bold transition cursor-pointer flex items-center gap-2 ${
              activeHubMode === 'CHAT'
                ? 'bg-[#4CAF50] text-white shadow-xs'
                : 'text-[#C7D0D8] hover:text-[#F4F4F4]'
            }`}
          >
            <span>💬</span>
            <span>Farmer Channels ({chatChannels.length})</span>
          </button>

          <button
            onClick={() => setActiveHubMode('REPORTS')}
            className={`px-5 py-2.5 rounded-lg text-xs sm:text-sm font-bold transition cursor-pointer flex items-center gap-2 ${
              activeHubMode === 'REPORTS'
                ? 'bg-[#4CAF50] text-white shadow-xs'
                : 'text-[#C7D0D8] hover:text-[#F4F4F4]'
            }`}
          >
            <Flag className="w-4 h-4 text-[#E76F51]" />
            <span>Reports</span>
            {pendingReportsCount > 0 && (
              <span className="ml-1 px-2 py-0.2 rounded-full text-xs font-bold bg-[#E76F51] text-white">
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
                <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-[#8A9BA8]" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search discussions..."
                  className="w-full h-10 pl-9 pr-3 text-xs bg-[#1D2429] border border-[#38434D] rounded-xl outline-none focus:border-[#4CAF50] text-[#F4F4F4] placeholder-[#8A9BA8]"
                />
              </div>

              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value as any)}
                className="h-10 px-3 text-xs font-semibold bg-[#1D2429] border border-[#38434D] rounded-xl outline-none text-[#F4F4F4] cursor-pointer"
              >
                <option value="PINNED">Pinned First</option>
                <option value="NEWEST">Newest</option>
                <option value="LIKES">Most Liked</option>
                <option value="COMMENTS">Most Comments</option>
              </select>

              <button
                onClick={() => setIsCreateModalOpen(true)}
                className="h-10 px-4 text-xs font-bold rounded-xl bg-[#4CAF50] hover:bg-[#388E3C] text-white flex items-center gap-2 transition cursor-pointer shadow-xs"
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
              className="h-10 px-3 text-xs font-semibold bg-[#1D2429] border border-[#38434D] rounded-xl outline-none text-[#F4F4F4] cursor-pointer"
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
            className="h-10 px-3 text-xs font-semibold rounded-xl bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] hover:text-[#F4F4F4] hover:border-[#4CAF50] transition flex items-center gap-1.5 shadow-xs cursor-pointer"
            title="Sync Database"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${refreshing ? 'animate-spin text-[#4CAF50]' : 'text-[#8A9BA8]'}`} />
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
              <div className="p-10 bg-[#2B3136] border border-[#38434D] rounded-2xl text-center text-[#8A9BA8] flex flex-col items-center justify-center gap-2 shadow-sm">
                <RefreshCw className="w-5 h-5 animate-spin text-[#4CAF50]" />
                <p className="text-xs font-semibold">Loading discussions...</p>
              </div>
            ) : filteredPosts.length === 0 ? (
              <div className="p-10 bg-[#2B3136] border border-[#38434D] rounded-2xl text-center text-[#8A9BA8] space-y-2 shadow-sm">
                <MessageSquare className="w-8 h-8 mx-auto text-[#8A9BA8] opacity-50" />
                <p className="text-sm font-bold text-[#F4F4F4]">No discussions found</p>
                <p className="text-xs text-[#8A9BA8]">Click "+ Create Post" to publish.</p>
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
                        ? 'bg-[#4CAF50]/15 border-[#4CAF50] ring-1 ring-[#4CAF50] shadow-sm'
                        : 'bg-[#2B3136] border-[#38434D] hover:border-[#4CAF50] shadow-sm hover:shadow-md'
                    }`}
                  >
                    {/* Header */}
                    <div className="flex items-center justify-between gap-3 pb-3 border-b border-[#38434D] mb-3">
                      <div className="flex items-center gap-2.5 min-w-0">
                        <div className="w-7 h-7 rounded-full bg-[#4CAF50]/20 text-[#4CAF50] font-bold text-xs flex items-center justify-center shrink-0">
                          {post.authorName.charAt(0)}
                        </div>
                        <div className="min-w-0">
                          <span className="font-bold text-[#F4F4F4] text-xs block truncate">
                            {post.authorName}
                          </span>
                          <span className="text-[11px] text-[#8A9BA8] font-mono">
                            {new Date(post.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}
                          </span>
                        </div>
                      </div>

                      <div className="flex items-center gap-1.5 shrink-0">
                        {hasPendingReport && (
                          <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-[#E76F51]/20 text-[#E76F51] border border-[#E76F51]/40">
                            Reported
                          </span>
                        )}
                        {post.isPinned && (
                          <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-[#F4A261]/20 text-[#F4A261] border border-[#F4A261]/40 flex items-center gap-1">
                            <Pin className="w-3 h-3" />
                            Pinned
                          </span>
                        )}
                      </div>
                    </div>

                    {/* Title */}
                    <h4 className="text-sm font-bold text-[#F4F4F4] leading-snug mb-1.5">
                      {post.title}
                    </h4>

                    {/* Excerpt */}
                    <p className="text-xs text-[#C7D0D8] line-clamp-2 leading-relaxed mb-3">
                      {post.content}
                    </p>

                    {/* Footer */}
                    <div className="flex items-center justify-between pt-3 border-t border-[#38434D] text-xs text-[#8A9BA8] font-mono">
                      <span className="text-[11px] text-[#4CAF50] font-semibold">
                        Community Discussion
                      </span>

                      <div className="flex items-center gap-3">
                        <span className="flex items-center gap-1 text-[#E76F51]">
                          <Heart className="w-3.5 h-3.5 fill-[#E76F51]/20" />
                          {post.likesCount}
                        </span>
                        <span className="flex items-center gap-1 text-[#4CAF50]">
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
              <div className="bg-[#2B3136] border border-[#38434D] rounded-2xl p-6 sm:p-7 space-y-6 shadow-sm">
                {/* Header */}
                <div className="flex items-start justify-between gap-4 pb-4 border-b border-[#38434D]">
                  <div>
                    <h3 className="text-xl font-bold text-[#F4F4F4] leading-tight">
                      {selectedPost.title}
                    </h3>
                    <p className="text-xs text-[#8A9BA8] mt-1 font-mono">
                      Posted by <span className="font-semibold text-[#C7D0D8]">{selectedPost.authorName}</span> • {new Date(selectedPost.createdAt).toLocaleString()}
                    </p>
                  </div>

                  <div className="flex items-center gap-2 shrink-0">
                    <button
                      onClick={(e) => handleTogglePin(selectedPost.id, selectedPost.isPinned, e)}
                      className={`p-2 rounded-xl border transition cursor-pointer ${
                        selectedPost.isPinned
                          ? 'bg-[#F4A261]/20 text-[#F4A261] border-[#F4A261]/40'
                          : 'bg-[#1D2429] text-[#8A9BA8] border-[#38434D] hover:text-[#F4A261]'
                      }`}
                      title={selectedPost.isPinned ? 'Unpin' : 'Pin'}
                    >
                      <Pin className="w-4 h-4" />
                    </button>
                    <button
                      onClick={(e) => handleDeletePost(selectedPost.id, e)}
                      className="p-2 rounded-xl bg-[#1D2429] text-[#8A9BA8] border border-[#38434D] hover:text-[#E76F51] hover:bg-[#E76F51]/20 transition cursor-pointer"
                      title="Delete Post"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* Content */}
                <div className="text-sm text-[#F4F4F4] leading-relaxed bg-[#1D2429] p-5 rounded-xl border border-[#38434D]">
                  <p className="whitespace-pre-line leading-relaxed">{selectedPost.content}</p>
                </div>

                {/* Comments */}
                <div className="space-y-4 pt-2 border-t border-[#38434D]">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-[#8A9BA8]">
                    Comments ({comments.length})
                  </h4>

                  <div className="space-y-3 max-h-[280px] overflow-y-auto pr-1">
                    {comments.length === 0 ? (
                      <div className="p-6 text-center text-xs text-[#8A9BA8] bg-[#1D2429] rounded-xl border border-dashed border-[#38434D]">
                        No responses yet. Write an official agronomy reply below.
                      </div>
                    ) : (
                      comments.map((comment) => (
                        <div
                          key={comment.id}
                          className="p-3.5 rounded-xl bg-[#1D2429] border border-[#38434D] text-xs space-y-1.5"
                        >
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <span className="font-bold text-[#F4F4F4]">{comment.authorName}</span>
                              {comment.authorName.includes('Admin') && (
                                <Badge variant="success">ADMIN</Badge>
                              )}
                            </div>
                            <div className="flex items-center gap-2 text-[#8A9BA8] font-mono text-[10px]">
                              <span>{new Date(comment.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                              <button
                                onClick={() => handleDeleteComment(comment.id)}
                                className="hover:text-[#E76F51] cursor-pointer"
                                title="Delete comment"
                              >
                                <Trash2 className="w-3.5 h-3.5" />
                              </button>
                            </div>
                          </div>
                          <p className="text-[#C7D0D8] leading-relaxed pl-1">{comment.content}</p>
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
                      className="flex-1 h-10 px-3.5 text-xs bg-[#1D2429] border border-[#38434D] rounded-xl outline-none focus:border-[#4CAF50] text-[#F4F4F4] placeholder-[#8A9BA8]"
                      onKeyDown={(e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); handleAddComment(); } }}
                    />
                    <button
                      onClick={handleAddComment}
                      disabled={replying || !replyText.trim()}
                      className="h-10 px-4 bg-[#4CAF50] hover:bg-[#388E3C] text-white text-xs font-bold rounded-xl disabled:opacity-50 flex items-center gap-1.5 cursor-pointer shadow-xs"
                    >
                      <Send className="w-3.5 h-3.5" />
                      <span>Reply</span>
                    </button>
                  </div>
                </div>
              </div>
            ) : (
              <div className="p-16 bg-[#2B3136] border border-[#38434D] rounded-2xl text-center text-[#8A9BA8] space-y-3 shadow-sm">
                <MessageSquare className="w-10 h-10 mx-auto text-[#8A9BA8] opacity-50" />
                <h4 className="text-base font-bold text-[#F4F4F4]">Select a Discussion</h4>
                <p className="text-xs text-[#8A9BA8] max-w-sm mx-auto">
                  Click on any discussion post on the left to inspect conversation details and post replies.
                </p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* MODE 2: FARMER DIRECT CHATS */}
      {activeHubMode === 'CHAT' && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Channels column */}
          <div className="lg:col-span-4 space-y-3">
            {/* Header with Conversations and Add Farmer button */}
            <div className="flex items-center justify-between pb-1 px-1">
              <span className="text-[11px] font-bold text-[#8A9BA8] uppercase tracking-wider">
                Conversations ({chatChannels.length})
              </span>
              <button
                type="button"
                onClick={() => setIsAddFarmerModalOpen(true)}
                className="flex items-center gap-1.5 px-2.5 py-1 bg-[#4CAF50]/15 hover:bg-[#4CAF50]/25 text-[#4CAF50] border border-[#4CAF50]/40 rounded-lg text-xs font-bold transition cursor-pointer"
              >
                <UserPlus className="w-3.5 h-3.5" />
                <span>Add Farmer</span>
              </button>
            </div>

            {/* Search bar for friend/farmer and conversation */}
            <div className="relative">
              <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-[#8A9BA8]" />
              <input
                type="text"
                value={chatSearchQuery}
                onChange={(e) => setChatSearchQuery(e.target.value)}
                placeholder="Search farmers & conversations..."
                className="w-full h-10 pl-9 pr-3 text-xs bg-[#1D2429] border border-[#38434D] rounded-xl outline-none focus:border-[#4CAF50] text-[#F4F4F4] placeholder-[#8A9BA8]"
              />
            </div>

            {chatChannels.length === 0 ? (
              <div className="p-8 bg-[#2B3136] border border-[#38434D] rounded-2xl text-center text-[#8A9BA8] space-y-2 shadow-xs">
                <Users className="w-8 h-8 mx-auto text-[#8A9BA8]/50" />
                <p className="text-xs font-bold text-[#F4F4F4]">No conversations yet</p>
                <p className="text-[11px] text-[#8A9BA8]">Click "+ Add Farmer" above to start direct messaging.</p>
              </div>
            ) : filteredChannels.length === 0 ? (
              <div className="p-6 bg-[#2B3136] border border-[#38434D] rounded-xl text-center text-[#8A9BA8] space-y-1">
                <p className="text-xs font-semibold text-[#F4F4F4]">No matching conversations</p>
                <p className="text-[11px] text-[#8A9BA8]">Try searching a different name or message phrase.</p>
              </div>
            ) : (
              <div className="space-y-2">
                {filteredChannels.map((channel) => {
                  const isSelected = selectedChannelId === channel.id;
                  return (
                    <div
                      key={channel.id}
                      onClick={() => setSelectedChannelId(channel.id)}
                      className={`p-3.5 rounded-xl border transition cursor-pointer flex items-center justify-between ${
                        isSelected
                          ? 'bg-[#4CAF50]/15 border-[#4CAF50] text-[#F4F4F4] shadow-xs'
                          : 'bg-[#2B3136] border-[#38434D] hover:border-[#4CAF50] text-[#F4F4F4] shadow-xs'
                      }`}
                    >
                      <div className="flex items-center gap-3 min-w-0">
                        <span className="text-2xl shrink-0">{channel.iconEmoji}</span>
                        <div className="min-w-0">
                          <p className="text-xs font-bold text-[#F4F4F4] truncate">
                            {channel.name}
                          </p>
                          <p className="text-[11px] text-[#8A9BA8] truncate mt-0.5">
                            {channel.subtitle}
                          </p>
                        </div>
                      </div>
                      {channel.unreadCount > 0 && (
                        <span className="px-2 py-0.5 rounded-full text-xs font-bold bg-[#4CAF50] text-white">
                          {channel.unreadCount}
                        </span>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Chat Stream or Empty State */}
          <div className="lg:col-span-8 bg-[#2B3136] border border-[#38434D] rounded-2xl p-6 flex flex-col h-[600px] shadow-sm">
            {!selectedChannel ? (
              <div className="h-full flex flex-col items-center justify-center text-center text-[#8A9BA8] space-y-3 py-16">
                <MessageSquare className="w-12 h-12 text-[#8A9BA8]/40" />
                <h4 className="text-base font-bold text-[#F4F4F4]">
                  {chatChannels.length === 0 ? 'No Conversations Yet' : 'No Conversation Selected'}
                </h4>
                <p className="text-xs text-[#8A9BA8] max-w-sm">
                  {chatChannels.length === 0
                    ? 'Start communicating directly with registered farmers by adding them to your conversations list.'
                    : 'Select a farmer from the conversations list on the left to review message history and reply.'}
                </p>
                {chatChannels.length === 0 && (
                  <button
                    type="button"
                    onClick={() => setIsAddFarmerModalOpen(true)}
                    className="mt-2 px-4 py-2 bg-[#4CAF50] hover:bg-[#388E3C] text-white text-xs font-bold rounded-xl flex items-center gap-2 cursor-pointer shadow-xs transition"
                  >
                    <UserPlus className="w-4 h-4" />
                    <span>Add Farmer to Chat</span>
                  </button>
                )}
              </div>
            ) : (
              <>
                <div className="flex items-center justify-between pb-3 border-b border-[#38434D] mb-3">
                  <div className="flex items-center gap-3">
                    <span className="text-2xl">{selectedChannel.iconEmoji}</span>
                    <div>
                      <h4 className="text-sm font-bold text-[#F4F4F4]">
                        {selectedChannel.name}
                      </h4>
                      <p className="text-xs text-[#4CAF50] font-medium">{selectedChannel.subtitle}</p>
                    </div>
                  </div>
                </div>

                <div className="flex-1 overflow-y-auto space-y-3.5 p-2">
                  {activeMessages.length === 0 ? (
                    <div className="h-full flex flex-col items-center justify-center text-center text-[#8A9BA8] space-y-2 py-12">
                      <MessageSquare className="w-8 h-8 text-[#8A9BA8]/40" />
                      <p className="text-xs font-semibold text-[#F4F4F4]">No messages in this conversation yet</p>
                      <p className="text-[11px] text-[#8A9BA8]">Send a message to start communicating with {selectedChannel.name}.</p>
                    </div>
                  ) : (
                    activeMessages.map((msg) => {
                      const isAdmin = msg.sender === 'admin';
                      return (
                        <div key={msg.id} className={`flex flex-col ${isAdmin ? 'items-end' : 'items-start'}`}>
                          <div className="flex items-center gap-2 text-[11px] text-[#8A9BA8] mb-1 font-mono">
                            <span className="font-bold text-[#C7D0D8]">{msg.senderName}</span>
                            <span>•</span>
                            <span>{msg.timestamp}</span>
                          </div>
                          <div
                            className={`max-w-[75%] px-4 py-2.5 rounded-xl text-xs leading-relaxed ${
                              isAdmin
                                ? 'bg-[#4CAF50] text-white'
                                : 'bg-[#1D2429] text-[#F4F4F4] border border-[#38434D]'
                            }`}
                          >
                            {msg.text}
                          </div>
                        </div>
                      );
                    })
                  )}
                </div>

                <form onSubmit={handleSendChatMessage} className="pt-3 border-t border-[#38434D] flex gap-2">
                  <input
                    type="text"
                    value={chatInputText}
                    onChange={(e) => setChatInputText(e.target.value)}
                    placeholder={`Message ${selectedChannel.name}...`}
                    className="flex-1 h-10 px-3 text-xs bg-[#1D2429] border border-[#38434D] rounded-xl outline-none focus:border-[#4CAF50] text-[#F4F4F4] placeholder-[#8A9BA8]"
                  />
                  <button
                    type="submit"
                    disabled={!chatInputText.trim()}
                    className="h-10 px-4 bg-[#4CAF50] hover:bg-[#388E3C] text-white text-xs font-bold rounded-xl disabled:opacity-50 flex items-center gap-1.5 cursor-pointer shadow-xs"
                  >
                    <Send className="w-3.5 h-3.5" />
                    <span>Send</span>
                  </button>
                </form>
              </>
            )}
          </div>
        </div>
      )}

      {/* MODE 3: MODERATION REPORTS */}
      {activeHubMode === 'REPORTS' && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Left: Reports list */}
          <div className="lg:col-span-5 space-y-3 max-h-[800px] overflow-y-auto pr-1">
            {filteredReports.length === 0 ? (
              <div className="p-10 bg-[#2B3136] border border-[#38434D] rounded-2xl text-center text-[#8A9BA8] space-y-2 shadow-sm">
                <ShieldCheck className="w-8 h-8 mx-auto text-[#4CAF50]" />
                <p className="text-sm font-bold text-[#F4F4F4]">No reports found</p>
                <p className="text-xs text-[#8A9BA8]">All community guidelines are complied with.</p>
              </div>
            ) : (
              filteredReports.map((report) => {
                const isSelected = selectedReport?.id === report.id;
                const statusBadge =
                  report.status === 'PENDING'
                    ? 'bg-[#F4A261]/20 text-[#F4A261]'
                    : report.status === 'RESOLVED'
                    ? 'bg-[#4CAF50]/20 text-[#4CAF50]'
                    : report.status === 'DISMISSED'
                    ? 'bg-[#1D2429] text-[#8A9BA8]'
                    : 'bg-[#E76F51]/20 text-[#E76F51]';

                return (
                  <div
                    key={report.id}
                    onClick={() => setSelectedReport(report)}
                    className={`p-4 rounded-xl border transition cursor-pointer text-left ${
                      isSelected
                        ? 'bg-[#E76F51]/15 border-[#E76F51] shadow-sm'
                        : 'bg-[#2B3136] border-[#38434D] hover:border-[#4CAF50] shadow-sm'
                    }`}
                  >
                    <div className="flex items-center justify-between pb-2 border-b border-[#38434D] mb-2">
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-[#1D2429] border border-[#38434D] text-[#8A9BA8] uppercase font-mono">
                        {report.targetType}
                      </span>
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${statusBadge}`}>
                        {report.status}
                      </span>
                    </div>

                    <h4 className="text-xs font-bold text-[#F4F4F4] truncate">
                      {report.targetName}
                    </h4>

                    <p className="text-xs text-[#E76F51] font-semibold mt-1">
                      {report.reason}
                    </p>

                    <div className="flex items-center justify-between pt-2 mt-2 border-t border-[#38434D] text-[10px] text-[#8A9BA8] font-mono">
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
              <div className="bg-[#2B3136] border border-[#38434D] rounded-2xl p-6 space-y-5 shadow-sm">
                <div className="flex items-start justify-between gap-4 pb-4 border-b border-[#38434D]">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <Badge variant={selectedReport.status === 'RESOLVED' ? 'success' : selectedReport.status === 'PENDING' ? 'warning' : 'danger'}>
                        {selectedReport.status}
                      </Badge>
                      <span className="text-xs font-mono text-[#8A9BA8]">Report #{selectedReport.id}</span>
                    </div>
                    <h3 className="text-base font-bold text-[#F4F4F4] flex items-center gap-2">
                      <Flag className="w-4 h-4 text-[#E76F51]" />
                      <span>{selectedReport.targetType} Violation Review</span>
                    </h3>
                  </div>

                  <div className="text-right text-xs text-[#8A9BA8] font-mono">
                    <p>{new Date(selectedReport.createdAt).toLocaleString()}</p>
                    <p className="text-[#C7D0D8] mt-0.5">By {selectedReport.reporterName}</p>
                  </div>
                </div>

                <div className="p-4 rounded-xl bg-[#E76F51]/10 border border-[#E76F51]/30 space-y-1.5">
                  <p className="text-[10px] font-bold text-[#E76F51] uppercase tracking-wider">Reported Target</p>
                  <h4 className="text-sm font-bold text-[#F4F4F4]">{selectedReport.targetName}</h4>
                  {selectedReport.targetContent && (
                    <p className="text-xs text-[#C7D0D8] font-mono mt-1 bg-[#1D2429] p-3 rounded-lg border border-[#38434D] leading-relaxed">
                      "{selectedReport.targetContent}"
                    </p>
                  )}
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                  <div className="p-3 rounded-xl bg-[#1D2429] border border-[#38434D]">
                    <p className="text-[#8A9BA8] font-bold uppercase text-[10px]">Violation Reason</p>
                    <p className="text-xs font-bold text-[#E76F51] mt-0.5">{selectedReport.reason}</p>
                  </div>
                  <div className="p-3 rounded-xl bg-[#1D2429] border border-[#38434D]">
                    <p className="text-[#8A9BA8] font-bold uppercase text-[10px]">Reporter Notes</p>
                    <p className="text-[#C7D0D8] mt-0.5">{selectedReport.details || 'None provided.'}</p>
                  </div>
                </div>

                <div className="pt-3 border-t border-[#38434D] space-y-2">
                  <h4 className="text-[11px] font-bold uppercase tracking-wider text-[#8A9BA8]">Moderation Actions</h4>
                  <div className="flex flex-wrap gap-2">
                    <button
                      onClick={() => handleDeleteReportedContent(selectedReport)}
                      className="h-9 px-4 bg-[#E76F51] hover:bg-[#D35400] text-white text-xs font-bold rounded-lg flex items-center gap-1.5 cursor-pointer shadow-xs"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                      <span>Delete & Resolve</span>
                    </button>

                    <button
                      onClick={() => handleUpdateReportStatus(selectedReport.id, 'INVESTIGATING', 'Under inspection.')}
                      className="h-9 px-4 bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] text-xs font-bold rounded-lg hover:border-[#4CAF50] flex items-center gap-1.5 cursor-pointer shadow-xs"
                    >
                      <Clock className="w-3.5 h-3.5 text-[#F4A261]" />
                      <span>Mark Investigating</span>
                    </button>

                    <button
                      onClick={() => handleDismissReport(selectedReport.id)}
                      className="h-9 px-4 bg-[#1D2429] border border-[#38434D] text-[#8A9BA8] text-xs font-bold rounded-lg hover:text-[#F4F4F4] flex items-center gap-1.5 cursor-pointer shadow-xs"
                    >
                      <X className="w-3.5 h-3.5" />
                      <span>Dismiss</span>
                    </button>
                  </div>
                </div>
              </div>
            ) : (
              <div className="p-16 bg-[#2B3136] border border-[#38434D] rounded-2xl text-center text-[#8A9BA8] space-y-3 shadow-sm">
                <ShieldAlert className="w-10 h-10 mx-auto text-[#8A9BA8] opacity-50" />
                <h4 className="text-base font-bold text-[#F4F4F4]">Select a Moderation Report</h4>
                <p className="text-xs text-[#8A9BA8] max-w-sm mx-auto">
                  Click on any user-submitted report on the left desk to review offending content.
                </p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Modal: Create Post */}
      {isCreateModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#112230]/80 backdrop-blur-xs animate-fadeIn">
          <div className="bg-[#2B3136] w-full max-w-lg p-6 rounded-2xl border border-[#38434D] shadow-2xl space-y-4 animate-scaleUp">
            <div className="flex items-center justify-between border-b border-[#38434D] pb-3">
              <h3 className="text-base font-bold text-[#F4F4F4]">
                Create Community Post
              </h3>
              <button
                onClick={() => setIsCreateModalOpen(false)}
                className="p-1.5 rounded-lg text-[#8A9BA8] hover:text-[#F4F4F4] cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreatePost} className="space-y-4 text-xs">
              <div className="space-y-1">
                <label className="font-bold text-[#C7D0D8] uppercase tracking-wider text-[11px]">Title</label>
                <input
                  type="text"
                  required
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  placeholder="Post title..."
                  className="w-full h-10 px-3 bg-[#1D2429] border border-[#38434D] rounded-xl outline-none focus:border-[#4CAF50] text-[#F4F4F4] placeholder-[#8A9BA8]"
                />
              </div>

              <div className="space-y-1">
                <label className="font-bold text-[#C7D0D8] uppercase tracking-wider text-[11px]">Author Name</label>
                <input
                  type="text"
                  list="authorSuggestions"
                  value={newAuthorName}
                  onChange={(e) => setNewAuthorName(e.target.value)}
                  placeholder="MapTanim Agronomy Desk"
                  className="w-full h-10 px-3 bg-[#1D2429] border border-[#38434D] rounded-xl outline-none focus:border-[#4CAF50] text-[#F4F4F4] placeholder-[#8A9BA8]"
                />
                <datalist id="authorSuggestions">
                  <option value="MapTanim Agronomy Desk" />
                  {farmers.map((f) => (
                    <option key={f.id} value={f.fullName} />
                  ))}
                </datalist>
                <div className="flex flex-wrap gap-1.5 pt-1">
                  <button
                    type="button"
                    onClick={() => setNewAuthorName('MapTanim Agronomy Desk')}
                    className="text-[10px] px-2 py-0.5 rounded-md bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] hover:text-[#4CAF50] hover:border-[#4CAF50] transition cursor-pointer"
                  >
                    Agronomy Desk
                  </button>
                  {farmers.slice(0, 3).map((f) => (
                    <button
                      key={f.id}
                      type="button"
                      onClick={() => setNewAuthorName(f.fullName)}
                      className="text-[10px] px-2 py-0.5 rounded-md bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] hover:text-[#4CAF50] hover:border-[#4CAF50] transition cursor-pointer"
                    >
                      {f.fullName}
                    </button>
                  ))}
                </div>
              </div>

              <div className="space-y-1">
                <label className="font-bold text-[#C7D0D8] uppercase tracking-wider text-[11px]">Content</label>
                <textarea
                  rows={4}
                  required
                  disabled={isPublishingPost}
                  value={newContent}
                  onChange={(e) => setNewContent(e.target.value)}
                  placeholder="Write your discussion content or guidance..."
                  className="w-full p-3 bg-[#1D2429] border border-[#38434D] rounded-xl outline-none focus:border-[#4CAF50] text-[#F4F4F4] placeholder-[#8A9BA8] resize-none disabled:opacity-60"
                />
              </div>

              <div className="flex items-center gap-2 pt-1">
                <input
                  type="checkbox"
                  id="pinCheck"
                  disabled={isPublishingPost}
                  checked={newIsPinned}
                  onChange={(e) => setNewIsPinned(e.target.checked)}
                  className="w-4 h-4 rounded border-[#38434D] bg-[#1D2429] text-[#4CAF50] focus:ring-[#4CAF50] cursor-pointer"
                />
                <label htmlFor="pinCheck" className="text-xs text-[#C7D0D8] font-semibold cursor-pointer">
                  Pin this post to the top of the forum
                </label>
              </div>

              <div className="flex justify-end gap-2.5 pt-3 border-t border-[#38434D]">
                <button
                  type="button"
                  disabled={isPublishingPost}
                  onClick={() => setIsCreateModalOpen(false)}
                  className="h-9 px-4 text-xs font-semibold rounded-lg bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] hover:text-[#F4F4F4] cursor-pointer disabled:opacity-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isPublishingPost || !newTitle.trim() || !newContent.trim()}
                  className="h-9 px-5 text-xs font-bold rounded-lg bg-[#4CAF50] hover:bg-[#388E3C] text-white flex items-center gap-1.5 cursor-pointer shadow-xs disabled:opacity-50"
                >
                  {isPublishingPost ? (
                    <>
                      <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                      <span>Publishing...</span>
                    </>
                  ) : (
                    <>
                      <Send className="w-3.5 h-3.5" />
                      <span>Publish Post</span>
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ADD FARMER MODAL */}
      {isAddFarmerModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-xs p-4 animate-fadeIn">
          <div className="bg-[#2B3136] border border-[#38434D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4 max-h-[85vh] flex flex-col">
            <div className="flex items-center justify-between pb-3 border-b border-[#38434D]">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-[#4CAF50]/15 flex items-center justify-center text-[#4CAF50]">
                  <UserPlus className="w-4 h-4" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-[#F4F4F4]">Add Farmer to Chat</h3>
                  <p className="text-[11px] text-[#8A9BA8]">Start a direct messaging conversation with a farmer.</p>
                </div>
              </div>
              <button
                onClick={() => setIsAddFarmerModalOpen(false)}
                className="text-[#8A9BA8] hover:text-[#F4F4F4] transition cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="relative">
              <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-[#8A9BA8]" />
              <input
                type="text"
                value={farmerSearchQuery}
                onChange={(e) => setFarmerSearchQuery(e.target.value)}
                placeholder="Search farmers by name or farm..."
                className="w-full h-10 pl-9 pr-3 text-xs bg-[#1D2429] border border-[#38434D] rounded-xl outline-none focus:border-[#4CAF50] text-[#F4F4F4] placeholder-[#8A9BA8]"
              />
            </div>

            <div className="flex-1 overflow-y-auto space-y-2 pr-1 min-h-[220px]">
              {availableFarmers.length === 0 ? (
                <div className="p-8 text-center text-[#8A9BA8] space-y-1">
                  <Users className="w-8 h-8 mx-auto text-[#8A9BA8]/40" />
                  <p className="text-xs font-semibold text-[#F4F4F4]">No farmers found</p>
                  <p className="text-[11px] text-[#8A9BA8]">Try searching with a different keyword.</p>
                </div>
              ) : (
                availableFarmers.map((f) => {
                  const isAlreadyAdded = activeFarmerIds.includes(f.id);
                  return (
                    <div
                      key={f.id}
                      className="p-3 bg-[#1D2429] border border-[#38434D] rounded-xl flex items-center justify-between gap-3"
                    >
                      <div className="flex items-center gap-2.5 min-w-0">
                        <div className="w-8 h-8 rounded-full bg-[#4CAF50]/20 border border-[#4CAF50]/40 flex items-center justify-center text-sm font-bold text-[#4CAF50] shrink-0">
                          {f.fullName.charAt(0).toUpperCase()}
                        </div>
                        <div className="min-w-0">
                          <p className="text-xs font-bold text-[#F4F4F4] truncate">{f.fullName}</p>
                          <p className="text-[11px] text-[#8A9BA8] truncate">
                            {f.farmName} • {f.isOnline ? 'Online' : f.lastActiveAt || 'Active'}
                          </p>
                        </div>
                      </div>

                      {isAlreadyAdded ? (
                        <button
                          type="button"
                          onClick={() => {
                            setSelectedChannelId(f.id);
                            setIsAddFarmerModalOpen(false);
                          }}
                          className="px-3 py-1.5 rounded-lg text-xs font-bold bg-[#4CAF50]/20 text-[#4CAF50] hover:bg-[#4CAF50]/30 transition shrink-0 cursor-pointer"
                        >
                          Open Chat
                        </button>
                      ) : (
                        <button
                          type="button"
                          onClick={() => {
                            const newIds = [...activeFarmerIds, f.id];
                            saveActiveFarmerIds(newIds);
                            setSelectedChannelId(f.id);
                            setIsAddFarmerModalOpen(false);
                            showNotice(`Started conversation with ${f.fullName}`);
                          }}
                          className="px-3 py-1.5 rounded-lg text-xs font-bold bg-[#4CAF50] hover:bg-[#388E3C] text-white transition shrink-0 flex items-center gap-1 cursor-pointer"
                        >
                          <Plus className="w-3 h-3" />
                          <span>Chat</span>
                        </button>
                      )}
                    </div>
                  );
                })
              )}
            </div>

            <div className="pt-3 border-t border-[#38434D] flex justify-end">
              <button
                type="button"
                onClick={() => setIsAddFarmerModalOpen(false)}
                className="h-9 px-4 text-xs font-semibold rounded-lg bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] hover:text-[#F4F4F4] cursor-pointer"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
