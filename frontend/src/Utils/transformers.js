export function formatIncome(rawData) {
  const months = rawData?.months ?? [
    'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
  ];
  const thisYear = rawData?.thisYear ?? [];
  const lastYear = rawData?.lastYear ?? [];

  return months.map((month, index) => ({
    month,
    thisYear: thisYear[index] ?? 0,
    lastYear: lastYear[index] ?? 0
  }));
}