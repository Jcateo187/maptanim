import React, { useEffect, useState } from 'react';
import { Search, Plus, Edit2, Trash2, Sprout, Droplets, Calendar, Filter, Bug, Mountain } from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { Crop, SoilType, SeasonType, CategoryType, PestGuide, SoilGuide } from '../types';
import { apiService } from '../services/api';
import { MOCK_PESTS, MOCK_SOILS } from '../services/mockData';

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

  // Form State
  const [name, setName] = useState('');
  const [botanicalName, setBotanicalName] = useState('');
  const [category, setCategory] = useState<CategoryType>('ROOT');
  const [idealSoil, setIdealSoil] = useState<SoilType>('LOAM');
  const [season, setSeason] = useState<SeasonType>('YEAR_ROUND');
  const [daysToHarvest, setDaysToHarvest] = useState(75);
  const [waterReq, setWaterReq] = useState(40);
  const [nVal, setNVal] = useState(80);
  const [pVal, setPVal] = useState(60);
  const [kVal, setKVal] = useState(90);
  const [imageUrl, setImageUrl] = useState('');

  const loadCrops = async () => {
    setLoading(true);
    const data = await apiService.getCrops();
    setCrops(data);
    setLoading(false);
  };

  useEffect(() => {
    loadCrops();
  }, []);

  const handleSaveCrop = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editingCrop) {
      await apiService.updateCrop(editingCrop.id, {
        name,
        botanicalName,
        category,
        idealSoil,
        season,
        daysToHarvest,
        waterReqMmPerWeek: waterReq,
        npkRequirement: { nitrogen: nVal, phosphorus: pVal, potassium: kVal },
        imageUrl: imageUrl || editingCrop.imageUrl
      });
    } else {
      await apiService.addCrop({
        name,
        botanicalName,
        category,
        idealSoil,
        season,
        daysToHarvest,
        waterReqMmPerWeek: waterReq,
        npkRequirement: { nitrogen: nVal, phosphorus: pVal, potassium: kVal },
        companionCropsGood: ['Tomato', 'Lettuce'],
        companionCropsBad: ['Fennel'],
        imageUrl: imageUrl || 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=600&q=80',
      });
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
    setBotanicalName('');
    setCategory('ROOT');
    setIdealSoil('LOAM');
    setSeason('YEAR_ROUND');
    setDaysToHarvest(75);
    setWaterReq(40);
    setNVal(80);
    setPVal(60);
    setKVal(90);
    setImageUrl('');
    setIsAddModalOpen(true);
  };

  const openEditModal = (crop: Crop) => {
    setEditingCrop(crop);
    setName(crop.name);
    setBotanicalName(crop.botanicalName);
    setCategory(crop.category);
    setIdealSoil(crop.idealSoil);
    setSeason(crop.season);
    setDaysToHarvest(crop.daysToHarvest);
    setWaterReq(crop.waterReqMmPerWeek);
    setNVal(crop.npkRequirement.nitrogen);
    setPVal(crop.npkRequirement.phosphorus);
    setKVal(crop.npkRequirement.potassium);
    setImageUrl(crop.imageUrl);
    setIsAddModalOpen(true);
  };

  const closeModal = () => {
    setIsAddModalOpen(false);
    setEditingCrop(null);
  };

  const filteredCrops = crops.filter(c => {
    const matchesSearch = c.name.toLowerCase().includes(search.toLowerCase()) ||
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
          <button onClick={openAddModal} className="btn btn-primary text-xs h-9 px-3">
            <Plus className="w-4 h-4" />
            <span>Add Crop Record</span>
          </button>
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
            <div key={crop.id} className="glass-card overflow-hidden group hover:border-emerald-500/50 transition-all duration-200 flex flex-col justify-between">
              <div>
                <div className="h-44 relative overflow-hidden bg-slate-900">
                  <img
                    src={crop.imageUrl}
                    alt={crop.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/20 to-transparent" />
                  <div className="absolute top-3 right-3">
                    <Badge variant="purple">{crop.category}</Badge>
                  </div>
                  <div className="absolute bottom-3 left-4 right-4">
                    <h3 className="text-base font-extrabold text-white leading-tight">
                      {crop.name}
                    </h3>
                    <p className="text-xs text-emerald-400 italic font-mono mt-0.5">
                      {crop.botanicalName}
                    </p>
                  </div>
                </div>

                <div className="p-4 space-y-3 text-xs">
                  <div className="grid grid-cols-3 gap-2 py-2 border-y border-slate-200 dark:border-slate-800">
                    <div className="text-center">
                      <p className="text-[10px] text-slate-400 uppercase font-bold flex items-center justify-center gap-1">
                        <Calendar className="w-3 h-3 text-emerald-500" /> Harvest
                      </p>
                      <p className="font-extrabold text-slate-800 dark:text-slate-200 mt-0.5">{crop.daysToHarvest} days</p>
                    </div>
                    <div className="text-center border-x border-slate-200 dark:border-slate-800">
                      <p className="text-[10px] text-slate-400 uppercase font-bold flex items-center justify-center gap-1">
                        <Droplets className="w-3 h-3 text-blue-500" /> Water
                      </p>
                      <p className="font-extrabold text-slate-800 dark:text-slate-200 mt-0.5">{crop.waterReqMmPerWeek} mm/wk</p>
                    </div>
                    <div className="text-center">
                      <p className="text-[10px] text-slate-400 uppercase font-bold flex items-center justify-center gap-1">
                        <Sprout className="w-3 h-3 text-amber-500" /> Soil
                      </p>
                      <p className="font-extrabold text-slate-800 dark:text-slate-200 mt-0.5">{crop.idealSoil}</p>
                    </div>
                  </div>

                  <div className="p-2.5 rounded-xl bg-slate-50 dark:bg-slate-800/60 flex items-center justify-between border border-slate-200/60 dark:border-slate-800">
                    <span className="font-bold text-slate-500 text-[11px]">NPK Ratio:</span>
                    <span className="font-mono font-extrabold text-emerald-600 dark:text-emerald-400 text-xs">
                      N:{crop.npkRequirement.nitrogen} • P:{crop.npkRequirement.phosphorus} • K:{crop.npkRequirement.potassium}
                    </span>
                  </div>
                </div>
              </div>

              <div className="px-4 pb-4 pt-1 flex items-center justify-between border-t border-slate-100 dark:border-slate-800/60">
                <span className="text-[11px] text-slate-400">
                  Active Plots: <strong className="text-slate-700 dark:text-slate-200 font-mono">{crop.activePlantingCount || 0}</strong>
                </span>
                <div className="flex items-center gap-1">
                  <button
                    onClick={() => openEditModal(crop)}
                    className="p-2 rounded-lg text-slate-500 hover:text-emerald-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition"
                    title="Edit Crop"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDeleteCrop(crop.id)}
                    className="p-2 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/30 transition"
                    title="Delete Crop"
                  >
                    <Trash2 className="w-4 h-4" />
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

      {/* Add / Edit Crop Modal */}
      {isAddModalOpen && (
        <Modal
          isOpen={isAddModalOpen}
          onClose={closeModal}
          title={editingCrop ? 'Edit Crop Profile' : 'Add Vegetable Crop'}
          maxWidth="lg"
        >
          <form onSubmit={handleSaveCrop} className="space-y-4 text-xs">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Crop Name</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Carrot (Karot)"
                  className="input-field"
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Botanical Name</label>
                <input
                  type="text"
                  required
                  value={botanicalName}
                  onChange={(e) => setBotanicalName(e.target.value)}
                  placeholder="e.g. Daucus carota"
                  className="input-field"
                />
              </div>
            </div>

            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Real Photo Image URL</label>
              <input
                type="url"
                value={imageUrl}
                onChange={(e) => setImageUrl(e.target.value)}
                placeholder="https://images.unsplash.com/photo-..."
                className="input-field"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Category</label>
                <select value={category} onChange={(e) => setCategory(e.target.value as CategoryType)} className="input-field select-field">
                  <option value="ROOT">ROOT</option>
                  <option value="LEAFY">LEAFY</option>
                  <option value="PODDED">PODDED</option>
                  <option value="FRUIT">FRUIT</option>
                </select>
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Ideal Soil</label>
                <select value={idealSoil} onChange={(e) => setIdealSoil(e.target.value as SoilType)} className="input-field select-field">
                  <option value="LOAM">LOAM</option>
                  <option value="CLAY">CLAY</option>
                  <option value="SANDY">SANDY</option>
                  <option value="SILTY">SILTY</option>
                </select>
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Season</label>
                <select value={season} onChange={(e) => setSeason(e.target.value as SeasonType)} className="input-field select-field">
                  <option value="YEAR_ROUND">YEAR_ROUND</option>
                  <option value="DRY">DRY</option>
                  <option value="WET">WET</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Days to Harvest</label>
                <input type="number" required value={daysToHarvest} onChange={(e) => setDaysToHarvest(Number(e.target.value))} className="input-field" />
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Water Req (mm/week)</label>
                <input type="number" required value={waterReq} onChange={(e) => setWaterReq(Number(e.target.value))} className="input-field" />
              </div>
            </div>

            <div className="p-3.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40">
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-2">NPK Ratio Requirement (kg/ha)</label>
              <div className="grid grid-cols-3 gap-3">
                <div>
                  <span className="text-[10px] text-slate-400">Nitrogen (N)</span>
                  <input type="number" value={nVal} onChange={(e) => setNVal(Number(e.target.value))} className="input-field mt-0.5" />
                </div>
                <div>
                  <span className="text-[10px] text-slate-400">Phosphorus (P)</span>
                  <input type="number" value={pVal} onChange={(e) => setPVal(Number(e.target.value))} className="input-field mt-0.5" />
                </div>
                <div>
                  <span className="text-[10px] text-slate-400">Potassium (K)</span>
                  <input type="number" value={kVal} onChange={(e) => setKVal(Number(e.target.value))} className="input-field mt-0.5" />
                </div>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button type="button" onClick={closeModal} className="btn btn-secondary text-xs h-9">Cancel</button>
              <button type="submit" className="btn btn-primary text-xs h-9">
                {editingCrop ? 'Save Changes' : 'Create Crop Record'}
              </button>
            </div>
          </form>
        </Modal>
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
              <div className="p-3 rounded-xl bg-slate-100 dark:bg-slate-800/60 space-y-1">
                <strong className="text-slate-900 dark:text-white">Affected Crops:</strong>
                <p className="text-slate-600 dark:text-slate-300">{selectedPest.affectedCrops.join(', ')}</p>
              </div>

              <div className="p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-800 dark:text-emerald-300 space-y-1">
                <strong className="text-emerald-600 dark:text-emerald-400 font-bold">🌿 Organic Control:</strong>
                <p>{selectedPest.organicControl}</p>
              </div>

              <div className="p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-800 dark:text-rose-300 space-y-1">
                <strong className="text-rose-600 dark:text-rose-400 font-bold">🧪 Chemical Control:</strong>
                <p>{selectedPest.chemicalControl}</p>
              </div>

              <div className="p-3.5 rounded-xl bg-blue-500/10 border border-blue-500/20 text-blue-800 dark:text-blue-300 space-y-1">
                <strong className="text-blue-600 dark:text-blue-400 font-bold">🛡️ Prevention Tips:</strong>
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
            <div className="h-48 relative rounded-xl overflow-hidden bg-slate-900">
              <img src={selectedSoil.imageUrl} alt={selectedSoil.title} className="w-full h-full object-cover" />
              <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-transparent to-transparent" />
              <div className="absolute bottom-3 left-4">
                <span className="text-amber-300 font-bold text-sm">{selectedSoil.localName}</span>
              </div>
            </div>

            <div className="space-y-3">
              <p className="text-slate-700 dark:text-slate-300 leading-relaxed">{selectedSoil.description}</p>
              
              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 rounded-xl bg-slate-100 dark:bg-slate-800/60">
                  <span className="text-[10px] text-slate-400 font-bold uppercase block">Drainage Speed</span>
                  <strong className="text-emerald-500 font-bold text-xs">{selectedSoil.drainageSpeed}</strong>
                </div>
                <div className="p-3 rounded-xl bg-slate-100 dark:bg-slate-800/60">
                  <span className="text-[10px] text-slate-400 font-bold uppercase block">pH Range</span>
                  <strong className="text-amber-500 font-bold text-xs">{selectedSoil.phRange}</strong>
                </div>
              </div>

              <div className="p-3 rounded-xl bg-amber-500/10 border border-amber-500/20 text-amber-900 dark:text-amber-200">
                <strong>Texture Profile:</strong> {selectedSoil.texture}
              </div>

              <div className="p-3 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-900 dark:text-emerald-200">
                <strong>Optimal Crop Matches:</strong> {selectedSoil.bestCrops.join(', ')}
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <button onClick={() => setSelectedSoil(null)} className="btn btn-secondary text-xs h-9">Close Guide</button>
            </div>
          </div>
        </Modal>
      )}

      {/* Crop Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredCrops.map((crop) => (
          <div key={crop.id} className="glass-card overflow-hidden group hover:border-emerald-500/50 transition-all duration-200 flex flex-col justify-between">
            <div>
              <div className="h-36 relative overflow-hidden bg-slate-900">
                <img
                  src={crop.imageUrl}
                  alt={crop.name}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/30 to-transparent" />
                <div className="absolute top-3 right-3">
                  <Badge variant="purple">{crop.category}</Badge>
                </div>
                <div className="absolute bottom-3 left-4 right-4">
                  <h3 className="text-base font-extrabold text-white leading-tight">
                    {crop.name}
                  </h3>
                  <p className="text-xs text-emerald-400 italic font-mono mt-0.5">
                    {crop.botanicalName}
                  </p>
                </div>
              </div>

              <div className="p-4 space-y-3 text-xs">
                <div className="grid grid-cols-3 gap-2 py-2 border-y border-slate-200 dark:border-slate-800">
                  <div className="text-center">
                    <p className="text-[10px] text-slate-400 uppercase font-bold flex items-center justify-center gap-1">
                      <Calendar className="w-3 h-3 text-emerald-500" /> Harvest
                    </p>
                    <p className="font-extrabold text-slate-800 dark:text-slate-200 mt-0.5">{crop.daysToHarvest} days</p>
                  </div>
                  <div className="text-center border-x border-slate-200 dark:border-slate-800">
                    <p className="text-[10px] text-slate-400 uppercase font-bold flex items-center justify-center gap-1">
                      <Droplets className="w-3 h-3 text-blue-500" /> Water
                    </p>
                    <p className="font-extrabold text-slate-800 dark:text-slate-200 mt-0.5">{crop.waterReqMmPerWeek} mm/wk</p>
                  </div>
                  <div className="text-center">
                    <p className="text-[10px] text-slate-400 uppercase font-bold flex items-center justify-center gap-1">
                      <Sprout className="w-3 h-3 text-amber-500" /> Soil
                    </p>
                    <p className="font-extrabold text-slate-800 dark:text-slate-200 mt-0.5">{crop.idealSoil}</p>
                  </div>
                </div>

                {/* NPK Ratio pill */}
                <div className="p-2.5 rounded-xl bg-slate-50 dark:bg-slate-800/60 flex items-center justify-between border border-slate-200/60 dark:border-slate-800">
                  <span className="font-bold text-slate-500 text-[11px]">NPK Ratio:</span>
                  <span className="font-mono font-extrabold text-emerald-600 dark:text-emerald-400 text-xs">
                    N:{crop.npkRequirement.nitrogen} • P:{crop.npkRequirement.phosphorus} • K:{crop.npkRequirement.potassium}
                  </span>
                </div>
              </div>
            </div>

            {/* Card Footer Actions */}
            <div className="px-4 pb-4 pt-1 flex items-center justify-between border-t border-slate-100 dark:border-slate-800/60">
              <span className="text-[11px] text-slate-400">
                Active Plots: <strong className="text-slate-700 dark:text-slate-200 font-mono">{crop.activePlantingCount || 0}</strong>
              </span>
              <div className="flex items-center gap-1">
                <button
                  onClick={() => openEditModal(crop)}
                  className="p-2 rounded-lg text-slate-500 hover:text-emerald-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition"
                  title="Edit Crop"
                >
                  <Edit2 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => handleDeleteCrop(crop.id)}
                  className="p-2 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/30 transition"
                  title="Delete Crop"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Add / Edit Crop Modal */}
      {isAddModalOpen && (
        <Modal
          isOpen={isAddModalOpen}
          onClose={closeModal}
          title={editingCrop ? 'Edit Crop Profile' : 'Add Vegetable Crop'}
          maxWidth="lg"
        >
          <form onSubmit={handleSaveCrop} className="space-y-4 text-xs">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Crop Name</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Carrot (Karot)"
                  className="input-field"
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Botanical Name</label>
                <input
                  type="text"
                  required
                  value={botanicalName}
                  onChange={(e) => setBotanicalName(e.target.value)}
                  placeholder="e.g. Daucus carota"
                  className="input-field"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Category</label>
                <select value={category} onChange={(e) => setCategory(e.target.value as CategoryType)} className="input-field select-field">
                  <option value="ROOT">ROOT</option>
                  <option value="LEAFY">LEAFY</option>
                  <option value="PODDED">PODDED</option>
                  <option value="FRUIT">FRUIT</option>
                </select>
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Ideal Soil</label>
                <select value={idealSoil} onChange={(e) => setIdealSoil(e.target.value as SoilType)} className="input-field select-field">
                  <option value="LOAM">LOAM</option>
                  <option value="CLAY">CLAY</option>
                  <option value="SANDY">SANDY</option>
                  <option value="SILTY">SILTY</option>
                </select>
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Season</label>
                <select value={season} onChange={(e) => setSeason(e.target.value as SeasonType)} className="input-field select-field">
                  <option value="YEAR_ROUND">YEAR_ROUND</option>
                  <option value="DRY">DRY</option>
                  <option value="WET">WET</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Days to Harvest</label>
                <input type="number" required value={daysToHarvest} onChange={(e) => setDaysToHarvest(Number(e.target.value))} className="input-field" />
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Water Req (mm/week)</label>
                <input type="number" required value={waterReq} onChange={(e) => setWaterReq(Number(e.target.value))} className="input-field" />
              </div>
            </div>

            <div className="p-3.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40">
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-2">NPK Ratio Requirement (kg/ha)</label>
              <div className="grid grid-cols-3 gap-3">
                <div>
                  <span className="text-[10px] text-slate-400">Nitrogen (N)</span>
                  <input type="number" value={nVal} onChange={(e) => setNVal(Number(e.target.value))} className="input-field mt-0.5" />
                </div>
                <div>
                  <span className="text-[10px] text-slate-400">Phosphorus (P)</span>
                  <input type="number" value={pVal} onChange={(e) => setPVal(Number(e.target.value))} className="input-field mt-0.5" />
                </div>
                <div>
                  <span className="text-[10px] text-slate-400">Potassium (K)</span>
                  <input type="number" value={kVal} onChange={(e) => setKVal(Number(e.target.value))} className="input-field mt-0.5" />
                </div>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button type="button" onClick={closeModal} className="btn btn-secondary text-xs h-9">Cancel</button>
              <button type="submit" className="btn btn-primary text-xs h-9">
                {editingCrop ? 'Save Changes' : 'Create Crop Record'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
