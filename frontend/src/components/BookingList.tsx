import React, { useEffect } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Chip,
  Button,
  CircularProgress,
  Alert,
  Box,
} from '@mui/material';

import { useBookingListStore, StatusFilter } from '../store/useBookingListStore';
import { useAdSpacesStore } from '../store/useAdSpacesStore';
import { BookingStatus } from '../api/bookingApi';

const STATUS_FILTERS: StatusFilter[] = ['ALL', 'Pending', 'Approved', 'Rejected'];

const statusColors: Record<BookingStatus, string> = {
  Pending: '#facc15',
  Approved: '#4ade80',
  Rejected: '#f87171',
};

export const BookingList: React.FC = () => {
  const {
    items,
    loading,
    error,
    statusFilter,
    setStatusFilter,
    fetchAll,
    approveOne,
    rejectOne,
  } = useBookingListStore();

  const { items: adSpaces } = useAdSpacesStore();

  useEffect(() => {
    fetchAll();
  }, [statusFilter, fetchAll]);

  const getAdSpaceName = (adSpaceId: number) => {
    const found = adSpaces.find((a) => a.id === adSpaceId);
    return found ? found.name : `Ad Space #${adSpaceId}`;
  };

  return (
    <Card sx={{ p: 4, backgroundColor: '#f7ebfa', pt: 10 }}>
      <CardContent>
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'baseline',
            mb: 2,
          }}
        >
          <Typography variant="h3" fontWeight={800} gutterBottom>
            Booking Requests
          </Typography>

          <Typography variant="body2" color="text.secondary">
            {items.length} bookings
          </Typography>
        </Box>

        <Grid container spacing={2} sx={{ mb: 2 }}>
          <Grid size={{ xs: 12, sm: 4, md: 3 }}>
            <FormControl fullWidth size="small">
              <InputLabel id="filter-status-label">Status</InputLabel>
              <Select
                labelId="filter-status-label"
                value={statusFilter}
                label="Status"
                onChange={(e) => setStatusFilter(e.target.value as StatusFilter)}
              >
                {STATUS_FILTERS.map((s) => (
                  <MenuItem key={s} value={s}>
                    {s}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
        </Grid>

        {loading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        )}

        {error && !loading && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {!loading && !error && (
          <>
            {items.length === 0 ? (
              <Typography variant="body1" color="text.secondary">
                No bookings found for the selected filter.
              </Typography>
            ) : (
              <Table size="small" sx={{ marginTop: 8 }}>
                <TableHead
                  sx={{
                    backgroundColor: '#c7d2fe',
                    '& .MuiTableCell-head': {
                      fontWeight: 600,
                    },
                  }}
                >
                  <TableRow>
                    <TableCell><Typography fontWeight={900}>Ad Space</Typography></TableCell>
                    <TableCell><Typography fontWeight={900}>Advertiser</Typography></TableCell>
                    <TableCell><Typography fontWeight={900}>Dates</Typography></TableCell>
                    <TableCell><Typography fontWeight={900}>Status</Typography></TableCell>
                    <TableCell><Typography fontWeight={900}>Total cost</Typography></TableCell>
                    <TableCell align="right"><Typography fontWeight={900}>Actions</Typography></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {items.map((b) => (
                    <TableRow key={b.id}
                      sx={{
                        '& td': {
                          py: 2.5,
                        },
                      }}
                    >
                      <TableCell>
                        <Typography fontWeight={600}>
                          {getAdSpaceName(b.adSpaceId)}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          #{b.adSpaceId}
                        </Typography>
                      </TableCell>

                      <TableCell>
                        <Typography fontWeight={500}>{b.advertiserName}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {b.advertiserEmail}
                        </Typography>
                      </TableCell>

                      <TableCell>
                        <Typography variant="body2">
                          {b.startDate} → {b.endDate}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          Created at {new Date(b.createdAt).toLocaleString()}
                        </Typography>
                      </TableCell>

                      <TableCell>
                        <Chip
                          label={b.status}
                          size="small"
                          sx={{
                            backgroundColor: statusColors[b.status],
                            color: '#111827',
                            fontWeight: 600,
                          }}
                        />
                      </TableCell>

                      <TableCell>
                        <Typography fontWeight={600}>{b.totalCost} RON</Typography>
                      </TableCell>

                      <TableCell align="right">
                        {b.status === 'Pending' ? (
                          <Grid container spacing={1} justifyContent="flex-end">
                            <Grid>
                              <Button
                                size="small"
                                variant="contained"
                                color="primary"
                                onClick={() => approveOne(b.id)}
                                sx={{
                                  textTransform: 'none',
                                  borderRadius: '999px',
                                  px: 2,
                                }}
                              >
                                Approve
                              </Button>
                            </Grid>
                            <Grid >
                              <Button
                                size="small"
                                variant="outlined"
                                color="error"
                                onClick={() => rejectOne(b.id)}
                                sx={{
                                  textTransform: 'none',
                                  borderRadius: '999px',
                                  px: 2,
                                }}
                              >
                                Reject
                              </Button>
                            </Grid>
                          </Grid>
                        ) : (
                          <Typography variant="body2" color="text.secondary">
                            No actions
                          </Typography>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </>
        )}
      </CardContent>
    </Card>
  );
};
