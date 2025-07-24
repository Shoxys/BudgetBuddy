/**
 * Tailwind CSS configuration for the application.
 */

export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary_blue: '#008CFF',
        secondary_red: '#E92C81',
        bb_purple: '#853FFF',
        bb_green: '#64E69C',
        bb_yellow: '#FFBD51',
        btn_hover: '#2180F0',
        bb_salmon: '#FFF8F2',
        bb_darkgrey: '#17191F',
        bb_neutral: '#F3F4F6',
        bb_aqua: '#F0F8FF80',
        bb_slate: '#FAFAFB',
      },
      fontFamily: {
        header: ['Archivo', 'sans-serif'],
        body: ['Inter', 'sans-serif'],
      },
      screens: {
        '3xl': '1921px',
      },
      boxShadow: {
        'custom-blue': '0px 8px 17px #008cff26, 0px 0px 2px #008cff1F',
        'bb-general': '0px 0px 1px #171a1f12, 0px 0px 2px #171a1f1F',
      },
      width: {
        '6/13': '47%',
      },
      border: {
        border_gray: '#008CFF',
      },
    },
  },
  plugins: [],
};