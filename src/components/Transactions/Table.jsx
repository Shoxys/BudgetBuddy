import Tag from "../tag";
import { formatMoney, formatDate } from '../../Utils/helpers';

const transactions = [
  { date: "2025-04-17", amount: -68.96, accountNumber: "899800000", type: "EFTPOS", desc: "POS 17/04", balance: 89.21, category: "Personal Care", merchant: "Beauty Amora" },
  { date: "2025-04-17", amount: 0, accountNumber: "899800000", type: "EFTPOS", desc: "POS 17/04", balance: 158.17, category: "Uncategorised", merchant: "" },
  { date: "2025-04-17", amount: -30, accountNumber: "899800000", type: "EFTPOS", desc: "POS 17/04", balance: 158.17, category: "Personal", merchant: "Sephora" },
  { date: "2025-04-16", amount: -455, accountNumber: "899800000", type: "TRANSFER", desc: "Queensland Electricity", balance: 188.17, category: "Transfers out", merchant: "" },
  { date: "2025-04-16", amount: 500, accountNumber: "899800000", type: "TRANSFER", desc: "ONLINE DEPOSIT", balance: 643.17, category: "Internal transfers", merchant: "" },
  { date: "2025-04-15", amount: -12.5, accountNumber: "899800000", type: "MISC", desc: "V3344 10/04", balance: 143.17, category: "Restaurants", merchant: "McDonald's (Waterford West)" },
  { date: "2025-04-15", amount: -50.44, accountNumber: "899800000", type: "MISC", desc: "V3344 05/04", balance: -168.27, category: "Fuel", merchant: "Shell Coles Express (Marsden)" },
  { date: "2025-04-14", amount: -25.38, accountNumber: "899800000", type: "MISC", desc: "V3344 05/04", balance: 218.71, category: "Attractions & Events", merchant: "On Air Singing Booth" },
  { date: "2025-04-13", amount: 12, accountNumber: "899800000", type: "TRANSFER", desc: "Shane N", balance: 244.09, category: "Transfers in", merchant: "" },
  { date: "2025-04-07", amount: 7.5, accountNumber: "899800000", type: "TRANSFER", desc: "MONFORT", balance: 232.09, category: "Transfers in", merchant: "" },
  { date: "2025-04-07", amount: 8, accountNumber: "899800000", type: "TRANSFER", desc: "JIMMYMONEY", balance: 224.59, category: "Transfers in", merchant: "" },
  { date: "2025-04-06", amount: 120, accountNumber: "899800000", type: "TRANSFER", desc: "ONLINE FEE RETURN", balance: 216.59, category: "Internal transfers", merchant: "" },
  { date: "2025-04-05", amount: 150, accountNumber: "899800000", type: "TRANSFER", desc: "ONLINE GIFT", balance: 265.28, category: "Internal transfers", merchant: "" },
  { date: "2025-04-01", amount: -10.69, accountNumber: "899800000", type: "MISC", desc: "V3344 30/03", balance: 96.59, category: "Medical", merchant: "Chemist Warehouse (Crestmead)"}
];

export default function Table() {
    return(
         <div>
            <table className="table-fixed w-full">
                <thead>
                    <tr className="font-header font-semibold text-md text-gray-800 ">
                        <th className="w-0"></th>
                        <th className="w-8">Date</th>
                        <th className="w-16">Details</th>
                        <th className="w-7 text-left">Category</th>
                        <th className="w-3">Debit</th>
                        <th className="w-3">Credit</th>
                        <th className="w-5">Balance</th>
                    </tr>
                </thead>
                <tbody className="">
                    {transactions.map(({date, amount, type, desc, balance, category, merchant}, index) =>(
                        <tr key={index} className="border-t border-b border-gray-200 text-md text-gray-600 font-body font-semibold">
                            <td>
                                <input className="h-8" type="checkbox" id={index} name={index}></input>
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
                            <td className="text-gray-800 text-center">
                                {balance < 0 ? (<span className="text-[#E11A0CFF]">-</span> ) :
                                               (<span className="text-[#2CBD00FF]">+</span> )}
                                {formatMoney(balance).replace("-", "")}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>  
    )
}