export default function FormatRelativeDate({ date }) {
  const now = new Date();
  const target = new Date(date);

  const diffMilliseconds = target - now;
  const diffDays = Math.round(diffMilliseconds / (1000 * 60 * 60 * 24));
  const absDiffDays = Math.abs(diffDays);
    
  let relativeDate;
  let color;
  
  if (diffDays === 0) {
    relativeDate = "Today"     
  }
  else if (diffDays === 1) {
    relativeDate = "Tommorrow"
  } 
  else if (diffDays === -1) {
    relativeDate = "Yesterday" 
  }
  else {
    if (absDiffDays >= 365) {
       const years = Math.floor(absDiffDays / 365)
       relativeDate = `${years} ${years === 1 ? "year" : "years"} ${diffDays > 0 ? "left" : "ago"}` 
    }
    else if (absDiffDays >= 30) {
        const months = Math.floor(absDiffDays / 30)
        relativeDate = `${months} ${months === 1 ? "month" : "months"} ${diffDays > 0 ? "left" : "ago"}`
    } 
    else if (absDiffDays > 7) {
        const weeks = Math.floor(absDiffDays / 7)
        relativeDate = `${weeks} ${weeks === 1 ? "week" : "weeks"} ${diffDays > 0 ? "left" : "ago"}` 
    }
    else {
        relativeDate = `${diffDays} ${diffDays === 1 ? "day" : "days"} ${diffDays > 0 ? "left" : "ago"}` 
    }
  }

  if (diffDays < 0) {
    color = "text-[#E92C81FF]"
  } 
  else if (diffDays < 14) {
    color = "text-[#FF8307FF]"
  }

  return (
    <span className={`${color}`}>{relativeDate}</span>
  )
  
}
