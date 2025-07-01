import { formatMoney } from "../../Utils/helpers"
export default function TotalBalanceCard({totalBalance}) {
    return(
        <div className="bg-primary_blue px-4 py-2 flex flex-row justify-between gap-2 text-white rounded-lg w-72">
            <div>
                {/* Title */}
                <h2 className="font-body text-md font-normal">Total Balance</h2>
                {/* Total Balance */}
                <h1 className="text-2xl font-header font-bold">{totalBalance ? formatMoney(totalBalance): "$0"}</h1>
            </div>
            <div className="flex items-center">
                <img className="w-11 h-11" src="src/assets/balance-icon.png" alt={`Balance icon`} />
            </div>
        </div>
    )
}