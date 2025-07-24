/**
 * IncomeCard component for displaying income trends.
 * Renders a line chart of income data.
 */

import IncomeLineChart from './IncomeLineChart';

export default function IncomeCard({ data = [] }) {
  // Layout
  return (
    <div className="h-[35vh] bg-white col-span-2 row-span-1 rounded-lg shadow-bb-general px-4 pb-2 pt-2">
      <IncomeLineChart data={data} />
    </div>
  );
}