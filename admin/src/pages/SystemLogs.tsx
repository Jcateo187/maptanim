import React, { useEffect, useState } from 'react';
import { FileText, RefreshCw } from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { SystemAuditLog } from '../types';
import { apiService } from '../services/api';

export const SystemLogs: React.FC = () => {
  const [logs, setLogs] = useState<SystemAuditLog[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const loadLogs = async () => {
    setLoading(true);
    const data = await apiService.getAuditLogs();
    setLogs(data);
    setLoading(false);
  };

  useEffect(() => {
    loadLogs();
  }, []);

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Action Header Card */}
      <div className="bg-[#2B3136] border border-[#38434D] rounded-xl p-4 sm:p-5 flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between border-l-4 border-l-[#4CAF50] shadow-md">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-[#4CAF50]/15 text-[#4CAF50] flex items-center justify-center font-bold">
            <FileText className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="font-extrabold text-sm text-[#F4F4F4]">
                Administrative System Audit Trail
              </h3>
              <Badge variant="purple">{logs.length} Immutable Logs</Badge>
            </div>
            <p className="text-xs text-[#8A9BA8] mt-0.5">
              State modifications, rule updates, and security authorization event records
            </p>
          </div>
        </div>

        <button onClick={loadLogs} className="btn btn-secondary text-xs h-9 px-3">
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          <span>Refresh Audit Trail</span>
        </button>
      </div>

      {/* Mobile Audit Logs Cards View (< sm) */}
      <div className="block sm:hidden space-y-3">
        {logs.map((log) => (
          <div key={log.id} className="bg-[#2B3136] border border-[#38434D] rounded-xl p-4 space-y-2 text-xs shadow-md">
            <div className="flex items-center justify-between">
              <span className="font-mono font-bold text-[#4CAF50]">
                {log.action}
              </span>
              <Badge variant={log.status === 'SUCCESS' ? 'success' : 'danger'}>
                {log.status}
              </Badge>
            </div>

            <p className="text-[#F4F4F4] font-semibold">{log.details}</p>

            <div className="flex items-center justify-between text-[11px] text-[#8A9BA8] font-mono pt-1">
              <span>{log.adminEmail}</span>
              <span>{new Date(log.timestamp).toLocaleTimeString()}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Desktop Audit Logs Table (>= sm) */}
      <div className="hidden sm:block table-container bg-[#2B3136] border border-[#38434D] rounded-xl overflow-hidden shadow-md">
        <table>
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>Admin Actor</th>
              <th>Action Type</th>
              <th>Target Module</th>
              <th>Action Details</th>
              <th>IP Address</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log) => (
              <tr key={log.id}>
                <td className="font-mono text-xs text-[#8A9BA8]">
                  {new Date(log.timestamp).toLocaleString()}
                </td>
                <td className="font-bold text-xs text-[#F4F4F4]">
                  {log.adminEmail}
                </td>
                <td className="font-mono text-xs text-[#4CAF50] font-bold">
                  {log.action}
                </td>
                <td className="text-xs font-semibold text-[#C7D0D8]">{log.targetModule}</td>
                <td className="text-xs text-[#C7D0D8] max-w-sm">
                  {log.details}
                </td>
                <td className="font-mono text-xs text-[#8A9BA8]">{log.ipAddress}</td>
                <td>
                  <Badge variant={log.status === 'SUCCESS' ? 'success' : 'danger'}>
                    {log.status}
                  </Badge>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
