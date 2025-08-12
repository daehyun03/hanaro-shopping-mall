 ## 요구사항 구현
1. 관리자 기능
2. 일반 사용자 기능
3. 공통기능
## DB 및 테스트코드
- test/java/com/example/hanaro/DataInitializationTest.java 테스트 코드를 이용하여 DB 초기 데이터 및 개발 단계에서 수집된 데이터를 불러올 수 있습니다.
## 예외 처리 및 에러 응답 명세화
- 예외 처리 및 에러 응답은 com.example.hanaro.exception 패키지에서 확인할 수 있습니다.
- 예외 발생 시, 적절한 HTTP 상태 코드와 함께 에러 메시지가 반환됩니다.
- Swagger 문서에는 발생할 수는 에러코드만 명세되어 있습니다. 실제 에러 발생시 구체적인 원인을 확인할 수 있습니다.
## 기능 명세
### 인증/인가
- JWT 토큰 기반 인증/인가 기능이 구현되어 있습니다.
- 로그인 시 JWT 토큰이 발급됩니다.
- 관리자 계정은 hanaro/12345678 입니다.

### 파일업로드
- 상품 이미지 업로드 기능이 구현되어 있습니다.
- 썸네일 파일도 함께 생성됩니다.
- 명세된 위치에 파일이 저장됩니다.

### 주문 상태 스케줄링 확인
- logs/business_order-2025-08-11.log 에서 확인 가능

### 배치 Job 통계 데이터
- 배치 Job 통계 데이터는 daily_sales_stats, product_sales_stats 테이블에서 확인할 수 있습니다.
- 통계 데이터 생성로그는 logs/batch.log 에서 확인할 수 있습니다.

### 로깅
- 로깅은 logs 디렉토리에서 확인할 수 있습니다.
- 일일 단위로 rolling 되며, 파일명은 logs/business_*-YYYY-MM-DD.log 형식입니다.
- YYYY-MM-DD-HH-mm은 테스트용으로 생성된 파일입니다.

### Swagger(OpenAPI 3)
- Swagger 문서는 http://localhost:8080/swagger-ui/index.html 에서 확인할 수 있습니다.
- 관리자 기능과 일반 사용자 기능에 대한 API 문서가 포함되어 있습니다.

### Validation
- request body에 대해서 해당 DTO에 정의된 validation이 적용됩니다.
- 예를 들어, 상품 등록 시 ProductCreateRequestDTO에 정의된 validation이 적용됩니다.
- validation 실패 시 400 Bad Request 응답이 반환됩니다.

### Actuator
- Actuator는 http://localhost:8080/actuator 에서 확인할 수 있습니다.
- /health, /beans, /env, /metrics 등의 엔드포인트가 활성화되어 있습니다.

## 참고
- application.yml 파일에서 jpa.hibernate.show_sql: true 설정을 통해 SQL 로그를 확인할 수 있습니다. (현재 false로 설정되어 있습니다.)
