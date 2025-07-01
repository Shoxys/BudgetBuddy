import MoneyInput from './MoneyInput'
export default function ValueInput() {
    return (
        <>
            <MoneyInput id="investments" label="Total Investments" placeholder="Enter value of total investments"/>   
            <MoneyInput id="savings" label="Total Savings" placeholder="Enter value of total savings"/>  
        </>
    )
}