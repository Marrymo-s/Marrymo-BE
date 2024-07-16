## README 작성

### 1. 깃 전략

    (main) : 최상위 브랜치

    (Deploy) : E2E 테스트 수행

    (dep/FE, dep/BE) : Deploy 하위 브랜치, CI/CD

    (dev/fe, dev/be) : dep/FE, dev/be 각각의 하위 브랜치

    (feat/fe/.., feat/be/..) : dev/fe. dev/be 각각의 하위 브랜치

![GitBranch](/uploads/bfe0771704df52f32760027c78b4f5f0/GitBranch.PNG)

### 2. 커밋 컨벤션


    feat : 새로운 기능 추가

    fix : 버그 수정

    docs : 문서 수정

    style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우

    refactor : 코드 리팩토링

    perf : 성능개선

    test : 테스트 추가

    chore : 빌드 과정 수정, 패키지 매니저 수정

    comment : 필요한 주석 추가 했을 경우

    design : css나 디자인 변경, 이미지 추가 등

### 3. FE 코딩 컨벤션
  1. 함수 형태는 `arrow function`을 사용합니다.
  2. 모든 변수 명은 camelCase 규칙을 따릅니다.
  3. 함수 이름은 ‘동사-설명’ 형태를 따릅니다.
    - ex) getUserPath
  4. custom hooks - 파일명은 use 로 시작한다.
  5. constants - 모두 대문자, _
  6. Array - 직접 배열에 항목을 대입하지 말고, Array#push를 이용하기
  7. 배열을 복사할때는 배열의 확장연산자 ... 를 이용하기
  8. 문자열은 싱크쿼트 ‘’ 사용하기
  9. 변수 및 함수
```js
// 배열: 복수형 이름 사용
const datas = [];
// 정규 표현식: 'r'로 시작
const rName = /.*/;

// 이벤트 핸들러: 'on'으로 시작
const onClick = () => {};
const onChange = () => {};

// 반환 값이 boolean인 경우: 'is'로 시작
const isLoading = false;

// Fetch함수: method(get, post, put, del)로 시작
const getEnginList = () => {...}
```
### 4. BE 코딩 컨벤션

    1. 기본적으로 네이버 코딩 컨벤션 사용한다.
    2.  boolean type은 변수 앞에 'is'를 붙인다.
    3. 변수와 함수명은 camelCase로 작성한다.
    4. ENUM이나 상수는 대문자로 네이밍한다.
    5. 함수명은 소문자로 시작하고 동사로 네이밍한다.
    6. 클래스명은 명사로 작성하고 UpperCamelCase를 사용한다.
    7. 패키지는 도메인 별로 생성한다. 

### 5. 개발스택
  #### FE
    Next.js 13.5.6
    Typescript 5.3.3
    vanilla-extract 1.14.1
    pnpm 8.15.4
    node 20.11.1
    zustand 4.5.1
    react-query 5.24.1

  #### BE
    Spring boot 3.2.3
    Spirng security
    MySQL 8.0.36
    Java Azul 17.0.10
    Swagger 3
    Lombok
    Jpa
    MyBatis
    Gradle
    Redis

  #### Infra
    Nginx
    Docker
    Jenkins
    Mobaxterm

