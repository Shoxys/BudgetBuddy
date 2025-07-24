/**
 * ExpenseAnalysisCard component for displaying expense distribution.
 * Renders a horizontal bar chart and legend for expense categories.
 */

import { formatMoney } from '../../Utils/helpers';

const expenseColors = [
  'bg-primary_blue',
  'bg-secondary_red',
  'bg-bb_green',
  'bg-bb_yellow',
  'bg-bb_purple',
];

const selectColor = (index) => expenseColors[index % expenseColors.length];

export default function ExpenseAnalysisCard({ expenses = [] }) {
  const isValidData = Array.isArray(expenses) && expenses.length > 0;
  const totalValue = isValidData ? expenses.reduce((sum, item) => sum + item.value, 0) : 0;

  // Layout
  return (
    <div className="bg-white col-span-1 row-span-1 row-start-2 rounded-lg shadow-bb-general px-5 pb-4 pt-2">
      {/* Header */}
      <h2 className="text-md 3xl:text-xl font-semibold font-header text-gray-700 mb-[1.5vh]">
        Expense Analysis
      </h2>

      {/* Bar Chart */}
      <div className="flex h-[4vh] w-full rounded overflow-hidden mb-5">
        {isValidData ? (
          expenses.map((item, index) => (
            <div
              key={item.label}
              className={`${selectColor(index)} h-full`}
              style={{ width: `${totalValue === 0 ? '0%' : (item.value / totalValue) * 100}%` }}
              title={`${item.label}: ${formatMoney(item.value)}`}
            />
          ))
        ) : (
          <div className="h-full bg-gray-300 w-full" />
        )}
      </div>

      {/* Legend */}
      <div className="space-y-2.5">
        {isValidData ? (
          expenses.map((item, index) => (
            <div key={item.label} className="flex items-center justify-between text-sm 3xl:text-lg">
              <div className="flex items-center gap-2">
                <span className={`w-2.5 h-2.5 rounded-full ${selectColor(index)}`} />
                <span className="text-gray-600">{item.label}</span>
              </div>
              <span className="font-medium text-gray-900">{formatMoney(item.value)}</span>
            </div>
          ))
        ) : (
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-gray-300" />
              <span className="text-gray-600">Empty</span>
            </div>
            <span className="font-medium text-gray-900">$0</span>
          </div>
        )}
      </div>
    </div>
  );
}