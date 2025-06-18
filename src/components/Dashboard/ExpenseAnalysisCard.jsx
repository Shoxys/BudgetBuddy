const expenses = [
  { label: 'Retail and Shopping', value: 1750, color: 'bg-primary_blue' },
  { label: 'Travel and Dining', value: 600, color: 'bg-secondary_red' },
  { label: 'Bills and Utilities', value: 1200, color: 'bg-bb_green' },
  { label: 'Others', value: 450, color: 'bg-bb_yellow' },
  { label: 'Available', value: 1000, color: 'bg-gray-300' },
];

const total = expenses.reduce((sum, item) => sum + item.value, 0);

export default function ExpenseAnalysisCard() {
    return(
        <div className="bg-white col-span-1 row-span-1 row-start-2 rounded-lg shadow-bb-general px-5 pb-4 pt-2">
            <h2 className="text-md font-semibold text-gray-700 mb-2">Expense Analysis</h2>

            {/* Bar */}
            <div className="flex h-7 w-full rounded overflow-hidden mb-4">
              {expenses.map((item, index) => (
                <div
                  key={index}
                  className={`${item.color} h-full`}
                  style={{ width: `${(item.value / total) * 100}%` }}
                  title={`${item.label}: $${item.value}`}
                />
              ))}
            </div>

            {/* Legend */}
            <div className="space-y-2.5">
              {expenses.map((item, index) => (
                <div key={index} className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className={`w-2.5 h-2.5 rounded-full ${item.color}`} />
                    <span className="text-sm text-gray-600">{item.label}</span>
                  </div>
                  <span className="font-medium text-sm text-gray-900">${item.value.toLocaleString()}</span>
                </div>
              ))}
            </div>     
        </div>
    )
}