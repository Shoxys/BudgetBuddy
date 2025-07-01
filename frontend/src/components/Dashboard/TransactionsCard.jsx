import { format } from 'date-fns';
const transactions = [
  {
    date: '2025-04-17',
    description: 'Grocery Store',
    category: 'Personal care',
    amount: -85.5,
  },
  {
    date: '2025-04-16',
    description: 'Salary Deposit',
    category: 'Restaurants & takeaway',
    amount: -85.5,
  },
  {
    date: '2025-03-01',
    description: 'Electricity Bill',
    category: 'Attractions & events',
    amount: -85.5,
  },
];

const categoryColors = {
  'Personal care': 'bg-pink-100 text-pink-500',
  'Restaurants & takeaway': 'bg-yellow-100 text-yellow-600',
  'Attractions & events': 'bg-purple-100 text-purple-500',
  'Bills and Utilities': 'bg-green-100 text-green-500',
  'Retail and Shopping': 'bg-blue-100 text-blue-500',
  'Available': 'bg-gray-100 text-gray-600',
};

export default function TransactionsCard() {
    return(
        <div className="bg-white col-span-1 col-start-2 row-span-1 row-start-2 rounded-lg shadow-bb-general px-5 pb-4 pt-2">
            <h2 className="text-md font-semibold text-gray-700 mb-1">Recent Transactions</h2>

            <div className="space-y-2.5">
                {transactions.map((transaction, index) => {
                const tagStyle = categoryColors[transaction.category] || 'bg-gray-100 text-gray-600';
                return (
                    <div key={index} className="bg-gray-50 py-2 px-3 rounded-md">
                        <div className="flex items-center justify-between mb-1">
                            <span className="text-sm font-semibold text-blue-600">
                            {format(new Date(transaction.date), 'dd MMM yyyy')}
                            </span>
                            <span className={`text-xs px-2 py-1 rounded-full font-medium ${tagStyle}`}>
                            {transaction.category}
                            </span>
                        </div>
                        <div className="flex items-center justify-between">
                            <span className="text-xs text-gray-700">{transaction.description}</span>
                            <span className="text-xs font-semibold text-gray-900">${Math.abs(transaction.amount).toFixed(2)}</span>
                        </div>
                    </div>
                );
                })}
            </div>
        </div>
    )
}