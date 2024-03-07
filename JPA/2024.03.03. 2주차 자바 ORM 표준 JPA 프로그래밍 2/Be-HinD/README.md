<h1> 섹션 6. 다양한 연관관계 매핑 </h1>
# 다중성

- 다대일 : @ManyToOne
- 일대다 : @OneToMany
- 일대일 : @OneToOne
- 다대다 : @ManyToMany

다대다는 실무에서 쓰면 안됨.

# 단방향, 양방향

- 테이블
  - 외래 키 하나로 양쪽 조인 가능
  - 사실 방향이라는 개념이 없음
- 객체
  - **참조용 필드가 있는 쪽으로만 참조 가능**
  - 한쪽만 참조하면 단방향
  - 양쪽이 서로 참조하면 양방향

# 연관관계의 주인

- 외래 키를 관리하는 참조
- 주인의 반대편은 외래 키에 영향을 주지 않음. 단순 조회만 가능

# 다대일 [N:1]

### 다대일 단방향

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/ca82ae0a-095e-428e-bb3e-2dd9d38da065/Untitled.png)

- 가장 많이 사용하는 연관관계
- 다대일의 반대는 일대다

### 다대일 양방향

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/c88be16a-8c2d-40c4-9076-3127f809add1/Untitled.png)

- 외래 키가 있는 쪽이 연관관계의 주인
- 양쪽을 서로 참조하도록 개발

# 일대다 [1:N]

### 일대다 단방향

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/648f78e9-98e3-4633-87eb-3f06ebd28f96/Untitled.png)

- 일대다에서 일(1)이 연관관계의 주인
- 항상 다(N)쪽에 외래 키가 있음
- 반대편 테이블의 외래 키를 관리하는 특이한 구조
- @JoinColumn을 꼭 사용해야함.
  - 그렇지 않으면 조인 테이블 방식을 사용함(중간에 테이블을 하나 추가함)

단점

- 엔티티가 관리하는 외래 키가 다른 테이블에 있음
- 연관관계 관리를 위해 추가로 UPDATE SQL 실행
- 일대다 단방향 매핑보다는 **다대일 양방향 매핑을 사용**하자.

### 일대다 양방향

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/c92cdf83-8136-4763-b2f3-d1969c55c6ef/Untitled.png)

- 이런 매핑은 공식적으로 존재X
- `@JoinColumn(insertable=false, updatable=false)`
- 읽기 전용 필드를 사용해서 양방향 처럼 사용하는 방법
- 다대일 양방향을 사용하자

# 일대일 관계

- 일대일 관계는 반대도 동일
- 주 테이블이나 대상 테이블 중에 외래 키 선택 가능
- 주 테이블에 외래 키
- 대상 테이블에 외래 키
- 외래 키에 데이터베이스 유니크(UNI) 제약조건 추가

### 일대일 : 주 테이블에 외래 키 단방향

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/57b8f180-8f2d-491e-ae6a-342f7241f7b3/Untitled.png)

다대일 단방향 매핑과 유사함.

다대일처럼 외래 키가 있는 곳이 연관관계의 주인(Member)

양방향 관계 생성 시 다대일과 동일하게 반대편은 mappedBy 사용해야함.

### 일대일 : 대상 테이블에 외래 키 단방향

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/1f5b6bbd-e195-4bc7-aef3-27380f5b6f96/Untitled.png)

- 단방향 관계는 JPA 지원 X
- 양방향 관계는 지원

### 일대일 : 대상 테이블에 외래 키 양방향

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/aa0f1efa-d609-40d0-9d01-6dfaa2dd9dcf/Untitled.png)

- 사실 일대일 주 테이블에 외래 키 양방향과 매핑 방법은 같음

## 일대일 정리

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/7eebe9b7-82c8-4186-816f-f51d2d890d61/Untitled.png)

### 주 테이블에 외래 키

- 주 객체가 대상 객체의 참조를 가지는 것 처럼 주 테이블에 외래 키를 두고 대상 테이블을 찾음
- 객체지향 개발자 선호
- JPA 매핑 편리
- 장점 : 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능
- 단점 : 값이 없으면 외래 키에 null 허용

### 대상 테이블에 외래 키

- 대상 테이블에 외래 키가 존재
- 전통적인 데이터베이스 개발자 선호
- 장점 : 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지
- 단점 : 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩됨(프록시는 뒤에서 설명)

# 다대다 [N:M]

결론적으로 실무에서 쓰면 안되는 연관관계

- 관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없음
- 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어내야함

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/6015c561-7d77-4a95-a595-20d7afc7ce7f/Untitled.png)

- **객체는 컬렉션을 사용해서 객체 2개로 다대다 관계 가능**
- `@ManyToMany` 사용

다대다 매핑의 한계

- **편리해 보이지만 실무에서 사용X**
- 연결 테이블이 단순히 연결만 하고 끝나지 않음
- 주문시간, 수량 같은 데이터가 들어올 수 있음

다대다 한계 극복

- 연결 테이블용 엔티티 추가(연결 테이블을 엔티티로 승격)
- `@ManyToMany` → `@OneToMany, @ManyToOne`

# 실전 예제

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/f20dbc92-6956-4ac0-b25b-f2b9733a5e48/Untitled.png)

### N:M 관계는 1:N, N:1로

- 테이블의 N:M 관계는 중간 테이블을 이용해서 1:N, N:1
- 실전에서는 중간 테이블이 단순하지 않다.
- @ManyToMany는 제약 : 필드 추가X, 엔티티 테이블 불일치
- 실전에서는 @ManyToMany 사용 X

<hr>
<h1> 섹션 8. 프록시와 연관관계 정리 </h1>

# 프록시

- `em.find()` vs `em.getReference()`
- `em.find()` : 데이터베이스를 통해서 실제 엔티티 객체 조회
- `em.getReference()` : 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/cf4876be-c38a-4487-8a19-127526487422/Untitled.png)

### 특징

- 실제 클래스를 상속 받아서 만들어짐
- 실제 클래스와 겉 모양이 같다.
- 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨. (이론상으로)
- 프록시 객체는 실제 객체의 참조(target)를 보관
- 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메서드 호출

- 프록시 객체는 처음 사용할 때 한 번만 초기화
- 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님, 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근 가능
- 프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크 시 주의해야함 (==비교 실패, 대신 instance of 사용)
- 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 `em.getReference()` 를 호출해도 실제 엔티티 반환
- 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화 시 문제 발생
  - (하이버네이트는 LazyInitializationException 예외를 터뜨림)

### 프록시 객체의 초기화

```java
Member member = em.getReference(Member.class, "id1");
member.getName();
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/0f14caa9-1eac-4335-9207-2bebaae4ae92/Untitled.png)

### 프록시 확인

- 프록시 인스턴스의 초기화 여부 확인
  - `PersistenceUnitUtil.isLoaded(Object entity)`
- 프록시 클래스 확인 방법
  - `entity.getClass().getName()` 출력(..javasist.. or HibernateProxy…)
- 프록시 강제 초기화
  - `org.hibernate.Hibernate.initialize(entity);`
- 참고 : JPA 표준은 강제 초기화 없음
  - 강제 호출 : `member.getName()`

# 즉시 로딩과 지연 로딩

### 지연 로딩 LAZY을 사용해서 프록시로 조회

```java
@ManyToOne(fetch = FetchType.LAZY)
private Team team;
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/d281da0e-a8b8-4e88-b72e-c156f59643af/Untitled.png)

```java
Team team = member.getTeam();
team.getName(); //실제 team을 사용하는 시점에 초기화(DB 조회)
```

**Member와 Team을 자주 함께 사용한다면 ? =⇒ 즉시 로딩 사용(EAGER)**

- 즉시 로딩 EAGER를 사용해서 함께 조회
  - em.find()할 때 바로 같이 조회
- `@ManyToOne(fetch = FetchType.EAGER)`

### 프록시와 즉시로딩 주의

- **가급적 지연 로딩만 사용(특히 실무에서)**
- 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생
- 즉시 로딩은 JPQL에서 N+1문제를 일으킨다.
- **@ManyToOne, @OneToOne은 기본이 즉시 로딩 → LAZY로 설정**
- @OneToMany, @ManyToMany는 기본이 지연 로딩

## 지연 로딩 활용

- Member와 Team은 자주 함께 사용 → 즉시 로딩
- Member와 Order는 가끔 사용 → 지연 로딩
- Order와 Product는 자주 함께 사용 → 즉시 로딩

## 지연 로딩 활용 - 실무

- 모든 연관관계에 지연 로딩을 사용해라!
- 실무에서 즉시 로딩을 사용하지 마라!
- JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라!
- 즉시 로딩은 상상하지 못한 쿼리가 나간다.

# 영속성 전이 : CASCADE

- 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을 때
- 예 : 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장.

심플하게 부모를 persist할 때 자식도 같이 persist

### 주의

- 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음
- 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐

### CASCADE 종류

- ALL : 모두 적용
- PERSIST : 영속
- REMOVE : 삭제

# 고아 객체

- 고아 객체 제거 : 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
- orphanRemoval = true

```java
Parent parent1 = em.find(Parent.class, id);
parent1.getChildren().remove(0);
//자식 엔티티를 컬렉션에서 제거
```

- `DELETE FROM CHILD WHRER ID = ?`

### 주의

- 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
- **참조하는 곳이 하나일 때 사용해야함!**
- 특정 엔티티가 개인 소유할 때 사용
- @OneToOne, @OneToMany만 가능
- 참고 : 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아 객체 제거 기능을 활성화 하면, 부모를 제거할 때 자식도 함께 제거된다. 이것은 CascadeType.REMOVE(ALL)처럼 동작한다.

# 영속성 전이 + 고아 객체, 생명주기

- `CascadeType.ALL + orphanRemoval=true`
- 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거
- 두 옵션을 모두 활성화하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있음.
- 도메인 주도 설계의 Aggregate Root개념을 구현할 때 유용

# 글로벌 페치 전략 설정

- 모든 연관관계를 지연 로딩으로
- @ManyToOne, @OneToOne은 기본이 즉시 로딩이므로 지연로딩으로 변경

# 영속성 전이 설정

- Order → Delivery를 영속성 전이 ALL 설정
- Order → OrderItem을 영속성 전이 ALL 설정

<hr>

<h1> 섹션 10. 객체지향 쿼리 언어1 - 기본 문법 </h1>

# 소개

## JPQL

- 가장 단순한 조회 방법
  - `EntityManager.find()`
  - 객체 그래프 탐색`(a.getB().getC())`
- 나이가 18살 이상인 회원을 모두 검색하고 싶다면?
- JPA를 사용하면 엔티티 객체를 중심으로 개발
- 문제는 검색 쿼리
- 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
- 모든 DB데이터를 객체로 변환해서 검색하는 것은 불가능
- 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요
- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
- SQL과 문법 유사
- JPQL은 엔티티 객체를 대상으로 쿼리
- SQL은 데이터베이스 테이블을 대상으로 쿼리
- String기반이라 동적 쿼리 작성이 힘듦!

## Criteria

- 문자가 아닌 자바 코드로 JPQL을 작성할 수 있음
- JPQL 빌더 역할
- JPA 공식 기능
- **단점 : 너무 복잡하고 실용성이 없다.**
- Criteria 대신에 QueryDSL 사용 권장

## QueryDSL

- 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
- JPQL 빌더 역할
- 컴파일 시점에 문법 오류를 찾을 수 있음
- 동적쿼리 작성 편리함
- 단순하고 쉬움
- 실무 사용 권장
- 공식 문서에 설명이 잘 되어 있음

## 네이티브 SQL

- JPA가 제공하는 SQL을 직접 사용하는 기능
- JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능
- 예) 오라클 CONNECT BY, 특정 DB만 사용하는 SQL 힌트

```java
String sql = "SELECT ID, NAME FROM MEMBER WHERE NAME = 'kim'";
List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();
```

## JDBC 직접 사용, SpringJdbcTemplate 등

- JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링JdbcTemplate, 마이바티스등을 함께 사용 가능
- 단, 영속성 컨텍스트를 적절한 시점에 강제로 플러시 필요
- 예) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시

# JPQL(Java Persistence Query Language)

- JPQL은 객체지향 쿼리 언어다. 따라서 테이블을 대상으로 쿼리하는 것이 아니라 **엔티티 객체를 대상으로 쿼리**한다.
- JPQL은 SQL을 추상화해서 특정데이터베이스 SQL에 의존하지 않는다.
- JPQL은 결국 SQL로 변환된다.

![aa.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/caaa8240-7331-485d-9583-9e36aeab227b/aa.png)

## JPQL 기본 문법

```
select_문 ::=
		select_절
		from_절
		[where_절]
		[groupby_절]
		[having_절]
		[orderbt_절]

update_문 ::= update_절 [where_절]
delete_문 ::= delete_절 [where_절]
```

- select m from **Member** as m where **m.age** > 18
  - `엔티티`와 `속성`은 대소문자 구분O (Member, age)
  - `JPQL 키워드`는 대소문자 구분X (select, from, where)
  - **엔티티 이름** 사용, 테이블 이름이 아님 (Member)
    - 예) `@Entity(name = “Member”)`의 name 값
  - **별칭은 필수**(m)
    - as는 생략 가능

# 집합과 정렬

```sql
select
 COUNT(m), //회원 수
 SUM(m.age), //나이 합
 AVG(m.age), //평균 나이
 MAX(m.age), //최대 나이
 MIN(m.age) //최소 나이
from Member m
```

- GROUP BY, HAVING
- ORDER BY

## TypeQuery, Query

- **TypeQuery**: 반환 타입이 명확할 때 사용

```sql
	TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);
	TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
```

- **Query**: 반환 타입이 명확하지 않을 때 사용(여러 개)

```sql
	Query query = em.createQuery("SELECT m.username, m.age from Member m");
```

## 결과 조회 API

- query.getResultList(): **결과가 하나 이상일 때**, 리스트 반환
  - 결과가 없으면 빈 리스트 반환

```java
    TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);

    List<Member> resultList = query.getResultList(); // 컬렉션 반환
    for (Member member1 : resultList) {
    		System.out.println("member1 = " + member1);
    }
```

- query.getSingleResult(): **결과가 정확히 하나**, 단일 객체 반환
  - 결과가 없으면: javax.persistence.NoResultException
  - 둘 이상이면: javax.persistence.NonUniqueResultException
  - Spring Data JPA에서 제공하는 함수에서는 결과가 없으면 NULL이나 Optional 반환한다.

```java
    TypedQuery<Member> singleQuery = em.createQuery("select m from Member m where m.id = 1", Member.class);
    Member singleResult = singleQuery.getSingleResult(); // 값이 정확히 1개여야 한다.
    System.out.println("singleResult = " + singleResult);
```

## 파라미터 바인딩

### 이름 기준

```sql
SELECT m FROM Member m where m.username = :username // 💡 :이름
query.setParameter("username", usernameParam); // usernameParam = "member1"

// => chainging
Member singleResult = em.createQuery("select m from Member m where m.username = :username", Member.class)
				.setParameter("username", "member1")
				.getSingleResult();
```

### 위치 기준

```sql
SELECT m FROM Member m where m.username = ?1 // 💡 ?위치
query.setParameter(1, usernameParam); // usernameParam = "member1"
```

- **위치 기반은 중간에 순서가 달라질 수 있기 때문에 쓰지 않는 것을 권장한다.**

## 프로젝션

- SELECT 절에 **조회할 대상을 지정**하는 것
- 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자등 기본 데이터 타입)
  - 세가지를 다 선택할 수 있다.
  - RDBMS에서는 스칼라 타입만 선택할 수 있다.
- SELECT **m** FROM Member m : 엔티티 프로젝션

```java
    List<Member> resultList = em.createQuery("select m from Member m", Member.class)
    				.getResultList();
    Member findMember = resultList.get(0);
    findMember.setAge(20); // 💡 값이 바뀜 => resultList가 영속성 컨텍스트에서 관리가 됨
```

- SELECT **[m.team](http://m.team)** FROM Member m : 엔티티 프로젝션
  - **웬만하면 SQL과 비슷하게 작성해야 한다.**
- JOIN은 성능에 영향을 줄 수 있는 요소가 많기 때문에, 한 눈에 보이도록 명시적으로 작성해야 한다.

```java
    List<Team> resultList = em.createQuery("select t from Member m join m.team t", Team.class)
				    .getResultList();
```

- SELECT **o.address** FROM Order o : 임베디드 타입 프로젝션
  - 소속되어 있기 때문에, 소속된 엔티티(Order)로부터 시작해야 한다.
- SELECT **m.username, m.age** FROM Member m : 스칼라 타입 프로젝션
- **DISTINCT**로 중복 제거가 가능하다

### 여러 값을 조회할 경우

- SELECT **m.username, m.age** FROM Member m

**1. Query 타입으로 조회**

```java
    List resultList = em.createQuery("select m.username, m.age from Member m")
    		.getResultList();
    Object o = resultList.get(0);
    Object[] result = (Object[]) o; // 타입 캐스팅
    System.out.println("username = " + result[0]);
    System.out.println("age = " + result[1]);
```

**2. Object[] 타입으로 조회**

```java
    List<Object[]> resultList = em.createQuery("select m.username, m.age from Member m")
    		.getResultList();
    Object o = resultList.get(0);
    System.out.println("username = " + result[0]);
    System.out.println("age = " + result[1]);
```

**3. new 명령어로 조회**

- 단순 값을 DTO로 바로 조회: SELECT **new** jpabook.jpql.UserDTO(m.username, m.age) FROM Member m
- 패키지 명을 포함한 전체 클래스 명 입력
- 순서와 타입이 일치하는 생성자 필요
- QueryDSL을 사용해 해결 가능

```java
    public class MemberDTO {
        private String username;
        private int age;

        public MemberDTO(String username, int age) {
            this.username = username;
            this.age = age;
        }
    }
```

```java
    List<MemberDTO> resultList5 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                        .getResultList();
    MemberDTO memberDTO = resultList5.get(0);
    System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
    System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
```

# 페이징 API

- JPA는 페이징을 다음 두 API로 추상화한다
- **setFirstResult**(int startPosition) : 조회 시작 위치 (0부터 시작)
  - startPosition이 0일 경우, 쿼리에 offset이 뜨지 않음
    ![1.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/2f77b5d5-c39a-44e5-a34a-ef8a6841cf65/1.png)
  - 0이 아닐 경우
    ![2.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/f4edbfae-1f30-45a7-b7cc-5506fb845310/2.png)
- **setMaxResults**(int maxResult) : 조회할 데이터 수

```java
	String jpql = "select m from Member m order by m.age desc"
	List<Member> resultList = em.createQuery(jpql, Member.class)
		.setFirstResult(1)
		.setMaxResults(10)
		.getResultList();
	System.out.println("resultList.size() = " + resultList.size());
	for (Member member1 : resultList) {
		System.out.println("member1 = " + member1);
	}
```

> Member.class에 toString() 추가 (단축키: Alt+Insert)
>
> ```java
> @Override
> public String toString() {
> 	return "Member{" +
> 		"id=" + id +
> 		", username='" + username + '\'' +
> 		", age=" + age +
> 		//", team=" + team + // 양방향에서 무한루프 주의! 삭제하는 것이 좋다.
> 		'}';
> }
> ```

### MySQL 방언

```sql
SELECT
		M.ID AS ID,
		M.AGE AS AGE,
		M.TEAM_ID AS TEAM_ID,
		M.NAME AS NAME
FROM
		MEMBER M
ORDER BY
		M.NAME DESC LIMIT ?, ?
```

### Oracle 방언

```sql
SELECT * FROM
		( SELECT ROW_.*, ROWNUM ROWNUM_
		FROM
				( SELECT
						M.ID AS ID,
						M.AGE AS AGE,
						M.TEAM_ID AS TEAM_ID,
						M.NAME AS NAME
						FROM MEMBER M
						ORDER BY M.NAME
				) ROW_
		WHERE ROWNUM <= ?
		)
WHERE ROWNUM_ > ?
```

- ‘SELECT - FROM’이 3번 들어감

# 조인

- 내부 조인
  - member가 있고 team이 없으면 데이터가 안 나옴

```sql
    SELECT m FROM Member m [INNER] JOIN m.team t
```

- 외부 조인
  - member가 있고 team이 없으면 member만 찾음

```sql
    SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
```

- 세타 조인
  - 연관관계가 없는 member와 team을 부르고 교집합을 찾음
  - cross join 쿼리가 나감

```sql
    SELECT count(m) FROM Member m, Team t WHERE m.username = t.name
```

## ON 절

- ON: JOIN할 때 **조건**
- ON절을 활용한 조인(JPA 2.1부터 지원)
  1. 조인 대상 필터링
  2. **연관관계 없는 엔티티 외부 조인**(하이버네이트 5.1부터)

### 1. 조인 대상 필터링

- 예) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
- **JPQL**:

```sql
    SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'A'
```

- **SQL**:

```sql
	SELECT m.*, t.* FROM
	Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='A'
							// PK = FK
```

![1.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/bd50b0fd-675b-41a4-9c64-cccbec0fb4a4/143c4f25-24cd-473b-be9d-cc2830dee365/1.png)

- Hibernate에서 id 조건이 자동으로 추가된걸 확인할 수 있음

### 2. 연관관계 없는 엔티티 외부 조인

- 내부 조인도 가능
- 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
- **JPQL**:

```sql
    SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
```

- **SQL**:

```sql
    SELECT m.**, t.** FROM Member m LEFT JOIN Team t ON m.username = t.name
```

# 서브 쿼리

- 나이가 평균보다 많은 회원

```sql
	select m from Member m
	where m.age > (select avg(m2.age) from Member m2) // 괄호 속 SELECT 절
```

- 한 건이라도 주문한 고객

```sql
	select m from Member m
	where (select count(o) from Order o where m = o.member) > 0 // 괄호 속 SELECT 절
```

## 서브 쿼리 지원 함수

- [NOT] EXISTS (subquery): 서브쿼리에 결과가 존재하면 참
  - {ALL | ANY | SOME} (subquery)
  - ALL 모두 만족하면 참
  - ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
- [NOT] IN (subquery): 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

### 예제

- 팀A 소속인 회원

```sql
	select m from Member m
	where exists (select t from m.team t where t.name = ‘팀A') // 💡 exists
```

- 전체 상품 각각의 재고보다 주문량이 많은 주문들

```sql
	select o from Order o
	where o.orderAmount > ALL (select p.stockAmount from Product p) // 💡 ALL
```

- 어떤 팀이든 팀에 소속된 회원

```sql
	select m from Member m
	where m.team = ANY (select t from Team t) // 💡 ANY
```

## JPA 서브 쿼리 한계

- JPA는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능
- SELECT 절도 가능(하이버네이트에서 지원)

```sql
	select (select avg(m1.age) from Member m1)
	as avgAge from Member m left join Team t on m.username = t.name
```

- **FROM 절의 서브 쿼리는 현재 JPQL에서 불가능**
  - **조인으로 풀 수 있으면 풀어서 해결**
  - 안되면 쿼리를 두 번 날리거나, 네이티브SQL을 사용하거나, 어플리케이션으로 가져와서 해결

```sql
	select mm.age, mm.username
	from (select m.age, m.username from Member m) as mm
```

# JPQL 타입 표현

- 문자: ‘HELLO’, ‘She’’s’(싱글 표현 2개)
- 숫자: 10L(Long), 10D(Double), 10F(Float)
- Boolean: TRUE, FALSE
- ENUM: jpabook.MemberType.Admin (패키지명 포함)
  - QueryDSL 쓰면 자바 코드로 쓰기 때문에,이런 패키지 자체를 import해서 쓸 수 있기 때문에 복잡해지지 않음

```java
	String query13 = "select m.username, 'HELLO', true From Member m " +
		"where m.type = :userType";
	List<Object[]> resultList8 = em.createQuery(query13)
		.setParameter("userType", MemberType.ADMIN)
		.getResultList();
```

- 엔티티 타입: TYPE(m) = Member (상속 관계에서 사용)

```java
	select i from Item i where type(i) = Book
```

- 상속 관계일 때 `@DiscriminatorValue(name=”DTYPE”)`

# JPQL 기타

- SQL과 문법이 같은 식
- EXISTS, IN
- AND, OR, NOT
- =, >, >=, <, <=, <>
- BETWEEN, LIKE, **IS NULL**

# 조건식 - CASE 식

- 기본 CASE식

```sql
    select
    	case when m.age <= 10 then '학생요금'
    		when m.age >= 60 then '경로요금'
    		else '일반요금'
    	end
    from Member m
```

- 단순 CASE식

```sql
    select
    	case t.name
    		when '팀A' then '인센티브110%'
    		when '팀B' then '인센티브120%'
    		else '인센티브105%'
    	end
    from Team t
```

- COALESCE: 하나씩 조회해서 null이 아니면 반환
  - 예) 사용자 이름이 없으면 이름 없는 회원을 반환

```sql
	select coalesce(m.username,'이름 없는 회원') from Member m
```

- NULLIF: 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
  - 예) 사용자 이름이 ‘관리자’면 null을 반환하고 나머지는 본인의 이름을 반환

```sql
	select NULLIF(m.username, '관리자') from Member m
```

# JPQL 기본 함수

- CONCAT

```sql
    select 'a' || 'b' From Member m
```

- SUBSTRING

```sql
    select substring(m.username, 2,3) From Member m
```

- TRIM
- LOWER, UPPER
- LENGTH
- LOCATE

```sql
    select locate('de', 'abcdegf') From Member m
```

- ABS, SQRT, MOD
- SIZE, INDEX(JPA 용도)
  - INDEX는 `@OrderColumn`과 함께 쓰는데, List 값 타입 컬렉션에서 옵션을 주어 위치값을 구할 때 쓸 수 있음. 쓰는 것을 추천하지 않음.

```sql
    select size(t.members) From Team t
```

# 사용자 정의 함수 호출

- 하이버네이트는 사용전 방언에 추가해야 한다.사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록한다

### MyH2Dialect.java

```java
public class MyH2Dialect extends H2Dialect {
    public MyH2Dialect() { // 실제 소스 코드를 열어보면 자세히 나와 있음
        registerFunction("group_concat",
                new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }
}
```

### persistence.xml

```xml
// persistence.xml
<!--<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>-->
<property name="hibernate.dialect" value="dialect.MyH2Dialect"/>
```

### SQL

```sql
select function('group_concat', m.username) from Member m
```
