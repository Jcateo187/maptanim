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
      <div className="glass-card p-4 sm:p-5 flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between border-l-4 border-l-emerald-500">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 flex items-center justify-center font-bold">
            <FileText className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="font-extrabold text-sm text-slate-900 dark:text-white">
                Administrative System Audit Trail
              </h3>
              <Badge variant="purple">{logs.length} Immutable Logs</Badge>
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
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
          <div key={log.id} className="glass-card p-4 space-y-2 text-xs">
            <div className="flex items-center justify-between">
              <span className="font-mono font-bold text-emerald-600 dark:text-emerald-400">
                {log.action}
              </span>
              <Badge variant={log.status === 'SUCCESS' ? 'success' : 'danger'}>
                {log.status}
              </Badge>
            </div>

            <p className="text-slate-800 dark:text-slate-200 font-semibold">{log.details}</p>

            <div className="flex items-center justify-between text-[11px] text-slate-500 font-mono pt-1">
              <span>{log.adminEmail}</span>
              <span>{new Date(log.timestamp).toLocaleTimeString()}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Desktop Audit Logs Table (>= sm) */}
      <div className="hidden sm:block table-container glass-card">
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
                <td className="font-mono text-xs text-slate-500">
                  {new Date(log.timestamp).toLocaleString()}
                </td>
                <td className="font-bold text-xs text-slate-800 dark:text-slate-200">
                  {log.adminEmail}
                </td>
                <td className="font-mono text-xs text-emerald-600 dark:text-emerald-400 font-bold">
                  {log.action}
                </td>
                <td className="text-xs font-semibold">{log.targetModule}</td>
                <td className="text-xs text-slate-600 dark:text-slate-300 max-w-sm">
                  {log.details}
                </td>
                <td className="font-mono text-xs text-slate-400">{log.ipAddress}</td>
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
