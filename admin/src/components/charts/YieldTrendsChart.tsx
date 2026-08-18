import React from 'react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { MoreHorizontal } from 'lucide-react';

interface YieldTrendsChartProps {
  data: { month: string; yieldKg: number; targetKg: number }[];
}

export const YieldTrendsChart: React.FC<YieldTrendsChartProps> = ({ data }) => {
  return (
    <div className="bg-white p-6 sm:p-7 rounded-2xl border border-slate-200/90 shadow-sm space-y-4 h-full flex flex-col box-border">
      {/* Header with Title and Legend */}
      <div className="flex items-center justify-between">
        <h4 className="font-bold text-sm text-slate-800">
          Yield vs Target
        </h4>
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 text-[11px] text-slate-500 font-medium">
            <span className="w-2 h-2 rounded-full bg-emerald-600"></span>
            <span>Actual</span>
          </div>
          <div className="flex items-center gap-1.5 text-[11px] text-slate-500 font-medium">
            <span className="w-2 h-2 rounded-full bg-blue-500"></span>
            <span>Target</span>
          </div>
          <button className="text-slate-400 hover:text-slate-600">
            <MoreHorizontal className="w-4 h-4" />
          </button>
        </div>
      </div>

      <div className="flex-1 min-h-[200px] w-full">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <defs>
              <linearGradient id="colorYield" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#059669" stopOpacity={0.2}/>
                <stop offset="95%" stopColor="#059669" stopOpacity={0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" vertical={false} />
            <XAxis dataKey="month" stroke="#64748B" fontSize={11} tickLine={false} axisLine={false} />
            <YAxis stroke="#64748B" fontSize={11} tickLine={false} axisLine={false} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#1E293B',
                border: 'none',
                borderRadius: '8px',
                color: '#FFF',
                fontSize: '12px',
                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
              }}
            />
            <Area type="monotone" dataKey="yieldKg" name="Actual (kg)" stroke="#059669" strokeWidth={2.5} fillOpacity={1} fill="url(#colorYield)" />
            <Area type="monotone" dataKey="targetKg" name="Target (kg)" stroke="#3B82F6" strokeWidth={2} strokeDasharray="3 3" fillOpacity={0} />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};
