import React, { useEffect, useState } from 'react';
import { Search, UserCheck, UserX, Mail, Phone, Eye, RefreshCw, Users, Filter } from 'lucide-react';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { Farmer, AccountStatus } from '../types';
import { apiService } from '../services/api';

export const FarmerManagement: React.FC = () => {
  const [farmers, setFarmers] = useState<Farmer[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [search, setSearch] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedFarmer, setSelectedFarmer] = useState<Farmer | null>(null);

  const loadFarmers = async () => {
    setLoading(true);
    const data = await apiService.getFarmers();
    setFarmers(data);
    setLoading(false);
  };

  useEffect(() => {
    loadFarmers();
  }, []);

  const handleToggleStatus = async (farmer: Farmer) => {
    const nextStatus: AccountStatus = farmer.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    await apiService.updateFarmerStatus(farmer.id, nextStatus);
    loadFarmers();
  };

  const filteredFarmers = farmers.filter((f) => {
    const matchesSearch =
      f.fullName.toLowerCase().includes(search.toLowerCase()) ||
      f.email.toLowerCase().includes(search.toLowerCase()) ||
      f.farmName.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || f.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6 animate-fadeIn pb-8">
      {/* Header & Actions */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 className="text-xl font-bold text-slate-900 flex items-center gap-2">
            <Users className="w-5 h-5 text-emerald-600" />
            <span>Registered Farmer Directory</span>
          </h3>
          <p className="text-xs text-slate-400 mt-1">
            Manage smallholder farmer profiles, account access, and farm plot allocations.
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <button
            onClick={loadFarmers}
            disabled={loading}
            className="btn btn-secondary text-xs h-10 px-3 flex items-center gap-2 shrink-0 cursor-pointer"
            title="Reload from Supabase"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-emerald-600' : ''}`} />
            <span className="hidden sm:inline">Sync Database</span>
          </button>
        </div>
      </div>

      {/* Search & Filter Bar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between">
        <div className="relative flex-1">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search by farmer name, email, or farm..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="input-field text-xs pl-10 py-2.5 w-full rounded-xl"
          />
        </div>

        <div className="flex items-center gap-2">
          <Filter className="w-4 h-4 text-slate-400" />
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="input-field select-field py-2 text-xs font-semibold rounded-xl"
          >
            <option value="ALL">All Account Status</option>
            <option value="ACTIVE">Active Only</option>
            <option value="PENDING">Pending Approval</option>
            <option value="SUSPENDED">Suspended</option>
          </select>
        </div>
      </div>

      {/* Mobile Card List (< sm) */}
      <div className="grid grid-cols-1 gap-3.5 sm:hidden">
        {filteredFarmers.map((farmer) => (
          <div
            key={farmer.id}
            className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm space-y-3"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-emerald-100 text-emerald-700 font-bold flex items-center justify-center text-xs border border-emerald-200">
                  {farmer.fullName.charAt(0)}
                </div>
                <div>
                  <h4 className="font-bold text-slate-900 text-sm">{farmer.fullName}</h4>
                  <p className="text-[11px] text-slate-400 font-mono">{farmer.email}</p>
                </div>
              </div>
              <Badge variant={farmer.status === 'ACTIVE' ? 'success' : farmer.status === 'PENDING' ? 'warning' : 'danger'}>
                {farmer.status}
              </Badge>
            </div>

            <div className="p-3 rounded-xl bg-slate-50 text-xs space-y-1.5 border border-slate-100">
              <div className="flex justify-between">
                <span className="text-slate-400">Farm Name:</span>
                <span className="font-semibold text-slate-800">{farmer.farmName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Plots:</span>
                <span className="font-mono text-emerald-600 font-bold">{farmer.activePlotsCount} plots</span>
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-1">
              <button
                onClick={() => setSelectedFarmer(farmer)}
                className="btn btn-secondary text-xs h-9 px-3"
              >
                <Eye className="w-3.5 h-3.5" />
                <span>Details</span>
              </button>
              <button
                onClick={() => handleToggleStatus(farmer)}
                className={`btn text-xs h-9 px-3 ${
                  farmer.status === 'ACTIVE'
                    ? 'btn-danger'
                    : 'btn-primary'
                }`}
              >
                {farmer.status === 'ACTIVE' ? <UserX className="w-3.5 h-3.5" /> : <UserCheck className="w-3.5 h-3.5" />}
                <span>{farmer.status === 'ACTIVE' ? 'Suspend' : 'Activate'}</span>
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Desktop Farmers Table (>= sm) */}
      <div className="hidden sm:block bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <table className="w-full text-left text-xs">
          <thead>
            <tr className="text-slate-400 border-b border-slate-100 font-semibold">
              <th className="pb-3">Farmer Profile</th>
              <th className="pb-3">Farm Name</th>
              <th className="pb-3">Plots</th>
              <th className="pb-3">Status</th>
              <th className="pb-3">Joined Date</th>
              <th className="pb-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-slate-700">
            {filteredFarmers.map((farmer) => (
              <tr key={farmer.id} className="hover:bg-slate-50/70 transition">
                <td className="py-3.5">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-emerald-100 text-emerald-700 font-bold flex items-center justify-center text-xs border border-emerald-200">
                      {farmer.fullName.charAt(0)}
                    </div>
                    <div>
                      <p className="font-bold text-slate-900 text-xs">
                        {farmer.fullName}
                      </p>
                      <p className="text-[11px] text-slate-400 font-mono">
                        {farmer.email}
                      </p>
                    </div>
                  </div>
                </td>
                <td className="py-3.5">
                  <p className="font-semibold text-xs text-slate-800">
                    {farmer.farmName}
                  </p>
                </td>
                <td className="py-3.5 font-mono text-xs font-bold text-emerald-600">
                  {farmer.activePlotsCount} plots
                </td>
                <td className="py-3.5">
                  <Badge variant={farmer.status === 'ACTIVE' ? 'success' : farmer.status === 'PENDING' ? 'warning' : 'danger'}>
                    {farmer.status}
                  </Badge>
                </td>
                <td className="py-3.5 text-xs text-slate-400">
                  {new Date(farmer.createdAt).toLocaleDateString()}
                </td>
                <td className="py-3.5 text-right">
                  <div className="flex items-center justify-end gap-2">
                    <button
                      onClick={() => setSelectedFarmer(farmer)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition cursor-pointer"
                      title="View Details"
                    >
                      <Eye className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleToggleStatus(farmer)}
                      className={`p-1.5 rounded-lg transition cursor-pointer ${
                        farmer.status === 'ACTIVE'
                          ? 'text-rose-500 hover:bg-rose-50'
                          : 'text-emerald-600 hover:bg-emerald-50'
                      }`}
                      title={farmer.status === 'ACTIVE' ? 'Suspend Account' : 'Activate Account'}
                    >
                      {farmer.status === 'ACTIVE' ? <UserX className="w-4 h-4" /> : <UserCheck className="w-4 h-4" />}
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Farmer Details Modal */}
      {selectedFarmer && (
        <Modal
          isOpen={!!selectedFarmer}
          onClose={() => setSelectedFarmer(null)}
          title="Farmer Profile & Farm Plot Overview"
          size="lg"
        >
          <div className="space-y-4 text-xs">
            <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 flex items-center gap-4">
              <div className="w-14 h-14 rounded-2xl bg-emerald-100 text-emerald-700 border border-emerald-200 flex items-center justify-center font-black text-xl">
                {selectedFarmer.fullName.charAt(0)}
              </div>
              <div className="flex-1">
                <h4 className="text-base font-bold text-slate-900">{selectedFarmer.fullName}</h4>
                <p className="text-slate-400 font-mono mt-0.5">{selectedFarmer.email}</p>
                <div className="flex items-center gap-2 mt-2">
                  <Badge variant={selectedFarmer.status === 'ACTIVE' ? 'success' : selectedFarmer.status === 'PENDING' ? 'warning' : 'danger'}>
                    {selectedFarmer.status}
                  </Badge>
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div className="p-3.5 rounded-xl border border-slate-100 bg-slate-50">
                <p className="text-slate-400 font-semibold uppercase text-[10px]">Contact Info</p>
                <p className="font-bold mt-1 flex items-center gap-1.5 text-slate-800"><Mail className="w-3.5 h-3.5 text-emerald-600" /> {selectedFarmer.email}</p>
                <p className="font-bold mt-1 flex items-center gap-1.5 text-slate-800"><Phone className="w-3.5 h-3.5 text-emerald-600" /> {selectedFarmer.phoneNumber}</p>
              </div>

              <div className="p-3.5 rounded-xl border border-slate-100 bg-slate-50">
                <p className="text-slate-400 font-semibold uppercase text-[10px]">Farm Details</p>
                <p className="font-bold mt-1 text-slate-800">{selectedFarmer.farmName}</p>
                <p className="text-emerald-600 font-bold mt-1 font-mono">
                  {selectedFarmer.activePlotsCount} active plots
                </p>
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <button onClick={() => setSelectedFarmer(null)} className="btn btn-secondary text-xs h-9">
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
