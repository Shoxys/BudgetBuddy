import IncomeExpensesBarChart from "./IncomeExpensesBarChart"
export default function IncomeExpenseCard() {
    return(
        <div className="bg-white rounded-lg shadow-bb-general px-3 py-2">
            <h1 className="text-md font-semibold font-header">Income vs Expenses</h1>
            <IncomeExpensesBarChart/>
        </div>
    )
}