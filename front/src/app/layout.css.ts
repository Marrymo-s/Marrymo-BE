import {globalStyle} from '@vanilla-extract/css';
import {vars} from '@/styles/vars.css';
// import { font } from '@/styles/font.css';
import {cardFont} from '@/styles/font.css';


globalStyle('html', {
  backgroundColor: vars.colors.white,
  scrollbarGutter: 'stable',
  fontFamily: 'pretendard', // 폰트 이름 직접 사용


});

globalStyle('body', {
  width: 'auto',
  maxWidth: 480,
  minHeight: '100svh',
  margin: '0 auto',
  padding: 0,
  backgroundColor: vars.colors.white,
  position: 'relative',
  boxShadow: `0 0 25px ${vars.colors.whitesmoke}`,
});

globalStyle('*::-webkit-scrollbar', {
  display: 'none',
});

globalStyle('ul', {
  listStyle: 'none',
  padding: 0,
  margin: 0,
});

globalStyle('textarea', {
  fontFamily: cardFont,
});
