import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid
} from "recharts";

const data = [
  {
    name: "Last month",
    Income: 3000,
    Expenses: 1250
  },
  {
    name: "This month",
    Income: 3500,
    Expenses: 2000
  }
];

const formatMoney = (amount) =>
  amount.toLocaleString("en-AU", {
    style: "currency",
    currency: "AUD"
  });

export default function IncomeExpensesBarChart() {
  return (
    <div className="w-full max-w-md h-[220px] flex flex-col items-center justify-between mt-4">
      {/* Bar Chart */}
      <div className="h-[100%] w-full">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} barCategoryGap="25%" barGap={10}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
            <YAxis hide />
            <Tooltip 
              formatter={(value) => (formatMoney(value))}
              />
            <Bar dataKey="Income" fill="#0096ff" radius={[4, 4, 0, 0]} />
            <Bar dataKey="Expenses" fill="#e92c81" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/*Net Income Display */}
      <div className="flex justify-around w-full">
        {data.map(({ name, Income, Expenses }, index) => (
          <div key={index} className="flex flex-col items-center">
            <div className="flex items-baseline mb-1">
              <span className="w-1 h-5 bg-primary_blue mr-0.5 rounded-sm"></span>
              <span className="w-1 h-5 bg-secondary_red mr-2 rounded-sm"></span>
              <span className="text-gray-500 text-sm">{name == "Last month" ? "Prev" : "Current"} Net Income</span>
            </div>
            <span className="font-semibold text-lg text-gray-900">
              {formatMoney(Income - Expenses)}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}
