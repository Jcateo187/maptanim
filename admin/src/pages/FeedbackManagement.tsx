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
      <div className="bg-[#2B3136] p-6 rounded-2xl border border-[#38434D] shadow-sm flex flex-col sm:flex-row gap-5 items-start sm:items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-[#4CAF50]/15 text-[#4CAF50] flex items-center justify-center font-bold shrink-0 border border-[#4CAF50]/30">
            <MessageSquare className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-3">
              <h3 className="font-extrabold text-base text-[#F4F4F4]">
                Farmer Support & Feedback Queue
              </h3>
              <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-[#183145] text-[#00BCD4] border border-[#38434D]">
                {feedbackList.length} Tickets
              </span>
            </div>
            <p className="text-xs text-[#C7D0D8] mt-0.5">
              Live smallholder support inquiries and push advisories
            </p>
          </div>
        </div>

        <button onClick={loadFeedback} className="h-10 px-4 rounded-xl bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] text-xs font-semibold hover:bg-[#38434D] transition flex items-center gap-2 shadow-xs cursor-pointer">
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-[#4CAF50]' : 'text-[#8A9BA8]'}`} />
          <span>Refresh Queue</span>
        </button>
      </div>

      {/* Feedback Items Section */}
      <div className="space-y-4">
        {feedbackList.map((item) => (
          <div key={item.id} className="bg-[#2B3136] p-6 rounded-2xl border border-[#38434D] shadow-sm space-y-4">
            {/* Header: Farmer Info & Status */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-[#38434D]">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-[#4CAF50]/15 text-[#4CAF50] font-extrabold flex items-center justify-center text-xs border border-[#4CAF50]/30 shrink-0">
                  {item.farmerName.charAt(0)}
                </div>
                <div>
                  <h4 className="font-bold text-sm text-[#F4F4F4]">
                    {item.subject}
                  </h4>
                  <p className="text-xs text-[#8A9BA8] mt-0.5 flex items-center gap-2 font-mono">
                    <span>By <strong className="text-[#F4F4F4]">{item.farmerName}</strong></span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <Clock className="w-3 h-3 text-[#8A9BA8]" />
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
                  className="h-9 px-3 text-xs font-bold rounded-lg border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] outline-none cursor-pointer"
                >
                  <option value="PENDING" className="bg-[#2B3136]">PENDING</option>
                  <option value="IN_PROGRESS" className="bg-[#2B3136]">IN PROGRESS</option>
                  <option value="RESOLVED" className="bg-[#2B3136]">RESOLVED</option>
                </select>
              </div>
            </div>

            {/* Farmer Inquiry Message */}
            <div className="p-4 rounded-xl bg-[#1D2429] text-xs text-[#F4F4F4] border border-[#38434D] leading-relaxed space-y-1">
              <span className="font-bold text-[#8A9BA8] text-[10px] uppercase tracking-wider block">
                Farmer Inquiry:
              </span>
              <p className="text-[#F4F4F4] font-medium">"{item.message}"</p>
            </div>

            {/* Admin Reply Box */}
            {item.adminReply && (
              <div className="p-4 rounded-xl bg-[#183145] border border-[#38434D] text-xs space-y-1.5 ml-2 sm:ml-4">
                <div className="flex items-center gap-1.5 text-[#4CAF50] font-bold text-xs">
                  <CornerDownRight className="w-3.5 h-3.5" />
                  <span>Admin Response (Sent to Farmer Mobile App)</span>
                </div>
                <p className="text-[#C7D0D8] leading-relaxed pl-5 font-medium">
                  "{item.adminReply}"
                </p>
              </div>
            )}

            {/* Footer Actions */}
            <div className="flex items-center justify-between pt-2 border-t border-[#38434D] text-xs text-[#8A9BA8] font-mono">
              <div>
                {item.resolvedAt ? (
                  <span className="text-[#4CAF50] font-bold flex items-center gap-1.5">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    Resolved on {new Date(item.resolvedAt).toLocaleDateString()}
                  </span>
                ) : (
                  <span>Status: <strong className="text-[#F4F4F4]">{item.status}</strong></span>
                )}
              </div>

              <button
                onClick={() => handleOpenReplyModal(item)}
                className="h-9 px-4 bg-[#4CAF50] hover:bg-[#388E3C] text-white text-xs font-semibold rounded-lg flex items-center gap-1.5 shadow-xs cursor-pointer transition"
              >
                <Send className="w-3.5 h-3.5" />
                <span>{item.adminReply ? 'Edit Response' : 'Reply to Farmer'}</span>
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
          title="Send Admin Response & Dispatch Mobile Notification"
          maxWidth="lg"
        >
          <div className="space-y-4 text-xs">
            <div className="p-4 rounded-xl bg-[#1D2429] border border-[#38434D] space-y-1.5">
              <div className="flex items-center justify-between">
                <span className="font-bold text-[#F4F4F4] text-sm">
                  {selectedTicket.subject}
                </span>
                <Badge variant="purple">{selectedTicket.category}</Badge>
              </div>
              <p className="text-[#8A9BA8] font-mono">
                Farmer: <strong className="text-[#F4F4F4]">{selectedTicket.farmerName}</strong>
              </p>
              <p className="text-[#C7D0D8] italic pt-1">
                "{selectedTicket.message}"
              </p>
            </div>

            <div className="space-y-1.5">
              <label className="block font-bold text-[#F4F4F4] uppercase tracking-wider text-[11px]">
                Admin Response:
              </label>
              <textarea
                rows={4}
                value={replyText}
                onChange={(e) => setReplyText(e.target.value)}
                placeholder="Type your admin response or advice here..."
                className="w-full p-3 text-xs bg-[#1D2429] border border-[#38434D] rounded-lg outline-none focus:border-[#4CAF50] text-[#F4F4F4] resize-none"
              />
            </div>

            <div className="flex items-center justify-between pt-2 border-t border-[#38434D]">
              <div className="flex items-center gap-2">
                <span className="font-semibold text-[#8A9BA8]">Status:</span>
                <select
                  value={replyStatus}
                  onChange={(e) => setReplyStatus(e.target.value as any)}
                  className="h-8 px-2.5 text-xs font-semibold rounded-lg border border-[#38434D] bg-[#1D2429] text-[#F4F4F4] outline-none cursor-pointer"
                >
                  <option value="IN_PROGRESS" className="bg-[#2B3136]">IN PROGRESS</option>
                  <option value="RESOLVED" className="bg-[#2B3136]">RESOLVED</option>
                  <option value="PENDING" className="bg-[#2B3136]">PENDING</option>
                </select>
              </div>

              <div className="flex items-center gap-2">
                <button onClick={() => setSelectedTicket(null)} className="h-8 px-3 text-xs font-semibold rounded-lg bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] hover:bg-[#38434D] cursor-pointer">
                  Cancel
                </button>
                <button
                  onClick={handleSendReply}
                  disabled={submitting || !replyText.trim()}
                  className="h-8 px-4 bg-[#4CAF50] hover:bg-[#388E3C] text-white text-xs font-bold rounded-lg flex items-center gap-1.5 shadow-xs cursor-pointer disabled:opacity-50 transition"
                >
                  <Send className="w-3.5 h-3.5" />
                  <span>{submitting ? 'Sending...' : 'Send Response'}</span>
                </button>
              </div>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
