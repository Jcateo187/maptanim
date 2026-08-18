import React, { useEffect, useState } from 'react';
import { MessageSquare, RefreshCw, Send, CheckCircle2, CornerDownRight, Clock } from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { FeedbackItem } from '../types';
import { apiService } from '../services/api';

export const FeedbackManagement: React.FC = () => {
  const [feedbackList, setFeedbackList] = useState<FeedbackItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedTicket, setSelectedTicket] = useState<FeedbackItem | null>(null);
  const [replyText, setReplyText] = useState<string>('');
  const [replyStatus, setReplyStatus] = useState<'PENDING' | 'IN_PROGRESS' | 'RESOLVED'>('RESOLVED');
  const [submitting, setSubmitting] = useState<boolean>(false);

  const loadFeedback = async () => {
    setLoading(true);
    const data = await apiService.getFeedback();
    setFeedbackList(data);
    setLoading(false);
  };

  useEffect(() => {
    loadFeedback();
  }, []);

  const handleOpenReplyModal = (ticket: FeedbackItem) => {
    setSelectedTicket(ticket);
    setReplyText(ticket.adminReply || '');
    setReplyStatus(ticket.status === 'PENDING' ? 'RESOLVED' : ticket.status);
  };

  const handleSendReply = async () => {
    if (!selectedTicket) return;
    setSubmitting(true);
    await apiService.updateFeedbackStatus(
      selectedTicket.id,
      replyStatus,
      replyText.trim() || undefined,
      selectedTicket.farmerId,
      selectedTicket.subject
    );
    setSubmitting(false);
    setSelectedTicket(null);
    setReplyText('');
    loadFeedback();
  };

  const handleStatusChangeOnly = async (id: string, newStatus: 'PENDING' | 'IN_PROGRESS' | 'RESOLVED') => {
    await apiService.updateFeedbackStatus(id, newStatus);
    loadFeedback();
  };

  return (
    <div className="space-y-6 animate-fadeIn pb-12">
      {/* Action Header Card */}
      <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm flex flex-col sm:flex-row gap-5 items-start sm:items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center font-bold shrink-0 border border-emerald-100">
            <MessageSquare className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-3">
              <h3 className="font-extrabold text-base text-slate-900">
                Farmer Support & Feedback Queue
              </h3>
              <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-purple-50 text-purple-700 border border-purple-100">
                {feedbackList.length} Tickets
              </span>
            </div>
            <p className="text-xs text-slate-400 mt-0.5">
              Live smallholder support inquiries and push advisories
            </p>
          </div>
        </div>

        <button onClick={loadFeedback} className="h-10 px-4 rounded-xl bg-white border border-slate-200 text-slate-700 text-xs font-semibold hover:bg-slate-50 transition flex items-center gap-2 shadow-xs cursor-pointer">
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-emerald-600' : 'text-slate-400'}`} />
          <span>Refresh Queue</span>
        </button>
      </div>

      {/* Feedback Items Section */}
      <div className="space-y-4">
        {feedbackList.map((item) => (
          <div key={item.id} className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm space-y-4">
            {/* Header: Farmer Info & Status */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-slate-100">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-emerald-100 text-emerald-700 font-extrabold flex items-center justify-center text-xs border border-emerald-200 shrink-0">
                  {item.farmerName.charAt(0)}
                </div>
                <div>
                  <h4 className="font-bold text-sm text-slate-900">
                    {item.subject}
                  </h4>
                  <p className="text-xs text-slate-400 mt-0.5 flex items-center gap-2 font-mono">
                    <span>By <strong className="text-slate-700">{item.farmerName}</strong></span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <Clock className="w-3 h-3 text-slate-400" />
                      {new Date(item.createdAt).toLocaleString()}
                    </span>
                  </p>
                </div>
              </div>

              <div className="flex items-center gap-2.5 self-end sm:self-auto">
                <Badge variant={item.category === 'AGRONOMIC_QUERY' ? 'purple' : item.category === 'BUG' ? 'danger' : 'info'}>
                  {item.category}
                </Badge>
                
                <select
                  value={item.status}
                  onChange={(e) => handleStatusChangeOnly(item.id, e.target.value as any)}
                  className="h-9 px-3 text-xs font-bold rounded-lg border border-slate-200 bg-slate-50 text-slate-800 outline-none cursor-pointer"
                >
                  <option value="PENDING">PENDING</option>
                  <option value="IN_PROGRESS">IN PROGRESS</option>
                  <option value="RESOLVED">RESOLVED</option>
                </select>
              </div>
            </div>

            {/* Farmer Inquiry Message */}
            <div className="p-4 rounded-xl bg-slate-50 text-xs text-slate-800 border border-slate-100 leading-relaxed space-y-1">
              <span className="font-bold text-slate-400 text-[10px] uppercase tracking-wider block">
                Farmer Inquiry:
              </span>
              <p className="text-slate-800 font-medium">"{item.message}"</p>
            </div>

            {/* Admin Reply Box */}
            {item.adminReply && (
              <div className="p-4 rounded-xl bg-emerald-50/70 border border-emerald-100 text-xs space-y-1.5 ml-2 sm:ml-4">
                <div className="flex items-center gap-1.5 text-emerald-700 font-bold text-xs">
                  <CornerDownRight className="w-3.5 h-3.5" />
                  <span>Extension Officer Advisory (Sent to Farmer Mobile App)</span>
                </div>
                <p className="text-slate-800 leading-relaxed pl-5 font-medium">
                  "{item.adminReply}"
                </p>
              </div>
            )}

            {/* Footer Actions */}
            <div className="flex items-center justify-between pt-2 border-t border-slate-100 text-xs text-slate-500 font-mono">
              <div>
                {item.resolvedAt ? (
                  <span className="text-emerald-600 font-bold flex items-center gap-1.5">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    Resolved on {new Date(item.resolvedAt).toLocaleDateString()}
                  </span>
                ) : (
                  <span>Status: <strong className="text-slate-800">{item.status}</strong></span>
                )}
              </div>

              <button
                onClick={() => handleOpenReplyModal(item)}
                className="h-9 px-4 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold rounded-lg flex items-center gap-1.5 shadow-xs cursor-pointer"
              >
                <Send className="w-3.5 h-3.5" />
                <span>{item.adminReply ? 'Edit Advisory' : 'Send Advisory & Reply'}</span>
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Reply Modal */}
      {selectedTicket && (
        <Modal
          isOpen={Boolean(selectedTicket)}
          onClose={() => setSelectedTicket(null)}
          title="Send Extension Advisory & Dispatch Mobile Notification"
          maxWidth="lg"
        >
          <div className="space-y-4 text-xs">
            <div className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-1.5">
              <div className="flex items-center justify-between">
                <span className="font-bold text-slate-900 text-sm">
                  {selectedTicket.subject}
                </span>
                <Badge variant="purple">{selectedTicket.category}</Badge>
              </div>
              <p className="text-slate-500 font-mono">
                Farmer: <strong className="text-slate-800">{selectedTicket.farmerName}</strong>
              </p>
              <p className="text-slate-700 italic pt-1">
                "{selectedTicket.message}"
              </p>
            </div>

            <div className="space-y-1.5">
              <label className="block font-bold text-slate-700 uppercase tracking-wider text-[11px]">
                Official Extension Advisory:
              </label>
              <textarea
                rows={4}
                value={replyText}
                onChange={(e) => setReplyText(e.target.value)}
                placeholder="Type your official agronomic advisory or support response here..."
                className="w-full p-3 text-xs bg-slate-50 border border-slate-200 rounded-lg outline-none focus:bg-white focus:ring-2 focus:ring-emerald-500 text-slate-900 resize-none"
              />
            </div>

            <div className="flex items-center justify-between pt-2 border-t border-slate-100">
              <div className="flex items-center gap-2">
                <span className="font-semibold text-slate-500">Status:</span>
                <select
                  value={replyStatus}
                  onChange={(e) => setReplyStatus(e.target.value as any)}
                  className="h-8 px-2.5 text-xs font-semibold rounded-lg border border-slate-200 bg-slate-50 text-slate-800 outline-none cursor-pointer"
                >
                  <option value="IN_PROGRESS">IN PROGRESS</option>
                  <option value="RESOLVED">RESOLVED</option>
                  <option value="PENDING">PENDING</option>
                </select>
              </div>

              <div className="flex items-center gap-2">
                <button onClick={() => setSelectedTicket(null)} className="h-8 px-3 text-xs font-semibold rounded-lg bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 cursor-pointer">
                  Cancel
                </button>
                <button
                  onClick={handleSendReply}
                  disabled={submitting || !replyText.trim()}
                  className="h-8 px-4 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg flex items-center gap-1.5 shadow-xs cursor-pointer disabled:opacity-50"
                >
                  <Send className="w-3.5 h-3.5" />
                  <span>{submitting ? 'Dispatching...' : 'Dispatch Advisory'}</span>
                </button>
              </div>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
