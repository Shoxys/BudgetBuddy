/**
 * Settings component for managing user account settings.
 * Displays account settings form alongside a decorative background image.
 */

// Component Imports
import Sidebar from '../components/Sidebar';
import AccountSettings from '../components/Settings/AccountSettings';

// Constant Imports
import { ROUTES } from '../constants/AppConstants';

export default function Settings() {
  // Layout
  return (
    <div className="relative min-h-screen bg-bb_slate pl-20">
      {/* Sidebar */}
      <Sidebar selectedNav={ROUTES.DASHBOARD} />

      {/* Main Content */}
      <div className="flex flex-col w-full pb-4">
        {/* Header */}
        <div className="flex flex-col bg-white py-1 mt-3 px-6 shadow-bb-general">
          <div className="flex flex-row items-center gap-2">
            <img className="w-10.5" src="/assets/settings-blue.png" alt="Settings icon" />
            <h2 className="text-2xl font-header font-bold">Settings</h2>
          </div>
        </div>

        {/* Settings and Background */}
        <div className="flex flex-row gap-8 ml-8 mr-8 mt-2">
          <div className="w-3/5 bg-white py-4 text-black rounded-sm shadow-bb-general">
            <AccountSettings />
          </div>
          <div className="w-2/5 bg-white">
            <img className="w-full rounded-md" src="/assets/settings-bg.png" alt="Settings background" />
          </div>
        </div>
      </div>
    </div>
  );
}