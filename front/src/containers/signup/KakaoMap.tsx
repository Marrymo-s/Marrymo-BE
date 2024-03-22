'use client';

import React, {useEffect} from "react";

import * as styles from './index.css'

interface keywordProps {
    searchKeyword: string;
}

const KakaoMap = ({searchKeyword}: keywordProps) => {

    console.log(searchKeyword);
    useEffect(() => {
        console.log('안녕')
        // console.log(window.kakao)
        if (window.kakao) {
            console.log('안녕2')
            window.kakao.maps.load(() => {
                console.log('kakaomaps 로드')
                // id가 'map'인 요소에 지도를 생성
                const mapContainer = document.getElementById("map"),
                    mapOption = {
                        // 해당 좌표는 멀티캠퍼스를 중심으로 함(기초 좌표)
                        center: new window.kakao.maps.LatLng(37.501286, 127.039602),
                        // 줌 레벨 기본값
                        level: 3,
                    };
                const map = new window.kakao.maps.Map(mapContainer, mapOption);
                console.log('지도 출력됨')
            });
        }
    }, [searchKeyword]);

    return (
        // id가 'map'인 div 출력, width와 height를 설정해줘야 정상 출력됨
        <div id="map" style={{width: '300px', height: '300px'}} />
    );
};

export default KakaoMap;