/**
 * TransactionsCard component for displaying recent transactions.
 * Renders a list of transactions with date, category, and amount.
 */

import { format } from 'date-fns';
import { CategoryColors } from '../../constants/TransactionConstants';

export default function TransactionsCard({ data = [] }) {
  const isDataValid = Array.isArray(data) && data.length > 0;

  // Layout
  return (
    <div className="bg-white col-span-1 col-start-2 row-span-1 row-start-2 rounded-lg shadow-bb-general px-5 pb-4 pt-2">
      {/* Header */}
      <h2 className="text-md 3xl:text-xl font-semibold font-header text-gray-700 mb-[1vh]">
        Recent Transactions
      </h2>

      {/* Transactions or Message */}
      <div className="space-y-2.5">
        {isDataValid ? (
          data.map((transaction, index) => {
            const tagStyle = CategoryColors[transaction.category] || 'bg-gray-100 text-gray-600';
            return (
              <div key={index} className="bg-gray-50 py-[1.3vh] px-[1.2vw] rounded-md">
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm 3xl:text-lg font-semibold text-blue-600">
                    {format(new Date(transaction.date), 'dd MMM yyyy')}
                  </span>
                  <span
                    className={`text-xs 3xl:text-lg px-[0.5vw] py-[0.5vh] rounded-full font-medium ${tagStyle}`}
                  >
                    {transaction.category}
                  </span>
                </div>
                <div className="flex items-center justify-between text-xs 3xl:text-lg">
                  <span className="text-gray-700">{transaction.description}</span>
                  <span className="font-semibold text-gray-900">
                    ${Math.abs(transaction.amount).toFixed(2)}
                  </span>
                </div>
              </div>
            );
          })
        ) : (
          <div className="p-4 mt-3 text-center font-body text-gray-700 text-lg bg-neutral-100 rounded-md shadow-bb-general">
            No Recent Transactions!
          </div>
        )}
      </div>
    </div>
  );
}