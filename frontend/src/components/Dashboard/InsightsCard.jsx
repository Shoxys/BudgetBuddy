const insightObjects = [
    {insight: "Consider shifting just 1 meal/week to home cooking,  you could save $120/month while still enjoying the occasional treat!"},
    {insight: "Bundling or pausing just 1–2 rarely used ones could save around $25–$40/month, with no major impact on your routine!"}
]

export default function InsightsCard() {
    return(
         <div className="bg-white col-span-1 row-span-1 px-4 pb-4 pt-3 flex flex-col gap-2 rounded-lg shadow-bb-general">
            <h1 className="font-header text-gray-700 font-semibold text-md text-wrap">💡 Spending Insight</h1>
            <ul className="font-body text-sm list-disc pl-5 flex flex-col gap-2">
                {insightObjects.map(({insight, index }) => (
                    <li key={index}>{insight}</li>
                ))}
            </ul>
        </div>
    )
}