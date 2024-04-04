import {style} from '@vanilla-extract/css';
import {vars} from '@/styles/vars.css';
import { flex } from '@/styles/common.css';

export const albumWrapper = style([
  {
    textAlign: 'center',
    justifyContent: 'center',
    paddingTop: '80px',
    width: 'auto',
    maxWidth: '480px',

  },
])

export const albumText = style({
  fontSize: vars.fontSize['2.5x'],
  fontWeight: vars.fontWeight.accent,
  paddingBottom: '10px',

})


export const albumPhotoBook = style([
  flex({ align: 'center', direction: 'column', justify: 'center' }),
  {
    alignItems: 'center',
    justifyContent: 'center',

  },
  {
    display:'grid',
    gridTemplateColumns: 'repeat(2, 1fr)',
    gap: vars.space['2x'],
    paddingTop: '40px',
    width: 'auto',
    maxWidth: 480,
    minHeight: '75svh',
    position: 'relative',
  }
])

export const imageContainer = style({
  // 이미지가 가운데 오도록 설정
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',

});