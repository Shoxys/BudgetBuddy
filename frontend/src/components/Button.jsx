export default function Button({ label, onClick, font = 'font-header', padding = 'px-6 py-2', weight='font-bold', size = 'text-lg', rounding = 'rounded-xl', className = ''}) {

  return (
    <button className={`bg-primary_blue ${padding} ${size} text-white ${rounding} ${font} ${weight} ${className} hover:bg-btn_hover outline-none`}
    onClick={onClick}>
    {label}
    </button>
  )
}
