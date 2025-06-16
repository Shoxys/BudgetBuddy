import Sidebar from "../../components/Siderbar"
import ValueCard from "../../components/ValueCard"
export default function Dashboard () {
   const totalBalance = "$4,950";
   return (
      <div className="relative min-h-screen min-w-screen bg-bb_slate pl-16">
        <Sidebar selectedNav="Dashboard"/>
        <div className="flex flex-col px-5 pb-4 w-full h-full gap-3">
         <div className="flex flex-row justify-between">
             <h2 className={`text-2xl font-header font-bold ml-4 mt-3`}>
                <span className="text-primary_blue">Budget</span>
                <span className="text-secondary_red">Buddy</span>
                <span> Dashboard</span>
            </h2>
            <select name="timeFrame" id="timeFrame" className="text-gray-700 font-body border border-gray-500 rounded-lg bg-transparent py-2 pl-1 pr-7 mt-2">
               <option className="text-center" value="lastWeek">Last Week</option>
               <option className="text-center" value="currentMonth" selected>This Month </option>
               <option className="text-center" value="lastMonth">Last Month</option>
               <option className="text-center" value="currentQuarter">This Quarter</option>
               <option className="text-center" value="currentYear">This Year</option>
            </select>
            <img className="absolute right-9 top-3 w-8" src="src/assets/calendar-icon.png" alt="calendar icon" />
         </div>
            <div className="w-full h-full border border-black py-3 px-6">
               <div className="flex flex-col bg-white px-4 pb-4 pt-3 w-full text-black rounded-sm shadow-bb-general">
                  <h1 className="font-header text-md font-semibold text-gray-700 mb-2"> Account Overview</h1>
                  <div className="flex flex-row gap-7">
                     <div className="bg-primary_blue px-4 py-2 flex flex-row justify-between gap-2 text-white rounded-lg w-60">
                        <div>
                           <h2 className="font-body text-md font-normal">Total Balance</h2>
                           <h1 className="text-2xl font-header font-bold">{totalBalance}</h1>
                        </div>
                        <div className="flex py-3">
                           <label className="bg-blue-100 py-2 px-4 text-primary_blue rounded-full">$</label> 
                        </div>
                     </div>
                     <ValueCard/>
                  </div>
               </div>
            </div>    
        </div>
      </div>
   )
}