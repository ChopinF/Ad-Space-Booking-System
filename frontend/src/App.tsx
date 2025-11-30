import './App.css';
import { AdSpaceList } from './components/AdSpaceList';
import { BookingRequestForm } from './components/BookingRequestForm';
import { BookingList } from './components/BookingList';
import { EditAdSpaceDialog } from './components/EditAdSpaceDialog';

import {
  Button,
  createTheme,
  CssBaseline,
  ThemeProvider,
  Box,
  Stack,
  Container,
} from '@mui/material';
import { purple } from '@mui/material/colors';

import { Routes, Route, Link } from 'react-router-dom';

const theme = createTheme({
  palette: {
    primary: {
      main: '#6d1b7b',
    },
    secondary: purple,
  },
  typography: {
    fontFamily: `"Geist", system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif`,
    h5: {
      fontWeight: 600,
      letterSpacing: '0.03em',
    },
    button: {
      textTransform: 'none',
      fontWeight: 500,
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="xl" sx={{ py: 4 }}>
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            mb: 4,
          }}
        >
          <Box>
            <span style={{ fontWeight: 700, fontSize: 24 }}>Generatik Ads</span>
          </Box>
          <Stack direction="row" spacing={2}>
            <Button
              variant="contained"
              color="primary"
              component={Link}
              to="/"
            >
              Ad Spaces
            </Button>
            <Button
              variant="outlined"
              color="primary"
              component={Link}
              to="/bookings"
            >
              Bookings
            </Button>
          </Stack>
        </Box>

        <Routes>
          <Route
            path="/"
            element={
              <>
                <AdSpaceList />
                <EditAdSpaceDialog />
                <Box sx={{ mt: 4 }}>
                  <BookingRequestForm />
                </Box>
              </>
            }
          />

          <Route
            path="/bookings"
            element={
              <Box>
                <BookingList />
              </Box>
            }
          />
        </Routes>
      </Container>
    </ThemeProvider>
  );
}

export default App;
