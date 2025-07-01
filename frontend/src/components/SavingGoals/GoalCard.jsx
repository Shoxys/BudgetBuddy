import { formatMoney } from "../../Utils/helpers"
import FormatRelativeDate from "./formatRelativeDate";
import { useState } from "react";
import ContributionModal from "./ContributionModal";
import ActionsDropdown from "../ActionsDropdown";
import DeletionPopup from "../DeletionPopup";
import GoalModal from "./GoalModal";

export default function GoalCard({ data, completed }) {
    const [contributeModal, setContributeModal] = useState(false) 
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [selectedGoal, setSelectedGoal] = useState(null);

    const getSelected = () => (
        data.find((goal => goal.id === selectedGoal))
    )
    
    return(
            <div className="flex flex-wrap gap-8">
            {data.map(({ id, title, contributed, target, image, endDate}) => {
                const percentage = Math.round(contributed / target * 100);

                return (<div  key={id} className={` px-4 pt-3 pb-4 flex flex-col gap-1 rounded-lg lg:w-[19rem] md:w-[13rem] 3xl:w-[25rem] shadow-bb-general bg-white`}>
                    {/* Goal Image */}
                    <div
                        className="relative border border-red w-full h-[18vh] bg-cover bg-center rounded-lg mb-2"
                        style={{ backgroundImage: `url('${image}')` }}
                    >  
                        <ActionsDropdown
                            onDelete={() => setShowDeleteModal(true)}
                            onEdit={() => {
                                setShowEditModal(true);
                                setSelectedGoal(id);
                            }}
                            >
                            <div className="rounded-bl-lg rounded-tr-lg absolute right-0 top-0 bg-white py-0.5 px-1 opacity-85">
                                <img src="src/assets/edit-blue.png" alt="edit icon" />
                            </div >
                        </ActionsDropdown>
                        <div className="rounded-tl-lg rounded-br-lg absolute right-0 bottom-0 bg-white py-0.5 px-2 font-body text-gray-800 text-sm opacity-85">
                            <FormatRelativeDate date={endDate}/>
                        </div>
                    </div>
                    {/* Title & goal */}
                    <h1 className="flex flex-row gap-3 font-header text-wrap">
                        <span className="font-bold text-md black">{title}</span>
                        <span className="text-[#03C149FF]">Goal: {formatMoney(target)}</span>
                    </h1>  
                    <h2 className="flex flex-row items-center gap-3">
                        <span className="text-2xl font-header black font-bold">
                            {formatMoney(contributed)}
                        </span>
                        <span className="text-lg text-gray-400">
                            / {target}
                        </span>
                    </h2>
                    {/* Progress bar */}
                    <div className="flex flex-row items-center gap-2 mb-1.5">
                        <div className="w-full h-2.5 bg-[#B4DDFFFF] rounded-full overflow-hidden relative">
                            <div
                                className={`h-full ${completed ? "bg-[#03D952FF]" : "bg-[#008CFFFF]"} transition-all duration-300`}
                                style={{ width: `${percentage}%` }}
                            />
                        </div>
                        <span className="font-body text-gray-500">{percentage}%</span>
                    </div>
                    {completed
                    ? <div className="w-full py-1 px-2 rounded-md text-white font-body font-semibold text-center bg-[#03D952FF]">
                        ✓ Completed
                    </div>
                    : <button 
                        className="w-full py-1 border border-primary_blue rounded-md text-primary_blue hover:bg-primary_blue hover:opacity-95 hover:text-white outline-none"
                        onClick={() => setContributeModal(true)}>
                            + Add Contribution
                      </button>
                    }
                </div>);
        })}
            {contributeModal && 
                <ContributionModal isOpen={contributeModal} onClose={() => setContributeModal(false)} data={{name: "Porshe"}}/>
            }
            {showDeleteModal && 
                <DeletionPopup isOpen={showDeleteModal} onClose={() => setShowDeleteModal(false)} desc="Are you sure you want to delete this goal?" />
            }
            {showEditModal && 
                <GoalModal isOpen={showEditModal} onClose={() => setShowEditModal(false)} data={getSelected()} />
            }
            </div>
            
        )
}