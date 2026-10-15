import React from 'react';
import { LucideIcon } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  trend?: { value: string; positive: boolean };
  icon: LucideIcon;
  iconBgColor?: string;
}

export const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  subtitle,
  icon: Icon,
  iconBgColor = 'bg-[#2E7D32]',
}) => {
  return (
    <div className="bg-white p-6 sm:p-7 rounded-2xl border border-[#E5E7EB] shadow-xs flex items-center gap-5 transition-all duration-200 hover:-translate-y-0.5 hover:border-[#2E7D32] hover:shadow-md box-border">
      {/* Vibrant Rounded Icon Block */}
      <div className={`w-14 h-14 rounded-2xl flex items-center justify-center text-white shrink-0 shadow-xs ${iconBgColor}`}>
        <Icon className="w-7 h-7" />
      </div>

      {/* Metric Content */}
      <div className="min-w-0 flex-1">
        <h3 className="text-2xl sm:text-3xl font-extrabold text-[#111813] tracking-tight leading-tight">
          {value}
        </h3>
        <p className="text-xs text-[#374151] font-bold mt-1 truncate">
          {title}
        </p>
        {subtitle && (
          <p className="text-[11px] text-[#6B7280] font-medium truncate mt-0.5">
            {subtitle}
          </p>
        )}
      </div>
    </div>
  );
};
