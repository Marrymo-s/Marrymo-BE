import {style} from "@vanilla-extract/css";
import {vars} from "@/styles/vars.css";
import {flex} from "@/styles/common.css";
import {contentWrapper} from "@/styles/wrapper.css";

export const agreementWrapper = style([
  contentWrapper({contentArea: 'header'}),
  flex({
    align: 'center',
    justify: 'flexStart',
    direction: 'column',
  }),
  {
    padding: vars.space['4x']
  },
  {
    '@media': {
      'screen and (min-width: 480px)': {
        width: '480px',
      },
    },
  },
]);

export const termsOfUseContainer = style([
  flex({justify: 'center', align: 'flexStart'}),  // center 정렬 적용이 안 됨(원인은 5주차에 잡을 예정)
  {
    width: 'auto',
    maxWidth: '83%',
    padding: vars.space["2x"],
    background: vars.colors.whitesmoke,
    height: 'calc(80dvh - 80px - 120px)',
    margin: '32px 0px',
    position: 'relative',
    boxShadow: `0px 10px 25px 0px ${vars.colors.whitesmoke}`,
    overflow: 'auto',
  }]);

export const checkboxWrapper = style({
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'flex-start', // 왼쪽 정렬
});