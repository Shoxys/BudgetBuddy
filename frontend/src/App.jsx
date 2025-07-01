import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Onboarding from './pages/Onboarding';
import Dashboard from './pages/Dashboard';
import Transactions from './pages/Transactions';
import SavingGoals from './pages/SavingGoals'
import Settings from './pages/Settings';
import NotFound from './pages/NotFound';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home/>}/>
      <Route path="/login" element={<Login/>}/>
      <Route path="/signup" element={<Signup/>}/>
      <Route path="/onboarding" element={<Onboarding/>}/>
      <Route path="/dashboard" element={<Dashboard/>}/>
      <Route path="/transactions" element={<Transactions/>}/>
      <Route path="/saving-goals" element={<SavingGoals/>}/>
      <Route path="/settings" element={<Settings/>} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App
