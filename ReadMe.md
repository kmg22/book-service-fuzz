# Book Service (API)
> **Spring Boot 4.0.1 기반의 보안 특화 도서 검색 백엔드 서비스**

본 프로젝트는 알라딘 Open API를 연동한 도서 검색 기능을 제공하며, **Jazzer**를 이용한 퍼즈 테스트(Fuzzing)와 **홈서버 기반 CI/CD** 환경을 구축하여 실제 운영 환경에서의 가용성과 보안성을 검증하는 데 초점을 맞췄습니다.

---

## Tech Stack & Decision Rationale

### 1. Backend Framework & DB
| 기술 | 핵심 특징 |
| :---  | :--- |
| **Spring Boot 4.0.1** | 최신 표준 사양 준수, 고성능 아키텍처 |
| **Java 17 (LTS)** | 안정적인 LTS, 생산성 높은 문법 |
| **PostgreSQL 15** | 강력한 인덱싱, 데이터 무결성 보장 |

### 2. Security & Quality Assurance
| 기술 |  핵심 특징 |
| :--- |  :--- |
| **Jazzer (Fuzzing)** | Coverage-guided Fuzzing, JVM 최적화 |

### 3. Infrastructure & DevOps
| 기술 | 핵심 특징 |
| :--- | :--- |
| **Home Server (N100)** |TDP 6W의 저전력 고효율 운영 |
| **Nginx Proxy Manager** |  GUI 기반 프록시 관리, HTTPS 강제 |
| **GitHub Actions** | 배포 자동화, 빌드 품질 보장 |

---

## Automated CI/CD Lifecycle
Nginx Proxy Manager와 Docker를 연동하여 배포 시 서비스 중단을 최소화합니다.

| 단계 | 수행 내용 | 기술적 이점 |
| :--- | :--- | :--- |
| **Build & Fuzz** | GitHub Actions 내에서 Gradle 빌드 및 Jazzer 테스트 수행 | 보안 결함이 있는 코드의 상용 환경 유입 차단 |
| **Push** | 빌드된 이미지를 Docker Hub(`kmg22/book-service`)에 푸시 | 이미지 기반의 일관된 배포 환경 유지 |
| **Deploy** | 서버 접속 후 `docker-compose up -d`를 통한 컨테이너 교체 | 자동화된 명령어로 휴먼 에러 방지 및 빠른 롤백 |

---

## Env Setting (local) ##
1. git clone Repository
    ```shell
    $ git clone https://github.com/kmg22/book-service-fuzz.git
    $ cd book-service-fuzz
    ```
2. PostgreSQL setting 
    - Open your psql and create Database(bookdb)
        ```shell
        CREATE DATABASE bookdb;
        CREATE USER YOUR_USERNAME WITH PASSWORD 'YOUR_PASSWORD';
        GRANT ALL PRIVILEGES ON DATABASE bookdb TO YOUR_USERNAME;
        ```
    - Edit `src/main/resources/application.yaml`
        ```yaml
        datasource:
            url: jdbc:postgresql://localhost:5432/bookdb
            username: YOUR_USERNAME
            password: YOUR_PASSWORD
            driver-class-name: org.postgresql.Driver
        ```
3. Aladin API Password
    - Get API Key
        https://www.aladin.co.kr/ttb/wblog_manage.aspx
    - edit `src/main/resources/application.yaml`
        ```yaml
        ALADIN_API_KEY: "YOUR_ALADIN_API_KEY"
        ```
---
## Swagger (API Specification)
1. run `BookServiceApplication.java`
2. get `http://localhost:8080/swagger-ui/index.html`

---
## Local Fuzz Test (Jazzer) ##
```shell
$ ./gradlew test --tests "com.kmg.BookService.fuzz.BookServiceFuzzTest" "-Djazzer.duration=60"
```
