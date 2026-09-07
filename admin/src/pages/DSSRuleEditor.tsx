import React, { useEffect, useState } from 'react';
import { Compass, Plus, CheckCircle2, XCircle, BookOpen, Trash2, Search, Filter, RefreshCw } from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { DSSRule, CompanionType } from '../types';
import { apiService } from '../services/api';

const APPROVED_CROPS = [
  { name: 'Ampalaya', label: 'Bitter Gourd (Ampalaya)' },
  { name: 'Cabbage', label: 'Cabbage (Repolyo)' },
  { name: 'Carrot', label: 'Carrot (Karot)' },
  { name: 'Chili Pepper', label: 'Chili Pepper (Sili)' },
  { name: 'Corn', label: 'Corn (Mais)' },
  { name: 'Cucumber', label: 'Cucumber (Pipino)' },
  { name: 'Eggplant', label: 'Eggplant (Talong)' },
  { name: 'Kangkong', label: 'Water Spinach (Kangkong)' },
  { name: 'Lettuce', label: 'Lettuce (Litsugas)' },
  { name: 'Okra', label: 'Okra (Okra)' },
  { name: 'Onion', label: 'Onion (Sibuyas)' },
  { name: 'Pechay', label: 'Pechay (Pechay)' },
  { name: 'Squash', label: 'Squash (Kalabasa)' },
  { name: 'Tomato', label: 'Tomato (Kamatis)' },
  { name: 'Yardlong String Bean', label: 'Yardlong String Bean (Sitaw)' },
];

export const DSSRuleEditor: React.FC = () => {
  const [rules, setRules] = useState<DSSRule[]>([]);
  const [availableCrops, setAvailableCrops] = useState<{ name: string; label: string }[]>(APPROVED_CROPS);
  const [loading, setLoading] = useState<boolean>(true);
  const [isAddModalOpen, setIsAddModalOpen] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterType, setFilterType] = useState<string>('ALL');

  // Form State
  const [cropA, setCropA] = useState('Carrot');
  const [cropB, setCropB] = useState('Tomato');
  const [relationship, setRelationship] = useState<CompanionType>('BENEFICIAL');
  const [reason, setReason] = useState('');
  const [daRef, setDaRef] = useState('MapTanim Field Study 2026');

  const loadRules = async () => {
    setLoading(true);
    try {
      const [rulesData, cropsData] = await Promise.all([
        apiService.getDSSRules(),
        apiService.getCrops(),
      ]);
      setRules(rulesData || []);
      if (cropsData && cropsData.length > 0) {
        const merged = [...APPROVED_CROPS];
        cropsData.forEach((c) => {
          if (!merged.some((m) => m.name.toLowerCase() === c.name.toLowerCase())) {
            merged.push({
              name: c.name,
              label: `${c.name}${c.localName ? ` (${c.localName})` : ''}`,
            });
          }
        });
        setAvailableCrops(merged);
      }
    } catch (err) {
      console.error('Failed to load DSS rules:', err);
    } finally {
      setLoading(false);
    }
  };

  const loadData = loadRules;

  useEffect(() => {
    loadRules();
  }, []);

  const handleAddRule = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiService.addDSSRule({
        cropA,
        cropB,
        relationship,
        reason,
        daReferenceDoc: daRef,
      });
      setIsAddModalOpen(false);
      setReason('');
      await loadRules();
    } catch (err) {
      console.error('Failed to add DSS rule:', err);
    }
  };

  const handleDeleteRule = async (id: string) => {
    if (window.confirm('Delete this companion rule? The change will propagate to mobile devices on next sync.')) {
      try {
        await apiService.deleteDSSRule(id);
        await loadRules();
      } catch (err) {
        console.error('Failed to delete DSS rule:', err);
      }
    }
  };

  const filteredRules = rules.filter((rule) => {
    const matchesSearch =
      rule.cropA.toLowerCase().includes(searchQuery.toLowerCase()) ||
      rule.cropB.toLowerCase().includes(searchQuery.toLowerCase()) ||
      rule.reason.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesFilter = filterType === 'ALL' || rule.relationship === filterType;
    return matchesSearch && matchesFilter;
  });

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Action Header Card */}
      <div className="bg-[#2B3136] border border-[#38434D] rounded-xl p-4 sm:p-5 flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between border-l-4 border-l-[#4CAF50] shadow-md">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-[#4CAF50]/15 text-[#4CAF50] flex items-center justify-center font-bold">
            <Compass className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="font-extrabold text-sm text-[#F4F4F4]">
                Companion Planting Matrix Rules (DSS Engine)
              </h3>
              <Badge variant="purple">Field Research Standards</Badge>
              <Badge variant="success">Mobile Live Sync Active</Badge>
            </div>
            <p className="text-xs text-[#8A9BA8] mt-0.5 max-w-xl">
              Deterministic agroecological rules driving companion planting compatibility badges and advice. Updates reflect automatically in the mobile app without app store updates.
            </p>
          </div>
        </div>

        <button onClick={() => setIsAddModalOpen(true)} className="btn btn-primary text-xs h-10 px-4">
          <Plus className="w-4 h-4" />
          <span>Add Companion Rule</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-[#2B3136] border border-[#38434D] rounded-xl p-3 sm:p-4 flex flex-col sm:flex-row gap-3 items-center justify-between shadow-md">
        <div className="relative w-full sm:w-72">
          <Search className="w-4 h-4 text-[#8A9BA8] absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search crops or mechanism..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="input-field pl-9 text-xs h-9 bg-[#1D2429] border-[#38434D] text-[#F4F4F4]"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto justify-end">
          <Filter className="w-4 h-4 text-[#8A9BA8]" />
          <span className="text-xs text-[#C7D0D8] font-medium">Type:</span>
          {(['ALL', 'BENEFICIAL', 'ANTAGONIST', 'NEUTRAL'] as const).map((type) => (
            <button
              key={type}
              onClick={() => setFilterType(type)}
              className={`px-2.5 py-1 rounded-lg text-xs font-semibold transition-all ${
                filterType === type
                  ? 'bg-[#4CAF50] text-white shadow-sm'
                  : 'bg-[#1D2429] border border-[#38434D] text-[#C7D0D8] hover:border-[#4CAF50]'
              }`}
            >
              {type}
            </button>
          ))}
        </div>
      </div>

      {/* Content Rendering: Loading / Empty / Rules */}
      {loading ? (
        <div className="p-12 bg-[#2B3136] border border-[#38434D] rounded-xl text-center flex flex-col items-center justify-center space-y-3 shadow-md">
          <RefreshCw className="w-8 h-8 text-[#4CAF50] animate-spin" />
          <p className="text-xs font-semibold text-[#C7D0D8]">Loading companion planting rules...</p>
        </div>
      ) : filteredRules.length === 0 ? (
        <div className="p-12 bg-[#2B3136] border border-[#38434D] rounded-xl text-center space-y-2 shadow-md">
          <Compass className="w-10 h-10 mx-auto text-[#8A9BA8]/40" />
          <p className="text-sm font-bold text-[#F4F4F4]">No companion rules found</p>
          <p className="text-xs text-[#8A9BA8]">
            {searchQuery.trim() || filterType !== 'ALL'
              ? 'Try adjusting your search query or filter type.'
              : 'Click "+ Add Companion Rule" above to create your first rule pairing.'}
          </p>
        </div>
      ) : (
        <>
          {/* Mobile Rules Cards View (< sm) */}
          <div className="block sm:hidden space-y-3">
            {filteredRules.map((rule) => (
              <div key={rule.id} className="bg-[#2B3136] border border-[#38434D] rounded-xl p-4 space-y-3 shadow-md">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2 font-extrabold text-sm text-[#F4F4F4]">
                    <span>{rule.cropA}</span>
                    <span className="text-[#4CAF50]">↔</span>
                    <span>{rule.cropB}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant={rule.relationship === 'BENEFICIAL' ? 'success' : rule.relationship === 'ANTAGONIST' ? 'danger' : 'neutral'}>
                      {rule.relationship === 'BENEFICIAL' && <CheckCircle2 className="w-3 h-3 mr-1" />}
                      {rule.relationship === 'ANTAGONIST' && <XCircle className="w-3 h-3 mr-1" />}
                      {rule.relationship}
                    </Badge>
                    <button
                      onClick={() => handleDeleteRule(rule.id)}
                      className="p-1 text-[#8A9BA8] hover:text-[#E76F51] transition-colors"
                      title="Delete Rule"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                <p className="text-xs text-[#C7D0D8] bg-[#1D2429] p-2.5 rounded-xl border border-[#38434D] leading-relaxed">
                  {rule.reason}
                </p>

                <div className="flex items-center justify-between text-[11px] text-[#8A9BA8] font-mono pt-1">
                  <div className="flex items-center gap-1">
                    <BookOpen className="w-3 h-3 text-[#4CAF50]" />
                    <span>{rule.daReferenceDoc || 'DA Standard'}</span>
                  </div>
                  <span className="text-[#8A9BA8]">{rule.id}</span>
                </div>
              </div>
            ))}
          </div>

          {/* Desktop Rules Table (>= sm) */}
          <div className="hidden sm:block table-container bg-[#2B3136] border border-[#38434D] rounded-xl overflow-hidden shadow-md">
            <table>
              <thead>
                <tr>
                  <th>Target Crop Pair</th>
                  <th>Relationship</th>
                  <th>Agroecological Rationale & Mechanism</th>
                  <th>Field Research Reference</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRules.map((rule) => (
                  <tr key={rule.id}>
                    <td>
                      <div className="flex items-center gap-2 font-bold text-xs">
                        <span className="text-[#F4F4F4]">{rule.cropA}</span>
                        <span className="text-[#8A9BA8]">↔</span>
                        <span className="text-[#F4F4F4]">{rule.cropB}</span>
                      </div>
                    </td>
                    <td>
                      <Badge variant={rule.relationship === 'BENEFICIAL' ? 'success' : rule.relationship === 'ANTAGONIST' ? 'danger' : 'neutral'}>
                        {rule.relationship === 'BENEFICIAL' && <CheckCircle2 className="w-3 h-3 mr-1" />}
                        {rule.relationship === 'ANTAGONIST' && <XCircle className="w-3 h-3 mr-1" />}
                        {rule.relationship}
                      </Badge>
                    </td>
                    <td className="text-xs text-[#C7D0D8] max-w-md">
                      {rule.reason}
                    </td>
                    <td className="text-xs text-[#8A9BA8] font-mono">
                      <div className="flex items-center gap-1">
                        <BookOpen className="w-3 h-3 text-[#4CAF50]" />
                        {rule.daReferenceDoc || 'DA Standard'}
                      </div>
                    </td>
                    <td>
                      <button
                        onClick={() => handleDeleteRule(rule.id)}
                        className="p-1.5 rounded-lg text-[#8A9BA8] hover:text-[#E76F51] hover:bg-[#E76F51]/10 transition-colors"
                        title="Delete Rule"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {/* Add Rule Modal */}
      {isAddModalOpen && (
        <Modal
          isOpen={isAddModalOpen}
          onClose={() => setIsAddModalOpen(false)}
          title="Create Companion Rule (Field Research Verified)"
          maxWidth="md"
        >
          <form onSubmit={handleAddRule} className="space-y-4 text-xs">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-[#C7D0D8] mb-1">Crop A</label>
                <select
                  value={cropA}
                  onChange={(e) => setCropA(e.target.value)}
                  className="input-field select-field"
                >
                  {availableCrops.map((c) => (
                    <option key={c.name} value={c.name}>
                      {c.label}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block font-bold text-[#C7D0D8] mb-1">Crop B</label>
                <select
                  value={cropB}
                  onChange={(e) => setCropB(e.target.value)}
                  className="input-field select-field"
                >
                  {availableCrops.map((c) => (
                    <option key={c.name} value={c.name}>
                      {c.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label className="block font-bold text-[#C7D0D8] mb-1">Companion Relationship</label>
              <select value={relationship} onChange={(e) => setRelationship(e.target.value as CompanionType)} className="input-field select-field">
                <option value="BENEFICIAL">BENEFICIAL (Synergistic / Soil Enhancing)</option>
                <option value="ANTAGONIST">ANTAGONIST (Pest Vector / Allelopathic Conflict)</option>
                <option value="NEUTRAL">NEUTRAL (Coexistence Without Active Interaction)</option>
              </select>
            </div>

            <div>
              <label className="block font-bold text-[#C7D0D8] mb-1">Biological Mechanism & Rationale</label>
              <textarea
                required
                rows={3}
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                placeholder="Explain nitrogen fixation, pest masking aroma, root exudates, or canopy architecture interaction..."
                className="input-field py-2 h-auto"
              />
            </div>

            <div>
              <label className="block font-bold text-[#C7D0D8] mb-1">Field Research / Survey Reference</label>
              <input type="text" value={daRef} onChange={(e) => setDaRef(e.target.value)} className="input-field" />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button type="button" onClick={() => setIsAddModalOpen(false)} className="btn btn-secondary text-xs h-9">Cancel</button>
              <button type="submit" className="btn btn-primary text-xs h-9">Save & Publish DSS Rule</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
