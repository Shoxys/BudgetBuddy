import Tag from "../tag";
import ActionsDropdown from "../ActionsDropdown";
import DeletionPopup from "../DeletionPopup";
import EditModal from "./EditModal";
import { formatMoney, formatDate } from '../../Utils/helpers';
import { useState } from "react";

export default function Table({ transactions, selectedIds, setSelectedIds }) {
    const [lastCheckedIndex, setLastCheckedIndex] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [currentActioned, setCurrentActioned] = useState(null);


    const isValid = Array.isArray(transactions) && transactions.length > 0;

    // Handles check box selection of transactions
    const handleSelect = (event, id, index) => {
        if (!isValid) return

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
            // Passes selected ids to state
            setSelectedIds((prev) =>
                prev.includes(id)
                    ? prev.filter((selectedId) => selectedId !== id)
                    : [...prev, id]
            );
        }
        console.log({ id, index, shift: event.shiftKey, lastCheckedIndex });

        setLastCheckedIndex(index)
    };

    // Handles selected all transactions button
    const handleSelectAll = () => {
        if (!isValid) return
        
        if (selectedIds.length === transactions.length) {
            setSelectedIds([]);
        } else {
            setSelectedIds(transactions.map((transaction) => (transaction.id)));
        }
    }

    const handleActioned = () => (
        transactions.find((transaction => transaction.id === currentActioned))
    )

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
                            checked={isValid && selectedIds.length === transactions.length}
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
                    {isValid && (
                        transactions.map(({id, date, amount, type, desc, balance, category, merchant}, index) =>(
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
                                        onEdit={() => {
                                            setShowEditModal(true);
                                            setCurrentActioned(id);
                                        }}
                                        onDelete={() => setShowDeleteModal(true)}
                                        desc="Are you sure you want to delete this transaction?"
                                    >
                                        ⋮
                                    </ActionsDropdown>  
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
            {showDeleteModal && (
                <DeletionPopup actionedTransaction={handleActioned} isOpen={showDeleteModal} onClose={() => setShowDeleteModal(false)} handleDelete={() => (console.log({showDeleteModal}))}/>
            )} 
            {showEditModal && (
                <EditModal actionedTransaction={handleActioned} isOpen={showEditModal} onClose={() => setShowEditModal(false)}/>
            )}  
        </>
    )
}