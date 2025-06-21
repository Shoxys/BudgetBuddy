import { useState } from "react"
import Sidebar from "../components/Siderbar"
import Button from "../components/Button"
import GoalStats from "../components/SavingGoals/GoalStats"
import GoalCard from "../components/SavingGoals/GoalCard"

export default function Transactions() {
   const [dataNull, setDataNull] = useState(false)
   const [openAddModal, setOpenAddModal] = useState(false)
   return (
      <div className="relative min-h-screen min-w-screen bg-bb_slate pl-20">
        <Sidebar selectedNav="Dashboard"/>
        <div className="flex flex-col pb-4 w-full">
            <div className="flex flex-col bg-white py-1 mt-3 px-10">
                <div className="flex flex-row justify-between">
                    <div className="flex flex-row w-full items-center gap-2">
                        <img className="w-11" src="src/assets/transaction-icon-selected.png" alt="transaction icon" />
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
                        <h1 className="font-header text-xl font-bold text-gray-800"> My Goals</h1>
                        <Button label="+ New Goal" weight="font-semibold" padding="px-4 py-2" size="text-md" ></Button>
                    </div>
                    {dataNull 
                        ? (<span className="font-body text-gray-800 text-md">Add new goals to get started!</span>)
                        : (<GoalCard/>)
                    }
                </div>
            </div>
            <div className="flex flex-col min-h-[40vh] gap-4 bg-[#E8FFF1FF] py-4 px-4 ml-8 mr-8 text-black rounded-sm shadow-bb-general">
                <h1 className="font-header text-xl font-bold text-gray-800"> Completed</h1>  
                {dataNull 
                        ? (<span className="font-body text-gray-800 text-md">Add new goals to get started!</span>)
                        : (<GoalCard completed={true}/>)
                    }       
            </div>
        </div>
    </div>
   )
}