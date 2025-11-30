import { create } from 'zustand';
import { AdSpace } from '../types/adSpace';
import { BookingCreationPayload, createBooking, BookingDTO } from '../api/bookingApi';

interface BookingState {
  dialogOpen: boolean;
  selectedAdSpace: AdSpace | null;

  submitting: boolean;
  submitError: string | null;
  submitSuccess: BookingDTO | null;

  openForAdSpace: (space: AdSpace) => void;
  closeDialog: () => void;

  submitBooking: (payload: BookingCreationPayload) => Promise<void>;
}

export const useBookingStore = create<BookingState>((set) => ({
  // initially
  dialogOpen: false,
  selectedAdSpace: null,
  submitting: false,
  submitError: null,
  submitSuccess: null,

  openForAdSpace(space) {
    set({
      dialogOpen: true,
      selectedAdSpace: space,
      submitError: null,
      submitSuccess: null,
    })
  },

  closeDialog() {
    set({
      dialogOpen: false,
      selectedAdSpace: null,
      submitError: null,
      submitSuccess: null,
    });
  },

  async submitBooking(payload) {
    set({ submitting: true, submitError: null, submitSuccess: null });
    try {
      const result = await createBooking(payload);
      set({ submitSuccess: result, submitting: false });
    } catch (e: any) {
      set({
        submitError: e.message ?? 'Failed to create booking',
        submitting: false,
      });
    }
  },

}));
