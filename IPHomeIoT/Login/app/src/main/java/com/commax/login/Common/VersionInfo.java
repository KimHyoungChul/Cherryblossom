package com.commax.login.Common;

/**
 * Created by OWNER on 2017-01-16.
 */
public class VersionInfo {
/*******************
 * Revision History: IP Home IoT
 -----------------------------------------------------------------------------
 [ver]		[Date]			[Description]
 -----------------------------------------------------------------------------
 롯데월드 타워 회원가입앱 통홥화 이전

 com.commax.login.1.0.4 => 품보 내려준 버전
 com.commax.login.1.0.6 => 품보 최신 내려준 버전 ( toast 메시지 적용, 필수 옵션 UI적용)
 com.commax.login.1.0.7 => resourceNo 값 파일에 적용된 버전 (이전 버전 회원가입 꼭 초기화후 1.0.7 업데이트 진행해야함)
 com.commax.login.1.0.8 => sitecode , dong, ho 를 파일에 저장한 버전
 com.commax.login.1.0.9 => 파일에 mac 주소 추가
 com.commax.login.1.1.0 => access API 6번 등록 진행 후 완료되면 파일에 access yes 등록한다.=> 임시 사이트 코드 BA0 로 진행
 com.commax.login.1.1.1 => access 등록, UC 까지 연동  2016-10-06

 com.commax.login.1.1.2 => access등록, UC등록 완료 (네비게이션 하단바 적용 제거) 2016-10-10

 com.commax.login.1.1.3 => 초기화 진행시 강제 종료되는 현상 디버깅 , 2016-10-18

 com.commax.login.1.1.4 => 강제 종료 디버깅

 com.commax.login.1.1.5 => MainActivity  , 비밀번호변경 Dialog에 하단 키바 적용
 영어, 중국어 번역본 적용

 com.commax.login.1.1.6 => 1.0.6 버전에서 업데이트 될시 동작 가능하도록 수정

 com.commax.login.1.1.7 => 국가코드 south korea(한국) 으로 다국어 지원되도록 수정

 com.commax.login.1.1.8  => 넥셀용 API레벨 낮춤

 com.commax.login.1.1.9  => access 멤버구분에서 경비실 -> 월패드

 com.commax.login.1.2.0  => 70gx+ 용으로 아이콘 변경 , onclick 312 줄 널포인트 임시 수정

 com.commax.login.1.2.1. => ns , ews 서버 정보를 클라우드 서버에서 가져오도록 저장 , cloud_svr.i 파일에 UC_Group_port 로 변경,
 번역 외국어 적용

 com.commax.login.1.2.2 => 여주 샘플용 7인치 예전 아이콘으로 변경해서 릴리즈

 com.commax.login.1.2.3 => 롯데 아이콘 변경 , 토스트 라운드 적용 , 번역 confirm ->retype 으로 변경

 com.commax.login.1.2.4 => 2016-11-11 Access API 제배포로 인한 소스 수정 (롯데)

 com.commax.login.1.2.5 => 70GX+ 앱 릴리즈

 2016-11-11 ACCESS API 수정됨 , 기존 70GX+ 현장 나간것들은 ACCESS 등록안되고 에러 메세지 표출 될예정

 com.commax.login.1.2.6 => 토큰에서 ssl 정보 가져와서 적용 (롯데)

 com.commax.login.1.2.7 => 토큰에서 ssl 정보 적용 (7인치)

 com.commax.login.1.2.8 => access url 토큰정보에서 가져오도록 수정 (롯데)

 com.commax.login.1.2.9 => access url 토큰 정보에서 가져오도록 수정(7인치)

 com.commax.login.1.3.0 => Full Ip 릴리즈

 com.commax.login.1.3.1 => 롯데월드 타워 , Full IP
 2016-12-02 : 로컬 서버에서 사이트 코드 가져와서 해당사이트 코드 저장 ,  사용하여 UC , 클라우드 서버에 회원가입 적용, 회원가입이 완료되면 resorceNo 를 로컬 서버에 전송 , 회원 초기화시 resourceNo 삭제하라고 로컬 서버에 전송

 com.commax.login.1.3.2 => IP Home IoT 용 릴리즈 (동/호 , 로컬 IP주소 읽어오기 적용)
 로컬 서버가 10.x.x.x 가 아닌경우 version.i 에서 읽어온다.

 ------------------------------------------------------------------------------------------------------------------------------------------


 IP Home Io T (회원가입 앱 통합화 진행 후)

 1.0.0      2016.12.28      A64 IP Home IoT 에 2차 개발 버전 release 로 1.0.0 버전으로 release 됨

 1.0.1      2016.           ?

 1.0.2      2017.01.16      토큰 만료 이슈로 Boradcase 를 Access등록 후에 보내는 걸로 변경 해서 release, 홍석 전임님이 Token 갱신시 CreateAccount.properties 파일에 Token 값 넣기로 결정 , SQA 버전 릴리즈

 1.0.3      2017.01.17      mnt/sdcard/networkState.i 파일에서 네트워크 상태 읽어서 API호출 여부 판단, networkState 가 false 면 다이얼로그 띄움

 1.0.4      2017.01.24      mac정보를  기존 파일에서 읽어와서 토근을 갱신하는 로직으로 변경, 만약 mac정보가 바뀔경우를 대비하기 위함
                            토큰이 만료되면 토큰을 3번까지 갱신하는 로직 적용 , try count 가 3이 넘어가면 토스트 메세지 표출

 1.0.5      2017.02.24     IPHomeIoT new UI 적용
                            BuyerID 로 경비실기 , 롯데 ,  IPHomeIoT ,  70UX 통합화 관리
 1.0.6      2017.02.28     IPHomeIoT new UI app icon 적용

 ********************/


    /*
    * 예외 사항
    * 1. 통합화 하는 과정에서 com.commax.loginnexel CDV-70UX 넥셀 버전에서 사용하는 /user/app/bin/CreateAccount.properties 파일에 접근 하려면 해당 파일 권한이 666으로 변경이 되어야 한다.
    * 업데이트 진행하는 시에 com.commax.loginnexel APP을 지우고 CreateAccount.properties 파일의 권한을 666으로 바꾸어 주어야 한다.
    *
    * */
}
