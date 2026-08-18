import React, { useEffect, useState } from 'react';
import { Compass, Plus, CheckCircle2, XCircle, BookOpen } from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { DSSRule, CompanionType } from '../types';
import { apiService } from '../services/api';

export const DSSRuleEditor: React.FC = () => {
  const [rules, setRules] = useState<DSSRule[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [isAddModalOpen, setIsAddModalOpen] = useState<boolean>(false);

  // Form State
  const [cropA, setCropA] = useState('Carrot (Karot)');
  const [cropB, setCropB] = useState('Tomato (Kamatis)');
  const [relationship, setRelationship] = useState<CompanionType>('BENEFICIAL');
  const [reason, setReason] = useState('');
  const [daRef, setDaRef] = useState('DA-BPI Companion Bulletin 2026');

  const loadRules = async () => {
    setLoading(true);
    const data = await apiService.getDSSRules();
    setRules(data);
    setLoading(false);
  };

  useEffect(() => {
    loadRules();
  }, []);

  const handleAddRule = async (e: React.FormEvent) => {
    e.preventDefault();
    await apiService.addDSSRule({
      cropA,
      cropB,
      relationship,
      reason,
      daReferenceDoc: daRef,
    });
    setIsAddModalOpen(false);
    setReason('');
    loadRules();
  };

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Action Header Card */}
      <div className="glass-card p-4 sm:p-5 flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between border-l-4 border-l-emerald-500">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 flex items-center justify-center font-bold">
            <Compass className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="font-extrabold text-sm text-slate-900 dark:text-white">
                Companion Planting Matrix Rules
              </h3>
              <Badge variant="purple">DA/BPI Standards</Badge>
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5 max-w-xl">
              Deterministic agroecological rules driving companion planting compatibility badges and advice
            </p>
          </div>
        </div>

        <button onClick={() => setIsAddModalOpen(true)} className="btn btn-primary text-xs h-10 px-4">
          <Plus className="w-4 h-4" />
          <span>Add Companion Rule</span>
        </button>
      </div>

      {/* Mobile Rules Cards View (< sm) */}
      <div className="block sm:hidden space-y-3">
        {rules.map((rule) => (
          <div key={rule.id} className="glass-card p-4 space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2 font-extrabold text-sm text-slate-900 dark:text-white">
                <span>{rule.cropA}</span>
                <span className="text-emerald-500">↔</span>
                <span>{rule.cropB}</span>
              </div>
              <Badge variant={rule.relationship === 'BENEFICIAL' ? 'success' : rule.relationship === 'ANTAGONIST' ? 'danger' : 'neutral'}>
                {rule.relationship === 'BENEFICIAL' && <CheckCircle2 className="w-3 h-3 mr-1" />}
                {rule.relationship === 'ANTAGONIST' && <XCircle className="w-3 h-3 mr-1" />}
                {rule.relationship}
              </Badge>
            </div>

            <p className="text-xs text-slate-700 dark:text-slate-300 bg-slate-50 dark:bg-slate-800/50 p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 leading-relaxed">
              {rule.reason}
            </p>

            <div className="flex items-center justify-between text-[11px] text-slate-500 font-mono pt-1">
              <div className="flex items-center gap-1">
                <BookOpen className="w-3 h-3 text-emerald-500" />
                <span>{rule.daReferenceDoc || 'DA Standard'}</span>
              </div>
              <span className="text-slate-400">{rule.id}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Desktop Rules Table (>= sm) */}
      <div className="hidden sm:block table-container glass-card">
        <table>
          <thead>
            <tr>
              <th>Target Crop Pair</th>
              <th>Relationship</th>
              <th>Agroecological Rationale & Mechanism</th>
              <th>DA / BPI Reference</th>
              <th>Rule ID</th>
            </tr>
          </thead>
          <tbody>
            {rules.map((rule) => (
              <tr key={rule.id}>
                <td>
                  <div className="flex items-center gap-2 font-bold text-xs">
                    <span className="text-slate-900 dark:text-white">{rule.cropA}</span>
                    <span className="text-slate-400">↔</span>
                    <span className="text-slate-900 dark:text-white">{rule.cropB}</span>
                  </div>
                </td>
                <td>
                  <Badge variant={rule.relationship === 'BENEFICIAL' ? 'success' : rule.relationship === 'ANTAGONIST' ? 'danger' : 'neutral'}>
                    {rule.relationship === 'BENEFICIAL' && <CheckCircle2 className="w-3 h-3 mr-1" />}
                    {rule.relationship === 'ANTAGONIST' && <XCircle className="w-3 h-3 mr-1" />}
                    {rule.relationship}
                  </Badge>
                </td>
                <td className="text-xs text-slate-600 dark:text-slate-300 max-w-md">
                  {rule.reason}
                </td>
                <td className="text-xs text-slate-500 font-mono">
                  <div className="flex items-center gap-1">
                    <BookOpen className="w-3 h-3 text-emerald-500" />
                    {rule.daReferenceDoc || 'DA Standard'}
                  </div>
                </td>
                <td className="text-xs text-slate-400 font-mono">{rule.id}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Add Rule Modal */}
      {isAddModalOpen && (
        <Modal
          isOpen={isAddModalOpen}
          onClose={() => setIsAddModalOpen(false)}
          title="Create Companion Rule"
          maxWidth="md"
        >
          <form onSubmit={handleAddRule} className="space-y-4 text-xs">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Crop A</label>
                <input type="text" required value={cropA} onChange={(e) => setCropA(e.target.value)} className="input-field" />
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Crop B</label>
                <input type="text" required value={cropB} onChange={(e) => setCropB(e.target.value)} className="input-field" />
              </div>
            </div>

            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Companion Relationship</label>
              <select value={relationship} onChange={(e) => setRelationship(e.target.value as CompanionType)} className="input-field select-field">
                <option value="BENEFICIAL">BENEFICIAL (Companion Pairing)</option>
                <option value="ANTAGONIST">ANTAGONIST (Pest/Nutrient Conflict)</option>
                <option value="NEUTRAL">NEUTRAL</option>
              </select>
            </div>

            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Biological Mechanism & Rationale</label>
              <textarea
                required
                rows={3}
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                placeholder="Explain nitrogen fixation, pest trap cropping, or root depth interaction..."
                className="input-field py-2 h-auto"
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">DA / BPI Bulletin Reference</label>
              <input type="text" value={daRef} onChange={(e) => setDaRef(e.target.value)} className="input-field" />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button type="button" onClick={() => setIsAddModalOpen(false)} className="btn btn-secondary text-xs h-9">Cancel</button>
              <button type="submit" className="btn btn-primary text-xs h-9">Save DSS Rule</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
