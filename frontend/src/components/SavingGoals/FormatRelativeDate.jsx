/**
 * FormatRelativeDate component for displaying relative date strings.
 * Renders dates as 'Today', 'Tomorrow', or time differences with dynamic colors.
 */

export default function FormatRelativeDate({ date }) {
  // Calculate time difference and format
  const now = new Date();
  const target = new Date(date);
  const diffMilliseconds = target - now;
  const diffDays = Math.round(diffMilliseconds / (1000 * 60 * 60 * 24));
  const absDiffDays = Math.abs(diffDays);

  let relativeDate;
  let color;

  if (diffDays === 0) {
    relativeDate = 'Today';
    color = 'text-[#FF8307]';
  } else if (diffDays === 1) {
    relativeDate = 'Tomorrow';
    color = 'text-[#FF8307]';
  } else if (diffDays === -1) {
    relativeDate = 'Yesterday';
    color = 'text-[#E92C81]';
  } else if (absDiffDays >= 365) {
    const years = Math.floor(absDiffDays / 365);
    relativeDate = `${years} ${years === 1 ? 'year' : 'years'} ${diffDays > 0 ? 'left' : 'ago'}`;
    color = diffDays > 0 ? 'text-[#008CFF]' : 'text-[#E92C81]';
  } else if (absDiffDays >= 30) {
    const months = Math.floor(absDiffDays / 30);
    relativeDate = `${months} ${months === 1 ? 'month' : 'months'} ${diffDays > 0 ? 'left' : 'ago'}`;
    color = diffDays > 0 ? 'text-[#008CFF]' : 'text-[#E92C81]';
  } else if (absDiffDays > 7) {
    const weeks = Math.floor(absDiffDays / 7);
    relativeDate = `${weeks} ${weeks === 1 ? 'week' : 'weeks'} ${diffDays > 0 ? 'left' : 'ago'}`;
    color = diffDays > 0 ? 'text-[#008CFF]' : 'text-[#E92C81]';
  } else {
    relativeDate = `${absDiffDays} ${absDiffDays === 1 ? 'day' : 'days'} ${diffDays > 0 ? 'left' : 'ago'}`;
    color = diffDays > 0 ? 'text-[#FF8307]' : 'text-[#E92C81]';
  }

  // Layout
  return <span className={color}>{relativeDate}</span>;
}