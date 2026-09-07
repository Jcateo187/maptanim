import React, { useEffect, useState } from 'react';
import {
  X, Edit2, Sparkles, Sprout, Droplets, Calendar, ShieldCheck, Check,
  Info, ExternalLink, HelpCircle, Layers, AlertTriangle, Bug, Award
} from 'lucide-react';
import { Crop } from '../../types';
import {
  CropMetadataInfo,
  CropVarietyInfo,
  WhyDetailInfo,
  getCropMetadata
} from '../../services/cropMetadata';

interface CropBreakdownModalProps {
  crop: Crop | null;
  isOpen: boolean;
  onClose: () => void;
  onEdit?: (crop: Crop) => void;
}

type WhyTopic = 'CATEGORY' | 'HARVEST' | 'WATERING' | 'SOIL';

export const CropBreakdownModal: React.FC<CropBreakdownModalProps> = ({
  crop,
  isOpen,
  onClose,
  onEdit,
}) => {
  const [metadata, setMetadata] = useState<CropMetadataInfo | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedVarietyId, setSelectedVarietyId] = useState<string | null>(null);
  const [activeWhyTopic, setActiveWhyTopic] = useState<WhyTopic | null>(null);

  useEffect(() => {
    if (!crop || !isOpen) {
      setMetadata(null);
      setSelectedVarietyId(null);
      setActiveWhyTopic(null);
      return;
    }

    let isMounted = true;
    setLoading(true);

    getCropMetadata(crop).then((meta) => {
      if (isMounted) {
        setMetadata(meta);
        setSelectedVarietyId(meta.varieties[0]?.varietyId || null);
        setLoading(false);
      }
    });

    return () => {
      isMounted = false;
    };
  }, [crop, isOpen]);

  if (!isOpen || !crop) return null;

  const activeVariety: CropVarietyInfo | undefined =
    metadata?.varieties.find((v) => v.varietyId === selectedVarietyId) ||
    metadata?.varieties[0];

  const activeHarvestDays = activeVariety?.growthDurationDays ?? crop.daysToHarvest;
  const activeWaterDays = activeVariety?.wateringIntervalDays ?? crop.wateringIntervalDays ?? 2;

  // Selected Why Detail
  const getWhyDetail = (topic: WhyTopic): { info: WhyDetailInfo; icon: string } => {
    if (!metadata) {
      return {
        info: { title: 'Parameter Science', summary: '', points: [] },
        icon: '💡',
      };
    }
    switch (topic) {
      case 'CATEGORY':
        return { info: metadata.whyReasoning.categoryWhy, icon: '🏷️' };
      case 'HARVEST':
        return { info: metadata.whyReasoning.harvestWhy, icon: '⏱️' };
      case 'WATERING':
        return { info: metadata.whyReasoning.wateringWhy, icon: '💧' };
      case 'SOIL':
        return { info: metadata.whyReasoning.soilWhy, icon: '🪴' };
    }
  };

  const imageSrc =
    crop.imageUrl ||
    metadata?.primaryPhotoUrl ||
    '/metadata/crops_images/tomato.png';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-5 bg-black/85 backdrop-blur-md animate-fadeIn">
      {/* Container recreating the Mobile CropDetailDialog Surface */}
      <div
        className="relative w-full max-w-3xl max-h-[92vh] flex flex-col rounded-[28px] overflow-hidden border-[1.5px] border-[#4CAF50]/60 shadow-[0_20px_50px_rgba(0,0,0,0.8),0_0_30px_rgba(76,175,80,0.25)] text-white"
        style={{ backgroundColor: '#162A1E' }}
      >
        {/* Floating Top Buttons: Close & Edit */}
        <div className="absolute top-3 right-3 z-30 flex items-center gap-2">
          {onEdit && (
            <button
              onClick={() => {
                onClose();
                onEdit(crop);
              }}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-black/60 hover:bg-emerald-700/80 border border-emerald-500/50 text-emerald-300 hover:text-white text-xs font-bold backdrop-blur-md transition shadow-md"
              title="Edit this Crop in Admin"
            >
              <Edit2 className="w-3.5 h-3.5" />
              <span>Edit Data</span>
            </button>
          )}
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-black/70 hover:bg-black/90 text-white flex items-center justify-center border border-white/20 transition shadow-md"
            title="Close"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Scrollable Modal Body */}
        <div className="flex-1 overflow-y-auto custom-scrollbar">
          {/* 1. HERO PHOTO BANNER */}
          <div className="relative h-56 sm:h-64 w-full bg-[#101E15] overflow-hidden">
            <img
              src={imageSrc}
              alt={crop.name}
              className="w-full h-full object-cover"
              onError={(e) => {
                (e.target as HTMLImageElement).src = '/metadata/crops_images/tomato.png';
              }}
            />
            {/* Smooth Gradient Overlay */}
            <div
              className="absolute inset-0"
              style={{
                background:
                  'linear-gradient(to bottom, rgba(0,0,0,0.45) 0%, rgba(22,42,30,0.1) 40%, #162A1E 100%)',
              }}
            />

            {/* Top Badges: Taxonomic Family & Mobile UI indicator */}
            <div className="absolute bottom-3 left-5 right-5 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-1 rounded-lg bg-[#2E7D32]/90 border border-emerald-400/40 text-white text-[11px] font-semibold backdrop-blur-sm shadow">
                  Family: {metadata?.taxonomicFamily || crop.taxonomicFamily || 'Agronomic Vegetable'}
                </span>
                <span className="px-2 py-0.5 rounded-lg bg-emerald-950/80 border border-emerald-500/40 text-[#A5D6A7] text-[10px] font-bold">
                  {crop.category}
                </span>
              </div>
              <span className="hidden sm:inline-flex items-center gap-1 text-[10px] text-emerald-300/80 bg-black/40 px-2 py-0.5 rounded-full border border-emerald-500/20">
                <Sparkles className="w-3 h-3 text-emerald-400" />
                Mobile View
              </span>
            </div>
          </div>

          {/* 2. CONTENT BODY */}
          <div className="p-5 sm:p-6 space-y-5">
            {/* Title, Local Name, and Botanical Name */}
            <div>
              <div className="flex flex-wrap items-baseline gap-2">
                <h2 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
                  {crop.name}
                </h2>
                {crop.localName && (
                  <span className="text-lg sm:text-xl font-semibold text-[#A5D6A7]">
                    ({crop.localName})
                  </span>
                )}
              </div>
              {crop.botanicalName && (
                <p className="text-xs sm:text-sm italic text-white/75 font-mono mt-0.5">
                  {crop.botanicalName}
                </p>
              )}
            </div>

            {/* 3. INTERACTIVE "WHY?" PARAMETER PILLS ROW */}
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-[#FFD54F] flex items-center gap-1">
                  <span>💡 Interactive Parameters (Tap to view science & why):</span>
                </span>
              </div>

              <div className="flex flex-wrap gap-2">
                {/* Category Pill */}
                <button
                  type="button"
                  onClick={() => setActiveWhyTopic('CATEGORY')}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#1E5E38] hover:bg-[#257345] border border-emerald-400/40 text-white text-xs font-semibold transition group shadow"
                >
                  <span>Category: {metadata?.cropType || crop.category}</span>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-emerald-950/60 text-emerald-300 font-bold group-hover:text-white">
                    Why? 💡
                  </span>
                </button>

                {/* Harvest Pill */}
                <button
                  type="button"
                  onClick={() => setActiveWhyTopic('HARVEST')}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#8D6E63] hover:bg-[#9E7D72] border border-amber-400/40 text-white text-xs font-semibold transition group shadow"
                >
                  <span>Harvest: {activeHarvestDays} days</span>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-amber-950/60 text-amber-200 font-bold group-hover:text-white">
                    Why? 💡
                  </span>
                </button>

                {/* Water Pill */}
                <button
                  type="button"
                  onClick={() => setActiveWhyTopic('WATERING')}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#0277BD] hover:bg-[#0288D1] border border-blue-400/40 text-white text-xs font-semibold transition group shadow"
                >
                  <span>Water: Every {activeWaterDays}d</span>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-blue-950/60 text-blue-200 font-bold group-hover:text-white">
                    Why? 💡
                  </span>
                </button>

                {/* Soil pH Pill */}
                <button
                  type="button"
                  onClick={() => setActiveWhyTopic('SOIL')}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#6A1B9A] hover:bg-[#7B1FA2] border border-purple-400/40 text-white text-xs font-semibold transition group shadow"
                >
                  <span>Soil pH: {crop.optimalPhMin || 6.0}–{crop.optimalPhMax || 6.8}</span>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-purple-950/60 text-purple-200 font-bold group-hover:text-white">
                    Why? 💡
                  </span>
                </button>
              </div>
            </div>

            {/* 4. CROP OVERVIEW DESCRIPTION */}
            {(crop.description || metadata?.description) && (
              <div className="p-4 rounded-2xl bg-[#122318] border border-emerald-500/25 space-y-1.5">
                <div className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                  <span>📝 Crop Profile & Purpose</span>
                </div>
                <p className="text-xs sm:text-sm text-white/90 leading-relaxed">
                  {crop.description || metadata?.description}
                </p>
              </div>
            )}

            {/* 5. CULTIVARS & 5-STAGE GROWTH BREAKDOWN */}
            {metadata && metadata.varieties.length > 0 && (
              <div className="p-4 rounded-2xl bg-[#122318] border border-emerald-500/25 space-y-3.5">
                <div>
                  <div className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                    <span>🌾 Priority Cultivars & 5-Stage Growth Breakdown</span>
                  </div>
                  <p className="text-[11px] text-white/70 mt-0.5">
                    Select a variety to inspect tailored growth days, bitterness level, and plant traits:
                  </p>
                </div>

                {/* Variety Selector Tabs */}
                <div className="flex flex-wrap gap-2">
                  {metadata.varieties.map((v) => {
                    const isSelected = v.varietyId === activeVariety?.varietyId;
                    return (
                      <button
                        key={v.varietyId}
                        type="button"
                        onClick={() => setSelectedVarietyId(v.varietyId)}
                        className={`px-3 py-1.5 rounded-xl text-xs font-bold transition border ${
                          isSelected
                            ? 'bg-[#4CAF50] text-[#0E2316] border-white shadow-md'
                            : 'bg-[#1E3A2B] text-[#A5D6A7] border-emerald-500/40 hover:border-emerald-400'
                        }`}
                      >
                        {v.varietyName}
                      </button>
                    );
                  })}
                </div>

                {/* Active Variety Details */}
                {activeVariety && (
                  <div className="p-3.5 rounded-xl bg-[#15261C] border border-[#81C784]/35 space-y-3">
                    <div className="flex items-center justify-between">
                      <div>
                        <h4 className="text-sm font-bold text-[#A5D6A7]">
                          {activeVariety.varietyName}
                        </h4>
                        {activeVariety.localNamePh && activeVariety.localNamePh !== activeVariety.varietyName && (
                          <span className="text-[11px] text-white/70">
                            {activeVariety.localNamePh}
                          </span>
                        )}
                      </div>
                      <span className="px-2.5 py-1 rounded-full bg-[#2E7D32] text-white text-[11px] font-bold shadow">
                        {activeVariety.growthDurationDays} Days Total
                      </span>
                    </div>

                    {activeVariety.description && (
                      <p className="text-xs text-white/85 leading-relaxed">
                        {activeVariety.description}
                      </p>
                    )}

                    {/* Traits Row */}
                    {(activeVariety.fruitLengthCm || activeVariety.bitternessLevel) && (
                      <div className="flex flex-wrap gap-2 pt-1">
                        {activeVariety.fruitLengthCm && (
                          <span className="px-2.5 py-1 rounded-lg bg-[#26A69A]/20 border border-[#26A69A]/40 text-[#80CBC4] text-[11px] font-medium">
                            Length: <strong>{activeVariety.fruitLengthCm}</strong>
                          </span>
                        )}
                        {activeVariety.bitternessLevel && (
                          <span className="px-2.5 py-1 rounded-lg bg-[#FFA726]/20 border border-[#FFA726]/40 text-[#FFE082] text-[11px] font-medium">
                            Bitterness: <strong>{activeVariety.bitternessLevel}</strong>
                          </span>
                        )}
                      </div>
                    )}

                    {activeVariety.diseaseResistance && (
                      <div className="p-2 rounded-lg bg-[#2E7D32]/20 border border-[#81C784]/30 text-[11px] text-[#A5D6A7] font-medium flex items-center gap-1.5">
                        <ShieldCheck className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                        <span>Resistance: {activeVariety.diseaseResistance}</span>
                      </div>
                    )}

                    <div className="h-px bg-white/10 my-2" />

                    {/* 5-STAGE GROWTH SCHEDULE TIMELINE */}
                    <div className="space-y-2">
                      <span className="text-[11px] font-bold text-[#FFD54F] block">
                        📊 5-Stage Agronomic Growth Schedule:
                      </span>
                      <div className="grid grid-cols-5 gap-1.5 sm:gap-2">
                        <div className="p-2 rounded-lg bg-[#1B3524] border border-emerald-500/30 text-center">
                          <span className="text-[9px] text-white/70 block uppercase font-bold">1. Sprout</span>
                          <span className="text-xs sm:text-sm font-extrabold text-[#81C784] mt-0.5 block">
                            {activeVariety.stageDays.stage1Sprout}d
                          </span>
                        </div>
                        <div className="p-2 rounded-lg bg-[#1B3524] border border-emerald-500/30 text-center">
                          <span className="text-[9px] text-white/70 block uppercase font-bold">2. Seedling</span>
                          <span className="text-xs sm:text-sm font-extrabold text-[#81C784] mt-0.5 block">
                            {activeVariety.stageDays.stage2Seedling}d
                          </span>
                        </div>
                        <div className="p-2 rounded-lg bg-[#1B3524] border border-emerald-500/30 text-center">
                          <span className="text-[9px] text-white/70 block uppercase font-bold">3. Veg</span>
                          <span className="text-xs sm:text-sm font-extrabold text-[#81C784] mt-0.5 block">
                            {activeVariety.stageDays.stage3Vegetative}d
                          </span>
                        </div>
                        <div className="p-2 rounded-lg bg-[#1B3524] border border-amber-500/40 text-center">
                          <span className="text-[9px] text-white/70 block uppercase font-bold">4. Bloom</span>
                          <span className="text-xs sm:text-sm font-extrabold text-[#FFD54F] mt-0.5 block">
                            {activeVariety.stageDays.stage4Flowering}d
                          </span>
                        </div>
                        <div className="p-2 rounded-lg bg-[#2E7D32]/40 border border-[#4CAF50] text-center shadow">
                          <span className="text-[9px] text-emerald-300 block uppercase font-bold">5. Harvest</span>
                          <span className="text-xs sm:text-sm font-black text-white mt-0.5 block">
                            {activeVariety.stageDays.stage5Harvest}d+
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* 6. SOIL, pH & NPK SPECIFICATIONS */}
            <div className="p-4 rounded-2xl bg-[#122318] border border-emerald-500/25 space-y-3">
              <div className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                <span>🌱 Soil, pH & Nutritional Specifications</span>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div className="p-2.5 rounded-xl bg-[#172D20] border border-emerald-500/20">
                  <span className="text-[10px] text-white/60 uppercase font-bold block">Optimal pH Range</span>
                  <span className="text-xs font-bold text-amber-300 mt-0.5 block">
                    {crop.optimalPhMin || 6.0} – {crop.optimalPhMax || 6.8}
                  </span>
                </div>
                <div className="p-2.5 rounded-xl bg-[#172D20] border border-emerald-500/20">
                  <span className="text-[10px] text-white/60 uppercase font-bold block">NPK Target (N:P:K)</span>
                  <span className="text-xs font-bold text-emerald-400 mt-0.5 block">
                    {crop.npkRequirement.nitrogen} : {crop.npkRequirement.phosphorus} : {crop.npkRequirement.potassium}
                  </span>
                </div>
                <div className="p-2.5 rounded-xl bg-[#172D20] border border-emerald-500/20">
                  <span className="text-[10px] text-white/60 uppercase font-bold block">Seasonality</span>
                  <span className="text-xs font-bold text-white mt-0.5 block">
                    {crop.season}
                  </span>
                </div>
              </div>

              {/* Soil Texture Suitability */}
              <div className="pt-1 flex flex-wrap items-center gap-2 text-xs">
                <span className="text-[#A5D6A7] font-semibold">Soil Texture:</span>
                <span className="px-2 py-0.5 rounded bg-[#2E7D32]/30 border border-[#81C784]/40 text-[#81C784] font-bold">
                  Ideal: {crop.idealSoil}
                </span>
                {crop.suitableSoils && crop.suitableSoils.length > 0 && (
                  <span className="px-2 py-0.5 rounded bg-amber-500/20 border border-amber-400/30 text-amber-300">
                    Suitable: {crop.suitableSoils.join(', ')}
                  </span>
                )}
              </div>
            </div>

            {/* 7. COMPANION PLANTS & ANTAGONISTS */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {/* Good Companions */}
              <div className="p-3.5 rounded-2xl bg-[#122318] border border-emerald-500/25 space-y-2">
                <span className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                  <span>🤝 Companion Plants</span>
                </span>
                {crop.companionCropsGood && crop.companionCropsGood.length > 0 ? (
                  <ul className="space-y-1 text-xs text-[#A5D6A7]">
                    {crop.companionCropsGood.map((plant, idx) => (
                      <li key={idx} className="flex items-center gap-1.5">
                        <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                        <span>{plant}</span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <span className="text-xs text-white/50 italic">No specific companion restrictions</span>
                )}
              </div>

              {/* Incompatible Plants */}
              <div className="p-3.5 rounded-2xl bg-[#122318] border border-rose-500/25 space-y-2">
                <span className="text-xs font-bold text-rose-400 flex items-center gap-1.5">
                  <span>⚠️ Avoid Planting With</span>
                </span>
                {crop.companionCropsBad && crop.companionCropsBad.length > 0 ? (
                  <ul className="space-y-1 text-xs text-[#FF8A80]">
                    {crop.companionCropsBad.map((plant, idx) => (
                      <li key={idx} className="flex items-center gap-1.5">
                        <span className="w-1.5 h-1.5 rounded-full bg-rose-400" />
                        <span>{plant}</span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <span className="text-xs text-white/50 italic">No known antagonistic crops</span>
                )}
              </div>
            </div>

            {/* 8. COMMON PESTS & DISEASES */}
            {crop.commonPests && crop.commonPests.length > 0 && (
              <div className="p-3.5 rounded-2xl bg-[#122318] border border-emerald-500/25 space-y-2">
                <span className="text-xs font-bold text-rose-400 flex items-center gap-1.5">
                  <Bug className="w-3.5 h-3.5" />
                  <span>Common Pests & Diseases</span>
                </span>
                <div className="flex flex-wrap gap-2">
                  {crop.commonPests.map((pest, idx) => (
                    <span
                      key={idx}
                      className="px-2.5 py-1 rounded-lg bg-[#D32F2F]/30 border border-[#EF5350]/40 text-[#FFCDD2] text-xs font-medium"
                    >
                      {pest}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {/* 9. HARVEST READINESS & POST-HARVEST */}
            {crop.harvestIndicators && (
              <div className="p-3.5 rounded-2xl bg-[#122318] border border-emerald-500/25 space-y-2 text-xs">
                <span className="font-bold text-[#FFD54F] flex items-center gap-1.5">
                  <span>🌾 Harvest Readiness & Field Handling</span>
                </span>
                <p className="text-white/85 leading-relaxed">
                  • <strong>Maturity Indicators:</strong> {crop.harvestIndicators}
                </p>
                <p className="text-white/75 leading-relaxed">
                  • <strong>Field Timing:</strong> Harvest during cool early morning hours to minimize field heat respiration.
                </p>
              </div>
            )}

            {/* 10. FIELD RESEARCH & AGRONOMIC PROFILE */}
            <div className="p-4 rounded-2xl bg-[#101E15] border border-emerald-500/20 space-y-2 text-xs">
              <span className="font-bold text-emerald-400 flex items-center gap-1.5">
                <Award className="w-3.5 h-3.5" />
                <span>📋 Field Research & Agronomic Profile</span>
              </span>
              <div className="space-y-1 text-white/80">
                <p>
                  <strong className="text-[#A5D6A7]">Data Source:</strong>{' '}
                  {metadata?.referenceSource.organization || 'MapTanim Agricultural Field Research'}
                </p>
                <p>
                  <strong className="text-[#A5D6A7]">Dataset:</strong>{' '}
                  {metadata?.referenceSource.publicationTitle || 'Field Survey & Agronomic Interview Dataset'}
                </p>
                <p className="text-[11px] text-white/65 leading-relaxed pt-1">
                  🎯 {metadata?.referenceSource.purposeStatement || 'Verified agricultural dataset gathered directly from farmer interviews and field trials.'}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-3.5 bg-[#101E15] border-t border-emerald-500/30 flex items-center justify-between text-xs">
          <div className="flex items-center gap-2 text-emerald-400 font-semibold text-[11px]">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span>MapTanim Mobile KnowledgeBase Preview</span>
          </div>
          <div className="flex items-center gap-2">
            {onEdit && (
              <button
                onClick={() => {
                  onClose();
                  onEdit(crop);
                }}
                className="px-4 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold transition flex items-center gap-1.5 shadow"
              >
                <Edit2 className="w-3.5 h-3.5" />
                <span>Edit Crop Profile</span>
              </button>
            )}
            <button
              onClick={onClose}
              className="px-4 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-white font-semibold transition"
            >
              Close
            </button>
          </div>
        </div>

        {/* ─── SECOND-SCREEN OVERLAY: "WHY?" SCIENCE MODAL ───────────────────────── */}
        {activeWhyTopic && (
          <div className="absolute inset-0 z-40 flex items-center justify-center p-4 bg-black/80 backdrop-blur-md animate-fadeIn">
            {(() => {
              const { info, icon } = getWhyDetail(activeWhyTopic);
              return (
                <div
                  className="w-full max-w-lg rounded-[28px] border-[1.5px] border-[#FFD54F]/60 p-5 sm:p-6 text-white space-y-4 shadow-2xl animate-scaleUp"
                  style={{ backgroundColor: '#14241B' }}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="text-xl">{icon}</span>
                      <h3 className="text-base font-bold text-[#FFD54F]">
                        {info.title}
                      </h3>
                    </div>
                    <button
                      onClick={() => setActiveWhyTopic(null)}
                      className="w-7 h-7 rounded-full bg-black/60 hover:bg-black/90 text-white flex items-center justify-center border border-white/20 transition"
                    >
                      <X className="w-3.5 h-3.5" />
                    </button>
                  </div>

                  <p className="text-xs sm:text-sm text-white/95 leading-relaxed font-medium bg-[#1B3324] p-3 rounded-xl border border-emerald-500/25">
                    {info.summary}
                  </p>

                  <div className="space-y-2 pt-1">
                    <span className="text-[11px] font-bold text-emerald-300 uppercase tracking-wider block">
                      Agronomic Principles:
                    </span>
                    <ul className="space-y-2 text-xs text-white/85">
                      {info.points.map((point, pIdx) => (
                        <li key={pIdx} className="flex items-start gap-2">
                          <span className="text-emerald-400 mt-0.5">•</span>
                          <span className="leading-relaxed">{point}</span>
                        </li>
                      ))}
                    </ul>
                  </div>

                  <div className="pt-2 flex justify-end">
                    <button
                      onClick={() => setActiveWhyTopic(null)}
                      className="px-4 py-1.5 rounded-xl bg-[#4CAF50] hover:bg-[#43A047] text-[#0E2316] font-bold text-xs transition shadow"
                    >
                      Got It
                    </button>
                  </div>
                </div>
              );
            })()}
          </div>
        )}
      </div>
    </div>
  );
};
