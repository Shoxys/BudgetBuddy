/**
 * TotalBalanceCard component for displaying total account balance.
 * Renders balance amount with an icon.
 */

import { formatMoney } from '../../Utils/helpers';

export default function TotalBalanceCard({ totalBalance = 0 }) {
  // Layout
  return (
    <div className="bg-primary_blue px-4 py-2 flex flex-row justify-between gap-2 text-white rounded-lg w-72">
      {/* Balance Info */}
      <div>
        <h2 className="font-body text-md font-normal">Total Balance</h2>
        <h1 className="text-2xl font-header font-bold">{formatMoney(totalBalance)}</h1>
      </div>
      {/* Icon */}
      <div className="flex items-center">
        <img className="w-11 h-11" src="/assets/balance-icon.png" alt="Balance icon" />
      </div>
    </div>
  );
}