/**
 * Main application component defining client-side routes.
 */

// Component Imports
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Onboarding from './pages/Onboarding';
import Dashboard from './pages/Dashboard';
import Transactions from './pages/Transactions';
import SavingGoals from './pages/SavingGoals';
import Settings from './pages/Settings';
import NotFound from './pages/NotFound';

// Constant Imports
import { ROUTES } from './constants/AppConstants';

export default function App() {
  // Layout
  return (
    <Routes>
      <Route path={ROUTES.HOME} element={<Home />} />
      <Route path={ROUTES.LOGIN} element={<Login />} />
      <Route path={ROUTES.SIGNUP} element={<Signup />} />
      <Route path={ROUTES.ONBOARDING} element={<Onboarding />} />
      <Route path={ROUTES.DASHBOARD} element={<Dashboard />} />
      <Route path={ROUTES.TRANSACTIONS} element={<Transactions />} />
      <Route path={ROUTES.SAVING_GOALS} element={<SavingGoals />} />
      <Route path={ROUTES.SETTINGS} element={<Settings />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}