import Sidebar from "../components/Siderbar"
import ValueCard from "../components/Dashboard/ValueCard"
import InsightsCard from "../components/Dashboard/InsightsCard";
import NetworthCard from "../components/Dashboard/NetworthCard";
import PeriodDropdown from "../components/Dashboard/PeriodDropdown";
import TotalBalanceCard from "../components/Dashboard/TotalBalanceCard";
import SavingGoalsCard from "../components/Dashboard/SavingGoalsCard";
export default function Dashboard () {
   const totalBalance = "$4,950";
   return (
      <div className="relative min-h-screen min-w-screen bg-bb_slate pl-16">
        <Sidebar selectedNav="Dashboard"/>
        <div className="flex flex-col px-5 pb-4 w-full h-full gap-2">
         <div className="flex flex-row justify-between">
             <h2 className={`text-2xl font-header font-bold ml-4 mt-3`}>
                <span className="text-primary_blue">Budget</span>
                <span className="text-secondary_red">Buddy</span>
                <span> Dashboard</span>
            </h2>
            <PeriodDropdown/>
         </div>
            <div className="w-full h-full border border-black">
               <div className="flex flex-col bg-white pl-4 pb-4 pt-3 w-full text-black rounded-sm shadow-bb-general">
                  <h1 className="font-header text-md font-semibold text-gray-700 mb-2"> Account Overview</h1>
                  <div className="flex flex-row gap-6">
                     <TotalBalanceCard totalBalance={totalBalance}/>
                     <ValueCard/>
                     <div>
                     </div>
                  </div>
               </div>
               <div className="border border-red-600 flex gap-2 mt-3 h-full">
                  <div className="border-blue-600 w-1/2 grid grid-col grid-auto-rows gap-3">
                     <InsightsCard/>
                     <NetworthCard/>
                     <SavingGoalsCard/>
                     <div className="bg-white rounded-lg shadow-bb-general px-3 py-2">
                        <h1 className="text-md font-semibold font-header">Income vs Expenses</h1>
                     </div>
                  </div>
                  <div className="border-bg-pink-500 w-1/2 bg-white">
                     
                  </div>
               </div>
            </div>    
        </div>
      </div>
   )
}