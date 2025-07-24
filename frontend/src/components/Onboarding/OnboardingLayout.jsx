/**
 * OnboardingLayout component for structuring onboarding steps.
 * Renders a layout with logo, progress, and navigation buttons.
 */

import Logo from '../../components/Logo';
import StepsProgress from './StepsProgress';

export default function OnboardingLayout({
  children,
  step = 0,
  onNext,
  onBack,
  onSkip,
  title,
  subtitle,
  isNextLoading = false,
  files = [],
}) {
  const MAX_STEPS = 3;
  const isLastStep = step === MAX_STEPS - 1;

  const continueButtonText = () => {
    if (isLastStep) {
      return files.length > 0 ? 'Upload & Finish →' : 'Finish →';
    }
    return 'Continue →';
  };

  // Layout
  return (
    <div className="flex justify-center min-h-screen min-w-screen bg-[url('/assets/onboard-bg.png')] bg-cover bg-center">
      <div className="w-6/12 bg-white rounded-xl opacity-80 py-3 px-12">
        {/* Header */}
        <div className="flex justify-between items-center mb-4">
          <Logo />
        </div>
        <p className="font-body text-gray-500 text-md mt-2">
          {step + 1} of {MAX_STEPS}
        </p>
        {/* Progress */}
        <div className="flex flex-row mt-2">
          <StepsProgress step={step} />
        </div>
        <h1 className="font-header text-3xl 3xl:text-5xl font-bold mt-5 3xl:mt-16 whitespace-pre-wrap">
          {title}
        </h1>
        <p className="text-gray-700 font-body 3xl:text-xl mt-3 3xl:mt-6 mb-8 3xl:mb-12">{subtitle}</p>

        {/* Content */}
        {children}

        {/* Navigation */}
        <div className="mt-14 3xl:mt-28 w-full flex justify-between items-center">
          {step !== 0 ? (
            <button
              onClick={onBack}
              className="font-body text-md 3xl:text-lg bg-gray-100 py-2 px-5 rounded-lg text-gray-600 hover:bg-slate-200 transition-colors"
            >
              ← Back
            </button>
          ) : (
            <div />
          )}
          <div className="flex gap-4">
            <button
              onClick={onSkip}
              className="text-md 3xl:text-lg px-5 py-2 font-body text-gray-700 hover:bg-slate-100 rounded-lg"
            >
              {isLastStep ? 'Do this later' : 'Skip for now'}
            </button>
            <button
              onClick={onNext}
              className="btn-primary shadow-custom-blue 3xl:text-lg px-7 py-2 text-md font-normal rounded-lg hover:bg-btn_hover transition-colors"
              disabled={isNextLoading}
            >
              {isNextLoading ? 'Processing...' : continueButtonText()}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}