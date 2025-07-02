import { useState } from "react"
import Sidebar from "../components/Sidebar"
import Table from "../components/Transactions/Table"
import AddModal from "../components/Transactions/AddModal"
import { formatDate } from "../Utils/helpers"

const data = {
    transactions: [
        { id: 1, date: "2025-04-17", amount: -68.96, type: "EFTPOS", desc: "POS 17/04", balance: 89.21, category: "Personal Care", merchant: "Beauty Amora" },
        { id: 2, date: "2025-04-17", amount: 0, type: "EFTPOS", desc: "POS 17/04", balance: 158.17, category: "Uncategorised", merchant: "" },
        { id: 3, date: "2025-04-17", amount: -30, type: "EFTPOS", desc: "POS 17/04", balance: 158.17, category: "Personal", merchant: "Sephora" },
        { id: 4, date: "2025-04-16", amount: -455, type: "TRANSFER", desc: "Queensland Electricity", balance: 188.17, category: "Transfers out", merchant: "" },
        { id: 5, date: "2025-04-16", amount: 500, type: "TRANSFER", desc: "ONLINE DEPOSIT", balance: 643.17, category: "Internal transfers", merchant: "" },
        { id: 6, date: "2025-04-15", amount: -12.5, type: "MISC", desc: "V3344 10/04", balance: 143.17, category: "Restaurants", merchant: "McDonald's (Waterford West)" },
        { id: 7, date: "2025-04-15", amount: -50.44, type: "MISC", desc: "V3344 05/04", balance: -168.27, category: "Fuel", merchant: "Shell Coles Express (Marsden)" },
        { id: 8, date: "2025-04-14", amount: -25.38, type: "MISC", desc: "V3344 05/04", balance: 218.71, category: "Attractions & Events", merchant: "On Air Singing Booth" },
        { id: 9, date: "2025-04-17", amount: -68.96, type: "EFTPOS", desc: "POS 17/04", balance: 89.21, category: "Personal Care", merchant: "Beauty Amora" },
        { id: 10, date: "2025-04-17", amount: 0, type: "EFTPOS", desc: "POS 17/04", balance: 158.17, category: "Uncategorised", merchant: "" },
        { id: 11, date: "2025-04-17", amount: -30, type: "EFTPOS", desc: "POS 17/04", balance: 158.17, category: "Personal", merchant: "Sephora" },
        { id: 12, date: "2025-04-16", amount: -455, type: "TRANSFER", desc: "Queensland Electricity", balance: 188.17, category: "Transfers out", merchant: "" },
        { id: 13, date: "2025-04-16", amount: 500, type: "TRANSFER", desc: "ONLINE DEPOSIT", balance: 643.17, category: "Internal transfers", merchant: "" },
        { id: 14, date: "2025-04-15", amount: -12.5, type: "MISC", desc: "V3344 10/04", balance: 143.17, category: "Restaurants", merchant: "McDonald's (Waterford West)" },
        { id: 15, date: "2025-04-15", amount: -50.44, type: "MISC", desc: "V3344 05/04", balance: -168.27, category: "Fuel", merchant: "Shell Coles Express (Marsden)" },
        { id: 16, date: "2025-04-14", amount: -25.38, type: "MISC", desc: "V3344 05/04", balance: 218.71, category: "Attractions & Events", merchant: "On Air Singing Booth" },
        { id: 17, date: "2025-04-16", amount: -455, type: "TRANSFER", desc: "Queensland Electricity", balance: 188.17, category: "Transfers out", merchant: "" },
        { id: 18, date: "2025-04-16", amount: 500, type: "TRANSFER", desc: "ONLINE DEPOSIT", balance: 643.17, category: "Internal transfers", merchant: "" },
        { id: 19, date: "2025-04-15", amount: -12.5, type: "MISC", desc: "V3344 10/04", balance: 143.17, category: "Restaurants", merchant: "McDonald's (Waterford West)" },
        { id: 20, date: "2025-04-15", amount: -50.44, type: "MISC", desc: "V3344 05/04", balance: -168.27, category: "Fuel", merchant: "Shell Coles Express (Marsden)" },
    ],
    dateRange: {
        start: "2025-04-14",
        end: "2025-04-14"
    }
}

export default function Transactions() {
   const [openAddModal, setOpenAddModal] = useState(false)
   const [selectedIds, setSelectedIds] = useState([]);

   return (
      /* Background Container */
      <div className="relative min-h-screen min-w-screen bg-bb_slate pl-20">
        {/* Left sidebar */}
        <Sidebar selectedNav="Dashboard"/>
        {/* Main Transactions Container */}
        <div className="flex flex-col pb-4 w-full h-full">
            <div className="flex flex-col bg-white py-1 mt-3 px-10">
                <div className="flex flex-row justify-between">
                    {/* Header Banner */}
                    <div className="flex flex-row w-full items-center gap-2">
                        <img className="w-11" src="src/assets/transaction-icon-selected.png" alt="transaction icon" />
                        {/* Title */}
                        <h2 className="text-2xl font-header font-bold">
                            Transaction History
                        </h2>
                    </div>
                    {/* Top Right Buttons Container */}
                    <div className="flex flex-row gap-3 items-center">
                        {/* Import Button */}
                        <button className="bg-transparent border border-secondary_red text-md text-secondary_red font-body font-semibold underline flex flex-row 
                                            pl-5 pr-11 py-3 items-center gap-2 rounded-md border-spacing-2 hover:bg-red-50 outline-none"> 
                            <img className="w-6" src="src/assets/import-icon.png" alt="Import icon" />
                            Import 
                        </button>
                        {/* Time Frame Filter Dropdown*/}
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
                {/* Date Range Subtitle*/}
                <h2 className="ml-11 mt-[-0.5rem] text-lg">
                    <span className="font-header font-semibold text-gray-700">Date range⠀-⠀</span>
                    <span className="font-header font-medium text-gray-600">{data ? formatDate(data.dateRange.start) + "⠀-⠀" + formatDate(data.dateRange.end) : ""}</span>
                </h2>
            </div>
            <div className="flex flex-col min-h-[63vh] 3xl:min-h-[75vh] gap-5 bg-white py-4 px-4 ml-8 mr-8 text-black rounded-sm shadow-bb-general">
                <div className="flex flex-row justify-between">
                    {/* Transaction Count */}
                    <h1 className="font-header text-xl font-bold text-gray-800 mb-2">{data ? data.transactions.length : ""} Transactions </h1>
                    <div className="flex flex-row gap-5 items-center">
                        {/* Sort By Dropdown */}
                         <select name="filter" id="filter" className="text-gray-500 font-header font-semibold shadow-bb-general text-md rounded-[0.2rem] bg-bb_slate py-2 px-3 outline-none cursor-pointer hover:bg-slate-50">
                            <option className="text-gray-600" value="latest" selected>Sort Latest</option>
                            <option className="text-gray-600" value="oldest">Sort Oldest</option>
                        </select>
                        {/* Switch when more than 2 transactions are selected */}
                        { selectedIds.length < 2 ?
                            /* Add New Transaction */
                            <button onClick={() => setOpenAddModal(true)} className="bg-primary_blue flex flex-row items-center text-white text-md font-header font-semibold gap-2 py-2 px-4 rounded-md hover:bg-btn_hover">
                                <img className="w-5" src="src/assets/plus-icon.png" alt="plus icon" />
                                Add New
                            </button> : 
                            /* Delete Selected Transactions */
                            <button onClick={() => (console.log("Handle Delete All"))} className="bg-[#F44336FF] flex flex-row items-center text-white text-md font-header font-semibold gap-2 py-2 px-3 rounded-md hover:bg-red-600">
                                <img className="w-5" src="src/assets/trash-white.png" alt="trash icon" />
                                Delete Selected
                            </button>
                        }   
                    </div>
                </div>
                <span className="z-0 absolute text-sm font-semibold font-header text-gray-500 text-left top-[9.6rem]">Select All</span>
                {/* Transactions table */}
               <Table transactions={data && data.transactions} selectedIds={selectedIds} setSelectedIds={setSelectedIds}/>   
            </div>
            <div className="flex flex-row justify-center gap-4 pb-4 bg-white ml-8 mr-8">
                <button className="bg-bb_neutral py-2 px-3 text-md text-gray-500 font-body font-semibold rounded-lg hover:bg-gray-200">‹⠀Previous</button>
                <button className="bg-bb_neutral py-2 px-5 text-md text-gray-500 font-body font-semibold rounded-lg hover:bg-gray-200">Next⠀⠀›</button>
            </div> 
        </div>
        {/* Add Transaction Popup */}
        <AddModal isOpen={openAddModal} onClose={() => setOpenAddModal(false)} />
    </div>
   )
}