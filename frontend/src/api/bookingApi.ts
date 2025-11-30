export interface BookingCreationPayload {
  adSpaceId: number;
  advertiserName: string;
  advertiserEmail: string;
  startDate: string; // ISO: YYYY-MM-DD
  endDate: string;   // ISO: YYYY-MM-DD
}
export type BookingStatus = 'Pending' | 'Approved' | 'Rejected';

export interface BookingDTO {
  id: number;
  adSpaceId: number;
  advertiserName: string;
  advertiserEmail: string;
  startDate: string;
  endDate: string;
  createdAt: string;
  status: 'Pending' | 'Approved' | 'Rejected';
  totalCost: number;
}

const API_BASE = '/api/v1/booking-requests';

// this is for BookingRequestForm 
export async function createBooking(payload: BookingCreationPayload): Promise<BookingDTO> {
  const res = await fetch(API_BASE, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    let message = `Failed to create booking (${res.status})`;
    try {
      const errorBody = await res.json();
      if (errorBody?.message) { // we use backend's message
        message = errorBody.message;
      }
    } catch {
    }
    throw new Error(message);
  }
  return res.json();
}

// for BookingList.tsx
export async function fetchBookings(status?: BookingStatus): Promise<BookingDTO[]> {
  const params = new URLSearchParams();
  if (status) params.append('status', status);
  const url = params.toString() ? `${API_BASE}?${params.toString()}` : API_BASE;

  const res = await fetch(url);
  if (!res.ok) {
    throw new Error(`Failed to fetch bookings: ${res.status}`);
  }
  return res.json();
}

export async function approveBooking(id: number): Promise<BookingDTO> {
  const res = await fetch(`${API_BASE}/${id}/approve`, { method: 'PATCH' })
  if (!res.ok) {
    let message = `Failed to approve booking (${res.status})`;
    try {
      const errorBody = await res.json();
      if (errorBody?.message) message = errorBody.message;
    } catch { }
    throw new Error(message);
  }
  return res.json();
}

export async function rejectBooking(id: number): Promise<BookingDTO> {
  const res = await fetch(`${API_BASE}/${id}/reject`, { method: 'PATCH' });
  if (!res.ok) {
    let message = `Failed to reject booking (${res.status})`;
    try {
      const errorBody = await res.json();
      if (errorBody?.message) message = errorBody.message;
    } catch { }
    throw new Error(message);
  }
  return res.json();
}
