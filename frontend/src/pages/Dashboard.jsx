import Sidebar from "../components/Sidebar";
import {
  ValueCard,
  InsightsCard,
  NetworthCard,
  DropdownPeriod,
  TotalBalanceCard,
  SavingGoalsCard,
  IncomeExpenseCard,
  IncomeCard,
  ExpenseAnalysisCard,
  TransactionsCard,
  UpdateSavings,
  UpdateInvestments,
} from "../components/Dashboard";

import { formatIncome } from "../Utils/transformers";
import { useState } from "react";

let data = {
  "totalBalance": 4950,
  "accounts": [
    { "type": "Spending", "name": "Spending Account", "balance": 1850 },
    { "type": "Savings", "name": "Savings Account", "balance": 2000 },
    { "type": "Investments", "name": "Investments", "balance": 1232 },
    { "type": "GoalSavings", "name": "Goal Savings", "balance": 1232 }
  ],
  "netWorth": {
    "total": 4950,
    "breakdown": [
      {"name": "Spending", "value": 1850},
      {"name": "Savings", "value": 2000},
      {"name": "Investments", "value": 1232},
      {"name": "Goal Savings", "value": 325}
    ]},
  "spendingInsights": [
    {"insight" : "Consider shifting just 1 meal/week to home cooking, you could save $120/month while still enjoying the occasional treat!"},
    {"insight": "Bundling or pausing just 1–2 rarely used ones could save around $25–$40/month, with no major impact on your routine!"}
  ],

  "savingGoals": [
    { "title": "PS5", "contributed": 261, "target": 500 },
    { "title": "Nike Airforces", "contributed": 35, "target": 140 },
    { "title": "Wallet", "contributed": 12, "target": 50 }
  ],
  "incomeVsExpenses": [
    {"name": "Last month", "income": 2600, "expenses": 1850 },
    {"name" : "This month", "income": 2000, "expenses": 1350 }
  ],
  "incomeTrend": {
    "months": ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 
                'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    "thisYear": [3200, 3400, 3600, 3500, 3700, 3900, 4000, 4200, 4300, 4500, 4700, 4800],
    "lastYear": [3000, 3100, 3300, 3400, 3600, 3800, 4000, 4100, 4200, 4300, 4400, 4600]
  },
  "expenseAnalysis": [
    {"label": "Groceries", "value": 1750},
    {"label": "Personal Care", "value": 600},
    {"label": "Fees & Charges", "value": 1200},
    {"label": "Transfers out", "value": 450},
    {"label": "Restaurants", "value": 1000}
  ],
  "recentTransactions": [
    {
      "date": "2025-04-17",
      "description": "Grocery Store",
      "category": "Personal Care",
      "amount": -85.5
    },
    {
      "date": "2025-04-16",
      "description": "Salary Deposit",
      "category": "Restaurants",
      "amount": 1500.0
    },
    {
      "date": "2025-03-01",
      "description": "Electricity Bill",
      "category": "Attractions & Events",
      "amount": -85.5
    }
  ]
}

data = null;

export default function Dashboard () {
   const UPDATE_TYPES = {
      SAVINGS: "Savings Account",
      INVESTMENTS: "Investments"
   };
   const transformedIncome = data ? formatIncome(data.incomeTrend) : null;
   const [update, setUpdate] = useState(null);
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
            <DropdownPeriod/>
         </div>
            <div className="w-full h-full">
               <div className="flex flex-col bg-white pl-4 pb-3 pt-3 w-full text-black rounded-sm shadow-bb-general">
                  <h1 className="font-header text-md font-semibold text-gray-700 mb-2"> Account Overview</h1>
                  <div className="flex flex-row gap-6">
                     <TotalBalanceCard totalBalance={data?.netWorth?.total ?? null}/>
                     <ValueCard accounts={data?.accounts ?? null} setUpdateActioned={setUpdate}/>
                     <div>
                     </div>
                  </div>
               </div>
               <div className="flex gap-2 mt-3 h-full">
                  <div className="w-1/2 grid grid-col grid-auto-rows gap-3">
                     <InsightsCard data={data?.spendingInsights ?? null}/>
                     <NetworthCard data={data?.netWorth ?? null}/>
                     <SavingGoalsCard data={data?.savingGoals ?? null}/>
                     <IncomeExpenseCard data={data?.incomeVsExpenses ?? null}/>
                  </div>   
                  <div className="w-1/2 grid grid-cols-2 grid-auto-rows gap-3">
                     <IncomeCard data={transformedIncome}/>
                     <ExpenseAnalysisCard expenses={data?.expenseAnalysis ?? null}/>
                     <TransactionsCard data={data?.recentTransactions ?? null}/>
                  </div>
               </div>
            </div>    
        </div>
        <UpdateSavings isOpen={update === UPDATE_TYPES.SAVINGS} onClose={() => setUpdate(null)} />
        <UpdateInvestments isOpen={update === UPDATE_TYPES.INVESTMENTS} onClose={() => setUpdate(null)} />
      </div>
   )
}