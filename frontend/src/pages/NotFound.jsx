/**
 * NotFound component for handling 404 errors.
 * Displays a styled error message with a navigation button to return to the home page.
 */


import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../constants/AppConstants';

export default function NotFound() {
  const navigate = useNavigate();

  // Handlers
  const handleReturnHome = () => {
    navigate(ROUTES.HOME);
  };

  // Layout
  return (
    <div className="min-h-screen bg-[url('/assets/home-bg.png')] bg-cover bg-no-repeat pl-40 3xl:pl-72 pr-9 flex items-center justify-center">
      {/* Main Content */}
      <div className="text-center w-1/2 bg-white bg-opacity-90 rounded-xl p-10 shadow-bb-general">
        <h1 className="font-header text-5xl 3xl:text-6xl font-bold text-gray-800">
          404 - Page Not Found
        </h1>
        <p className="mt-6 text-2xl 3xl:text-3xl font-body text-gray-500">
          The requested page does not exist.
        </p>
        <button
          onClick={handleReturnHome}
          className="btn-primary font-header font-bold text-2xl 3xl:text-3xl px-10 py-3 3xl:px-12 3xl:py-4 rounded-xl mt-8"
        >
          Return to Home
        </button>
      </div>
    </div>
  );
}