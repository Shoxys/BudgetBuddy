import NetworthPieChart from "./NetworthPieChart"
export default function NetworthCard({ data }) {
    return(
        <div className="w-full bg-white col-start-2 col-span-1 row-span-1 rounded-lg shadow-bb-general px-4 pb-4 pt-3">
        <h1 className="font-header font-semibold text-md 3xl:text-xl">Net Worth</h1>
            <NetworthPieChart data={data} />
        </div>
    )
}