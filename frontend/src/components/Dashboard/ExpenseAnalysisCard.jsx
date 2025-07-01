import { formatMoney } from "../../Utils/helpers";

// Predefined chart colours
const expenseColors = ['bg-primary_blue', 'bg-secondary_red', 'bg-bb_green', 'bg-bb_yellow', 'bg-bb_purple']

// Select predefined colours via index 
const selectColor = (index) => (
  expenseColors[index % expenseColors.length]
)
export default function ExpenseAnalysisCard({ expenses }) {
    const isValid = Array.isArray(expenses) && expenses.length > 0;
    const total = isValid ? expenses.reduce((sum, item) => sum + item.value, 0) : 0;
    return(
        <div className="bg-white col-span-1 row-span-1 row-start-2 rounded-lg shadow-bb-general px-5 pb-4 pt-2">
            <h2 className="text-md 3xl:text-xl font-semibold text-gray-700 mb-[1.5vh]">Expense Analysis</h2>

            {/* Horizontal bar chart  */}
            <div className="flex h-[4vh] w-full rounded overflow-hidden mb-5">
              {isValid 
                /* Display with data */
                ? (expenses.map((item, index) => (
                <div
                  key={index}
                  className={`${selectColor(index)} h-full`}
                  style={{ width: `${total === 0 ? "0%" : (item.value / total) * 100}%` }}
                  title={`${item.label}: $${item.value}`}
                />
              )))
                /* Display empty chart */  
                : <div className="h-full bg-gray-300 w-full"/>
              
              }
             
            </div>

            {/* Legend */}
            <div className="space-y-2.5">
              {isValid 
              /* Display with data */
                ? (expenses.map((item, index) => (
                <div key={index} className="flex items-center justify-between text-sm 3xl:text-lg">
                  <div className="flex items-center gap-2">
                    <span className={`w-2.5 h-2.5 rounded-full ${selectColor(index)}`} />
                    <span className=" text-gray-600">{item.label}</span>
                  </div>
                  <span className="font-medium text-gray-900">{formatMoney(item.value)}</span>
                </div>
               )))
               /* Display empty legend */
               : <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className={`w-2.5 h-2.5 rounded-full bg-gray-300`} />
                    <span className=" text-gray-600">Empty</span>
                  </div>
                  <span className="font-medium text-gray-900">$0</span>
                </div>
              }

            </div>     
        </div>
    )
}