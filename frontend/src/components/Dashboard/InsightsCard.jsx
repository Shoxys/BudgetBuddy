import { Link } from "react-router-dom"
export default function InsightsCard({ data }) {
    return(
         <div className="w-full min-w-[23vw] bg-white col-span-1 row-span-1 px-3 pb-4 pt-3 flex flex-col gap-2 rounded-lg shadow-bb-general">
            { /* Title */}
            <h1 className="font-header text-gray-700 font-semibold text-md 3xl:text-xl text-wrap">💡 Spending Insight</h1>
            {data 
            /* Insights with data */
            ? (<ul className="font-body text-sm 3xl:text-lg list-disc pl-5 flex flex-col gap-3">
                {data.map(({insight, index }) => (
                    <li key={index}>{insight}</li>
                ))}
            </ul>)
            /* Default message if no data*/
            : <div className="flex flex-row w-full py-1 pl-4 pr-2 gap-1 bg-gray-100 rounded-md items-center">
                <span className="font-medium text-lg text-gray-600">Add transactions for insights!</span>
                <Link to="/transactions">
                    <img src="src/assets/spending-plus.png" alt="transactions plus icon" />
                </Link>  
              </div>
            }
            
        </div>
    )
}