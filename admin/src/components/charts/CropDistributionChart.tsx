import React from 'react';
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, Cell } from 'recharts';
import { MoreHorizontal } from 'lucide-react';

interface CropDistributionChartProps {
  data: { category: string; value: number; color: string }[];
}

export const CropDistributionChart: React.FC<CropDistributionChartProps> = ({ data }) => {
  return (
    <div className="bg-[#2B3136] p-6 sm:p-7 rounded-2xl border border-[#38434D] shadow-sm space-y-4 h-full flex flex-col box-border">
      <div className="flex items-center justify-between">
        <h4 className="font-bold text-sm text-[#F4F4F4]">
          Crops Cultivated
        </h4>
        <button className="text-[#8A9BA8] hover:text-[#F4F4F4] transition cursor-pointer">
          <MoreHorizontal className="w-4 h-4" />
        </button>
      </div>

      <div className="flex-1 min-h-[200px] w-full">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} margin={{ top: 10, right: 10, left: -25, bottom: 0 }}>
            <XAxis dataKey="category" stroke="#8A9BA8" fontSize={11} tickLine={false} axisLine={false} />
            <YAxis stroke="#8A9BA8" fontSize={11} tickLine={false} axisLine={false} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#183145',
                border: '1px solid #38434D',
                borderRadius: '8px',
                color: '#F4F4F4',
                fontSize: '12px',
                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.3)',
              }}
            />
            <Bar dataKey="value" radius={[4, 4, 0, 0]} barSize={18}>
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color || '#00BCD4'} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};
