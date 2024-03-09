# 객체지향 쿼리 언어 소개

### JPA로 사용할 수 있는 쿼리방법

- JPQL : 객체 관점에서 SQL 문을 작성한다. “쿼리문이 문자열”이다.
- JPA Criteria : 프로그래밍 코드로 쿼리를 작성 할 수 있다.
- QueryDSL(권장) : 프로그래밍 코드로 쿼리를 작성 할 수 있다.
- 네이티브 SQL : 생 쿼리문
- JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용

### 각각의 쿼리방식으로 동일한 쿼리문을 작성해보자

- SQL

```sql
SELECT * FROM users WHERE name = 'John Doe'
```

- JPQL

```jsx
"SELECT u FROM User u WHERE u.name = :name"  // :name은 인자로 받은 'John Doe'를 매핑
```

- JPA Criteria

```java
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<User> query = cb.createQuery(User.class);
Root<User> u = query.from(User.class);
query.select(u).where(cb.equal(u.get("name"), "John Doe"));
```

- QueryDSL

```java
JPAQuery<User> query = new JPAQuery<>(entityManager);
QUser qUser = QUser.user;
List<User> users = query.select(qUser).from(qUser).where(qUser.name.eq("John Doe")).fetch();
```

# JPQL

### 왜 나타났나?

- 기존의 문제점(DB → 모든 USERES → 모 Users —> JPA —> 필요한 Users)
    - JPA의 검색 대상은 DB가 아닌 “엔티티”라는 점에서 한계점있다.
    - DB에서 특정 조건을 나이가 18세 이상인 모든 유저의 이름을 알고싶음
    - 그러면 일단 나이에 상광없이 DB에 저장된 모든 유저와 필요한 필드를 가져와서 엔티티를 만들어야됨(그래야 JPA는 거기서 찾으니까)
    - 그리고 JAP가 가져온 엔티티 중에서 조건에 만족하는 엔티티들을 반환함
    - **따라서 DB에서 모든 정보를 다 끌고와서 필요없는 엔티티까지 생성해야되는 문제점이 있음**
- JPQL(DB → JPQL → 필요한 USERS —> 필요한 Users)
    - JPQL로 DB에서 딱 필요한 데이터만 가져와서 엔티티를 만들 수 있도록 해줌
    - 그냥 JPQL로 인해서 쿼리 칠 수 있게 되면소 원하는 데이터만 바로 DB에서 가져와서 엔티티로 만들 수 있게됨

<aside>
💡 기존에는 쿼리를 칠 방법이 없었지만 JPQL이 생기고 “쿼리”를 칠 수 있게 됐다.

</aside>

### 특징

- 객체 지향 SQL
- SQL 문법과 유사하게 SELECT, FROM, WHERE, GROUP BY, HAVING, ORDER BY, JOIN 지원

### 장점

- 엔티티를 대상으로 쿼리 친게 DB에 쿼리  친걸로 자동 변환 해준다.
- 딱 검색 조건에 필요한 엔티티만 생성 할 수 있다.

### 단점

- 쿼리문이 문자열로 작성되기 때문에 잘못 적어도 컴파일에러가 뜨지 않는다.(러타임때 오류 발생)

# QueryDSL

- 기존 JPQL의 문제점
    - JPQL은 쿼리를 문자열로 치기 때문에 에초에 쿼리문 자체가 자바 코드가 아님
    - 자바 코드가 아니기 때문에 컴파일 에러, 동적 쿼리..자동완성…과 같은 기능을 사용 할 수 없음
- QueryDSL
    - 쿼리문까지 자바로 작성 할 수 있게됨
    - 그렇게 되면서 컴파일 에러, 자동 완성, 동적 쿼리와 같은 기능을 사용 할 수 있게

# 네이티브 SQL

- JPQL로 해결 할 수 없는 특정 데이터베이스에 의존적 기능을 사용할때 필요함
    - 오라클 CONNECT BY…
- JPQL 자동 완성 쿼리의 성능이 마음에 안들 때 내가 만들어서 사용.

# JDBC 직접 사용, SpringJdbcTemplate 등(SQL 매퍼)

- 영속성 관리가 안되기 때문에 사용시 적절한 시점에서 플러시해야됨.(주로 SQL 매퍼에 의해 SQL 실행 되기 전 )

# JPQL 사용법

### 나이가 18세 이상이고 거주지가 서울인 유저 조회

```java
EntityManager em; // 이미 초기화되어 있다고 가정

// 1. jpql 작성
String jpql = "SELECT u FROM User u WHERE u.age >= 18 AND u.address.city = '서울'";
// 2. 쿼리 생성
TypedQuery<User> query = em.createQuery(jpql, User.class);
// 3. 퀴리 가공
List<User> users = query.getResultList();

// 결과 사용
for (User user : users) {
    System.out.println(user.getName()); // 예시로 유저 이름 출력
}
```

1. JPQL 작성
2. 쿼리 생성
    1. 파라미터 바인딩이 있는가?
        1. 네임드 파라미터, 포지셔널 파라미터
    2. 반환 엔티티 타입이 정해져있는가?
    3. 반환 타입이 엔티티가 아닌가?
3. 쿼리 가공
    1. 반환 결과가 단수인가 복수인가?

### JPQL 작성(위서 다양한 사례에 대한 연습)

```java
String jpql = "SELECT u FROM User u WHERE u.age >= :age AND u.address.city = :city";
```

### 쿼리 생성

- 파라미터 바인딩이 있고 반환 타입이 정해진 경우(JPQL문에서 :age, :city 로 설정 되어있어야됨)

```java
TypedQuery<User> query = em.createQuery(jpql, User.class)
                            .setParameter("age", 18)
                            .setParameter("city", "서울");
```

- 반환 타입이 정해지지 않은 경우

```java
Query query = em.createQuery(jpql)
								.setParameter("age", 18)
								.setParameter("city", "서울")
```

- 파라미터 바인딩인 경우

```java
String jpql = "SELECT u FROM User u WHERE u.age >= ?1 AND u.address.city = ?2";
TypedQuery<User> query = em.createQuery(jpql, User.class)
                            .setParameter(1, 18)
                            .setParameter(2, "서울");
```

- 반환 타입이 엔티티가 아닌 DTO인경우(UserDTO는 사전에 정의 되어야 멤버 이름만 존재)

```java
String jpqlProjection = "SELECT new com.package.UserDTO(u.name) FROM User u WHERE u.age >= :age AND u.address.city = :city";
TypedQuery<UserDTO> queryProjection = em.createQuery(jpqlProjection, UserDTO.class)
                                         .setParameter("age", 18)
                                         .setParameter("city", "서울");
List<UserDTO> userDTOs = queryProjection.getResultList();
```

- `SELECT new com.package.UserDTO(u.name)` 부분을 통해 UserDTO 객체를 생성해 전달한다.
    - 특이한 점은 From 절 부터 필터링된 엔티티가 SELECT로 넘겨주고 최종적으로 조회할 대상을 결정하는 return 처럼 활용됨(이를 프로젝션이라고함)
    - 프로젝션 대상 : 엔티티, 임베디드 타입, 스칼라 타입
    - SELECT에 DISTINCT 가능

```java
package com.package;

// UserDTO.java
public class UserDTO {
    private String name;
		
		public UserDTO(String name){
				this.name = name;
		}
}
```

### 쿼리 가공

- query가 단수인 경우`query.getSingleResult();`

```java
User user = query.getSingleResult();
```

- query가 복수인 경우 `query.getResultList();`

```java
List<User> users = query.getResultList();
```

# JPQL 어디까지 가능한가?

### 페이징 API(방언에 따라 알아서 처리해줌)

- setFirstResult(int startPosition) : 조회한 엔티티에서 반환할 첫 엔티티의 인덱스 번호
- setMaxResults(int maxResult) : 가져올 엔티티의 수
- 멤버를 이름 내림차순으로 정렬한거 가져와서 11번 째 엔티티부터 20개 뽑아서 반환하는 경우

```java
//페이징 쿼리
 String jpql = "select m from Member m order by m.name desc";
 List<Member> resultList = em.createQuery(jpql, Member.class)
														 .setFirstResult(10)
														 .setMaxResults(20)
														 .getResultList()
```

### 조인

- 내부 조인(Join을 기준으로 후자의 객체가 null이면 조회 x) —> 한 엔티티 내에서 선언 가능

```java
SELECT m FROM Member m [INNER] JOIN m.team t
```

- 외부 조인(Join을 기준으로 후자의 객체가 null이어도 null로 조회) —> 한 엔티티 내에서 선언 가능

```java
SELECCT m FROM Member m LEFT [OUTTER] JOIN m.team t
```

- 세타 조인 —> 연관관계 설정이 되어있지 않은 두 개의 테이블을 크로스 조인 —> 한 엔티티 내에서 해결 불가(FROM 절에 두개의 엔티티 들어가는 순간 카르테시안 곱 발생)

```java
SELECT m FROM Member count(m), Team t WHERE m.username = t.name
```

### 조인 on절

> 조인 되는 대상의 엔티티를 on 절로 한번 조인 되는 대상을 필터링 해서 조인 할 수 있다.
> 
- Member와 Team을 외부 조인하는데 이때 Team의 이름이 “A”인 팀과 외부조인함(on 절로 인해 외부 조인 대상인 Team의 범위가 이름이 A인 팀이라는 조건으로 필터링 됨)

```java
SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'A'
```

### 연관 관계가 없는 외부 조인

```java
SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
```

### 서브 쿼리(WHERE과 HAVING ,FROM(하이버네이트 6부터 지원) 다 사용 가능)

```java
select m from Member m
where m.age > (select avg(m2.age) from Member m2)
```

- 서브 쿼리 칠때 동일한 엘리어스 사용 하면 안됨

### 서브쿼리 지원함수

- [NOT] EXIST : 서브쿼리의 결과가 하나라도 있으면 TRUE 반환

```java
SELECT m 
FROM Member m 
WHERE EXISTS (SELECT o FROM Order o WHERE o.member = m AND o.status = 'DELIVERED')
```

- ALL : 조회 하려는 엔티티의 조건이 서브 쿼리로 반환된 모든 엔티티를 대상으로 충족 했을 때 해해당 엔티티를 반환함

```java
SELECT m 
FROM Member m 
WHERE m.age > ALL (SELECT m2.age FROM Member m2 WHERE m2.status = 'INACTIVE')
```

- SOME, ANY

```java
SELECT m 
FROM Member m 
WHERE m.age < ANY (SELECT m2.age FROM Member m2 WHERE m2.status = 'ACTIVE')
```

### 조건식 - CASE

```java
select
 case when m.age <= 10 then '학생요금'
 when m.age >= 60 then '경로요금'
 else '일반요금'
 end
from Member m
```

### null 처리 함수

- COALESCE: 조회 값이 null인 경우 디폴트 반환 값을 설정 할 수 있다.

```java
select coalesce(m.username,'이름 없는 회원') from Member m
```

- NULLIF : 매개변수가 같다면 null을 반환 다르면 원래 값 반환

```java
select NULLIF(m.username, '관리자') from Member m
```

# JPQL 타입

- 문자 : `''` 에  감싼다. `""` 아님
- 숫자 : Long, Double, Float
- Boolean
- ENUM
- 엔티티 타입

# JPQL 기본  WHERE 연산자

- AND, OR, NOT
- =, >, <, ≤, <>
- BETWEEN, LIE, IS NULL

# JPQL 기본 함수

- AVG, MAX, MIN, SUM 당연히 있음
- CONCAT : 문자열 결합

```java
SELECT CONCAT(m.name, ' from ', m.country) FROM Member m
```

- SUBSTRING : 문자열에서 시작 문자와 그 문자에서 반환할 문자의 개수만큼 문자열을 반환

```java
SELECT SUBSTRING(m.name, 1, 3) FROM Member m
// m.name = "abcde"라면 1부터 3개 즉 "abc"가 반환됨
```

- TRIM : 공백 제거

```java
SELECT TRIM(m.name) FROM Member m
```

- LOWER, UPPER : 소문자, 대문자로 변환

```java
SELECT UPPER(m.name), LOWER(m.name) FROM Member m
```

- LENGTH : 길이 반환

```java
SELECT m.name, LENGTH(m.name) FROM Member m
```

- LOCATE : 첫 번째 문자열이 두 번째 문자열 내에서 처음 나타나는 위치를 반환한다. 1 부터 시작하며 없을 경우 0을 반환한다.

```java
SELECT LOCATE('de', m.name) FROM Member m
```

- MOD : 첫 번째 인자를 두 번째 인자로 나눈 나머지를 반환

```java
SELECT MOD(m.age, 10) FROM Member m
```

- SIZE : 컬랙션 타입의 요소수를 반환함

```java
SELECT SIZE(m.orders) FROM Member m
```

- 사용자 정의 함수 만들기 가능