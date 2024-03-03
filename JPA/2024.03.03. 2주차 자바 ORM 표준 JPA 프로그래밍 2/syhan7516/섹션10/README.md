## 객체지향 쿼리 언어

- JPA는 다양한 쿼리 방법을 지원
    - JPQL
        - 표준 문법
    - JPA Criteria
        - JPQL 기반으로 자바 코드를 JPQL을 빌드해주는 제너레이터 클래스 모음
    - Query DSL
        - JPQL 기반으로 자바 코드를 JPQL을 빌드해주는 제너레이터 클래스 모음
    - 네이티브 SQL
        - DB의 종속적 쿼리를 위한 SQL (생쿼리)

## JPQL

- JPQL 필요성
    - JPA는 엔티티 객체를 중심으로 개발 수행
    - 검색 수행 또한 테이블이 아닌 엔티티 객체를 대상으로 검색
    - 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
    - → **검색 조건이 포함된 SQL 필요 !**
    - → **예) select m from Member m where m.age > 18;**
- JPQL 특징
    - JPA가 제공하는 SQL을 추상화한 객체 지향 쿼리 언어
    - SQL 문법과 유사하며, 엔티티 객체를 대상으로 쿼리
    - 특정 데이터베이스 SQL에 의존하지 않고, 알아서 방언으로 해석 가능
- JPQ 단점
    - 문자열이라 동적 쿼리를 만들기 어려움 (띄워쓰기, 오타 등등)
- JPQL 사용법
    - 기본 문법
        - 엔티티와 속성은 대소문자 구분 O
        - 키워드는 대소문자 구분 X
        - 엔티티 이름 사용
        - 별칭은 필수
    - 반환 타입
        - TypeQuery
            - 반환 타입이 명확할 때 사용
            - 예) TypedQuery<Member> query
            - = em.createQuery(”SELECT m FROM Member m”, Member.class);
        - Query
            - 반환 타입이 명확하지 않을 때 사용
            - 예) Query query
            - = em.createQuery(”SELECT m.username, m.age from Member m”);
    - 반환 개수
        - query.getResultList()
            - 결과가 하나 이상일 경우 리스트 반환
            - 결과가 없으면 빈 리스트 반환
        - query.getSingleResult()
            - 결과가 정확히 하나, 단일 객체 반환
            - 결과 無 : NoResultException
            - 결과 多 : NonUniqueResultException
    - 파라미터 바인딩
        - 이름 기준
            - 예) SELECT m FROM Member m where m.username=:username
            - query.setParameter(”username”,usernameParam);
        - 위치 기준
            - 예) SELECT m FROM Member m where m.username=?1
            - query.setParameter(1,usernameParam);
        - → **순서가 잘못되면 밀려서 문제가 발생하여 이름 기준을 권장**
    - 프로젝션
        - SELECT 절에 조회할 대상을 지정하며, 대상은 **엔티티, 임베디드 타입, 스칼라 타입**
        - 다중 엔티티 반환 시에도 모두 영속성 컨텍스트에 저장 관리하며, DISTINCT로 중복 제거
        - 프로젝션 대상에 따른 종류
            - 엔티티
                - 예) SELECT m FROM Member m
                - 예) SELECT [m.team](http://m.team) FROM Member m
            - 임베디드 타입
                - 예) SELECT m.address FROM Member m
            - 스칼라 타입
                - 예) SELECT m.username, m.age FROM Member m
        - 프로젝션의 여러 값 조회
            - Query 타입으로 조회
            - Object[] 타입으로 조회
            - new 명령어로 조회
    - 페이징
        - 조회 시작 위치
            - setFirstResult(시작 위치)
        - 조회 데이터 수
            - setMAX(Results(데이터 수)
        
        ```java
        // 11번째부터 20개씩 가져오기
        // 방언은 자동으로 알아서 처리
        
        .setFirstResult(10)
        .setMaxResults(20)
        .getResultList();
        ```
        
    - 조인
        - 내부 조인
        - 외부 조인
        - 세타 조인
        - ON절
            - 조인 대상 필터링
            - 연관관계 없는 엔티티 외부 조인
    - 서브 쿼리
        - 지원 함수
        - 한계
    - 기타
        - 타입 표현
            - 문자, 숫자(L, D, F), Boolean, ENUM(패키지명 포함), 엔티티 타입
        - 추가 문법
            - EXISTS, IN, AND, OR, NOT, =, >, ≥, <, ≤, <>, BETWEEN, LIKE, IS NULL
        - 조건식
            - case
                - case when 조건 then 설정 값….. else end 설정 값
            - coalesce
                - 없으면 설정 값 반환
            - nullif
                - 조건이면 null 반환하고 나머지는 해당 값
        - 기본 함수
            - CONCAT, SUBSTRING, TRIM, LOWER, UPPER, LENGTH, LOCATE ….
        - 사용자 정의 함수 호출
            - 함수를 정의하여 함수 호출 가능
            - → 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록
            - → 등록 후 설정에서 해당 DB 클래스 파일로 변경87\]

## JPA Criteria

- Criteria 필요성
    - JPQL 단점인 동적 쿼리를 만들기 위한 대안 중 하나
- Criteria 특징
    - 문자가 아닌 자바 코드로 JPQL 작성
    - JPQL 빌더 역할, JPA 공식 기능
- Criteria 단점
    - 너무 복잡하고, 실용성이 없으며, SQL스럽지 않아 유지 보수 저하

## Query DSL

- Query DSL 필요성
    - Criteria 단점인 너무 복잡하고, 실용성이 없으며, SQL스럽지 않은 점 보완
    - → **예) query.selectFrom(m).where.(m.age.gt(18)).orderBy(m.name.desc()).fetch()**
- Query DSL 특징
    - Criteria 특징을 그대로 가져옴
    - 컴파일 시점에 문법 오류 찾기 가능
    - 단순, 작성 편리, SQL스러움

## 네이티브 SQL

- 네이티브 SQL 필요성
    - JPQL 해결할 수 없는 특정 데이터베이스에 의존적인 기능
    - → **예) select ID, AGE, TEAM_ID, NAME from MEMBER where NAME = ‘kim’;**
- 네이티브 SQL 특징
    - SQL 직접 사용