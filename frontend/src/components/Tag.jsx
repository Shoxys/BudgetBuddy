import { categoryColors } from "../Utils/categoryColors"

export default function Tag({category} ) {
    const colorClass = categoryColors[category] === undefined || null ? 'bg-bb_neutral text-gray-700' : categoryColors[category]
    return(
        <div className={`inline-block px-3 py-1 rounded-2xl ${colorClass}`}>
            {category}
        </div>
         
    )
}