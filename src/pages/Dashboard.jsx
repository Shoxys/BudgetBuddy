import Sidebar from "../components/Siderbar"
import ValueCard from "../components/Dashboard/ValueCard"
import InsightsCard from "../components/Dashboard/InsightsCard";
import NetworthCard from "../components/Dashboard/NetworthCard";
import PeriodDropdown from "../components/Dashboard/PeriodDropdown";
import TotalBalanceCard from "../components/Dashboard/TotalBalanceCard";
import SavingGoalsCard from "../components/Dashboard/SavingGoalsCard";
import IncomeExpenseCard from "../components/Dashboard/IncomeExpensesCard";
import RevenueCard from "../components/Dashboard/RevenueCard";
import ExpenseAnalysisCard from "../components/Dashboard/ExpenseAnalysisCard";
import TransactionsCard from "../components/Dashboard/TransactionsCard";

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
            <div className="w-full h-full">
               <div className="flex flex-col bg-white pl-4 pb-3 pt-3 w-full text-black rounded-sm shadow-bb-general">
                  <h1 className="font-header text-md font-semibold text-gray-700 mb-2"> Account Overview</h1>
                  <div className="flex flex-row gap-6">
                     <TotalBalanceCard totalBalance={totalBalance}/>
                     <ValueCard/>
                     <div>
                     </div>
                  </div>
               </div>
               <div className="flex gap-2 mt-3 h-full">
                  <div className="w-1/2 grid grid-col grid-auto-rows gap-3">
                     <InsightsCard/>
                     <NetworthCard/>
                     <SavingGoalsCard/>
                     <IncomeExpenseCard/>
                  </div>   
                  <div className="w-1/2 grid grid-cols-2 grid-auto-rows gap-3">
                     <RevenueCard/>
                     <ExpenseAnalysisCard/>
                     <TransactionsCard/>
                  </div>
               </div>
            </div>    
        </div>
      </div>
   )
}