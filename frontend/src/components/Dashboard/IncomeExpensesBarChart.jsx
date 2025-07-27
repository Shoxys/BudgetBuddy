/**
 * IncomeExpensesBarChart component for comparing income and expenses.
 * Renders a vertical bar chart with net income display.
 */

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts';
import { formatMoney } from '../../Utils/helpers';

const DEFAULT_DATA = [
  { name: 'Last month', income: 0, expenses: 0 },
  { name: 'This month', income: 0, expenses: 0 },
];

export default function IncomeExpensesBarChart({ data = DEFAULT_DATA }) {
  // Layout
  return (
    <div className="w-full h-[32vh] flex flex-col items-center justify-between mt-4">
      {/* Bar Chart */}
      <div className="h-full w-full 3xl:text-lg">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} barCategoryGap="25%" barGap={10}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
            <YAxis hide />
            <Tooltip formatter={(value) => formatMoney(value)} />
            <Bar dataKey="income" name="Income" fill="#0096ff" radius={[4, 4, 0, 0]} />
            <Bar dataKey="expenses" name="Expenses" fill="#e92c81" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Net Income Display */}
      <div className="flex justify-around w-full">
        {data.map(({ name, income, expenses }) => (
          <div key={name} className="flex flex-col items-center">
            <div className="flex items-baseline mb-1">
              <span className="w-1 h-5 bg-primary_blue mr-0.5 rounded-sm" />
              <span className="w-1 h-5 bg-secondary_red mr-2 rounded-sm" />
              <span className="text-gray-500 text-sm 3xl:text-lg">
                {name === 'Last month' ? 'Prev' : 'Current'} Net Income
              </span>
            </div>
            <span className="font-semibold text-lg text-gray-900">
              {formatMoney(income + expenses)}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}