import React, { useState } from 'react';
import {
  Smartphone, Sprout, Droplets, Calendar, ShieldCheck,
  Award, Bug, Wifi, Battery, Signal, Eye, Check,
  Layers, ChevronRight, Info, AlertTriangle, HelpCircle
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
  needsTrellis?: boolean;
  trellisType?: string;
  preferredPlantingMethod?: string;
  weedingIntervalDays?: number;
}

type HakbangTab = 'LUPA' | 'TANIM' | 'BALAG' | 'ALAGA' | 'PESTE' | 'ANI';

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
  needsTrellis = false,
  trellisType = 'A_FRAME',
  preferredPlantingMethod = 'RAISED_BED',
  weedingIntervalDays = 7,
}) => {
  const [previewMode, setPreviewMode] = useState<'BREAKDOWN' | 'TRAY'>('BREAKDOWN');
  const [activeTab, setActiveTab] = useState<HakbangTab>('LUPA');
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
            Hakbang Dialog
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
            Tray Card
          </button>
        </div>
      </div>

      {/* Simulated Phone Chassis */}
      <div className="mx-auto w-full max-w-[360px] rounded-[38px] p-2 bg-gradient-to-b from-slate-700 via-slate-800 to-slate-900 shadow-2xl border-4 border-slate-700/80 ring-1 ring-white/10">
        {/* Phone Screen Frame */}
        <div className="relative w-full rounded-[30px] overflow-hidden bg-[#132319] text-white flex flex-col h-[580px] border border-emerald-500/40 select-none shadow-inner">
          {/* Top Status Bar */}
          <div className="h-6 bg-black/80 px-4 flex items-center justify-between text-[10px] font-semibold text-white/90 z-20 shrink-0">
            <span>9:41</span>
            <div className="w-14 h-3 bg-black rounded-full mx-auto" />
            <div className="flex items-center gap-1">
              <Signal className="w-2.5 h-2.5" />
              <Wifi className="w-2.5 h-2.5" />
              <Battery className="w-3 h-3" />
            </div>
          </div>

          {/* Mode A: CropTray Selection Card */}
          {previewMode === 'TRAY' ? (
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
                    Tap to view 6 Hakbang & planting guides
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
                  <span className="text-white/60">Balag / Trellis:</span>
                  <span className="font-bold text-amber-300">{needsTrellis ? `Kailangan (${trellisType})` : 'Hindi Kailangan'}</span>
                </div>
              </div>
            </div>
          ) : (
            /* Mode B: Compact Landscape-Optimized 6 Hakbang Breakdown Dialog */
            <div className="flex-1 flex flex-col overflow-hidden text-[11px]">
              {/* Compact Header (Matching mobile 80dp thumbnail design) */}
              <div className="p-2.5 bg-[#172D20] border-b border-emerald-500/30 flex items-center gap-2.5 shrink-0">
                <div className="w-12 h-12 rounded-xl bg-[#101E15] border border-emerald-500/40 overflow-hidden flex items-center justify-center shrink-0 shadow">
                  <img
                    src={imageSrc}
                    alt="Crop"
                    className="w-11 h-11 object-contain"
                    onError={(e) => {
                      (e.target as HTMLImageElement).src = '/metadata/crops_images/tomato.png';
                    }}
                  />
                </div>
                <div className="min-w-0 flex-1">
                  <div className="flex items-center justify-between">
                    <h3 className="text-xs font-bold text-white truncate leading-tight">
                      {name || 'Crop Name'} {localName && <span className="text-[#A5D6A7]">({localName})</span>}
                    </h3>
                    <span className="text-[8px] px-1.5 py-0.5 rounded bg-emerald-600/60 font-bold text-white shrink-0">
                      {daysToHarvest}d Ani
                    </span>
                  </div>
                  <div className="flex items-center gap-1 mt-0.5">
                    <span className="text-[9px] text-white/60 italic font-mono truncate">
                      {botanicalName || 'Botanical sp.'}
                    </span>
                    <span className="text-[8px] px-1 py-0.2 rounded bg-white/10 text-emerald-300 font-semibold">
                      {taxonomicFamily || 'Family'}
                    </span>
                  </div>
                </div>
              </div>

              {/* 6 Hakbang Educational Tab Bar */}
              <div className="flex overflow-x-auto bg-[#102016] border-b border-white/10 px-1 py-1 gap-1 shrink-0 text-[10px] custom-scrollbar">
                {[
                  { id: 'LUPA' as HakbangTab, icon: '🌍', label: 'Lupa' },
                  { id: 'TANIM' as HakbangTab, icon: '🌱', label: 'Tanim' },
                  { id: 'BALAG' as HakbangTab, icon: '🪵', label: 'Balag' },
                  { id: 'ALAGA' as HakbangTab, icon: '💧', label: 'Alaga' },
                  { id: 'PESTE' as HakbangTab, icon: '🐛', label: 'Peste' },
                  { id: 'ANI' as HakbangTab, icon: '🌾', label: 'Ani' },
                ].map((tab) => {
                  const isActive = activeTab === tab.id;
                  return (
                    <button
                      key={tab.id}
                      type="button"
                      onClick={() => setActiveTab(tab.id)}
                      className={`flex items-center gap-1 px-2 py-1 rounded-lg font-bold transition whitespace-nowrap ${
                        isActive
                          ? 'bg-emerald-600 text-white shadow-sm'
                          : 'bg-white/5 text-white/70 hover:bg-white/10'
                      }`}
                    >
                      <span className="text-xs">{tab.icon}</span>
                      <span>{tab.label}</span>
                    </button>
                  );
                })}
              </div>

              {/* Tab Content Area */}
              <div className="flex-1 overflow-y-auto p-2.5 space-y-2.5 custom-scrollbar text-[10px]">
                {/* ── TAB 1: LUPA ── */}
                {activeTab === 'LUPA' && (
                  <div className="space-y-2">
                    <div className="p-2 rounded-xl bg-[#1B3524] border border-emerald-500/20 space-y-1">
                      <span className="font-bold text-emerald-400 block text-[11px]">🌍 Hakbang 1: Paghahanda ng Lupa</span>
                      <p className="text-white/85 text-[10px] leading-relaxed">
                        Lupang <strong>{idealSoil}</strong> na buhaghag at may maayos na daluyan ng tubig. Iwasan ang matubig na pwesto.
                      </p>
                      <div className="grid grid-cols-2 gap-1 pt-1 text-[9px]">
                        <div className="p-1 rounded bg-[#13281B]">
                          <span className="text-white/60 block">Lalim ng Hukay:</span>
                          <strong className="text-amber-300">20–30 cm (Deep Dig)</strong>
                        </div>
                        <div className="p-1 rounded bg-[#13281B]">
                          <span className="text-white/60 block">Halong Compost:</span>
                          <strong className="text-emerald-400">2–3 kilo / m²</strong>
                        </div>
                      </div>
                    </div>

                    {/* Educational SVG Cross-section Diagram with CM/M rulers */}
                    <div className="p-2 rounded-xl bg-[#0E1A12] border border-emerald-500/30 space-y-1">
                      <div className="flex justify-between items-center text-[9px] font-bold">
                        <span className="text-[#81C784]">📐 Sukat ng Kama at Tudling (Cross-Section)</span>
                        <span className="text-amber-300">DA-BPI Gabay</span>
                      </div>

                      {/* SVG Bed & Furrow Diagram */}
                      <svg viewBox="0 0 320 120" className="w-full h-24 rounded-lg bg-[#142319]">
                        {/* Ruler top for 1.0m Bed Width */}
                        <line x1="60" y1="14" x2="260" y2="14" stroke="#81C784" strokeWidth="1.5" />
                        <line x1="60" y1="10" x2="60" y2="18" stroke="#81C784" strokeWidth="1.5" />
                        <line x1="260" y1="10" x2="260" y2="18" stroke="#81C784" strokeWidth="1.5" />
                        <text x="160" y="11" fill="#81C784" fontSize="8" fontWeight="bold" textAnchor="middle">1.0 Metro (Lapad ng Kama)</text>

                        {/* Raised Bed polygon */}
                        <polygon points="40,90 70,40 250,40 280,90" fill="#2E4A35" stroke="#4CAF50" strokeWidth="1.5" />

                        {/* Furrows (Tudling) left & right */}
                        <polygon points="10,90 40,90 30,110 5,110" fill="#1C2D20" />
                        <polygon points="280,90 310,90 315,110 290,110" fill="#1C2D20" />

                        {/* Subsoil Deep Dig (20-30 cm) */}
                        <rect x="70" y="70" width="180" height="40" fill="#1B3222" stroke="#8D6E63" strokeDasharray="3,3" strokeWidth="1" />
                        <text x="160" y="92" fill="#FFA726" fontSize="7" fontWeight="bold" textAnchor="middle">Lalim ng Hukay: 20–30 cm</text>

                        {/* Bed Height Marker (15-20 cm) */}
                        <line x1="265" y1="40" x2="265" y2="90" stroke="#FFD54F" strokeWidth="1.5" />
                        <text x="270" y="68" fill="#FFD54F" fontSize="7" fontWeight="bold">15–20 cm taas</text>

                        {/* 2 Plant spots on top with spacing line */}
                        <circle cx="110" cy="38" r="4" fill="#66BB6A" />
                        <circle cx="210" cy="38" r="4" fill="#66BB6A" />
                        <line x1="110" y1="30" x2="210" y2="30" stroke="#FFD54F" strokeWidth="1" strokeDasharray="2,2" />
                        <text x="160" y="27" fill="#FFD54F" fontSize="7" fontWeight="bold" textAnchor="middle">Distansya: 30–50 cm</text>
                      </svg>
                    </div>

                    <div className="p-2 rounded-xl bg-[#172D20] border border-emerald-500/20 text-[9px] space-y-1">
                      <span className="font-bold text-amber-300 block">Inirerekomendang Paraan:</span>
                      <p className="text-white/80">
                        {preferredPlantingMethod === 'RAISED_BED' && 'Kama (Raised Bed): Pinoprotektahan ang ugat sa baha at pinapabuti ang aeration ng lupa.'}
                        {preferredPlantingMethod === 'DIRECT_SEEDING' && 'Diretsong Tanim (Direct Seeding): Diretsong ibaon ang 2-3 buto sa mababaw na tudling (2-3 cm lalim).'}
                        {preferredPlantingMethod === 'CONTAINER' && 'Paso / Container Gardening: Gumamit ng 5-10 litrong paso na may butas sa ilalim.'}
                      </p>
                    </div>
                  </div>
                )}

                {/* ── TAB 2: TANIM ── */}
                {activeTab === 'TANIM' && (
                  <div className="space-y-2">
                    <div className="p-2 rounded-xl bg-[#1B3524] border border-emerald-500/20 space-y-1.5">
                      <span className="font-bold text-emerald-400 block text-[11px]">🌱 Hakbang 2: Pagpupunla at Bariyedad</span>
                      <p className="text-white/80 text-[10px]">
                        Pumili ng rehistradong bariyedad ng DA-BPI para sa mas mataas na ani at proteksyon sa peste.
                      </p>

                      {/* 5-Stage Agronomic Growth Schedule */}
                      <div className="p-2 rounded-lg bg-[#122318] border border-emerald-500/30 space-y-1 mt-1">
                        <div className="flex justify-between items-center text-[9px]">
                          <span className="font-bold text-amber-300">📊 5-Stage Growth Schedule</span>
                          <span className="px-1.5 py-0.2 rounded bg-emerald-700 text-white font-bold">{daysToHarvest} Araw Hanggang Ani</span>
                        </div>

                        <div className="grid grid-cols-5 gap-1 text-center pt-1">
                          <div className="p-1 rounded bg-[#1B3524]">
                            <span className="text-[7px] text-white/60 uppercase block">Sprout</span>
                            <span className="text-[10px] font-bold text-[#81C784]">{stageSprout}d</span>
                          </div>
                          <div className="p-1 rounded bg-[#1B3524]">
                            <span className="text-[7px] text-white/60 uppercase block">Seedling</span>
                            <span className="text-[10px] font-bold text-[#81C784]">{stageSeedling}d</span>
                          </div>
                          <div className="p-1 rounded bg-[#1B3524]">
                            <span className="text-[7px] text-white/60 uppercase block">Veg</span>
                            <span className="text-[10px] font-bold text-[#81C784]">{stageVegetative}d</span>
                          </div>
                          <div className="p-1 rounded bg-[#1B3524]">
                            <span className="text-[7px] text-white/60 uppercase block">Bloom</span>
                            <span className="text-[10px] font-bold text-[#FFD54F]">{stageFlowering}d</span>
                          </div>
                          <div className="p-1 rounded bg-[#2E7D32]">
                            <span className="text-[7px] text-white uppercase block font-bold">Ani</span>
                            <span className="text-[10px] font-bold text-white">{stageHarvest}d+</span>
                          </div>
                        </div>
                      </div>

                      <div className="grid grid-cols-2 gap-1 pt-1 text-[9px]">
                        <div className="p-1.5 rounded bg-[#13281B]">
                          <span className="text-white/60 block">Distansya ng Puno:</span>
                          <strong className="text-white">30–50 sentimetro</strong>
                        </div>
                        <div className="p-1.5 rounded bg-[#13281B]">
                          <span className="text-white/60 block">Panahon:</span>
                          <strong className="text-emerald-400">{season}</strong>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {/* ── TAB 3: BALAG ── */}
                {activeTab === 'BALAG' && (
                  <div className="space-y-2">
                    <div className="p-2 rounded-xl bg-[#1B3524] border border-emerald-500/20 space-y-1.5">
                      <span className="font-bold text-emerald-400 block text-[11px]">🪵 Hakbang 3: Balag at Suporta (Trellis)</span>

                      {needsTrellis ? (
                        <div className="p-2 rounded-lg bg-amber-500/20 border border-amber-500/40 text-[10px] space-y-1">
                          <span className="font-bold text-amber-300 block">⚠️ KAILANGAN NG BALAG</span>
                          <p className="text-white/85 leading-relaxed">
                            Gumagapang o may mabigat na bunga. Ilagay ang balag sa loob ng 15-20 araw (Seedling stage) upang hindi masira ang mga ugat sa pagtulos.
                          </p>
                        </div>
                      ) : (
                        <div className="p-2 rounded-lg bg-emerald-500/20 border border-emerald-500/40 text-[10px] space-y-1">
                          <span className="font-bold text-emerald-300 block">✅ HINDI KAILANGAN NG BALAG</span>
                          <p className="text-white/85 leading-relaxed">
                            May sariling matatag na tangkay o mababang gulay. Hindi nangangailangan ng structural trellis.
                          </p>
                        </div>
                      )}

                      {/* Educational Trellis Architecture Visual Diagrams */}
                      <div className="p-2 rounded-xl bg-[#0E1A12] border border-amber-600/30 space-y-1.5">
                        <span className="text-[9px] font-bold text-amber-300 block">🪵 Mga Uri ng Balag (Trellis Types & Height)</span>

                        <div className="grid grid-cols-3 gap-1.5 text-center text-[8px]">
                          {/* 1. Tulos */}
                          <div className={`p-1.5 rounded-lg border flex flex-col items-center ${
                            trellisType === 'TULOS' ? 'bg-amber-900/40 border-amber-400 text-white' : 'bg-white/5 border-white/10 text-white/70'
                          }`}>
                            <svg viewBox="0 0 40 45" className="w-10 h-10 mb-1">
                              <line x1="20" y1="40" x2="20" y2="8" stroke="#A1887F" strokeWidth="2.5" />
                              <circle cx="20" cy="18" r="3" stroke="#81C784" strokeWidth="1" fill="none" />
                              <circle cx="20" cy="28" r="3.5" stroke="#81C784" strokeWidth="1" fill="none" />
                              <line x1="5" y1="40" x2="35" y2="40" stroke="#795548" strokeWidth="1.5" />
                            </svg>
                            <span className="font-bold block">1. TULOS</span>
                            <span className="text-amber-300 font-semibold">1.5m Taas</span>
                          </div>

                          {/* 2. A-Frame */}
                          <div className={`p-1.5 rounded-lg border flex flex-col items-center ${
                            trellisType === 'A_FRAME' ? 'bg-amber-900/40 border-amber-400 text-white' : 'bg-white/5 border-white/10 text-white/70'
                          }`}>
                            <svg viewBox="0 0 40 45" className="w-10 h-10 mb-1">
                              <line x1="8" y1="40" x2="20" y2="8" stroke="#D7CCC8" strokeWidth="2" />
                              <line x1="32" y1="40" x2="20" y2="8" stroke="#D7CCC8" strokeWidth="2" />
                              <line x1="12" y1="26" x2="28" y2="26" stroke="#A1887F" strokeWidth="1.5" />
                              <line x1="5" y1="40" x2="35" y2="40" stroke="#795548" strokeWidth="1.5" />
                            </svg>
                            <span className="font-bold block">2. A-FRAME</span>
                            <span className="text-amber-300 font-semibold">1.8m Taas</span>
                          </div>

                          {/* 3. Overhead */}
                          <div className={`p-1.5 rounded-lg border flex flex-col items-center ${
                            trellisType === 'OVERHEAD' ? 'bg-amber-900/40 border-amber-400 text-white' : 'bg-white/5 border-white/10 text-white/70'
                          }`}>
                            <svg viewBox="0 0 40 45" className="w-10 h-10 mb-1">
                              <line x1="8" y1="40" x2="8" y2="12" stroke="#D7CCC8" strokeWidth="2" />
                              <line x1="32" y1="40" x2="32" y2="12" stroke="#D7CCC8" strokeWidth="2" />
                              <line x1="4" y1="12" x2="36" y2="12" stroke="#D7CCC8" strokeWidth="2" />
                              <line x1="5" y1="40" x2="35" y2="40" stroke="#795548" strokeWidth="1.5" />
                            </svg>
                            <span className="font-bold block">3. OVERHEAD</span>
                            <span className="text-amber-300 font-semibold">2.0m Taas</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {/* ── TAB 4: ALAGA ── */}
                {activeTab === 'ALAGA' && (
                  <div className="space-y-2">
                    <div className="p-2 rounded-xl bg-[#1B3524] border border-emerald-500/20 space-y-1.5 text-[10px]">
                      <span className="font-bold text-emerald-400 block text-[11px]">💧 Hakbang 4: Pagdidilig, Pataba at Damo</span>

                      <div className="p-2 rounded-lg bg-[#122318] space-y-1">
                        <span className="text-blue-300 font-bold block">💧 Iskedyul ng Pagdidilig:</span>
                        <p className="text-white/85">
                          Diligan tuwing <strong>{wateringIntervalDays} araw</strong> sa maagang umaga (6:00 AM - 8:00 AM). Iwasan ang pagdilig sa tanghaling tapat.
                        </p>
                      </div>

                      <div className="p-2 rounded-lg bg-[#122318] space-y-1">
                        <span className="text-emerald-400 font-bold block">🧪 Pataba at Sustansya:</span>
                        <p className="text-white/85">
                          • Organiko: Vermicast o dumi ng hayop tuwing 2 linggo.<br />
                          • Sintetiko: Complete 14-14-14 (paglilipat), Urea (dahon), Potash (pamumunga).
                        </p>
                      </div>

                      <div className="p-2 rounded-lg bg-[#122318] space-y-1">
                        <span className="text-amber-300 font-bold block">🌿 Pag-aalis ng Damo (Weeding):</span>
                        <p className="text-white/85">
                          Linisin ang damo tuwing <strong>{weedingIntervalDays} araw</strong> upang hindi maagawan ng pataba at tubig ang pananim.
                        </p>
                      </div>
                    </div>
                  </div>
                )}

                {/* ── TAB 5: PESTE ── */}
                {activeTab === 'PESTE' && (
                  <div className="space-y-2">
                    <div className="p-2 rounded-xl bg-[#1B3524] border border-emerald-500/20 space-y-1.5 text-[10px]">
                      <span className="font-bold text-emerald-400 block text-[11px]">🐛 Hakbang 5: Peste at Kaibigang Pananim</span>

                      <div className="p-2 rounded-lg bg-[#122318] space-y-1">
                        <span className="text-rose-400 font-bold block">🌿 Likas na Pangontra (Organic Remedy):</span>
                        <p className="text-white/85">
                          1L tubig + 1 kutsaritang sabong panlaba (walang bleach) + 1 kutsaritang mantika/sili. I-spray sa ilalim ng dahon sa hapon.
                        </p>
                      </div>

                      <div className="grid grid-cols-2 gap-1.5 pt-1 text-[9px]">
                        <div className="p-1.5 rounded-lg bg-[#1E3A2B] border border-emerald-500/30">
                          <span className="text-emerald-300 font-bold block">🤝 Kasama (Good)</span>
                          <span className="text-white/80 block mt-0.5 truncate">
                            {goodCompanions.length > 0 ? goodCompanions.join(', ') : 'Walang limitasyon'}
                          </span>
                        </div>
                        <div className="p-1.5 rounded-lg bg-[#3A1E1E] border border-rose-500/30">
                          <span className="text-rose-300 font-bold block">⚠️ Iwasan (Avoid)</span>
                          <span className="text-white/80 block mt-0.5 truncate">
                            {badCompanions.length > 0 ? badCompanions.join(', ') : 'Walang kalaban'}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {/* ── TAB 6: ANI ── */}
                {activeTab === 'ANI' && (
                  <div className="space-y-2">
                    <div className="p-2 rounded-xl bg-[#1B3524] border border-emerald-500/20 space-y-1.5 text-[10px]">
                      <span className="font-bold text-emerald-400 block text-[11px]">🌾 Hakbang 6: Pag-aani at Crop Rotation</span>

                      <div className="p-2 rounded-lg bg-[#122318] space-y-1">
                        <span className="text-amber-300 font-bold block">🔍 Palatandaan ng Ani:</span>
                        <p className="text-white/85 leading-relaxed">
                          {harvestIndicators || 'Tingnan ang tamang kulay, laki at katigasan ng bunga sa pagsapit ng tamang gulang.'}
                        </p>
                      </div>

                      <div className="p-2 rounded-lg bg-[#122318] space-y-1">
                        <span className="text-emerald-300 font-bold block">🔄 Crop Rotation para sa Susunod:</span>
                        <p className="text-white/85 leading-relaxed">
                          Iwasan ang pagtatanim ng parehong pamilya ({taxonomicFamily || 'Solanaceae'}). Magtanim ng Sitaw o munggo pagkatapos nito upang maibalik ang Nitrogen sa lupa.
                        </p>
                      </div>
                    </div>
                  </div>
                )}

                {/* Bottom Science Explainer Pills */}
                <div className="pt-1 flex items-center gap-1 text-[9px] overflow-x-auto custom-scrollbar">
                  <span className="text-amber-300 font-bold whitespace-nowrap">💡 Bakit?</span>
                  <button type="button" className="px-2 py-0.5 rounded-full bg-[#1E5E38] text-white whitespace-nowrap">Kategorya 💡</button>
                  <button type="button" className="px-2 py-0.5 rounded-full bg-[#8D6E63] text-white whitespace-nowrap">Araw ng Ani 💡</button>
                  <button type="button" className="px-2 py-0.5 rounded-full bg-[#0277BD] text-white whitespace-nowrap">Dilig 💡</button>
                  <button type="button" className="px-2 py-0.5 rounded-full bg-[#6A1B9A] text-white whitespace-nowrap">Lupa 💡</button>
                </div>
              </div>
            </div>
          )}

          {/* Bottom Virtual Home Bar */}
          <div className="h-4 bg-black/80 flex items-center justify-center shrink-0">
            <div className="w-20 h-1 bg-white/40 rounded-full" />
          </div>
        </div>
      </div>
    </div>
  );
};
