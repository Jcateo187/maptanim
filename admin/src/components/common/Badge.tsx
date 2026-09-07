import React from 'react';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'purple' | 'neutral';
  size?: 'sm' | 'md';
}

export const Badge: React.FC<BadgeProps> = ({ children, variant = 'neutral', size = 'sm' }) => {
  const styles: Record<string, string> = {
    success: 'bg-[#4CAF50]/20 text-[#4CAF50] border-[#4CAF50]/40',
    warning: 'bg-[#F4A261]/20 text-[#F4A261] border-[#F4A261]/40',
    danger: 'bg-[#E76F51]/20 text-[#E76F51] border-[#E76F51]/40',
    info: 'bg-[#00BCD4]/20 text-[#00BCD4] border-[#00BCD4]/40',
    purple: 'bg-[#4C579E]/25 text-[#A5D6A7] border-[#4C579E]/40',
    neutral: 'bg-[#1D2429] text-[#C7D0D8] border-[#38434D]',
  };

  const pad = size === 'sm' ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-sm';

  return (
    <span className={`inline-flex items-center font-semibold rounded-full border ${styles[variant]} ${pad}`}>
      {children}
    </span>
  );
};
