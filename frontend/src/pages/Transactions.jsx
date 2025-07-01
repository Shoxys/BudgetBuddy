import { useState } from "react"
import Sidebar from "../components/Siderbar"
import Table from "../components/Transactions/Table"
import AddModal from "../components/Transactions/AddModal"

export default function Transactions() {
   const [openAddModal, setOpenAddModal] = useState(false)
   return (
      <div className="relative min-h-screen min-w-screen bg-bb_slate pl-20">
        <Sidebar selectedNav="Dashboard"/>
        <div className="flex flex-col pb-4 w-full h-full gap-3">
            <div className="flex flex-col bg-white py-1 mt-3 px-10">
                <div className="flex flex-row justify-between">
                    <div className="flex flex-row w-full items-center gap-2">
                        <img className="w-11" src="src/assets/transaction-icon-selected.png" alt="transaction icon" />
                        <h2 className="text-2xl font-header font-bold">
                            Transaction History
                        </h2>
                    </div>
                    <div className="flex flex-row gap-3 items-center">
                        <button className="bg-transparent border border-secondary_red text-md text-secondary_red font-body font-semibold underline flex flex-row 
                                            pl-5 pr-11 py-3 items-center gap-2 rounded-md border-spacing-2 hover:bg-red-50 outline-none"> 
                            <img className="w-6" src="src/assets/import-icon.png" alt="Import icon" />
                            Import 
                        </button>
                        <select name="timeFrame" id="timeFrame" className="text-primary_blue font-body font-semibold border-[3px] border-primary_blue rounded-[0.25rem] 
                                                                            bg-transparent py-[1rem] px-2 outline-none cursor-pointer hover:bg-blue-50">
                            <option className="text-gray-700" value="week">Last 7 Days</option>
                            <option className="text-gray-700" value="month" selected>Last 30 Days</option>
                            <option className="text-gray-700" value="biMonth">Last 60 Days</option>
                            <option className="text-gray-700" value="triMonth">Last 90 Days</option>
                            <option className="text-gray-700" value="allTime">All time</option>
                        </select>
                    </div>
                </div>
                <h2 className="ml-11 mt-[-0.5rem] text-lg">
                    <span className="font-header font-semibold text-gray-700">Date range⠀-⠀</span>
                    <span className="font-header font-medium text-gray-600">18 Mar 2025⠀-⠀17 Apr 2025</span>
                </h2>
            </div>
            <div className="flex flex-col gap-5 bg-white py-4 px-4 ml-8 mr-8 text-black rounded-sm shadow-bb-general">
                <div className="flex flex-row justify-between">
                    <h1 className="font-header text-xl font-bold text-gray-800 mb-2"> 24 Transactions</h1>
                    <div className="flex flex-row gap-5 items-center">
                         <select name="filter" id="filter" className="text-gray-500 font-header font-semibold shadow-bb-general text-md rounded-[0.2rem] bg-bb_slate py-2 px-3 outline-none cursor-pointer hover:bg-slate-50">
                            <option className="text-gray-600" value="latest" selected>Sort Latest</option>
                            <option className="text-gray-600" value="oldest">Sort Oldest</option>
                        </select>
                        <button onClick={() => setOpenAddModal(true)} className="bg-primary_blue flex flex-row items-center text-white text-md font-header font-semibold gap-2 py-2 px-4 rounded-md hover:bg-btn_hover">
                            <img className="w-5" src="src/assets/plus-icon.png" alt="plus icon" />
                             Add New
                        </button>
                    </div>
                </div>
                <span className="z-0 absolute text-sm font-semibold font-header text-gray-500 text-left top-[10.5rem]">Select All</span>
               <Table/>    
            </div>
        </div>
        {openAddModal && (
            <AddModal isOpen={openAddModal} onClose={() => setOpenAddModal(false)} />
        )}
    </div>
   )
}