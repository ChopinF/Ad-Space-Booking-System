import { AdSpace, AdSpaceType, AvailabilityStatus, City } from "../types/adSpace";

const API_BASE = '/api/v1/ad-spaces';

export interface AdSpaceFilters {
  type?: AdSpaceType;
  city?: City;
}

export async function fetchAdSpaces(filters?: AdSpaceFilters): Promise<AdSpace[]> {
  const params = new URLSearchParams();

  if (filters?.type) params.append('type', filters.type);
  if (filters?.city) params.append('city', filters.city);

  const queryString = params.toString();
  const url = queryString ? `${API_BASE}?${queryString}` : API_BASE;

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Failed to fetch ad spaces: ${response.status}`);
  }

  const data = await response.json();
  return data;
}

export async function deleteAdSpace(id: number): Promise<void> {
  const response = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
  if (!response.ok) {
    throw new Error(`Failed to delete ad space ${id}`);
  }
}

export async function updateAdSpace(id: number, payload: AdSpace): Promise<AdSpace> {
  const response = await fetch(`${API_BASE}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(`Failed to update ad space ${id}`);
  }

  return response.json();
}
