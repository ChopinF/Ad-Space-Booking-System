import React, { useMemo, useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress,
} from '@mui/material';
import { useBookingStore } from '../store/useBookingStore';

export const BookingRequestForm: React.FC = () => {
  const {
    dialogOpen,
    selectedAdSpace,
    closeDialog,
    submitting,
    submitError,
    submitSuccess,
    submitBooking,
  } = useBookingStore();

  const [advertiserName, setAdvertiserName] = useState('');
  const [advertiserEmail, setAdvertiserEmail] = useState('');
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [errors, setErrors] = useState<Record<string, string>>({});

  // reset form when dialog opens/closes
  React.useEffect(() => {
    if (dialogOpen && selectedAdSpace) {
      setAdvertiserName('');
      setAdvertiserEmail('');
      setStartDate('');
      setEndDate('');
      setErrors({});
    }
  }, [dialogOpen, selectedAdSpace]);

  // compute days + total cost
  const { totalDays, totalCost } = useMemo(() => {
    if (!startDate || !endDate || !selectedAdSpace) {
      return { totalDays: 0, totalCost: 0 };
    }
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffMs = end.getTime() - start.getTime();
    const days = diffMs > 0 ? diffMs / (1000 * 60 * 60 * 24) : 0;
    const roundedDays = Math.floor(days);
    return {
      totalDays: roundedDays,
      totalCost: roundedDays * selectedAdSpace.pricePerDay,
    };
  }, [startDate, endDate, selectedAdSpace]);

  const validate = () => {
    const newErrors: Record<string, string> = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!advertiserName.trim()) {
      newErrors.advertiserName = 'Advertiser name is required';
    }
    if (!advertiserEmail.trim()) {
      newErrors.advertiserEmail = 'Advertiser email is required';
    } else if (!emailRegex.test(advertiserEmail.trim())) {
      newErrors.advertiserEmail = 'Invalid email format';
    }
    if (!startDate) {
      newErrors.startDate = 'Start date is required';
    }
    if (!endDate) {
      newErrors.endDate = 'End date is required';
    }

    if (startDate && endDate) {
      const today = new Date();
      const start = new Date(startDate);
      const end = new Date(endDate);

      // normalize "today" to midnight to compare days only
      today.setHours(0, 0, 0, 0);

      if (start <= today || end <= today) {
        newErrors.startDate =
          newErrors.startDate || 'Start and end dates must both be in the future';
      }

      if (end <= start) {
        newErrors.endDate = 'End date must be after start date';
      }

      const diffMs = end.getTime() - start.getTime();
      const days = diffMs / (1000 * 60 * 60 * 24);
      if (days < 7) {
        newErrors.endDate = 'Minimum booking duration is 7 days';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!selectedAdSpace) return;
    const isValid = validate();
    if (!isValid) return;

    await submitBooking({
      adSpaceId: selectedAdSpace.id,
      advertiserName: advertiserName.trim(),
      advertiserEmail: advertiserEmail.trim(),
      startDate,
      endDate,
    });
  };

  const handleClose = () => {
    if (submitting) return;
    closeDialog();
  };

  if (!selectedAdSpace) {
    // nothing selected -> don't render dialog content
    return null;
  }

  return (
    <Dialog open={dialogOpen} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>Book Ad Space</DialogTitle>

      <DialogContent sx={{ pt: 1 }}>
        <Box sx={{ mb: 2 }}>
          <Typography variant="subtitle2" color="text.secondary">
            Ad Space
          </Typography>
          <Typography variant="h6">{selectedAdSpace.name}</Typography>
          <Typography variant="body2" color="text.secondary">
            {selectedAdSpace.city} · {selectedAdSpace.type}
          </Typography>
          <Typography variant="body2" sx={{ mt: 0.5 }}>
            Price: <strong>{selectedAdSpace.pricePerDay} RON / day</strong>
          </Typography>
        </Box>

        {submitError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {submitError}
          </Alert>
        )}

        {submitSuccess && (
          <Alert severity="success" sx={{ mb: 2 }}>
            Booking request created successfully! You should close this window now!
          </Alert>
        )}

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <TextField
            label="Advertiser name"
            size="small"
            value={advertiserName}
            onChange={(e) => setAdvertiserName(e.target.value)}
            error={!!errors.advertiserName}
            helperText={errors.advertiserName}
            fullWidth
          />

          <TextField
            label="Advertiser email"
            size="small"
            value={advertiserEmail}
            onChange={(e) => setAdvertiserEmail(e.target.value)}
            error={!!errors.advertiserEmail}
            helperText={errors.advertiserEmail}
            fullWidth
          />

          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Start date"
              type="date"
              size="small"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              error={!!errors.startDate}
              helperText={errors.startDate}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />

            <TextField
              label="End date"
              type="date"
              size="small"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              error={!!errors.endDate}
              helperText={errors.endDate}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
          </Box>

          <Box sx={{ mt: 1 }}>
            <Typography variant="body2" color="text.secondary">
              Total days: <strong>{totalDays || '-'}</strong>
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Estimated total cost:{' '}
              <strong>
                {totalCost ? `${totalCost} RON` : '-'}
              </strong>
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Minimum booking duration is 7 days.
            </Typography>
          </Box>
        </Box>
      </DialogContent>

      <DialogActions sx={{ p: 2 }}>
        <Button onClick={handleClose} disabled={submitting}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={submitting}
        >
          {submitting ? <CircularProgress size={20} /> : 'Submit booking'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
