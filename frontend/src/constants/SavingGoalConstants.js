/**
 * Constants for Saving Goals
 */

export const GoalTypes = {
  COMPLETED: 'COMPLETED',
  INPROGRESS: 'IN_PROGRESS',
  OVERDUE: 'OVERDUE',
  TOTAL: 'TOTAL',
};

export const DEFAULT_IMAGE = "/assets/default.png";

export const GoalMeta = {
  [GoalTypes.COMPLETED]: {
    title: 'Completed Goals',
    icon: '/assets/completed-icon.png',
    color: 'bg-[#E8FFF1]',
  },
  [GoalTypes.INPROGRESS]: {
    title: 'Goals in Progress',
    icon: '/assets/inprogress-icon.png',
    color: 'bg-[#FFF0CD]',
  },
  [GoalTypes.OVERDUE]: {
    title: 'Overdue Goals',
    icon: '/assets/overdue-icon.png',
    color: 'bg-[#FBD8E8]',
  },
  [GoalTypes.TOTAL]: {
    title: 'Total Goals',
    icon: '/assets/total-icon.png',
    color: 'bg-[#F0F8FF]',
  },
};