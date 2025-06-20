import Tag from "../tag";
import ActionsDropdown from "./ActionsDropdown";
import DeletionPopup from "./DeletionPopup";
import EditModal from "./EditModal";
import { formatMoney, formatDate } from '../../Utils/helpers';
import { useState } from "react";

const transactions = [
  { id: 1, date: "2025-04-17", amount: -68.96, accountNumber: "899800000", type: "EFTPOS", desc: "POS 17/04", balance: 89.21, category: "Personal Care", merchant: "Beauty Amora" },
  { id: 2, date: "2025-04-17", amount: 0, accountNumber: "899800000", type: "EFTPOS", desc: "POS 17/04", balance: 158.17, category: "Uncategorised", merchant: "" },
  { id: 3, date: "2025-04-17", amount: -30, accountNumber: "899800000", type: "EFTPOS", desc: "POS 17/04", balance: 158.17, category: "Personal", merchant: "Sephora" },
  { id: 4, date: "2025-04-16", amount: -455, accountNumber: "899800000", type: "TRANSFER", desc: "Queensland Electricity", balance: 188.17, category: "Transfers out", merchant: "" },
  { id: 5, date: "2025-04-16", amount: 500, accountNumber: "899800000", type: "TRANSFER", desc: "ONLINE DEPOSIT", balance: 643.17, category: "Internal transfers", merchant: "" },
  { id: 6, date: "2025-04-15", amount: -12.5, accountNumber: "899800000", type: "MISC", desc: "V3344 10/04", balance: 143.17, category: "Restaurants", merchant: "McDonald's (Waterford West)" },
  { id: 7, date: "2025-04-15", amount: -50.44, accountNumber: "899800000", type: "MISC", desc: "V3344 05/04", balance: -168.27, category: "Fuel", merchant: "Shell Coles Express (Marsden)" },
  { id: 8, date: "2025-04-14", amount: -25.38, accountNumber: "899800000", type: "MISC", desc: "V3344 05/04", balance: 218.71, category: "Attractions & Events", merchant: "On Air Singing Booth" },
];

export default function Table() {
    const [selectedIds, setSelectedIds] = useState([]);
    const [lastCheckedIndex, setLastCheckedIndex] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);


    // Handles check box selection of transactions
    const handleSelect = (event, id, index) => {
        // Handle Shift Key + Click group select
        if (event.shiftKey && lastCheckedIndex !== null) {
            const start = Math.min(index, lastCheckedIndex); // Start checkbox
            const end = Math.max(index, lastCheckedIndex); // Last checkbox

            // Slice the selected transactions from the data object
            const rangeIds = transactions.slice(start, end + 1).map((transaction) => transaction.id);
            const isDeselecting = selectedIds.includes(id)
            
            // Remove deselected ids if selected is in selected array else add selected ids
            if (isDeselecting){
                setSelectedIds((prev) => prev.filter((id) => !rangeIds.includes(id)));
            } else {
                 setSelectedIds((prev) => {
                    const merged = new Set([...prev, ...rangeIds]);
                    return Array.from(merged);
                });
            }       
        } else {
            setSelectedIds((prev) =>
                prev.includes(id)
                    ? prev.filter((selectedId) => selectedId !== id)
                    : [...prev, id]
            );
        }
        console.log({ id, index, shift: event.shiftKey, lastCheckedIndex });

        setLastCheckedIndex(index)
    };

    const handleSelectAll = () => {
        if (selectedIds.length === transactions.length) {
            setSelectedIds([]);
        } else {
            setSelectedIds(transactions.map((transaction) => (transaction.id)));
        }
    }

    return(
        <>
            <table className="table-fixed w-full">
                <thead>
                    <tr className="font-header font-semibold text-md text-gray-800 ">
                        <th className="w-1 text-center py-2">
                            <input
                            className="h-5 w-5 outline-none"
                            name="selectAll"
                            type="checkbox"
                            onChange={() => handleSelectAll()}
                            checked={selectedIds.length === transactions.length}
                            />
                        </th>
                        <th className="w-5 text-center">Date</th>
                        <th className="w-11">Details</th>
                        <th className="w-4">Category</th>
                        <th className="w-3.5">Debit</th>
                        <th className="w-3.5">Credit</th>
                        <th className="w-3.5">Balance</th>
                        <th className="w-0.5"></th>
                    </tr>
                </thead>
                <tbody>
                    {transactions.map(({id, date, amount, type, desc, balance, category, merchant}, index) =>(
                        <tr key={id} className={`border-t border-b ${selectedIds.includes(id) ? 'bg-[#008CFF66] opacity-85 border-blue-400' : 'border-gray-200'} text-md text-gray-700 font-body font-semibold`}>
                            <td className="text-center">
                                <input className="h-4 w-4 outline-none" 
                                       type="checkbox" 
                                       onClick={(event) => handleSelect(event, id, index)} 
                                       checked={selectedIds.includes(id)}>
                                </input>
                            </td>
                            <td className="py-3.5 px-2 text-center">{formatDate(date)}</td>
                            <td className="break-words whitespace-normal max-w-[250px]">{merchant === "" ? desc + " " + type : merchant}</td>
                            <td className="text-sm">
                               <Tag category={category}/>
                            </td>
                            <td className="text-center text-gray-800">{amount < 0 && (
                                <>
                                    <span className="text-[#E11A0CFF]">-</span>
                                    {formatMoney(amount).replace("-","")}
                                </>
                                )}
                            </td>
                            <td className="text-gray-800 text-center">{amount >= 0 && (
                                <>
                                    <span className="text-[#2CBD00FF]">+</span> 
                                    {formatMoney(amount)}
                                </>)}
                            </td>
                            <td className="text-gray-800 text-center items-center">
                                {balance < 0 ? (<span className="text-[#E11A0CFF]">-</span> ) :
                                               (<span className="text-[#2CBD00FF]">+</span> )}
                                {formatMoney(balance).replace("-", "")}
                            </td>
                            <td className="text-left"> 
                                <ActionsDropdown
                                    onEdit={() => setShowEditModal(true)}
                                    onDelete={() => setShowDeleteModal(true)}
                                />     
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <div className="flex flex-row justify-center gap-4">
                    <button className="bg-bb_neutral py-2 px-3 text-md text-gray-500 font-body font-semibold rounded-lg hover:bg-gray-200">‹⠀Previous</button>
                    <button className="bg-bb_neutral py-2 px-5 text-md text-gray-500 font-body font-semibold rounded-lg hover:bg-gray-200">Next⠀⠀›</button>
            </div>
            {showDeleteModal && (
                <DeletionPopup isOpen={showDeleteModal} onClose={() => setShowDeleteModal(false)} handleDelete={() => (console.log({showDeleteModal}))}/>
            )} 
            {showEditModal && (
                <EditModal isOpen={showEditModal} onClose={() => setShowEditModal(false)}/>
            )}  
        </>
    )
}