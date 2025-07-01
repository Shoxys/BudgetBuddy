export default function DropdownPeriod() {
    return(
        <>
            <select name="timeFrame" id="timeFrame" className="text-gray-700 font-body border border-gray-500 rounded-lg bg-transparent py-2 pl-1 pr-7 mt-2">
                <option className="text-center" value="lastWeek">Last Week</option>
                <option className="text-center" value="currentMonth" selected>This Month </option>
                <option className="text-center" value="lastMonth">Last Month</option>
                <option className="text-center" value="currentQuarter">This Quarter</option>
                <option className="text-center" value="currentYear">This Year</option>
            </select>
            <img className="absolute right-9 top-3 w-8" src="src/assets/calendar-icon.png" alt="calendar icon" />
        </>
    )
}