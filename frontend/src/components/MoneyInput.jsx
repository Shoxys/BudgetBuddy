export default function MoneyInput({ id, label, placeholder}) {
    return (
    <>
        <label htmlFor={id} className="font-body font-semibold text-lg">{label}</label>
        <div className="relative mt-2 mb-5">
            <input id={id} className="pl-7 border border-border_gray text-base font-body py-2.5 px-2 shadow-sm shadow-gray-300 outline-primary_blue focus:shadow-sm focus:shadow-blue-300 w-1/2 rounded-md" type="number" placeholder={placeholder} min="0" step="5"/>
            <label className="absolute left-3 top-2 text-lg text-gray-500">$</label>
        </div>
    </>
    )
}