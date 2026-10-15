import React from 'react';
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, Cell } from 'recharts';
import { MoreHorizontal } from 'lucide-react';

interface CropDistributionChartProps {
  data: { category: string; value: number; color: string }[];
}

export const CropDistributionChart: React.FC<CropDistributionChartProps> = ({ data }) => {
  return (
    <div className="bg-white p-6 sm:p-7 rounded-2xl border border-[#E5E7EB] shadow-sm space-y-4 h-full flex flex-col box-border">
      <div className="flex items-center justify-between">
        <h4 className="font-bold text-sm text-[#111813]">
          Crops Cultivated
        </h4>
        <button className="text-[#6B7280] hover:text-[#111813] transition cursor-pointer">
          <MoreHorizontal className="w-4 h-4" />
        </button>
      </div>

      <div className="flex-1 min-h-[200px] w-full">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} margin={{ top: 10, right: 10, left: -25, bottom: 0 }}>
            <XAxis dataKey="category" stroke="#6B7280" fontSize={11} tickLine={false} axisLine={false} />
            <YAxis stroke="#6B7280" fontSize={11} tickLine={false} axisLine={false} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#FFFFFF',
                border: '1px solid #E5E7EB',
                borderRadius: '8px',
                color: '#111813',
                fontSize: '12px',
                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.08)',
              }}
            />
            <Bar dataKey="value" radius={[4, 4, 0, 0]} barSize={18}>
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color || '#2E7D32'} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};
