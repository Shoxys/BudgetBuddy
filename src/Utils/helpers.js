export function formatMoney(amount, currency = 'AUD') {
  return amount.toLocaleString('en-AU', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
  });
}

export function formatDate({date}) {
    const formatted = new Intl.DateTimeFormat("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
    }).format(date);

    return formatted
}