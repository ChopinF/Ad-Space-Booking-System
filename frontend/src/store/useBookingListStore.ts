import { create } from 'zustand';
import {
  BookingDTO,
  BookingStatus,
  fetchBookings,
  approveBooking,
  rejectBooking,
} from '../api/bookingApi';

export type StatusFilter = BookingStatus | 'ALL';

interface BookingListState {
  items: BookingDTO[];
  loading: boolean;
  error: string | null;

  statusFilter: StatusFilter;

  fetchAll: () => Promise<void>;
  setStatusFilter: (status: StatusFilter) => void;

  approveOne: (id: number) => Promise<void>;
  rejectOne: (id: number) => Promise<void>;
}

export const useBookingListStore = create<BookingListState>((set, get) => ({
  items: [],
  loading: false,
  error: null,

  statusFilter: 'ALL',

  async fetchAll() {
    set({ loading: true, error: null });
    try {
      const { statusFilter } = get();
      const status = statusFilter === 'ALL' ? undefined : statusFilter;
      const data = await fetchBookings(status as BookingStatus | undefined);
      set({ items: data, loading: false });
    } catch (e: any) {
      set({
        error: e.message ?? 'Failed to fetch bookings',
        loading: false,
      });
    }
  },

  setStatusFilter(status) {
    set({ statusFilter: status });
  },

  async approveOne(id) {
    const prev = get().items;
    // optimistic update
    set({
      items: prev.map((b) =>
        b.id === id ? { ...b, status: 'Approved' } : b
      ),
    });

    try {
      const updated = await approveBooking(id);
      set({
        items: get().items.map((b) =>
          b.id === id ? updated : b
        ),
      });
    } catch (e: any) {
      // rollback
      set({ items: prev, error: e.message ?? 'Failed to approve booking' });
    }
  },

  async rejectOne(id) {
    const prev = get().items;
    set({
      items: prev.map((b) =>
        b.id === id ? { ...b, status: 'Rejected' } : b
      ),
    });

    try {
      const updated = await rejectBooking(id);
      set({
        items: get().items.map((b) =>
          b.id === id ? updated : b
        ),
      });
    } catch (e: any) {
      set({ items: prev, error: e.message ?? 'Failed to reject booking' });
    }
  },
}));
