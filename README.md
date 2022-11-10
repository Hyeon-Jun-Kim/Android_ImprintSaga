# Imprint Saga
> Android Studio | Java / Firebase 

일학습병행제 Off-JT 프로젝트

![무제 002](https://user-images.githubusercontent.com/59905688/201081217-8bf04718-f681-4672-aaa3-dd3c97eac45d.jpeg)

## 프로젝트 소개

영단어 학습을 돕는 퀴즈형 Game Application

- 사용 기술 및 라이브러리
    + Java, Android
    + FireBase
    + Google Login
   
- 구현 기능
    + 로그인 & 회원가입
    + 상점 & 아이템 구입
    + 단계별 활성화 Map
    + Battle(Word Quiz)
    + 캐릭터 Status
    + 오답문제 반복 학습(Training)
    + Pixel UI
    
- 참여자 : 김현준(Developer) holden.developer@gmail.com, 최수현(Developer) shChoie@gmail.com (총 2인) 

- 진행 기간 : 2021.03.12 - 2022.06.10 (총 4주) 
<br/>

### UI Design Plot

<img width="1271" alt="스크린샷 2022-11-10 오후 8 50 44" src="https://user-images.githubusercontent.com/59905688/201083453-ac92450c-d673-44bf-9e51-d73e3d4b9ea1.png">

## 구현 기능

- Google Login
    + 최초로 동작할 때 보여지는 레이아웃입니다. Firebase와 연결되어 구글 계정을 통해 로그인/회원가입을 할 수 있도록 구현했습니다.
    + 로그인에 성공한다면 메인페이지로 이동하거나 로그아웃 / 탈퇴가 가능한 개인정보창이 보여집니다.
    <img width="1440" alt="Login" src="https://user-images.githubusercontent.com/59905688/201118057-075b1756-6506-43ae-a476-0e7d55f2f5d7.png" width="40%" height="40%">
    
- Main
    + 로그인 후 보여지는 화면으로 Store, Status, Ranking, Battle, Training 으로 이동할 수 있습니다.
    + 스테이지 정보는 FireBase와 연동되어 이전 스테이지를 클리어하지 않으면 활성화 되지 않도록 하였고, 클리어시 정답률에 따라 점수(★)가 매겨집니다.
    <img width="1440" alt="Main" src="https://user-images.githubusercontent.com/59905688/201118374-e0d88127-5e31-4385-a4b4-04b758346b16.png" width="40%" height="40%">
    
- Shop
    + 사용자가 스테이지를 클리어하며 얻게되는 포인트를 아이템으로 교환할 수 있는 페이지로, 구매가 가능한 아이템은 컬러로 보여지지만 구매할 수 없는 아이템은 흑백으로 보여지게 됩니다.
    + 아이템을 구매하면 해당 아이템은 중복으로 구매할 수 없도록 하였습니다.
    <img width="1440" alt="Shop" src="https://user-images.githubusercontent.com/59905688/201118692-57427d66-2242-472f-9f4b-f5b070cff062.png" width="40%" height="40%">
    
- Status
    + 사용자의 캐릭터와 능력치를 보여주는 페이지로 구매한 아이템을 장착 / 해제 할 수 있고, 이에 따라 캐릭터의 모습이 변화되도록 구현했습니다.
    + 특정 스테이지를 클리어 했을 때 리워드로 얻게되는 메달을 확인하거나 전체 맵의 달성도와 정답률을 계산하여 보여주도록 했습니다.
    <img width="1440" alt="Status" src="https://user-images.githubusercontent.com/59905688/201118929-a80476d3-6dea-4311-9d85-0f3784dc7d19.png" width="40%" height="40%">
    
- Battle
    + 실질적인 단어 학습이 이루어지는 페이지로 무작위로 섞여 나오는 단어의 철자(Button)을 선택하여 맞추는 방식으로, 먼저 체력이 0이 되는쪽이 패배하게 됩니다.
    + 제한 시간을 초과하거나 오답을 입력할 시 사용자의 캐릭터 체력이 감소하게 되며 정확한 철자를 입력했을 때에 NPC의 체력이 감소합니다.
    + Skill Button은 스킬을 사용할 수 있는 아이템을 착용했을 때 Visible 처리가 됩니다.
    + 클리어 시 정답률에 따라 스코어가 매겨지며 해당 정보는 FireBase에 업로드됩니다.
    <img width="1440" alt="Battle" src="https://user-images.githubusercontent.com/59905688/201119141-3e805eb0-eda3-4be7-8da1-bd410e9424a3.png" width="40%" height="40%">

- Training
    + 체력 제한이 없이 원하는 만큼 복습을 할 수 있는 페이지로, 내부 저장소에 저장된 오답 문제 리스트를 불러와 Battle 페이지와 같은 방식으로 진행됩니다.
    <img width="1440" alt="Training" src="https://user-images.githubusercontent.com/59905688/201119332-7d970f5d-7a58-4713-8458-fba863bc66b3.png" width="40%" height="40%">
    
- FireBase
    + 저장되는 데이터는 유저의 계정 정보와 각각의 계정이 갖는 상태정보 입니다.
    + 각각의 테이블의 루트 경로는 어플리케이션에 로그인 시 연결한 구글 이메일 주소가 됩니다.
    <img width="1440" alt="FireBase_1" src="https://user-images.githubusercontent.com/59905688/201119588-eab5da58-5907-4b3e-8ae2-90a5866710eb.png" width="40%" height="40%">
    <img width="1440" alt="FireBase_2" src="https://user-images.githubusercontent.com/59905688/201119611-ccd48c8b-f73c-4ae4-80aa-1ff746f58521.png" width="40%" height="40%">
    
