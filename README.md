# Shopping API

**Shopping API**는 현대적인 쇼핑 플랫폼을 위한 백엔드 RESTful 서비스입니다.  
Spring Boot, Spring Security, Spring Data JPA/Hibernate, JWT 등의 최신 기술을 활용하여 사용자 인증, 주문 관리 등 주요 기능을 제공합니다.  
이 프로젝트는 확장성과 유지보수를 고려한 모듈화된 아키텍처로 구성되어 있으며, 프론트엔드 애플리케이션과 원활하게 통신할 수 있도록 설계되었습니다.

## 주요 기능

- **사용자 인증 및 권한 부여**
    - JSON 기반 로그인 및 로그아웃 API 제공
    - JWT를 활용한 보안 인증 및 커스텀 JSON 세션 로그인 필터 구현
- **주문 관리**
    - 주문 생성, 조회, 수정, 취소 등 CRUD 기능
    - 사용자별 주문 내역 및 상세 정보 제공
- **RESTful API 설계**
    - 명확한 엔드포인트 구성 및 HTTP 상태 코드를 활용한 응답 처리
- **데이터 영속성**
    - Spring Data JPA/Hibernate를 통한 ORM 기반 데이터베이스 연동
    - H2DB 및 MySQL 지원

## 기술 스택

- **Java 21**
- **Spring Boot** (3.4 기반)
- **Spring Security** (JWT 및 커스텀 JSON 세션 로그인 필터)
- **Spring Data JPA / Hibernate**
- **Gradle** (빌드 도구, 버전 8.12.1)
- **H2DB/MySQL** (데이터베이스)

## 프로젝트 구조

```
shopping-api/
├── src/
│   ├── main/
│   │   ├── java/com/shop/shoppingapi/
│   │   │   ├── config/           // 애플리케이션 및 보안 관련 설정
│   │   │   ├── controller/       // REST 컨트롤러 (API 엔드포인트)
│   │   │   ├── entity/           // JPA 엔티티 (예: Order, User 등)
│   │   │   ├── repository/       // Spring Data JPA 리포지토리
│   │   │   ├── security/         // 보안 설정, 커스텀 필터, 핸들러 등
│   │   │   ├── service/          // 비즈니스 로직을 처리하는 서비스 레이어
│   │   │   └── ShoppingApiApplication.java  // 메인 Spring Boot 애플리케이션 클래스
│   │   └── resources/
│   │       ├── application.yml  // 애플리케이션 설정 파일
│   │       └── ...                
│   └── test/                     // 단위 테스트 및 통합 테스트
├── pom.xml                       // Maven 빌드 설정 파일
└── README.md                     // 프로젝트 문서 (현재 파일)
```

## 시작하기

### 사전 준비 사항

- **Java 21**
- **Gradle 8.12.1**
- **H2DB 또는 MySQL** (또는 지원되는 다른 관계형 데이터베이스)

### 설치 및 실행

1. **저장소 클론**
   ```bash
   git clone https://github.com/youseonghyeon/shopping-api.git
   cd shopping-api
   ```

2. **데이터베이스 설정**  
   `src/main/resources/application.properties` 파일에서 데이터베이스 연결 정보를 수정합니다:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/shopping
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **프로젝트 빌드 및 실행**
   ```bash
    ./gradlew build
    ./gradlew bootRun
   ```

## API 엔드포인트

### 사용자 인증
- `POST /login`  
  JSON 형식으로 사용자 아이디와 비밀번호를 전송하여 로그인합니다.
- `POST /logout`  
  로그아웃 요청을 보내어 세션을 무효화하고 인증 정보를 삭제합니다.
- `POST /signup`  
  사용자 등록 기능을 제공합니다.

### 주문 관리
- `GET /orders`  
  인증된 사용자에 대한 주문 내역(페이징 지원)을 조회합니다.
- `GET /orders/{id}`  
  특정 주문의 상세 정보를 조회합니다.
- `POST /orders`  
  새로운 주문을 생성합니다.
- `PUT /orders/{id}`  
  기존 주문을 수정합니다.
- `DELETE /orders/{id}`  
  주문을 취소 또는 삭제합니다.

## 테스트

프로젝트 테스트는 아래 명령어로 실행할 수 있습니다:
```bash
gradle test
```

## 배포

이 서비스는 Spring Boot 애플리케이션으로 독립 실행형으로 배포할 수 있으며, 필요에 따라 Docker와 같은 컨테이너 환경에서 배포할 수 있습니다.
