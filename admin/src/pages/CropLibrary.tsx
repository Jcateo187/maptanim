import React, { useEffect, useState, useRef } from 'react';
import {
  Search, Plus, Edit2, Trash2, Sprout, Droplets, Calendar, Filter, Bug, Mountain,
  UploadCloud, Image as ImageIcon, Check, RefreshCw, Smartphone, AlertCircle,
  ExternalLink, Sparkles, Layers, ShieldCheck, Link2, Cloud, Settings, Terminal
} from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { Crop, SoilType, SeasonType, CategoryType, PestGuide, SoilGuide } from '../types';
import { apiService } from '../services/api';
import { MOCK_PESTS, MOCK_SOILS } from '../services/mockData';
import { CropBreakdownModal } from '../components/crops/CropBreakdownModal';
import { MobileCropBreakdownPreview } from '../components/crops/MobileCropBreakdownPreview';

const OFFICIAL_METADATA_PRESETS = [
  { label: 'Tomato (Kamatis)', url: '/metadata/crops_images/tomato.png', fileName: 'tomato.png' },
  { label: 'Eggplant (Talong)', url: '/metadata/crops_images/eggplant.png', fileName: 'eggplant.png' },
  { label: 'Chili Pepper (Sili)', url: '/metadata/crops_images/sili.png', fileName: 'sili.png' },
  { label: 'Cabbage (Repolyo)', url: '/metadata/crops_images/cabbage.png', fileName: 'cabbage.png' },
  { label: 'Pechay (Bok Choy)', url: '/metadata/crops_images/pechay.png', fileName: 'pechay.png' },
  { label: 'Red Onion (Sibuyas)', url: '/metadata/crops_images/onion.png', fileName: 'onion.png' },
  { label: 'Carrot (Karot)', url: '/metadata/crops_images/carrot.png', fileName: 'carrot.png' },
  { label: 'Yardlong Bean (Sitaw)', url: '/metadata/crops_images/sitaw.png', fileName: 'sitaw.png' },
  { label: 'Lettuce (Litsugas)', url: '/metadata/crops_images/lettuce.png', fileName: 'lettuce.png' },
  { label: 'Cucumber (Pipino)', url: '/metadata/crops_images/pipino.png', fileName: 'pipino.png' },
  { label: 'Bitter Gourd (Ampalaya)', url: '/metadata/crops_images/ampalaya.png', fileName: 'ampalaya.png' },
  { label: 'Okra (Okra)', url: '/metadata/crops_images/okra.png', fileName: 'okra.png' },
  { label: 'Sweet Corn (Mais)', url: '/metadata/crops_images/corn.png', fileName: 'corn.png' },
  { label: 'Squash (Kalabasa)', url: '/metadata/crops_images/pumpkin.png', fileName: 'pumpkin.png' },
  { label: 'Water Spinach (Kangkong)', url: '/metadata/crops_images/kangkong.png', fileName: 'kangkong.png' },
];

const ALL_SOIL_OPTIONS: SoilType[] = ['LOAM', 'CLAY', 'SANDY', 'SILTY', 'PEATY', 'CHALKY'];

export const CropLibrary: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'CROPS' | 'PESTS' | 'SOILS'>('CROPS');
  const [crops, setCrops] = useState<Crop[]>([]);
  const [pests] = useState<PestGuide[]>(MOCK_PESTS as PestGuide[]);
  const [soils] = useState<SoilGuide[]>(MOCK_SOILS as SoilGuide[]);
  const [loading, setLoading] = useState<boolean>(true);
  const [search, setSearch] = useState<string>('');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [isAddModalOpen, setIsAddModalOpen] = useState<boolean>(false);
  const [editingCrop, setEditingCrop] = useState<Crop | null>(null);
  const [selectedPest, setSelectedPest] = useState<PestGuide | null>(null);
  const [selectedSoil, setSelectedSoil] = useState<SoilGuide | null>(null);

  // Mobile Crop Breakdown Dialog State
  const [selectedCropForBreakdown, setSelectedCropForBreakdown] = useState<Crop | null>(null);
  const [isBreakdownModalOpen, setIsBreakdownModalOpen] = useState<boolean>(false);

  const openBreakdownModal = (crop: Crop) => {
    setSelectedCropForBreakdown(crop);
    setIsBreakdownModalOpen(true);
  };

  const closeBreakdownModal = () => {
    setIsBreakdownModalOpen(false);
    setSelectedCropForBreakdown(null);
  };

  // Form State
  const [name, setName] = useState('');
  const [localName, setLocalName] = useState('');
  const [botanicalName, setBotanicalName] = useState('');
  const [taxonomicFamily, setTaxonomicFamily] = useState('');
  const [category, setCategory] = useState<CategoryType>('ROOT');
  const [idealSoil, setIdealSoil] = useState<SoilType>('LOAM');
  const [suitableSoils, setSuitableSoils] = useState<SoilType[]>(['LOAM']);
  const [season, setSeason] = useState<SeasonType>('YEAR_ROUND');
  const [daysToHarvest, setDaysToHarvest] = useState(75);
  const [wateringIntervalDays, setWateringIntervalDays] = useState(2);
  const [fertilizeIntervalDays, setFertilizeIntervalDays] = useState(14);
  const [optimalPhMin, setOptimalPhMin] = useState(6.0);
  const [optimalPhMax, setOptimalPhMax] = useState(6.8);
  const [waterReq, setWaterReq] = useState(40);
  const [nVal, setNVal] = useState(80);
  const [pVal, setPVal] = useState(60);
  const [kVal, setKVal] = useState(90);
  const [stageSprout, setStageSprout] = useState(5);
  const [stageSeedling, setStageSeedling] = useState(12);
  const [stageVegetative, setStageVegetative] = useState(20);
  const [stageFlowering, setStageFlowering] = useState(16);
  const [stageHarvest, setStageHarvest] = useState(7);
  const [harvestIndicators, setHarvestIndicators] = useState('');
  const [description, setDescription] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [companionGoodStr, setCompanionGoodStr] = useState('Tomato, Lettuce');
  const [companionBadStr, setCompanionBadStr] = useState('Fennel');
  const [broadcastToMobile, setBroadcastToMobile] = useState<boolean>(true);
  const [isUploadingImage, setIsUploadingImage] = useState<boolean>(false);
  const [uploadStatus, setUploadStatus] = useState<string | null>(null);

  // Supabase Storage Manager State
  const [isStorageModalOpen, setIsStorageModalOpen] = useState<boolean>(false);
  const [isUploadingTestImage, setIsUploadingTestImage] = useState<boolean>(false);
  const [testUploadUrl, setTestUploadUrl] = useState<string | null>(null);
  const [isSyncingBatch, setIsSyncingBatch] = useState<boolean>(false);
  const [syncBatchProgress, setSyncBatchProgress] = useState<string | null>(null);
  const [syncBatchResult, setSyncBatchResult] = useState<string | null>(null);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const loadCrops = async () => {
    setLoading(true);
    const data = await apiService.getCrops();
    setCrops(data);
    setLoading(false);
  };

  useEffect(() => {
    loadCrops();
  }, []);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsUploadingImage(true);
    setUploadStatus('Uploading image to Supabase Storage...');
    try {
      const url = await apiService.uploadCropImage(file);
      setImageUrl(url);
      setUploadStatus('✔ Image uploaded successfully!');
      setTimeout(() => setUploadStatus(null), 3000);
    } catch (err) {
      setUploadStatus('✘ Failed to upload image');
    } finally {
      setIsUploadingImage(false);
    }
  };

  const handleTestUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setIsUploadingTestImage(true);
    setTestUploadUrl(null);
    try {
      const url = await apiService.uploadCropImage(file);
      setTestUploadUrl(url);
    } catch (err: any) {
      alert('Upload test failed: ' + (err?.message || err));
    } finally {
      setIsUploadingTestImage(false);
    }
  };

  const handleBatchSyncMetadataImages = async () => {
    setIsSyncingBatch(true);
    setSyncBatchProgress('Starting batch sync...');
    setSyncBatchResult(null);
    try {
      const res = await apiService.syncMetadataImagesToStorage((current, total, cropName) => {
        setSyncBatchProgress(`Uploading ${current}/${total}: ${cropName}...`);
      });
      setSyncBatchResult(`✔ Successfully synced ${res.results.length} official crop images to Supabase Storage!`);
      loadCrops();
    } catch (err: any) {
      setSyncBatchResult(`✘ Batch sync failed: ${err.message || err}`);
    } finally {
      setIsSyncingBatch(false);
      setSyncBatchProgress(null);
    }
  };

  const handleSaveCrop = async (e: React.FormEvent) => {
    e.preventDefault();

    const goodCompanions = companionGoodStr.split(',').map((s) => s.trim()).filter(Boolean);
    const badCompanions = companionBadStr.split(',').map((s) => s.trim()).filter(Boolean);

    const cropPayload = {
      name,
      localName: localName || undefined,
      botanicalName,
      taxonomicFamily: taxonomicFamily || undefined,
      category,
      idealSoil,
      suitableSoils,
      season,
      daysToHarvest,
      wateringIntervalDays,
      fertilizeIntervalDays,
      optimalPhMin,
      optimalPhMax,
      waterReqMmPerWeek: waterReq,
      npkRequirement: { nitrogen: nVal, phosphorus: pVal, potassium: kVal },
      growthStages: {
        sprout: stageSprout,
        seedling: stageSeedling,
        vegetative: stageVegetative,
        flowering: stageFlowering,
        harvest: stageHarvest,
      },
      companionCropsGood: goodCompanions,
      companionCropsBad: badCompanions,
      harvestIndicators: harvestIndicators || `Harvest at peak maturity around ${daysToHarvest} days`,
      description: description || `${name} (${localName || ''}) - Field research verified crop variety.`,
      imageUrl: imageUrl || '/metadata/crops_images/tomato.png',
    };

    if (editingCrop) {
      await apiService.updateCrop(editingCrop.id, cropPayload, broadcastToMobile);
      if (selectedCropForBreakdown?.id === editingCrop.id) {
        setSelectedCropForBreakdown({ ...editingCrop, ...cropPayload, id: editingCrop.id });
      }
    } else {
      await apiService.addCrop(cropPayload, broadcastToMobile);
    }
    closeModal();
    loadCrops();
  };

  const handleDeleteCrop = async (id: string) => {
    if (window.confirm('Are you sure you want to remove this crop from the DA knowledge base?')) {
      await apiService.deleteCrop(id);
      loadCrops();
    }
  };

  const openAddModal = () => {
    setEditingCrop(null);
    setName('');
    setLocalName('');
    setBotanicalName('');
    setTaxonomicFamily('');
    setCategory('ROOT');
    setIdealSoil('LOAM');
    setSuitableSoils(['LOAM']);
    setSeason('YEAR_ROUND');
    setDaysToHarvest(65);
    setWateringIntervalDays(2);
    setFertilizeIntervalDays(14);
    setOptimalPhMin(6.0);
    setOptimalPhMax(6.8);
    setWaterReq(40);
    setNVal(80);
    setPVal(60);
    setKVal(90);
    setStageSprout(5);
    setStageSeedling(12);
    setStageVegetative(20);
    setStageFlowering(16);
    setStageHarvest(7);
    setHarvestIndicators('');
    setDescription('');
    setImageUrl('');
    setCompanionGoodStr('Tomato, Lettuce');
    setCompanionBadStr('Fennel');
    setBroadcastToMobile(true);
    setUploadStatus(null);
    setIsAddModalOpen(true);
  };

  const openEditModal = (crop: Crop) => {
    setEditingCrop(crop);
    setName(crop.name);
    setLocalName(crop.localName || '');
    setBotanicalName(crop.botanicalName);
    setTaxonomicFamily(crop.taxonomicFamily || '');
    setCategory(crop.category);
    setIdealSoil(crop.idealSoil);
    setSuitableSoils(crop.suitableSoils && crop.suitableSoils.length > 0 ? crop.suitableSoils : [crop.idealSoil]);
    setSeason(crop.season);
    setDaysToHarvest(crop.daysToHarvest);
    setWateringIntervalDays(crop.wateringIntervalDays || 2);
    setFertilizeIntervalDays(crop.fertilizeIntervalDays || 14);
    setOptimalPhMin(crop.optimalPhMin || 6.0);
    setOptimalPhMax(crop.optimalPhMax || 6.8);
    setWaterReq(crop.waterReqMmPerWeek || 40);
    setNVal(crop.npkRequirement.nitrogen);
    setPVal(crop.npkRequirement.phosphorus);
    setKVal(crop.npkRequirement.potassium);
    setStageSprout(crop.growthStages?.sprout || 5);
    setStageSeedling(crop.growthStages?.seedling || 12);
    setStageVegetative(crop.growthStages?.vegetative || 20);
    setStageFlowering(crop.growthStages?.flowering || 16);
    setStageHarvest(crop.growthStages?.harvest || 7);
    setHarvestIndicators(crop.harvestIndicators || '');
    setDescription(crop.description || '');
    setImageUrl(crop.imageUrl);
    setCompanionGoodStr(crop.companionCropsGood ? crop.companionCropsGood.join(', ') : '');
    setCompanionBadStr(crop.companionCropsBad ? crop.companionCropsBad.join(', ') : '');
    setBroadcastToMobile(true);
    setUploadStatus(null);
    setIsAddModalOpen(true);
  };

  const closeModal = () => {
    setIsAddModalOpen(false);
    setEditingCrop(null);
  };

  const toggleSuitableSoil = (soil: SoilType) => {
    setSuitableSoils((prev) =>
      prev.includes(soil) ? (prev.length > 1 ? prev.filter((s) => s !== soil) : prev) : [...prev, soil]
    );
  };

  const filteredCrops = crops.filter(c => {
    const matchesSearch = c.name.toLowerCase().includes(search.toLowerCase()) ||
                          (c.localName && c.localName.toLowerCase().includes(search.toLowerCase())) ||
                          c.botanicalName.toLowerCase().includes(search.toLowerCase());
    const matchesCategory = categoryFilter === 'ALL' || c.category === categoryFilter;
    return matchesSearch && matchesCategory;
  });

  const filteredPests = pests.filter(p =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    p.localName.toLowerCase().includes(search.toLowerCase()) ||
    p.affectedCrops.some(ac => ac.toLowerCase().includes(search.toLowerCase()))
  );

  const filteredSoils = soils.filter(s =>
    s.title.toLowerCase().includes(search.toLowerCase()) ||
    s.localName.toLowerCase().includes(search.toLowerCase()) ||
    s.description.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Module Navigation Tabs Bar */}
      <div className="glass-card p-2 flex items-center justify-between gap-2 overflow-x-auto">
        <div className="flex items-center gap-1">
          <button
            onClick={() => setActiveTab('CROPS')}
            className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'CROPS'
                ? 'bg-emerald-500 text-white shadow-lg shadow-emerald-500/20'
                : 'text-slate-500 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <Sprout className="w-4 h-4" />
            <span>Crop Catalog ({crops.length})</span>
          </button>
          <button
            onClick={() => setActiveTab('PESTS')}
            className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'PESTS'
                ? 'bg-rose-500 text-white shadow-lg shadow-rose-500/20'
                : 'text-slate-500 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <Bug className="w-4 h-4" />
            <span>Pests & Diseases ({pests.length})</span>
          </button>
          <button
            onClick={() => setActiveTab('SOILS')}
            className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'SOILS'
                ? 'bg-amber-500 text-white shadow-lg shadow-amber-500/20'
                : 'text-slate-500 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <Mountain className="w-4 h-4" />
            <span>Soil Guides ({soils.length})</span>
          </button>
        </div>

        {activeTab === 'CROPS' && (
          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => {
                setTestUploadUrl(null);
                setIsStorageModalOpen(true);
              }}
              className="btn btn-secondary text-xs h-9 px-3 flex items-center gap-2 text-emerald-600 dark:text-emerald-400 border-emerald-500/40 hover:bg-emerald-500/10 transition-all"
              title="Supabase Storage Manager"
            >
              <Cloud className="w-4 h-4" />
              <span className="hidden sm:inline">Storage Manager</span>
              <span className="text-[10px] px-1.5 py-0.5 rounded-full bg-emerald-500/20 font-bold text-emerald-600 dark:text-emerald-400">
                Supabase Storage
              </span>
            </button>
            <button onClick={openAddModal} className="btn btn-primary text-xs h-9 px-3">
              <Plus className="w-4 h-4" />
              <span>Add Crop Record</span>
            </button>
          </div>
        )}
      </div>

      {/* Search & Action Controls Card */}
      <div className="glass-card p-4 sm:p-5 space-y-4">
        <div className="flex flex-col md:flex-row gap-3 items-center justify-between">
          <div className="relative w-full md:w-80">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder={`Search ${activeTab.toLowerCase()} by name or detail...`}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="input-field pl-9"
            />
          </div>

          {activeTab === 'CROPS' && (
            <div className="flex items-center gap-3 w-full md:w-auto justify-end">
              <div className="flex items-center gap-1.5 text-xs text-slate-400">
                <Filter className="w-3.5 h-3.5" />
                <span className="font-bold uppercase text-[10px]">Category:</span>
              </div>
              <select
                value={categoryFilter}
                onChange={(e) => setCategoryFilter(e.target.value)}
                className="input-field select-field py-1.5 text-xs w-40"
              >
                <option value="ALL">All Categories</option>
                <option value="ROOT">Root Vegetables</option>
                <option value="PODDED">Podded Legumes</option>
                <option value="FRUIT">Fruiting Vegetables</option>
                <option value="LEAFY">Leafy Greens</option>
              </select>
            </div>
          )}
        </div>
      </div>

      {/* TAB 1: CROPS CATALOG GRID */}
      {activeTab === 'CROPS' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredCrops.map((crop) => (
            <div
              key={crop.id}
              onClick={() => openBreakdownModal(crop)}
              className="glass-card overflow-hidden group hover:border-emerald-500/50 transition-all duration-200 flex flex-col justify-between cursor-pointer"
            >
              <div>
                <div className="h-48 relative overflow-hidden bg-slate-900">
                  <img
                    src={crop.imageUrl}
                    alt={crop.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                    onError={(e) => {
                      (e.target as HTMLImageElement).src = '/metadata/crops_images/tomato.png';
                    }}
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/20 to-transparent" />
                  <div className="absolute top-3 right-3 flex items-center gap-1.5">
                    <Badge variant="purple">{crop.category}</Badge>
                  </div>
                  <div className="absolute bottom-3 left-4 right-4">
                    <h3 className="text-lg font-extrabold text-white leading-tight flex items-baseline gap-1.5">
                      <span>{crop.name}</span>
                      {crop.localName && (
                        <span className="text-sm text-emerald-300 font-medium">({crop.localName})</span>
                      )}
                    </h3>
                    <p className="text-xs text-emerald-400 italic font-mono mt-0.5">
                      {crop.botanicalName}
                    </p>
                  </div>
                </div>

                {/* Base Card Info: Informational notice that specific agronomic parameters vary by variety */}
                <div className="p-4 space-y-3 text-xs">
                  <div className="p-2.5 rounded-xl bg-slate-800/40 border border-slate-700/50 text-[11px] text-slate-300 space-y-1">
                    <div className="flex items-center gap-1.5 font-bold text-emerald-400">
                      <Layers className="w-3.5 h-3.5 text-emerald-400" />
                      <span>Varietal Agronomics</span>
                    </div>
                    <p className="text-[10px] text-slate-400 leading-relaxed">
                      Harvest timelines, soil tolerance, and watering cycles vary per cultivar variety. Click below to inspect specific varietal breakdowns.
                    </p>
                  </div>

                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      openBreakdownModal(crop);
                    }}
                    className="w-full py-2.5 px-3 rounded-xl bg-emerald-500/15 hover:bg-emerald-500/25 text-emerald-300 font-bold text-xs border border-emerald-500/40 transition flex items-center justify-center gap-2 shadow-sm group-hover:bg-emerald-500 group-hover:text-white"
                  >
                    <Smartphone className="w-4 h-4" />
                    <span>Inspect Varietal Breakdown</span>
                  </button>
                </div>
              </div>

              <div className="px-4 pb-3 pt-1 flex items-center justify-between border-t border-slate-100 dark:border-slate-800/60 text-xs">
                <span className="text-[10px] text-slate-400 font-mono">
                  Base Crop Species
                </span>
                <div className="flex items-center gap-1">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      openEditModal(crop);
                    }}
                    className="p-1.5 rounded-lg text-slate-400 hover:text-emerald-400 hover:bg-slate-800 transition"
                    title="Edit Crop Profile"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDeleteCrop(crop.id);
                    }}
                    className="p-1.5 rounded-lg text-slate-400 hover:text-rose-400 hover:bg-rose-950/30 transition"
                    title="Delete Crop"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* TAB 2: PESTS & DISEASES GRID */}
      {activeTab === 'PESTS' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredPests.map((pest) => (
            <div
              key={pest.id}
              onClick={() => setSelectedPest(pest)}
              className="glass-card overflow-hidden cursor-pointer group hover:border-rose-500/50 transition-all duration-200 flex flex-col justify-between"
            >
              <div>
                <div className="h-44 relative overflow-hidden bg-slate-900">
                  <img
                    src={pest.imageUrl}
                    alt={pest.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/30 to-transparent" />
                  <div className="absolute top-3 right-3">
                    <Badge variant="danger">{pest.category}</Badge>
                  </div>
                  <div className="absolute bottom-3 left-4 right-4">
                    <h3 className="text-base font-extrabold text-white leading-tight">
                      {pest.name}
                    </h3>
                    <p className="text-xs text-rose-300 font-medium">
                      {pest.localName}
                    </p>
                  </div>
                </div>

                <div className="p-4 space-y-2.5 text-xs">
                  <p className="text-slate-400 italic text-[11px] font-mono">{pest.scientificName}</p>
                  <p className="text-slate-700 dark:text-slate-300 font-medium">
                    <strong className="text-slate-900 dark:text-white">Affected Crops:</strong> {pest.affectedCrops.join(', ')}
                  </p>
                  <div className="p-2.5 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-700 dark:text-emerald-300 text-[11px]">
                    <strong>🌿 Organic:</strong> {pest.organicControl?.length ? pest.organicControl.slice(0, 60) : pest.organicControl}...
                  </div>
                </div>
              </div>
              <div className="p-3 bg-slate-50 dark:bg-slate-900/40 text-center border-t border-slate-200/60 dark:border-slate-800 text-xs font-bold text-rose-500">
                Click to inspect full treatment guide →
              </div>
            </div>
          ))}
        </div>
      )}

      {/* TAB 3: SOIL GUIDES GRID */}
      {activeTab === 'SOILS' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredSoils.map((soil) => (
            <div
              key={soil.soilType}
              onClick={() => setSelectedSoil(soil)}
              className="glass-card overflow-hidden cursor-pointer group hover:border-amber-500/50 transition-all duration-200 flex flex-col justify-between"
            >
              <div>
                <div className="h-44 relative overflow-hidden bg-slate-900">
                  <img
                    src={soil.imageUrl}
                    alt={soil.title}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/30 to-transparent" />
                  <div className="absolute top-3 right-3">
                    <span className="px-2.5 py-1 rounded-full text-[10px] font-bold text-white uppercase bg-amber-600/90 backdrop-blur-sm">
                      {soil.soilType}
                    </span>
                  </div>
                  <div className="absolute bottom-3 left-4 right-4">
                    <h3 className="text-base font-extrabold text-white leading-tight">
                      {soil.title}
                    </h3>
                    <p className="text-xs text-amber-300 font-medium">
                      {soil.localName}
                    </p>
                  </div>
                </div>

                <div className="p-4 space-y-3 text-xs">
                  <p className="text-slate-600 dark:text-slate-300 line-clamp-2">{soil.description}</p>
                  <div className="flex items-center justify-between p-2 rounded-lg bg-slate-100 dark:bg-slate-800/60 text-[11px]">
                    <span>💧 Drainage: <strong className="text-emerald-500">{soil.drainageSpeed}</strong></span>
                    <span>🧪 pH: <strong className="text-amber-500">{soil.phRange}</strong></span>
                  </div>
                </div>
              </div>
              <div className="p-3 bg-slate-50 dark:bg-slate-900/40 text-center border-t border-slate-200/60 dark:border-slate-800 text-xs font-bold text-amber-500">
                Click for suitable crops & texture profile →
              </div>
            </div>
          ))}
        </div>
      )}



      {/* Pest Detail Modal */}
      {selectedPest && (
        <Modal
          isOpen={Boolean(selectedPest)}
          onClose={() => setSelectedPest(null)}
          title={selectedPest.name}
          maxWidth="md"
        >
          <div className="space-y-4 text-xs">
            <div className="h-48 relative rounded-xl overflow-hidden bg-slate-900">
              <img src={selectedPest.imageUrl} alt={selectedPest.name} className="w-full h-full object-cover" />
              <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-transparent to-transparent" />
              <div className="absolute bottom-3 left-4">
                <span className="text-rose-300 font-bold text-sm">{selectedPest.localName}</span>
                <p className="text-slate-300 italic text-xs">{selectedPest.scientificName}</p>
              </div>
            </div>

            <div className="space-y-3">
              <div className="p-3 rounded-xl bg-[#1D2429] border border-[#38434D] space-y-1">
                <strong className="text-[#F4F4F4]">Affected Crops:</strong>
                <p className="text-[#C7D0D8]">{selectedPest.affectedCrops.join(', ')}</p>
              </div>

              <div className="p-3.5 rounded-xl bg-[#4CAF50]/15 border border-[#4CAF50]/30 text-[#A5D6A7] space-y-1">
                <strong className="text-[#4CAF50] font-bold">🌿 Organic Control:</strong>
                <p>{selectedPest.organicControl}</p>
              </div>

              <div className="p-3.5 rounded-xl bg-[#E76F51]/15 border border-[#E76F51]/30 text-[#F4A261] space-y-1">
                <strong className="text-[#E76F51] font-bold">🧪 Chemical Control:</strong>
                <p>{selectedPest.chemicalControl}</p>
              </div>

              <div className="p-3.5 rounded-xl bg-[#00BCD4]/15 border border-[#00BCD4]/30 text-[#80DEEA] space-y-1">
                <strong className="text-[#00BCD4] font-bold">🛡️ Prevention Tips:</strong>
                <p>{selectedPest.preventionTips}</p>
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <button onClick={() => setSelectedPest(null)} className="btn btn-secondary text-xs h-9">Close Guide</button>
            </div>
          </div>
        </Modal>
      )}

      {/* Soil Detail Modal */}
      {selectedSoil && (
        <Modal
          isOpen={Boolean(selectedSoil)}
          onClose={() => setSelectedSoil(null)}
          title={selectedSoil.title}
          maxWidth="md"
        >
          <div className="space-y-4 text-xs">
            <div className="h-48 relative rounded-xl overflow-hidden bg-[#112230]">
              <img src={selectedSoil.imageUrl} alt={selectedSoil.title} className="w-full h-full object-cover" />
              <div className="absolute inset-0 bg-gradient-to-t from-[#112230] via-transparent to-transparent" />
              <div className="absolute bottom-3 left-4">
                <span className="text-[#F4A261] font-bold text-sm">{selectedSoil.localName}</span>
              </div>
            </div>

            <div className="space-y-3">
              <p className="text-[#C7D0D8] leading-relaxed">{selectedSoil.description}</p>
              
              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 rounded-xl bg-[#1D2429] border border-[#38434D]">
                  <span className="text-[10px] text-[#8A9BA8] font-bold uppercase block">Drainage Speed</span>
                  <strong className="text-[#4CAF50] font-bold text-xs">{selectedSoil.drainageSpeed}</strong>
                </div>
                <div className="p-3 rounded-xl bg-[#1D2429] border border-[#38434D]">
                  <span className="text-[10px] text-[#8A9BA8] font-bold uppercase block">pH Range</span>
                  <strong className="text-[#F4A261] font-bold text-xs">{selectedSoil.phRange}</strong>
                </div>
              </div>

              <div className="p-3 rounded-xl bg-[#F4A261]/15 border border-[#F4A261]/30 text-[#F4A261]">
                <strong>Texture Profile:</strong> {selectedSoil.texture}
              </div>

              <div className="p-3 rounded-xl bg-[#4CAF50]/15 border border-[#4CAF50]/30 text-[#A5D6A7]">
                <strong>Optimal Crop Matches:</strong> {selectedSoil.bestCrops.join(', ')}
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <button onClick={() => setSelectedSoil(null)} className="btn btn-secondary text-xs h-9">Close Guide</button>
            </div>
          </div>
        </Modal>
      )}

      {/* Add / Edit Crop Modal — Comprehensive Crop Uploader & Manager */}
      {isAddModalOpen && (
        <Modal
          isOpen={isAddModalOpen}
          onClose={closeModal}
          title={editingCrop ? `Edit Crop: ${editingCrop.name}` : 'Crop Uploader & Agronomic Suite'}
          maxWidth="6xl"
          position="top"
        >
          <form onSubmit={handleSaveCrop} className="space-y-4 text-xs">
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
              {/* Left Column: Form Controls (7 cols) */}
              <div className="lg:col-span-7 space-y-4 max-h-[75vh] overflow-y-auto pr-2 custom-scrollbar">
                {/* 1. Supabase Storage Image Upload Section */}
                <div className="p-3.5 rounded-2xl border border-emerald-500/30 bg-emerald-500/5 space-y-3">
                  <div className="flex flex-wrap items-center justify-between gap-2">
                    <span className="font-bold text-slate-800 dark:text-slate-200 flex items-center gap-1.5 text-xs">
                      <UploadCloud className="w-4 h-4 text-emerald-500" />
                      <span>Crop Image (Supabase Storage)</span>
                    </span>
                    <div className="flex items-center gap-2">
                      <span className="text-[10px] text-emerald-600 dark:text-emerald-400 font-bold bg-emerald-500/10 border border-emerald-500/30 px-2 py-0.5 rounded-full flex items-center gap-1">
                        <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
                        Supabase Storage (Free • No Card)
                      </span>
                      <button
                        type="button"
                        onClick={() => {
                          setTestUploadUrl(null);
                          setIsStorageModalOpen(true);
                        }}
                        className="text-[11px] text-emerald-600 dark:text-emerald-400 hover:underline font-semibold flex items-center gap-1"
                      >
                        <Settings className="w-3 h-3" />
                        <span>Storage Manager</span>
                      </button>
                    </div>
                  </div>

                  {/* File Upload Controls */}
                  <div className="flex items-center gap-3">
                    <input
                      ref={fileInputRef}
                      type="file"
                      accept="image/*"
                      onChange={handleFileUpload}
                      className="hidden"
                    />
                    <button
                      type="button"
                      onClick={() => fileInputRef.current?.click()}
                      disabled={isUploadingImage}
                      className="btn btn-secondary text-xs h-9 px-3 flex items-center gap-1.5"
                    >
                      {isUploadingImage ? (
                        <RefreshCw className="w-4 h-4 animate-spin text-emerald-500" />
                      ) : (
                        <UploadCloud className="w-4 h-4 text-emerald-500" />
                      )}
                      <span>{isUploadingImage ? 'Uploading...' : 'Choose Image File'}</span>
                    </button>
                    <span className="text-[10px] text-slate-400">PNG, WebP, JPG up to 5MB</span>
                  </div>

                  {uploadStatus && (
                    <p className={`text-[11px] font-semibold ${uploadStatus.startsWith('✔') ? 'text-emerald-500' : 'text-rose-500'}`}>
                      {uploadStatus}
                    </p>
                  )}

                  {/* Direct Image URL input */}
                  <div>
                    <label className="block font-semibold text-slate-600 dark:text-slate-300 mb-1 text-[11px]">
                      Or Direct Image URL / Public Link:
                    </label>
                    <div className="relative">
                      <input
                        type="url"
                        value={imageUrl}
                        onChange={(e) => setImageUrl(e.target.value)}
                        placeholder="https://ojilvcglpzbtpjxguhzj.supabase.co/... or /metadata/crops_images/tomato.png"
                        className="input-field pr-8 text-xs font-mono"
                      />
                      {imageUrl && (
                        <button
                          type="button"
                          onClick={() => window.open(imageUrl, '_blank')}
                          className="absolute right-2 top-2.5 text-slate-400 hover:text-emerald-500"
                          title="Open Image"
                        >
                          <ExternalLink className="w-3.5 h-3.5" />
                        </button>
                      )}
                    </div>
                  </div>

                  {/* Preset Official Metadata Image shortcuts for quick testing */}
                  <div>
                    <span className="text-[10px] text-slate-400 block mb-1 font-semibold">Official MapTanim Metadata Image Presets:</span>
                    <div className="flex flex-wrap gap-1.5">
                      {OFFICIAL_METADATA_PRESETS.map((preset) => (
                        <button
                          key={preset.label}
                          type="button"
                          onClick={() => setImageUrl(preset.url)}
                          className={`px-2 py-0.5 rounded-lg text-[10px] font-medium border transition ${
                            imageUrl === preset.url
                              ? 'bg-emerald-500 text-white border-emerald-500'
                              : 'bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300 border-slate-200 dark:border-slate-700 hover:border-emerald-500'
                          }`}
                        >
                          {preset.label}
                        </button>
                      ))}
                    </div>
                  </div>
                </div>

                {/* 2. Crop Identity & Botanical Classification */}
                <div className="p-3.5 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 space-y-3">
                  <h4 className="font-bold text-slate-800 dark:text-slate-200 text-xs">
                    🌱 Taxonomic & Local Identity (Field Research Standards)
                  </h4>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Common Name *</label>
                      <input
                        type="text"
                        required
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="e.g. Tomato, Carrot, Bitter Gourd"
                        className="input-field"
                      />
                    </div>
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Local / Filipino Name</label>
                      <input
                        type="text"
                        value={localName}
                        onChange={(e) => setLocalName(e.target.value)}
                        placeholder="e.g. Kamatis, Karot, Ampalaya"
                        className="input-field"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Botanical Name (Scientific) *</label>
                      <input
                        type="text"
                        required
                        value={botanicalName}
                        onChange={(e) => setBotanicalName(e.target.value)}
                        placeholder="e.g. Solanum lycopersicum"
                        className="input-field italic"
                      />
                    </div>
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Taxonomic Family</label>
                      <input
                        type="text"
                        value={taxonomicFamily}
                        onChange={(e) => setTaxonomicFamily(e.target.value)}
                        placeholder="e.g. Solanaceae, Cucurbitaceae"
                        className="input-field"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Horticultural Category *</label>
                      <select
                        value={category}
                        onChange={(e) => setCategory(e.target.value as CategoryType)}
                        className="input-field select-field"
                      >
                        <option value="LEAFY">LEAFY (Pechay, Lettuce, Cabbage)</option>
                        <option value="ROOT">ROOT (Carrot, Radish)</option>
                        <option value="BULB">BULB (Onion, Garlic)</option>
                        <option value="FRUIT">FRUIT (Tomato, Eggplant, Pepper, Squash)</option>
                        <option value="PODDED">PODDED / LEGUME (String Beans, Okra)</option>
                        <option value="STEM">STEM / GRAIN (Corn, Celery)</option>
                        <option value="TUBER">TUBER (Potato, Camote)</option>
                      </select>
                    </div>
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Planting Season</label>
                      <select
                        value={season}
                        onChange={(e) => setSeason(e.target.value as SeasonType)}
                        className="input-field select-field"
                      >
                        <option value="YEAR_ROUND">YEAR_ROUND (Taon-taon)</option>
                        <option value="DRY">DRY SEASON (Tag-araw)</option>
                        <option value="WET">WET SEASON (Tag-ulan)</option>
                      </select>
                    </div>
                  </div>
                </div>

                {/* 3. Agronomic Lifecycle & Irrigation */}
                <div className="p-3.5 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 space-y-3">
                  <h4 className="font-bold text-slate-800 dark:text-slate-200 text-xs flex items-center gap-1.5">
                    <Calendar className="w-4 h-4 text-blue-500" />
                    <span>Agronomic Timeline & Irrigation (Offline Mobile Schedules)</span>
                  </h4>

                  <div className="grid grid-cols-3 gap-3">
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Maturity (Days) *</label>
                      <input
                        type="number"
                        min="15"
                        max="300"
                        required
                        value={daysToHarvest}
                        onChange={(e) => setDaysToHarvest(Number(e.target.value))}
                        className="input-field font-mono font-bold"
                      />
                    </div>
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Water Every (Days) *</label>
                      <input
                        type="number"
                        min="1"
                        max="14"
                        required
                        value={wateringIntervalDays}
                        onChange={(e) => {
                          const v = Number(e.target.value);
                          setWateringIntervalDays(v);
                          setWaterReq(Math.round((7 / (v || 1)) * 12));
                        }}
                        className="input-field font-mono font-bold"
                      />
                    </div>
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Fertilize Every (Days)</label>
                      <input
                        type="number"
                        min="3"
                        max="60"
                        required
                        value={fertilizeIntervalDays}
                        onChange={(e) => setFertilizeIntervalDays(Number(e.target.value))}
                        className="input-field font-mono font-bold"
                      />
                    </div>
                  </div>

                  {/* 5 Growth Stages Duration */}
                  <div className="pt-2 border-t border-slate-200 dark:border-slate-700">
                    <span className="text-[10px] text-slate-400 block mb-1.5 font-bold uppercase">
                      Growth Stages Duration (Days Breakdown):
                    </span>
                    <div className="grid grid-cols-5 gap-2 text-center">
                      <div>
                        <span className="text-[9px] text-slate-500 block font-bold">1. Sprout</span>
                        <input
                          type="number"
                          value={stageSprout}
                          onChange={(e) => setStageSprout(Number(e.target.value))}
                          className="input-field text-center font-mono py-1"
                        />
                      </div>
                      <div>
                        <span className="text-[9px] text-slate-500 block font-bold">2. Seedling</span>
                        <input
                          type="number"
                          value={stageSeedling}
                          onChange={(e) => setStageSeedling(Number(e.target.value))}
                          className="input-field text-center font-mono py-1"
                        />
                      </div>
                      <div>
                        <span className="text-[9px] text-slate-500 block font-bold">3. Vegetative</span>
                        <input
                          type="number"
                          value={stageVegetative}
                          onChange={(e) => setStageVegetative(Number(e.target.value))}
                          className="input-field text-center font-mono py-1"
                        />
                      </div>
                      <div>
                        <span className="text-[9px] text-slate-500 block font-bold">4. Flowering</span>
                        <input
                          type="number"
                          value={stageFlowering}
                          onChange={(e) => setStageFlowering(Number(e.target.value))}
                          className="input-field text-center font-mono py-1"
                        />
                      </div>
                      <div>
                        <span className="text-[9px] text-slate-500 block font-bold">5. Harvest</span>
                        <input
                          type="number"
                          value={stageHarvest}
                          onChange={(e) => setStageHarvest(Number(e.target.value))}
                          className="input-field text-center font-mono py-1"
                        />
                      </div>
                    </div>
                  </div>
                </div>

                {/* 4. Soil Compatibility & NPK Bioavailability */}
                <div className="p-3.5 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 space-y-3">
                  <h4 className="font-bold text-slate-800 dark:text-slate-200 text-xs flex items-center gap-1.5">
                    <Sprout className="w-4 h-4 text-amber-500" />
                    <span>Soil & Nutrient Bioavailability (Soil Grid Mapping)</span>
                  </h4>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Primary Ideal Soil *</label>
                      <select
                        value={idealSoil}
                        onChange={(e) => setIdealSoil(e.target.value as SoilType)}
                        className="input-field select-field font-bold"
                      >
                        {ALL_SOIL_OPTIONS.map((st) => (
                          <option key={st} value={st}>{st}</option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Optimal pH Range (Min – Max)</label>
                      <div className="grid grid-cols-2 gap-2">
                        <input
                          type="number"
                          step="0.1"
                          value={optimalPhMin}
                          onChange={(e) => setOptimalPhMin(Number(e.target.value))}
                          placeholder="Min (e.g. 5.5)"
                          className="input-field font-mono"
                        />
                        <input
                          type="number"
                          step="0.1"
                          value={optimalPhMax}
                          onChange={(e) => setOptimalPhMax(Number(e.target.value))}
                          placeholder="Max (e.g. 6.8)"
                          className="input-field font-mono"
                        />
                      </div>
                    </div>
                  </div>

                  <div>
                    <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1.5">Suitable Soil Types (Multi-select)</label>
                    <div className="flex flex-wrap gap-2">
                      {ALL_SOIL_OPTIONS.map((soil) => {
                        const isSelected = suitableSoils.includes(soil);
                        return (
                          <button
                            key={soil}
                            type="button"
                            onClick={() => toggleSuitableSoil(soil)}
                            className={`px-3 py-1 rounded-xl text-[11px] font-bold border transition ${
                              isSelected
                                ? 'bg-amber-500 text-white border-amber-500 shadow-sm'
                                : 'bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 border-slate-200 dark:border-slate-700 hover:border-amber-500'
                            }`}
                          >
                            {isSelected && <Check className="w-3 h-3 inline-block mr-1" />}
                            {soil}
                          </button>
                        );
                      })}
                    </div>
                  </div>

                  {/* NPK Ratio */}
                  <div>
                    <span className="text-[10px] text-slate-400 block mb-1 font-bold uppercase">
                      Recommended N-P-K Nutrient Ratio (kg/ha):
                    </span>
                    <div className="grid grid-cols-3 gap-2">
                      <div>
                        <span className="text-[10px] text-slate-400">Nitrogen (N)</span>
                        <input
                          type="number"
                          value={nVal}
                          onChange={(e) => setNVal(Number(e.target.value))}
                          className="input-field font-mono"
                        />
                      </div>
                      <div>
                        <span className="text-[10px] text-slate-400">Phosphorus (P)</span>
                        <input
                          type="number"
                          value={pVal}
                          onChange={(e) => setPVal(Number(e.target.value))}
                          className="input-field font-mono"
                        />
                      </div>
                      <div>
                        <span className="text-[10px] text-slate-400">Potassium (K)</span>
                        <input
                          type="number"
                          value={kVal}
                          onChange={(e) => setKVal(Number(e.target.value))}
                          className="input-field font-mono"
                        />
                      </div>
                    </div>
                  </div>
                </div>

                {/* 5. Companion Planting & DSS Rules */}
                <div className="p-3.5 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 space-y-3">
                  <h4 className="font-bold text-slate-800 dark:text-slate-200 text-xs flex items-center gap-1.5">
                    <Layers className="w-4 h-4 text-emerald-500" />
                    <span>Companion Planting & Decision Support (DSS Rules)</span>
                  </h4>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                        Good Companions (Comma separated)
                      </label>
                      <input
                        type="text"
                        value={companionGoodStr}
                        onChange={(e) => setCompanionGoodStr(e.target.value)}
                        placeholder="e.g. Tomato, Lettuce, Carrot"
                        className="input-field"
                      />
                    </div>
                    <div>
                      <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                        Incompatible Plants (Comma separated)
                      </label>
                      <input
                        type="text"
                        value={companionBadStr}
                        onChange={(e) => setCompanionBadStr(e.target.value)}
                        placeholder="e.g. Fennel, Cabbage"
                        className="input-field"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Harvest Indicators</label>
                    <input
                      type="text"
                      value={harvestIndicators}
                      onChange={(e) => setHarvestIndicators(e.target.value)}
                      placeholder="e.g. Fruit turns glossy purple; firm flesh; calyx green"
                      className="input-field"
                    />
                  </div>

                  <div>
                    <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Extension & Agronomic Notes</label>
                    <textarea
                      rows={2}
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      placeholder="Agronomic details, spacing tips, field cultivation recommendations..."
                      className="input-field resize-none"
                    />
                  </div>
                </div>
              </div>

              {/* Right Column: Live Mobile UI Breakdown Preview & System Broadcast (5 cols) */}
              <div className="lg:col-span-5 space-y-4 flex flex-col justify-between max-h-[75vh] overflow-y-auto pl-1 pr-1 custom-scrollbar">
                <div className="space-y-4">
                  {/* Real-time Interactive Mobile UI Breakdown Preview */}
                  <MobileCropBreakdownPreview
                    name={name}
                    localName={localName}
                    botanicalName={botanicalName}
                    taxonomicFamily={taxonomicFamily}
                    category={category}
                    idealSoil={idealSoil}
                    suitableSoils={suitableSoils}
                    season={season}
                    daysToHarvest={daysToHarvest}
                    wateringIntervalDays={wateringIntervalDays}
                    optimalPhMin={optimalPhMin}
                    optimalPhMax={optimalPhMax}
                    nVal={nVal}
                    pVal={pVal}
                    kVal={kVal}
                    stageSprout={stageSprout}
                    stageSeedling={stageSeedling}
                    stageVegetative={stageVegetative}
                    stageFlowering={stageFlowering}
                    stageHarvest={stageHarvest}
                    harvestIndicators={harvestIndicators}
                    description={description}
                    imageUrl={imageUrl}
                    companionGoodStr={companionGoodStr}
                    companionBadStr={companionBadStr}
                  />

                  {/* Architecture & Quota Protection Info */}
                  <div className="p-3 rounded-2xl bg-slate-100 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800 text-[11px] space-y-1.5">
                    <div className="flex items-center gap-1.5 font-bold text-slate-700 dark:text-slate-300 text-xs">
                      <ShieldCheck className="w-4 h-4 text-emerald-500" />
                      <span>Zero-Code Mobile Sync</span>
                    </div>
                    <p className="text-slate-500 dark:text-slate-400 leading-relaxed text-[10px]">
                      When saved, Supabase stores relational agronomic rules and image assets. The mobile app automatically syncs new and updated crops on launch or via System Update alert.
                    </p>
                  </div>

                  {/* System Update Broadcast Toggle */}
                  <div className="p-3 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 text-[11px] space-y-1.5">
                    <label className="flex items-start gap-2 cursor-pointer select-none">
                      <input
                        type="checkbox"
                        checked={broadcastToMobile}
                        onChange={(e) => setBroadcastToMobile(e.target.checked)}
                        className="rounded text-emerald-600 focus:ring-emerald-500 mt-0.5"
                      />
                      <div>
                        <span className="font-bold text-emerald-900 dark:text-emerald-200 block text-xs">
                          📢 Broadcast System Update Alert
                        </span>
                        <span className="text-[10px] text-slate-500 dark:text-slate-400 leading-snug block mt-0.5">
                          Alerts mobile farmers to download newly added or modified crops directly into their local offline map.
                        </span>
                      </div>
                    </label>
                  </div>
                </div>

                {/* Form Action Buttons */}
                <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-200 dark:border-slate-800">
                  <button type="button" onClick={closeModal} className="btn btn-secondary text-xs h-10 px-4">
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={isUploadingImage}
                    className="btn btn-primary text-xs h-10 px-5 flex items-center gap-2 shadow-lg shadow-emerald-500/25"
                  >
                    <Sparkles className="w-4 h-4" />
                    <span>{editingCrop ? 'Save Changes' : 'Publish Crop Record'}</span>
                  </button>
                </div>
              </div>
            </div>
          </form>
        </Modal>
      )}

      {/* Storage Gateway Modal (Supabase Storage) */}
      {isStorageModalOpen && (
        <Modal
          isOpen={isStorageModalOpen}
          onClose={() => setIsStorageModalOpen(false)}
          title="Image Storage Manager (Supabase Storage)"
          maxWidth="max-w-2xl"
        >
          <div className="space-y-5 text-xs">
            {/* Supabase Free Storage Active Banner */}
            <div className="p-3.5 rounded-2xl bg-gradient-to-br from-emerald-500/15 via-emerald-500/5 to-transparent border border-emerald-500/30 flex items-start gap-3">
              <div className="p-2 rounded-xl bg-emerald-500 text-white shrink-0 shadow-md shadow-emerald-500/20">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <div className="space-y-1">
                <h4 className="font-bold text-slate-800 dark:text-slate-100 text-sm flex items-center gap-2">
                  <span>Supabase Storage (100% Free • Connected)</span>
                  <span className="text-[10px] px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-600 dark:text-emerald-400 font-bold">
                    Active Storage
                  </span>
                </h4>
                <p className="text-slate-600 dark:text-slate-400 text-[11px] leading-relaxed">
                  Your connected Supabase project (<code className="font-mono text-emerald-500">ojilvcglpzbtpjxguhzj.supabase.co</code>) includes <strong>1 GB free image storage</strong> in the <code className="font-mono text-emerald-500">crop-images</code> bucket with permanent public URLs.
                </p>
              </div>
            </div>

            {/* Live Upload Verification Test */}
            <div className="p-3.5 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/50 space-y-2.5">
              <span className="font-bold text-slate-800 dark:text-slate-200 text-xs flex items-center gap-1.5">
                <UploadCloud className="w-4 h-4 text-emerald-500" />
                <span>Verify Live Storage Upload</span>
              </span>
              <p className="text-[11px] text-slate-500 dark:text-slate-400">
                Upload a test image to confirm that Supabase storage writes and public image URLs are functioning end-to-end.
              </p>

              <div className="flex items-center gap-3">
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleTestUpload}
                  disabled={isUploadingTestImage}
                  className="text-xs file:mr-2 file:py-1.5 file:px-3 file:rounded-xl file:border-0 file:text-xs file:font-semibold file:bg-emerald-500 file:text-white hover:file:bg-emerald-600 cursor-pointer"
                />
                {isUploadingTestImage && (
                  <span className="text-emerald-500 flex items-center gap-1 font-semibold text-[11px]">
                    <RefreshCw className="w-3.5 h-3.5 animate-spin" /> Uploading to Supabase Storage...
                  </span>
                )}
              </div>

              {testUploadUrl && (
                <div className="p-2.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center gap-3">
                  <img
                    src={testUploadUrl}
                    alt="Test upload"
                    className="w-10 h-10 rounded-lg object-cover border border-emerald-500/30 shrink-0"
                  />
                  <div className="flex-1 min-w-0">
                    <span className="text-[10px] font-bold text-emerald-700 dark:text-emerald-300 block">
                      ✔ Upload verified! Public Storage URL:
                    </span>
                    <a
                      href={testUploadUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="text-[10px] font-mono text-emerald-600 dark:text-emerald-400 hover:underline truncate block"
                    >
                      {testUploadUrl}
                    </a>
                  </div>
                </div>
              )}
            </div>

            {/* Bulk Sync All 15 Metadata Images */}
            <div className="p-3.5 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 space-y-2.5">
              <div className="flex items-center justify-between">
                <span className="font-bold text-slate-800 dark:text-slate-100 text-xs flex items-center gap-1.5">
                  <Sparkles className="w-4 h-4 text-emerald-500" />
                  <span>Sync Official Metadata Images to Supabase</span>
                </span>
                <span className="text-[10px] bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 font-bold px-2 py-0.5 rounded-full">
                  Authentic Assets
                </span>
              </div>
              <p className="text-[11px] text-slate-600 dark:text-slate-400 leading-relaxed">
                Uploads all 15 authentic MapTanim crop illustrations directly into your Supabase Storage (<code className="font-mono text-emerald-600 dark:text-emerald-400">crop-images</code>) and updates the database URLs automatically.
              </p>
              <div className="flex items-center gap-3">
                <button
                  type="button"
                  onClick={handleBatchSyncMetadataImages}
                  disabled={isSyncingBatch}
                  className="btn btn-primary text-xs h-9 px-4 flex items-center gap-1.5 shadow-md shadow-emerald-500/20"
                >
                  {isSyncingBatch ? (
                    <RefreshCw className="w-4 h-4 animate-spin" />
                  ) : (
                    <UploadCloud className="w-4 h-4" />
                  )}
                  <span>{isSyncingBatch ? (syncBatchProgress || 'Syncing...') : 'Upload All 15 Metadata Images to Supabase'}</span>
                </button>
              </div>
              {syncBatchResult && (
                <p className={`text-[11px] font-semibold ${syncBatchResult.startsWith('✔') ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-500'}`}>
                  {syncBatchResult}
                </p>
              )}
            </div>

            {/* Storage Setup Guidance */}
            <div className="p-3.5 rounded-2xl bg-slate-100 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 space-y-2">
              <span className="font-bold text-slate-700 dark:text-slate-300 text-xs flex items-center gap-1.5">
                <Terminal className="w-4 h-4 text-emerald-500" />
                <span>Supabase Storage Bucket Configuration</span>
              </span>
              <div className="space-y-2 text-[11px] text-slate-600 dark:text-slate-400 leading-relaxed">
                <div className="p-2 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-900 dark:text-emerald-200">
                  <strong>✅ Zero-Card Supabase Storage:</strong> Supabase includes 1GB free storage and is directly connected. Ensure migration <code className="font-mono text-emerald-600 dark:text-emerald-300">017_setup_supabase_storage_bucket.sql</code> has been run in your Supabase SQL Editor to grant public image permissions, then click <em>Upload All 15 Metadata Images to Supabase</em> above!
                </div>
              </div>
            </div>

            {/* Modal Actions */}
            <div className="flex items-center justify-end pt-2 border-t border-slate-200 dark:border-slate-800">
              <button
                type="button"
                onClick={() => setIsStorageModalOpen(false)}
                className="btn btn-secondary text-xs h-9 px-4"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Full Mobile UI Crop Breakdown Modal (Recreating CropDetailDialog.kt) */}
      <CropBreakdownModal
        crop={selectedCropForBreakdown}
        isOpen={isBreakdownModalOpen}
        onClose={closeBreakdownModal}
        onEdit={(c) => {
          closeBreakdownModal();
          openEditModal(c);
        }}
      />
    </div>
  );
};
