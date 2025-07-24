import IncomeExpensesBarChart from "./IncomeExpensesBarChart"
export default function IncomeExpenseCard({ data }) {
    return(
        <div className="bg-white w-full min-w-[17vw] rounded-lg shadow-bb-general px-3 py-2">
            <h1 className="text-md 3xl:text-xl font-semibold font-header">Income vs Expenses</h1>
            <IncomeExpensesBarChart data={data}/>
        </div>
    )
}