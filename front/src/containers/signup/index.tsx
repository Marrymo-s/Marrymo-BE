'use client';

import {MouseEventHandler, useState} from 'react';
import {useRouter} from 'next/navigation';

import Header from '@/components/Header'
import * as styles from './index.css'
import KakaoMap from '@/components/KakaoMap'

import WeddingDatepicker from "@/containers/signup/WeddingDatepicker";
import InputBox from "@/components/InputBox";
import InvitationMessage from "@/containers/signup/InvitationMessage";
import Button from '@/components/Button/index'
import useModal from '@/hooks/useModal'

import WeddingImageUpload from "@/containers/signup/WeddingImageUpload";
import {userInfoStore} from "@/store/store";


const Signup = () => {
  const [weddingLocation, setWeddingLocation] = useState<string>('');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const userCode = userInfoStore((state) => state.userCode);

  const handleLocationSelect = (location: string) => {
    setWeddingLocation(location);
    setIsModalOpen(false); // 장소 선택 시 모달 닫기
  };

  const router = useRouter()
  const {Modal, openModal, closeModal} = useModal();

  const openKakaoMapSearch = async () => {
    openModal()
  };
  console.log(userCode)
  return (
    <>
      <Header title={'개인 정보 입력'} hasPrevious/>
      <main className={styles.signupWrapper}>
        <div>
          <InputBox
            inputBoxHeader='신랑 이름'
            placeholder='신랑 이름을 입력하세요.'
            asterisk={true}/>
        </div>
        <div>
          <InputBox
            inputBoxHeader='신랑 아버지 이름'
            placeholder='신랑 아버지 이름을 입력하세요.'/>
        </div>
        <div>
          <InputBox
            inputBoxHeader='신랑 어머니 이름'
            placeholder='신랑 어머니 이름을 입력하세요.'/>
        </div>
        <div>
          <InputBox
            inputBoxHeader='신부 이름'
            placeholder='신부 이름을 입력하세요.'
            asterisk={true}/>
        </div>
        <div>
          <InputBox
            inputBoxHeader='신부 아버지 이름'
            placeholder='신부 아버지 이름을 입력하세요.'/>
        </div>
        <div>
          <InputBox
            inputBoxHeader='신부 어머니 이름'
            placeholder='신부 어머니 이름을 입력하세요.'/>
        </div>
        {/*TODO: 연락처 error message - 숫자형식만 받기(숫자 형식이 아니면 에러 메시지 */}
        <div>
          <InputBox
            inputBoxHeader='신랑 연락처'
            placeholder='신랑 연락처를 입력하세요.'
            asterisk={true}/>
        </div>
        <div>
          <InputBox
            inputBoxHeader='신부 연락처'
            placeholder='신부 연락처를 입력하세요.'
            asterisk={true}/>
        </div>
        <div>
          <InvitationMessage/>
        </div>
        <div>
          <InputBox
            inputBoxHeader='이메일 주소'
            placeholder='이메일 주소를 입력하세요.'
            asterisk={true}
            button={{
              text: '인증',
              // 추후에 인증 메일 보내는 함수 작성
              onClick: () => {
              },
              type: 'button',
              size: 'small'
            }}
          />
        </div>
        <div>
          {/*TODO: 인증번호가 일치하면 safeGreen 색깔로 안내 문구 뜨게 만들기*/}
          <InputBox
            inputBoxHeader='인증 번호 입력'
            placeholder='인증 번호를 입력해주세요.'
            asterisk={true}
          />
        </div>
        <div className={styles.weddingDatePickerContainer}>
          <div>
            결혼식 일자 선택
            <span className={styles.asteriskStyle}>*</span>
          </div>
          <div>
            <WeddingDatepicker/>
          </div>
        </div>
        <div>
          <InputBox
            inputBoxHeader='결혼식 장소 선택'
            placeholder='결혼식 장소를 선택해주세요.'
            asterisk={true}
            button={{
              text: '검색',
              // 추후에 인증 메일 보내는 함수 작성
              onClick: () => {
                openKakaoMapSearch()
              },
              type: 'button',
              size: 'small'
            }}
          />
        </div>
        <div>
          <WeddingImageUpload/>
        </div>
        <div>
          {/*TODO: */}
          <Button
            onClick={() => {
              router.push('/preview')
            }}
            type='button'
          >다음</Button>
        </div>
        <Modal>
          <>
            <KakaoMap setWeddingLocation={setWeddingLocation}/>
            <input type="text" value={weddingLocation} readOnly/>
          </>
        </Modal>
      </main>
    </>
  )
}

export default Signup;