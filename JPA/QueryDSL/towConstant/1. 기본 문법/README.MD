# Test 할 때 유의사항

- @SpringBootTest는 해당 프로젝트의 설정된 모든 빈이 만들어지고 트리거의 경우 실행됨
    - 그래서 @PostContructor나 Test에 @BeforEach 다 실행됨 —> 이것때문에 DB가 중복으로 생겨서 자꾸 문제가 생겼음
- @Transcational은 해당 테스트에서 생성된 애들은 테스트가 끝나면 다시 롤백한다는 설정

```java
@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void startJPQL() {
        // member1을 찾아라
        Member findByJPQL = em.createQuery("select m from Member m WHERE m.userName = :username", Member.class)
                .setParameter("username", "user1").getSingleResult();
        Assertions.assertThat(findByJPQL.getUserName()).isEqualTo("user1");
    }
}
```

- AssertIons는 `import org.assertj.core.api.Assertions;` 를 사용한다.

# 간단한 queryDSL 사용하기

```java
    @Test
    public void startQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        QMember m = new QMember("m");

        Member findMember = jpaQueryFactory
                .select(m)
                .from(m)
                .where(m.userName.eq("user1"))
                .fetchOne();

        Assertions.assertThat(findMember.getUserName()).isEqualTo("user1");
    }

}
```

### Q클래스 인스턴스 사용하는 방법

- 직접 Q인스턴스를 생성 할 수 도있다.
- 그냥 Q클래스에 생성된 클래스 인스턴스를 활용 할 수도 있다.
- 아니면 그냥 import static해서 바로 사용 할 수도 있다.(권장)

```java
QMember qMember = new QMember("m"); //별칭 직접 지정
QMember qMember = QMember.member; //기본 인스턴스 사용
```

### 검색 조건 쿼리

- 자료 참고
- .and의 경우 , 로 치환하여 사용할 수 있다.(조건 중에 null이 있어도 상관없다. 그냥 정상 동작함)

```java
    @Test
    public void search() {
        // 이름이 user1이면서 나이가 10살인 사람을 조회하는 쿼리를 날린다.
        // 만약에 비어있으면 알아서 null을 날린다.
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        Member findMember = jpaQueryFactory
                .selectFrom(member)
                .where(member.userName.eq("user1"),
                        member.age.eq(10))
                .fetchOne();
        Assertions.assertThat(findMember.getUserName().isBlank());
    }
```

### 결과 조회

- `fetch()` : 복수 조회, 없으면 그냥 빈 리스트 반환
- `fetchOne()` : 결과가 없으면 `null`을 반환함. 만약 같은 2개 이상의 값이 조회 될 경우 `NonUniqueResultException` 터트린다.
- `fetchResults(), fetchCount() 는 이제 지원 안한다.

### 정렬

```java
 List<Member> result = queryFactory
			 .selectFrom(member)
			 .where(member.age.eq(100))
			 .orderBy(member.age.desc(), member.username.asc().nullsLast())
			 .fetch();
```

- desc() , asc() : 일반 정렬
- nullsLast() , nullsFirst() : null 데이터 순서 부여

### 집합

- 프로젝션 타입이 여러개이면 Tuple 타입으로 반환하면 된다.(권장 x → DTO로 뽑음)

```java
 List<Tuple> result = queryFactory
			 .select(member.count(),
							 member.age.sum(),
							 member.age.avg(),
							 member.age.max(),
							 member.age.min())
			 .from(member)
			 .fetch();
```

```java
List<Tuple> result = queryFactory
 .select(member.username, member.age)
 .from(member)
 .fetch();
```

- 데이터 꺼낼때는 tuple.get(member.name), tuple.get(member.age)하면됨 엥간하면 Tuple 자료형은 repository 안에서만 놀아라, Service나 Controller 단에 던지지 마라
- gruopBy()와 having()

```java
 …
.groupBy(item.price)
.having(item.price.gt(1000))
```

### 기본 조인

```java
    @Test
    public void join(){
        List<Member> memberList= jpaQueryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("SKT"))
                .fetch();
    }
```

- 외래 키로 설정된 필드와 진짜 그 객체를 연결 시켜주는게 조인이다.
    - 그래서 외래 키 부분인 member.team이고 진짜 그 테이블인 team이 조인되는것이다.
    - 키값을 통해서만 테이블을 결합 시킬 수 있다.
    - 더 쉽게 그냥 외래 키 부분에 진짜 엔티티를 소환하는 것

### 세타 조인(연관관계 x 테이블 끼리 조인)

```java
    @Test
    public void theta_join(){
        List<Member> memberList= jpaQueryFactory
                .select(member)
                .from(member, team)
                .where(team.name.eq("SKT"))
                .fetch();
    }
```

- 근데 키 값없이고 그냥 조인시킬 수 있는데 그게 세타 조인임(이건 엄밀히 조인이 아니기 때문에 join 선언이 없는것)

### 조인 on절

1. 조인 대상을 필터링

```java
@Test
public void join_on_filtering() throws Exception {
 List<Tuple> result = queryFactory
		 .select(member, team)
		 .from(member)
		 .leftJoin(member.team, team).on(team.name.eq("teamA"))
		 .fetch();
```

- 문법 차이 주의!!
    - 일반조인: leftJoin(member.team, team)
    - on조인: from(member).leftJoin(team).on(xxx)
1. 연관관계 없는 엔티티를 외부 조인

```java
 List<Tuple> result = queryFactory
		 .select(member, team)
		 .from(member)
		 .leftJoin(team).on(member.username.eq(team.name))
		 .fetch();
```

### 페치 조인 : fetchJoin()

```java
 Member findMember = queryFactory
		 .selectFrom(member)
		 .join(member.team, team).fetchJoin()
		 .where(member.username.eq("member1"))
		 .fetchOne();
```

### 서브쿼리

- com.querydsl.jpa.JPAExpressions 사용

```java
QMember memberSub = new QMember("memberSub");
List<Member> result = queryFactory
			 .selectFrom(member)
			 .where(member.age.eq(
			 JPAExpressions
						 .select(memberSub.age.max())
						 .from(memberSub)
						 ))
			 .fetch();
```

- 본 쿼리문과 서브 쿼리문의 Q인스턴스 별칭이 동일하면 안되기 때문에 별도로 서브쿼리용 Q인스턴스를 생성해주는 부분만 조심해주면 된다.
- where, select, from 절 서브쿼리 다 가능하다.

### 상수, 문자 더하기

- 상수 : Expression

```java
Tuple result = queryFactory
 .select(member.username, Expressions.constant("A"))
 .from(member)
 .fetchFirst();
```

- 문자 더하기 : .concate

```java
String result = queryFactory
 .select(member.username.concat("_").concat(member.age.stringValue()))
 .from(member)
 .where(member.username.eq("member1"))
 .fetchOne();
```

- Enum 타입은 String이 아니기 때문에 결합할때 stringValue()로 변경해야한다.