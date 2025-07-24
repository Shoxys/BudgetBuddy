/**
 * Investments component for selecting account types during onboarding.
 * Renders toggleable buttons for investments and savings.
 */

import { AccountTypes } from '../../constants/AccountConstants';

export default function Investments({ accountSelections = {}, setAccountSelections }) {
  // Handlers
  const toggleSelection = (type) => {
    setAccountSelections((prev) => ({ ...prev, [type]: !prev[type] }));
  };

  // Layout
  return (
    <div className="flex justify-center flex-row gap-4 3xl:gap-10 mt-8 3xl:mt-20">
      {/* Investments Button */}
      <button
        onClick={() => toggleSelection(AccountTypes.INVESTMENTS)}
        className={`rounded-2xl relative overflow-hidden transition-all duration-200 ${
          accountSelections[AccountTypes.INVESTMENTS]
            ? 'ring-4 ring-primary_blue shadow-custom-blue'
            : 'ring-2 ring-transparent'
        }`}
      >
        <img
          className="rounded-2xl 3xl:w-96"
          src="/assets/investments.png"
          alt="Investment options"
        />
      </button>
      {/* Savings Button */}
      <button
        onClick={() => toggleSelection(AccountTypes.SAVINGS)}
        className={`rounded-2xl relative transition-all duration-200 ${
          accountSelections[AccountTypes.SAVINGS]
            ? 'ring-4 ring-primary_blue shadow-custom-blue'
            : 'ring-2 ring-transparent'
        }`}
      >
        <img className="rounded-2xl 3xl:w-96" src="/assets/savings.png" alt="Savings options" />
      </button>
    </div>
  );
}