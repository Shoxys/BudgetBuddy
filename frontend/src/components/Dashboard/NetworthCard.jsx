import NetworthPieChart from "./NetworthPieChart"
export default function NetworthCard() {
    return(
        <div className="bg-white col-start-2 col-span-1 row-span-1 rounded-lg shadow-bb-general px-4 pb-4 pt-3 w-80">
        <h1 className="font-header font-semibold text-md">Net Worth</h1>
            <NetworthPieChart />
        </div>
    )
}