import React from 'react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { MoreHorizontal } from 'lucide-react';

interface YieldTrendsChartProps {
  data: { month: string; yieldKg: number; targetKg: number }[];
}

export const YieldTrendsChart: React.FC<YieldTrendsChartProps> = ({ data }) => {
  return (
    <div className="bg-[#2B3136] p-6 sm:p-7 rounded-2xl border border-[#38434D] shadow-md space-y-4 h-full flex flex-col box-border">
      {/* Header with Title and Legend */}
      <div className="flex items-center justify-between">
        <h4 className="font-bold text-sm text-[#F4F4F4]">
          Yield vs Target
        </h4>
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 text-[11px] text-[#C7D0D8] font-medium">
            <span className="w-2 h-2 rounded-full bg-[#4CAF50]"></span>
            <span>Actual (Field Tracked)</span>
          </div>
          <div className="flex items-center gap-1.5 text-[11px] text-[#C7D0D8] font-medium">
            <span className="w-2 h-2 rounded-full bg-[#F4A261]"></span>
            <span>Target Benchmark</span>
          </div>
          <button className="text-[#8A9BA8] hover:text-[#F4F4F4]">
            <MoreHorizontal className="w-4 h-4" />
          </button>
        </div>
      </div>

      <div className="flex-1 min-h-[200px] w-full">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <defs>
              <linearGradient id="colorYield" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#4CAF50" stopOpacity={0.25}/>
                <stop offset="95%" stopColor="#4CAF50" stopOpacity={0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#38434D" vertical={false} />
            <XAxis dataKey="month" stroke="#8A9BA8" fontSize={11} tickLine={false} axisLine={false} />
            <YAxis stroke="#8A9BA8" fontSize={11} tickLine={false} axisLine={false} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#183145',
                border: '1px solid #38434D',
                borderRadius: '12px',
                color: '#F4F4F4',
                fontSize: '12px',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.4)',
              }}
            />
            <Area type="monotone" dataKey="yieldKg" name="Actual (kg)" stroke="#4CAF50" strokeWidth={2.5} fillOpacity={1} fill="url(#colorYield)" />
            <Area type="monotone" dataKey="targetKg" name="Target (kg)" stroke="#F4A261" strokeWidth={2} strokeDasharray="3 3" fillOpacity={0} />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};
