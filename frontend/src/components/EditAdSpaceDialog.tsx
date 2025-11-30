import React, { useEffect, useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Grid,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
} from '@mui/material';
import { useAdSpacesStore } from '../store/useAdSpacesStore';
import { AdSpaceType, City } from '../types/adSpace';
import { z } from 'zod';

const CITIES: City[] = [
  'Bucuresti',
  'Cluj',
  'Roman',
  'Brasov',
  'Sibiu',
  'Constanta',
  'Craiova',
  'Iasi',
  'Suceava',
];

const TYPES: AdSpaceType[] = [
  'Billboard',
  'BusStop',
  'MallDisplay',
  'TransitAd',
];

const editAdSpaceSchema = z.object({// zod schema for validation
  name: z
    .string()
    .min(2, 'Name must be at least 2 characters')
    .max(20, 'Name must be at max 20 characters'),
  pricePerDay: z
    .number()
    .positive('Price must be greater than 0'),
  address: z
    .string()
    .min(3, 'Address must be at least 3 characters'),
});

type EditAdSpaceForm = z.infer<typeof editAdSpaceSchema>;

export const EditAdSpaceDialog: React.FC = () => {
  const { editingItem, closeEdit, saveEdit } = useAdSpacesStore();

  const [name, setName] = useState('');
  const [city, setCity] = useState<City | ''>('');
  const [type, setType] = useState<AdSpaceType | ''>('');
  const [pricePerDay, setPricePerDay] = useState<number | ''>('');
  const [address, setAddress] = useState('');
  const [saving, setSaving] = useState(false);

  const [errors, setErrors] = useState<
    Partial<Record<keyof EditAdSpaceForm, string>>
  >({});

  // populate from editingItem when opening the dialogue 
  useEffect(() => {
    if (editingItem) {
      setName(editingItem.name);
      setCity(editingItem.city);
      setType(editingItem.type);
      setPricePerDay(editingItem.pricePerDay);
      setAddress(editingItem.address);
      setErrors({});
    }
  }, [editingItem]);

  const handleClose = () => {
    if (saving) return;
    closeEdit();
  };

  const handleSave = async () => {
    if (!editingItem) return;

    const rawData: EditAdSpaceForm = {
      name,
      pricePerDay:
        typeof pricePerDay === 'number' ? pricePerDay : Number(pricePerDay),
      address,
    };

    const result = editAdSpaceSchema.safeParse(rawData);

    if (!result.success) {
      const fieldErrors: Partial<Record<keyof EditAdSpaceForm, string>> = {};
      result.error.issues.forEach((issue) => {
        const path = issue.path[0] as keyof EditAdSpaceForm;
        if (!fieldErrors[path]) {
          fieldErrors[path] = issue.message;
        }
      });
      setErrors(fieldErrors);
      return;
    }

    setSaving(true);
    await saveEdit({
      ...editingItem,
      ...result.data, // validated data
    });
    setSaving(false);
  };

  const open = !!editingItem;

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>Edit Ad Space</DialogTitle>
      <DialogContent sx={{ pt: 2 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12 }}>
            <TextField
              label="Name"
              fullWidth
              size="small"
              value={name}
              onChange={(e) => setName(e.target.value)}
              error={!!errors.name}
              helperText={errors.name}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <FormControl fullWidth size="small">
              <InputLabel id="edit-city-label">City</InputLabel>
              <Select
                labelId="edit-city-label"
                label="City"
                value={city}
                onChange={(e) => setCity(e.target.value as City)}
              >
                {CITIES.map((c) => (
                  <MenuItem key={c} value={c}>
                    {c}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <FormControl fullWidth size="small">
              <InputLabel id="edit-type-label">Type</InputLabel>
              <Select
                labelId="edit-type-label"
                label="Type"
                value={type}
                onChange={(e) => setType(e.target.value as AdSpaceType)}
              >
                {TYPES.map((t) => (
                  <MenuItem key={t} value={t}>
                    {t}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Price per day (RON)"
              fullWidth
              size="small"
              type="number"
              value={pricePerDay}
              onChange={(e) =>
                setPricePerDay(
                  e.target.value === '' ? '' : Number(e.target.value)
                )
              }
              error={!!errors.pricePerDay}
              helperText={errors.pricePerDay}
            />
          </Grid>

          <Grid size={{ xs: 12 }}>
            <TextField
              label="Address"
              fullWidth
              size="small"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              error={!!errors.address}
              helperText={errors.address}
            />
          </Grid>
        </Grid>
      </DialogContent>

      <DialogActions sx={{ p: 2 }}>
        <Button onClick={handleClose} disabled={saving}>
          Cancel
        </Button>
        <Button
          onClick={handleSave}
          variant="contained"
          disabled={saving}
        >
          {saving ? 'Saving...' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
