/**
 * Tag component for displaying transaction categories with color coding.
 */

import { CategoryColors } from "../constants/TransactionConstants";

export default function Tag({ category = 'Other' }) {
  const colorClass = CategoryColors[category] || CategoryColors.Other;

  // Layout
  return (
    <div className={`inline-block px-3 py-1 rounded-2xl ${colorClass}`}>
      {category}
    </div>
  );
}