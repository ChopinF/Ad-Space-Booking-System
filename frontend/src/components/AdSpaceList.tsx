import React, { useEffect } from 'react';
import { useBookingStore } from '../store/useBookingStore';
import {
  Grid,
  Card,
  Chip,
  CardContent,
  Typography,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  CircularProgress,
  Alert,
  Box,
} from '@mui/material';

import { useAdSpacesStore } from '../store/useAdSpacesStore';
import { FilterType, FilterCity } from '../types/adSpace';
import { AdSpace } from '../types/adSpace';

const AD_SPACE_TYPES: FilterType[] = ['ALL', 'Billboard', 'BusStop', 'MallDisplay', 'TransitAd'];
const CITIES: FilterCity[] = ['ALL', 'Bucuresti', 'Cluj', 'Roman', 'Brasov', 'Sibiu', 'Constanta', 'Craiova', 'Iasi', 'Suceava'];
const typeColors: Record<string, string> = {
  Billboard: '#c86ad8',
  BusStop: '#5cbba5',
  MallDisplay: '#6b8fe6',
  TransitAd: '#f2a55f',
};

interface AdSpaceListProps {
  onEdit?: (adSpaceId: number) => void;
};

export const AdSpaceList: React.FC<AdSpaceListProps> = () => {
  const {
    items,
    loading,
    error,
    filterType,
    filterCity,
    setFilterType,
    setFilterCity,
    fetchAll,
    deleteOne,
    getFiltered,
    openEdit
  } = useAdSpacesStore();

  const { openForAdSpace } = useBookingStore();

  // fetch every time when we switch the filters
  useEffect(() => {
    fetchAll()
  }, [filterType, filterCity, fetchAll]);

  const filteredItems = getFiltered();

  //const handleBookNow = (id: number) => {
  //  onBookNow?.(id);
  //};
  const handleBookNow = (space: AdSpace) => {
    openForAdSpace(space);
    console.log(space);
  };

  const handleEdit = (id: number) => {
    openEdit(id);
  };

  const handleDelete = (id: number) => {
    if (window.confirm('Are you sure you want to delete this ad space?')) {
      deleteOne(id);
    }
  };

  return (
    <Card sx={{ p: 4, backgroundColor: '#f7ebfa', pt: 10 }}>
      <CardContent>
        <Typography variant="h3" gutterBottom fontWeight={800} marginBottom={10}>
          Available Ad Spaces
        </Typography>

        {/* filters */}
        <Grid container spacing={10} sx={{ mb: 2 }}>
          <Grid container size={{ xs: 12, sm: 6, md: 3 }}>
            <FormControl fullWidth size="small">
              <InputLabel id="filter-type-label">Type</InputLabel>
              <Select
                labelId="filter-type-label"
                value={filterType}
                label="Type"
                onChange={(e) => setFilterType(e.target.value as FilterType)}
              >
                {AD_SPACE_TYPES.map((t) => (
                  <MenuItem key={t} value={t}>
                    {t}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          <Grid container size={{ xs: 12, sm: 6, md: 3 }}>
            <FormControl fullWidth size="small">
              <InputLabel id="filter-city-label">City</InputLabel>
              <Select
                labelId="filter-city-label"
                value={filterCity}
                label="City"
                onChange={(e) => setFilterCity(e.target.value as FilterCity)}
              >
                {CITIES.map((c) => (
                  <MenuItem key={c} value={c}>
                    {c}
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
            {filteredItems.length === 0 ? (
              <Typography variant="body1" color="text.secondary">
                No ad spaces found for the selected filters.
              </Typography>
            ) : (
              <Table size="small" sx={{ marginTop: 8 }}>
                <TableHead style={{ backgroundColor: '#c4b5fd' }}>
                  <TableRow style={{ color: "white" }}>
                    <TableCell><Typography fontWeight={900}>Name</Typography></TableCell>
                    <TableCell><Typography fontWeight={900}>Type</Typography></TableCell>
                    <TableCell><Typography fontWeight={900}>City</Typography></TableCell>
                    <TableCell><Typography fontWeight={900}>Adress</Typography></TableCell>
                    <TableCell><Typography fontWeight={900}>Price / day</Typography></TableCell>
                    <TableCell align="right"><Typography fontWeight={900}>Actions</Typography></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredItems.map((space) => (
                    <TableRow key={space.id}
                      sx={{
                        '& td': {
                          py: 2.5,
                        },
                      }}
                    >
                      <TableCell>
                        <Typography fontWeight={500}>{space.name}</Typography>
                      </TableCell>

                      <TableCell>
                        <Chip
                          label={space.type}
                          sx={{
                            backgroundColor: typeColors[space.type],
                            color: "#fff",
                            fontWeight: 600,
                            px: 1,
                          }}
                          size="small"
                        />
                      </TableCell>

                      <TableCell>
                        <Typography fontWeight={500}>{space.city}</Typography>
                      </TableCell>

                      <TableCell>
                        <Typography fontWeight={500}>{space.address}</Typography>
                      </TableCell>

                      <TableCell>
                        <Typography fontWeight={500}>
                          {space.pricePerDay} RON
                        </Typography>
                      </TableCell>

                      <TableCell align="right">
                        <Grid container spacing={1} justifyContent="flex-end">
                          <Grid>
                            <Button
                              size="small"
                              variant="contained"
                              sx={{
                                textTransform: 'none',
                                borderRadius: '999px',
                                px: 4,
                                py: 1,
                                fontWeight: 500,
                              }}
                              onClick={() => handleBookNow(space)}
                            >
                              <Typography fontWeight={700}>Book now</Typography>
                            </Button>
                          </Grid>
                          <Grid >
                            <Button
                              variant="outlined"
                              size="small"
                              onClick={() => handleEdit(space.id)}
                            >
                              Edit
                            </Button>
                          </Grid>
                          <Grid>
                            <Button
                              variant="text"
                              color="error"
                              size="small"
                              onClick={() => handleDelete(space.id)}
                            >
                              Delete
                            </Button>
                          </Grid>
                        </Grid>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </>
        )}
      </CardContent>
    </Card >
  );
}
