import { useState } from "react"
import Sidebar from "../components/Sidebar"
import GoalStats from "../components/SavingGoals/GoalStats"
import GoalCard from "../components/SavingGoals/GoalCard"
import GoalModal from "../components/SavingGoals/GoalModal"

const data = {
    goals: [
        {id: 0, title: "Emergency Fund", contributed: 125, target: 2000, image: "src/assets/default.png", endDate: "2025-05-21"},
        {id: 1, title: "PS5", contributed: 230, target: 500, image: "src/assets/ps5.png", endDate: "2025-06-30"},
        {id: 2, title: "Nike Air Forces", contributed: 30, target: 100, image: "src/assets/default.png", endDate: "2025-02-27"},
        {id: 3, title: "Wallet", contributed: 40, target: 85, image: "src/assets/default.png", endDate: "2025-12-30"},
        {id: 4, title: "Emergency Fund", contributed: 125, target: 2000, image: "src/assets/default.png", endDate: "2025-05-21"},
        {id: 5, title: "PS5", contributed: 230, target: 500, image: "src/assets/ps5.png", endDate: "2025-06-30"},
        {id: 6, title: "Nike Air Forces", contributed: 30, target: 100, image: "src/assets/default.png", endDate: "2025-02-27"},
        {id: 7, title: "Wallet", contributed: 40, target: 85, image: "src/assets/default.png", endDate: "2025-12-30"},
    ],
    goalsComplete: [
        {id: 0, title: "Emergency Fund", contributed: 125, target: 2000, image: "src/assets/default.png", endDate: "2025-05-21"},
        {id: 1, title: "PS5", contributed: 230, target: 500, image: "src/assets/ps5.png", endDate: "2025-06-30"},
        {id: 2, title: "Nike Air Forces", contributed: 30, target: 100, image: "src/assets/default.png", endDate: "2025-02-27"},
    
    ],
}

export default function SavingGoals() {
   const [addGoal, setAddGoal] = useState(false)

   const isGoalsValid = Array.isArray(data.goals) && data.goals.length > 0;
   const isGoalsCompleteValid = Array.isArray(data.goalsComplete) && data.goalsComplete.length > 0;

   return (
      <div className="relative min-h-screen min-w-screen bg-bb_slate pl-20">
        <Sidebar selectedNav="Dashboard"/>
        <div className="flex flex-col pb-4 w-full">
            <div className="flex flex-col bg-white py-1 mt-3 px-10">
                <div className="flex flex-row justify-between">
                    <div className="flex flex-row w-full items-center gap-2">
                        <img className="w-10.5" src="src/assets/goals-blue.png" alt="goals icon" />
                        <h2 className="text-2xl font-header font-bold">
                            Saving Goals
                        </h2>
                    </div>
                </div>
            </div>
            <div className="flex flex-col gap-5 bg-white py-4 px-6 ml-8 mr-8 mt-2 text-black rounded-sm shadow-bb-general">
                <GoalStats/>
                <div className="flex flex-col gap-4 min-h-[40vh]">
                    <div className="flex flex-row gap-6 items-center">
                        <h1 className="font-header text-2xl font-bold text-gray-800"> My Goals</h1>
                        <button onClick={() => setAddGoal(true)} className="btn-primary font-semibold text-md hover-effect ">+ New Goal</button>
                    </div>
                    {isGoalsValid 
                        ? (<GoalCard data={data.goals}/>)
                        : (<span className="font-body text-gray-800 text-md">Add new goals to get started!</span>)
                    }
                </div>
            </div>
            <div className="flex flex-col min-h-[40vh] gap-4 bg-[#E8FFF1FF] py-4 px-4 ml-8 mr-8 text-black rounded-sm shadow-bb-general">
                <h1 className="font-header text-xl font-bold text-gray-800"> Completed</h1>  
                {isGoalsCompleteValid 
                        ? (<GoalCard data={data.goalsComplete} completed={true}/>) 
                        : (<span className="font-body text-gray-800 text-md">Add new goals to get started!</span>)
                    }       
            </div>
            <GoalModal isOpen={addGoal} onClose={() => setAddGoal(false)} />
        </div>
    </div>
   )
}