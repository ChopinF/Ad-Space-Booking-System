import { create } from 'zustand';
import { AdSpace, AdSpaceType, City } from '../types/adSpace';
import { fetchAdSpaces, deleteAdSpace, AdSpaceFilters, updateAdSpace } from '../api/adSpaceApi';
import { FilterType, FilterCity } from '../types/adSpace';


interface AdSpacesState {
  items: AdSpace[];
  loading: boolean;
  error: string | null;

  filterType: FilterType;
  filterCity: FilterCity;

  editingItem: AdSpace | null;

  fetchAll: () => Promise<void>;
  setFilterType: (type: FilterType) => void;
  setFilterCity: (city: FilterCity) => void;

  deleteOne: (id: number) => Promise<void>;

  openEdit: (id: number) => void;
  closeEdit: () => void;
  saveEdit: (updated: AdSpace) => Promise<void>;

  getFiltered: () => AdSpace[];
}

export const useAdSpacesStore = create<AdSpacesState>((set, get) => ({
  items: [],
  loading: false,
  error: null,

  editingItem: null,

  filterType: 'ALL',
  filterCity: 'ALL',

  async fetchAll() {
    set({ loading: true, error: null });
    try {
      const { filterType, filterCity } = get();
      const filters: AdSpaceFilters = {};
      if (filterType !== 'ALL') filters.type = filterType;
      if (filterCity !== 'ALL') filters.city = filterCity;

      const data = await fetchAdSpaces(filters);
      set({ items: data, loading: false });
    }
    catch (e: any) {
      set({ error: e.message ?? 'Something went wrong', loading: false });
    }
  },

  setFilterType(type) {
    set({ filterType: type })
  },

  setFilterCity(city) {
    set({ filterCity: city })
  },

  async deleteOne(id) {
    const prev = get().items;
    set({ items: prev.filter((item) => item.id !== id) });

    try {
      await deleteAdSpace(id);
      await get().fetchAll();// auto refresh 
    } catch (e) {
      // rollback in case of failure  
      set({ items: prev, error: 'Failed to delete ad space' });
    }
  },

  openEdit(id) {
    const { items } = get();
    const found = items.find((i) => i.id === id) || null;
    set({ editingItem: found });
  },

  closeEdit() {
    set({ editingItem: null });
  },

  async saveEdit(updated) {
    try {
      const saved = await updateAdSpace(updated.id, updated);
      // update local list
      set({
        items: get().items.map((item) =>
          item.id === saved.id ? saved : item
        ),
        editingItem: null,
      });
    } catch (e: any) {
      set({ error: e.message ?? 'Failed to update ad space' });
    }
  },

  getFiltered() {
    const { items, filterType, filterCity } = get();
    return items.filter((item) => {
      const typeMatch = filterType === 'ALL' || item.type === filterType;
      const cityMatch = filterCity === 'ALL' || item.city === filterCity;
      return typeMatch && cityMatch;
    });
  },

}));
