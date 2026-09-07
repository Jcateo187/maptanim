import React, { useState } from 'react';
import {
  Smartphone, Sparkles, Sprout, Droplets, Calendar, ShieldCheck,
  Award, Bug, Wifi, Battery, Signal, Eye, Check
} from 'lucide-react';
import { CategoryType, SeasonType, SoilType } from '../../types';

interface MobileCropBreakdownPreviewProps {
  name: string;
  localName: string;
  botanicalName: string;
  taxonomicFamily: string;
  category: CategoryType;
  idealSoil: SoilType;
  suitableSoils: SoilType[];
  season: SeasonType;
  daysToHarvest: number;
  wateringIntervalDays: number;
  optimalPhMin: number;
  optimalPhMax: number;
  nVal: number;
  pVal: number;
  kVal: number;
  stageSprout: number;
  stageSeedling: number;
  stageVegetative: number;
  stageFlowering: number;
  stageHarvest: number;
  harvestIndicators: string;
  description: string;
  imageUrl: string;
  companionGoodStr: string;
  companionBadStr: string;
}

export const MobileCropBreakdownPreview: React.FC<MobileCropBreakdownPreviewProps> = ({
  name,
  localName,
  botanicalName,
  taxonomicFamily,
  category,
  idealSoil,
  suitableSoils,
  season,
  daysToHarvest,
  wateringIntervalDays,
  optimalPhMin,
  optimalPhMax,
  nVal,
  pVal,
  kVal,
  stageSprout,
  stageSeedling,
  stageVegetative,
  stageFlowering,
  stageHarvest,
  harvestIndicators,
  description,
  imageUrl,
  companionGoodStr,
  companionBadStr,
}) => {
  const [previewMode, setPreviewMode] = useState<'BREAKDOWN' | 'TRAY'>('BREAKDOWN');
  const [activeWhyTopic, setActiveWhyTopic] = useState<string | null>(null);

  const goodCompanions = companionGoodStr
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean);

  const badCompanions = companionBadStr
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean);

  const imageSrc =
    imageUrl.trim() || '/metadata/crops_images/tomato.png';

  return (
    <div className="space-y-3">
      {/* Header with Mode Switcher */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-1.5 text-xs font-bold text-emerald-400">
          <Smartphone className="w-4 h-4 text-emerald-500" />
          <span>Live Mobile UI Preview</span>
        </div>
        <div className="flex items-center bg-slate-800 rounded-lg p-0.5 border border-slate-700 text-[10px] font-bold">
          <button
            type="button"
            onClick={() => setPreviewMode('BREAKDOWN')}
            className={`px-2 py-0.5 rounded transition ${
              previewMode === 'BREAKDOWN'
                ? 'bg-emerald-600 text-white shadow'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Breakdown Dialog
          </button>
          <button
            type="button"
            onClick={() => setPreviewMode('TRAY')}
            className={`px-2 py-0.5 rounded transition ${
              previewMode === 'TRAY'
                ? 'bg-emerald-600 text-white shadow'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Crop Tray Card
          </button>
        </div>
      </div>

      {/* Simulated Phone Chassis */}
      <div className="mx-auto w-full max-w-[340px] rounded-[38px] p-2 bg-gradient-to-b from-slate-700 via-slate-800 to-slate-900 shadow-2xl border-4 border-slate-700/80 ring-1 ring-white/10">
        {/* Phone Screen Frame */}
        <div className="relative w-full rounded-[30px] overflow-hidden bg-[#162A1E] text-white flex flex-col h-[560px] border border-emerald-500/40 select-none shadow-inner">
          {/* Top Status Bar & Dynamic Island */}
          <div className="h-6 bg-black/70 px-4 flex items-center justify-between text-[10px] font-semibold text-white/90 z-20 shrink-0">
            <span>9:41</span>
            {/* Dynamic Island Pill */}
            <div className="w-16 h-3 bg-black rounded-full mx-auto" />
            <div className="flex items-center gap-1">
              <Signal className="w-2.5 h-2.5" />
              <Wifi className="w-2.5 h-2.5" />
              <Battery className="w-3 h-3" />
            </div>
          </div>

          {/* Screen Content based on Mode */}
          {previewMode === 'TRAY' ? (
            /* Mode A: CropTray Selection Card */
            <div className="flex-1 p-3 flex flex-col justify-center items-center space-y-4 bg-slate-950/80">
              <span className="text-[11px] text-emerald-400 font-bold uppercase tracking-wider">
                Mobile Tray Card Appearance
              </span>

              <div className="w-full p-3 rounded-2xl bg-[#F8F9FA] text-slate-900 border border-slate-300 shadow-lg flex items-center gap-3">
                <div className="w-12 h-12 rounded-xl bg-[#E8F5E9] overflow-hidden flex items-center justify-center shrink-0 border border-emerald-500/40">
                  <img
                    src={imageSrc}
                    alt="Crop"
                    className="w-10 h-10 object-contain rounded-md"
                    onError={(e) => {
                      (e.target as HTMLImageElement).src = '/metadata/crops_images/tomato.png';
                    }}
                  />
                </div>
                <div className="min-w-0 flex-1">
                  <div className="font-extrabold text-sm text-slate-900 truncate">
                    {name || 'Crop Name'} {localName ? `(${localName})` : ''}
                  </div>
                  <div className="text-[10px] text-slate-500 truncate mt-0.5 font-medium">
                    {category} • {daysToHarvest} days
                  </div>
                  <div className="text-[9px] text-emerald-600 font-bold mt-0.5">
                    Tap to view breakdown & select
                  </div>
                </div>
              </div>

              <div className="w-full p-3 rounded-xl bg-[#14261B] border border-emerald-500/30 text-[10px] space-y-1.5 text-white/90">
                <div className="flex justify-between">
                  <span className="text-white/60">Seasonality:</span>
                  <span className="font-bold text-emerald-300">{season}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-white/60">Water Frequency:</span>
                  <span className="font-bold text-blue-300">Every {wateringIntervalDays}d</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-white/60">Optimal Soil:</span>
                  <span className="font-bold text-amber-300">{idealSoil}</span>
                </div>
              </div>
            </div>
          ) : (
            /* Mode B: Full Crop Breakdown Screen (Matching CropDetailDialog.kt) */
            <div className="flex-1 overflow-y-auto custom-scrollbar text-[11px]">
              {/* Hero Banner */}
              <div className="relative h-36 w-full bg-[#101E15] overflow-hidden">
                <img
                  src={imageSrc}
                  alt="Crop"
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    (e.target as HTMLImageElement).src = '/metadata/crops_images/tomato.png';
                  }}
                />
                <div
                  className="absolute inset-0"
                  style={{
                    background:
                      'linear-gradient(to bottom, rgba(0,0,0,0.3) 0%, rgba(22,42,30,0.2) 50%, #162A1E 100%)',
                  }}
                />
                {/* Family Badge */}
                <div className="absolute bottom-2 left-3 right-3 flex items-center justify-between">
                  <span className="px-2 py-0.5 rounded bg-[#2E7D32]/90 border border-emerald-400/40 text-white text-[9px] font-bold">
                    Family: {taxonomicFamily || 'Solanaceae'}
                  </span>
                  <span className="px-1.5 py-0.5 rounded bg-emerald-950/80 border border-emerald-500/30 text-emerald-300 text-[8px] font-bold">
                    {category}
                  </span>
                </div>
              </div>

              {/* Content Body */}
              <div className="p-3 space-y-3">
                {/* Title & Names */}
                <div>
                  <div className="flex items-baseline gap-1.5">
                    <h3 className="text-base font-extrabold text-white leading-tight">
                      {name || 'Crop Name'}
                    </h3>
                    {localName && (
                      <span className="text-xs text-[#A5D6A7] font-semibold">
                        ({localName})
                      </span>
                    )}
                  </div>
                  {botanicalName && (
                    <p className="text-[10px] text-white/70 italic font-mono mt-0.5">
                      {botanicalName}
                    </p>
                  )}
                </div>

                {/* Interactive Why? Pills */}
                <div className="space-y-1">
                  <span className="text-[9px] font-bold text-[#FFD54F] block">
                    💡 Interactive Parameters (Live Science):
                  </span>
                  <div className="grid grid-cols-2 gap-1 text-[9px]">
                    <div className="p-1.5 rounded-lg bg-[#1E5E38] border border-emerald-400/30 flex items-center justify-between">
                      <span className="truncate">Type: {category}</span>
                      <span className="text-[8px] text-emerald-300 font-bold">Why? 💡</span>
                    </div>
                    <div className="p-1.5 rounded-lg bg-[#8D6E63] border border-amber-400/30 flex items-center justify-between">
                      <span>Harvest: {daysToHarvest}d</span>
                      <span className="text-[8px] text-amber-200 font-bold">Why? 💡</span>
                    </div>
                    <div className="p-1.5 rounded-lg bg-[#0277BD] border border-blue-400/30 flex items-center justify-between">
                      <span>Water: Every {wateringIntervalDays}d</span>
                      <span className="text-[8px] text-blue-200 font-bold">Why? 💡</span>
                    </div>
                    <div className="p-1.5 rounded-lg bg-[#6A1B9A] border border-purple-400/30 flex items-center justify-between">
                      <span>pH: {optimalPhMin}–{optimalPhMax}</span>
                      <span className="text-[8px] text-purple-200 font-bold">Why? 💡</span>
                    </div>
                  </div>
                </div>

                {/* Description Profile */}
                {description && (
                  <div className="p-2 rounded-xl bg-[#122318] border border-emerald-500/20 text-[10px] space-y-1">
                    <span className="font-bold text-emerald-400 block">📝 Crop Profile</span>
                    <p className="text-white/80 line-clamp-3 leading-relaxed">
                      {description}
                    </p>
                  </div>
                )}

                {/* 5-STAGE GROWTH SCHEDULE TIMELINE */}
                <div className="p-2.5 rounded-xl bg-[#122318] border border-emerald-500/30 space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] font-bold text-[#FFD54F]">
                      📊 5-Stage Growth Schedule
                    </span>
                    <span className="text-[9px] px-1.5 py-0.2 rounded bg-emerald-700/60 font-bold text-white">
                      {daysToHarvest}d Total
                    </span>
                  </div>

                  <div className="grid grid-cols-5 gap-1 text-center">
                    <div className="p-1 rounded bg-[#1B3524] border border-emerald-500/30">
                      <span className="text-[7px] text-white/60 uppercase block">Sprout</span>
                      <span className="text-[10px] font-bold text-[#81C784]">{stageSprout}d</span>
                    </div>
                    <div className="p-1 rounded bg-[#1B3524] border border-emerald-500/30">
                      <span className="text-[7px] text-white/60 uppercase block">Seedling</span>
                      <span className="text-[10px] font-bold text-[#81C784]">{stageSeedling}d</span>
                    </div>
                    <div className="p-1 rounded bg-[#1B3524] border border-emerald-500/30">
                      <span className="text-[7px] text-white/60 uppercase block">Veg</span>
                      <span className="text-[10px] font-bold text-[#81C784]">{stageVegetative}d</span>
                    </div>
                    <div className="p-1 rounded bg-[#1B3524] border border-amber-500/40">
                      <span className="text-[7px] text-white/60 uppercase block">Bloom</span>
                      <span className="text-[10px] font-bold text-[#FFD54F]">{stageFlowering}d</span>
                    </div>
                    <div className="p-1 rounded bg-[#2E7D32]/50 border border-emerald-400">
                      <span className="text-[7px] text-emerald-300 uppercase block font-bold">Harvest</span>
                      <span className="text-[10px] font-extrabold text-white">{stageHarvest}d+</span>
                    </div>
                  </div>
                </div>

                {/* Soil, pH & NPK Specs */}
                <div className="p-2.5 rounded-xl bg-[#122318] border border-emerald-500/20 space-y-1.5 text-[10px]">
                  <span className="font-bold text-emerald-400 block">🌱 Soil & Nutritional Specs</span>
                  <div className="grid grid-cols-2 gap-1.5">
                    <div className="p-1.5 rounded bg-[#16291C] border border-emerald-500/20">
                      <span className="text-[8px] text-white/60 block">Optimal pH Range</span>
                      <strong className="text-amber-300">{optimalPhMin} – {optimalPhMax}</strong>
                    </div>
                    <div className="p-1.5 rounded bg-[#16291C] border border-emerald-500/20">
                      <span className="text-[8px] text-white/60 block">NPK Target</span>
                      <strong className="text-emerald-400">N:{nVal} • P:{pVal} • K:{kVal}</strong>
                    </div>
                  </div>
                  <div className="pt-0.5 text-[9px] text-white/80">
                    <span className="text-[#81C784] font-bold">Ideal Soil:</span> {idealSoil}
                    {suitableSoils.length > 0 && (
                      <span className="text-amber-300 ml-1.5">
                        (Suitable: {suitableSoils.join(', ')})
                      </span>
                    )}
                  </div>
                </div>

                {/* Companions */}
                <div className="grid grid-cols-2 gap-1.5 text-[9px]">
                  <div className="p-2 rounded-xl bg-[#122318] border border-emerald-500/20 space-y-0.5">
                    <span className="font-bold text-emerald-400 block">🤝 Companions</span>
                    <span className="text-[#A5D6A7] block truncate">
                      {goodCompanions.length > 0 ? goodCompanions.join(', ') : 'None specified'}
                    </span>
                  </div>
                  <div className="p-2 rounded-xl bg-[#122318] border border-rose-500/20 space-y-0.5">
                    <span className="font-bold text-rose-400 block">⚠️ Incompatible</span>
                    <span className="text-rose-300 block truncate">
                      {badCompanions.length > 0 ? badCompanions.join(', ') : 'None'}
                    </span>
                  </div>
                </div>

                {/* Harvest Readiness */}
                {harvestIndicators && (
                  <div className="p-2 rounded-xl bg-[#122318] border border-emerald-500/20 text-[9px] space-y-0.5">
                    <span className="font-bold text-[#FFD54F] block">🌾 Harvest Indicator</span>
                    <p className="text-white/80 line-clamp-2">{harvestIndicators}</p>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Bottom Virtual Home Bar */}
          <div className="h-4 bg-black/60 flex items-center justify-center shrink-0">
            <div className="w-24 h-1 bg-white/40 rounded-full" />
          </div>
        </div>
      </div>
    </div>
  );
};
