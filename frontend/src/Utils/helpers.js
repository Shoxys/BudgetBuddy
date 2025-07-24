// helpers.js
export const formatMoney = (value) => {
    if (value == null) {
        return '0.00';
    }
    return Number(value).toLocaleString('en-AU', { style: 'currency', currency: 'AUD' });
};

export function formatDate(date) {
  if (!date) return "Invalid Date";
  const parsedDate = new Date(date);

  return new Intl.DateTimeFormat("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(parsedDate);
}
