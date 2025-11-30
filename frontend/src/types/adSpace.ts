export type AdSpaceType = 'Billboard' | 'BusStop' | 'MallDisplay' | 'TransitAd';
export type City = 'Bucuresti' | 'Cluj' | 'Roman' | 'Brasov' | 'Sibiu' | 'Constanta' | 'Craiova' | 'Iasi' | 'Suceava';
export type AvailabilityStatus = 'Available' | 'Booked' | 'Maintenance'

export interface AdSpace {
  id: number;
  name: string;
  pricePerDay: number;
  city: City;
  address: string;
  availabilityStatus: AvailabilityStatus;
  type: AdSpaceType;
}

export type FilterType = AdSpaceType | "ALL";
export type FilterCity = City | "ALL";

