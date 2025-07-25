/**
 * Home page component for landing and navigation.
 */
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../constants/AppConstants';
/**
 * Home page component for landing and navigation.
 */
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../constants/AppConstants';
import Logo from '../components/Logo';

/**
 * Home page with navigation to signup and login.
 * @returns {JSX.Element} Home component.
 */
/**
 * Home page with navigation to signup and login.
 * @returns {JSX.Element} Home component.
 */
export default function Home() {
  const navigate = useNavigate();

  // Main layout

  // Main layout
  return (
    <div
  className="min-h-screen
    pl-4 pr-4 sm:pl-8 sm:pr-8 md:pl-16 md:pr-16 lg:pl-24 lg:pr-12 xl:pl-32 xl:pr-16 2xl:pl-36 2xl:pr-20 3xl:pl-72 3xl:pr-9
    bg-[url('/assets/home-bg.png')] bg-cover bg-no-repeat"
  style={{ backgroundSize: 'cover', backgroundPosition: 'center', width: '100vw', height: '100vh' }}
>
  {/* Header with logo and navigation */}
  <div className="pt-4 sm:pt-6 md:pt-8 3xl:pt-10 flex items-center justify-between">
    <Logo />
    <div className="flex items-center">
      <button
        className="text-bb_darkgrey bg-bb_salmon font-body
          text-sm sm:text-base md:text-lg lg:text-xl xl:text-2xl 3xl:text-4xl
          rounded-lg sm:rounded-xl font-bold
          px-3 py-2 sm:px-4 sm:py-2.5 md:px-5 md:py-3 lg:px-6 lg:py-4 xl:px-8 xl:py-5 3xl:px-10 3xl:py-7
          mr-2 sm:mr-3 md:mr-4 lg:mr-5 xl:mr-5 3xl:mr-8
          hover-effect hover:bg-neutral-50"
        onClick={() => navigate(ROUTES.SIGNUP)}
      >
        Sign Up
      </button>
      <button
        className="btn-primary font-header font-bold
          rounded-lg sm:rounded-xl
          px-4 py-2 sm:px-5 sm:py-2.5 md:px-6 md:py-3 lg:px-8 lg:py-4 xl:px-10 xl:py-5 3xl:px-14 3xl:py-7
          text-sm sm:text-base md:text-lg lg:text-xl xl:text-2xl 3xl:text-4xl"
        onClick={() => navigate(ROUTES.LOGIN)}
      >
        Login
      </button>
    </div>
  </div>
  {/* Main content */}
  <div className="w-full sm:w-11/12 md:w-4/5 lg:w-3/5 xl:w-1/2 2xl:w-2/5 pt-6 sm:pt-8 md:pt-10 lg:pt-10 xl:pt-12">
    <h1 className="font-header
      text-3xl sm:text-4xl md:text-5xl lg:text-6xl 2xl:text-7xl 3xl:text-8xl
      font-bold 3xl:leading-tight lg:leading-[]">
      Gain <br /> Personalised <br /> Financial <br /> Insights
    </h1>
    <h2 className="mt-3 sm:mt-4 md:mt-5 lg:mt-8 xl:mt-10 3xl:mt-12
      mb-3 sm:mb-4 md:mb-5 lg:mb-6 xl:mb-7 3xl:mb-12
      font-header
      text-base sm:text-lg md:text-xl lg:text-2xl xl:text-2xl 3xl:text-4xl">
      Built for users seeking to manage their finances effectively, it offers transaction logging, spending categorisation, goal tracking, and visualisations.
    </h2>
    <button
      className="btn-primary
        text-base sm:text-lg md:text-xl lg:text-2xl xl:text-3xl 3xl:text-4xl
        px-6 py-2 sm:px-8 sm:py-2.5 md:px-7 md:py-2 lg:px-8 lg:py-3 xl:px-10 xl:py-3.5 3xl:px-14 3xl:py-5
        rounded-lg sm:rounded-xl"
      onClick={() => navigate(ROUTES.SIGNUP)}
    >
      Get Started
    </button>
  </div>
</div>
  );
}