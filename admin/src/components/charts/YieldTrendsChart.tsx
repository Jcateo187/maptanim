import React from 'react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { MoreHorizontal } from 'lucide-react';

interface YieldTrendsChartProps {
  data: { month: string; yieldKg: number; targetKg: number }[];
}

export const YieldTrendsChart: React.FC<YieldTrendsChartProps> = ({ data }) => {
  return (
    <div className="bg-white p-6 sm:p-7 rounded-2xl border border-[#E5E7EB] shadow-sm space-y-4 h-full flex flex-col box-border">
      {/* Header with Title and Legend */}
      <div className="flex items-center justify-between">
        <h4 className="font-bold text-sm text-[#111813]">
          Yield vs Target
        </h4>
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 text-[11px] text-[#374151] font-medium">
            <span className="w-2 h-2 rounded-full bg-[#2E7D32]"></span>
            <span>Actual (Field Tracked)</span>
          </div>
          <div className="flex items-center gap-1.5 text-[11px] text-[#374151] font-medium">
            <span className="w-2 h-2 rounded-full bg-[#F59E0B]"></span>
            <span>Target Benchmark</span>
          </div>
          <button className="text-[#6B7280] hover:text-[#111813] transition">
            <MoreHorizontal className="w-4 h-4" />
          </button>
        </div>
      </div>

      <div className="flex-1 min-h-[200px] w-full">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <defs>
              <linearGradient id="colorYield" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#2E7D32" stopOpacity={0.25}/>
                <stop offset="95%" stopColor="#2E7D32" stopOpacity={0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" vertical={false} />
            <XAxis dataKey="month" stroke="#6B7280" fontSize={11} tickLine={false} axisLine={false} />
            <YAxis stroke="#6B7280" fontSize={11} tickLine={false} axisLine={false} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#FFFFFF',
                border: '1px solid #E5E7EB',
                borderRadius: '12px',
                color: '#111813',
                fontSize: '12px',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
              }}
            />
            <Area type="monotone" dataKey="yieldKg" name="Actual (kg)" stroke="#2E7D32" strokeWidth={2.5} fillOpacity={1} fill="url(#colorYield)" />
            <Area type="monotone" dataKey="targetKg" name="Target (kg)" stroke="#F59E0B" strokeWidth={2} strokeDasharray="3 3" fillOpacity={0} />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};
