# 조회 API

# main환경과 test 환경 분리설정 하기

> main 환경과 test 환경에 별도의 application 프로퍼티파일 생성하기
> 
- `spring.profiles.active=local`
- `spring.profiles.active=test`
- local : 로컬 환경
- dev : 개발 환경
- deploy : 배포 환경
- test : 환경

### 데이터 생성 담당 클래스 선언

`package com.ssafy.querydsl.init;`

```java
@Profile("local")    // 실행 환경이 local인 경우 실행하겠다.
@Component    // 빈 주입이느 클래스별로 필요한 작업이다..
@RequiredArgsConstructor
public class MemberInit {
    private final InitMemberService initMemberService;

    @PostConstruct    // 스프링 부트 실행전 트리거
    public void init(){
        initMemberService.inIt();
    }

    @Component
    static class InitMemberService{

        @PersistenceContext
        EntityManager em;

        @Transactional
        public void inIt() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Member member = new Member("user" + i, i);
                if(i % 2 == 0){
                    member.setTeam(teamA);
                } else {
                    member.setTeam(teamB);
                }
                em.persist(member);
            }
        }
    }
}
```

- 굳이 @Component 부분과 @Transaction 부분을 구분하는 이유는 둘의 생의 주기 때문에 같은 클래스에 동시에 선언 할 수 없다.

# 사용자 정의 리포지토리 구성

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/a2320d02-fb15-435f-983b-c24d2e1a1ed7/Untitled.png)

- interface MemberRepositoryCustom
- class MemberRepositoryImpl implements MemberRepositoryCustom

```java
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Member> findAll() {
        return jpaQueryFactory
                    .selectFrom(QMember.member)
                    .fetch();
    }
}
```

- interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom

> 결국 기존의 JpaRepository에 추가기능을 확장하는 과정이다.
> 

# 쿼리 프로젝션

### MemberTeamDTO 정의

```java
@Getter
@Setter
@NoArgsConstructor
public class MemberTeamDTO {

    private Long memberId;
    private String memberName;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection
    public MemberTeamDTO(Long memberId, String memberName, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}

```

> DTO 정의하고 반드시 gradle.Tasks.compilJava 실행하고 build.generated.source.annotationProcessor.java.main에 Q클래스 생성 확인
> 

### 회원 이름, 나이, 팀명…을 기입하거나 하지 않은경우 검색 서비스

- StringUtils.hasText(); (SpringFramwork임)

```java
StringUtils.hasText(" "); // false
StringUtils.hasText(null); // false
StringUtils.hasText(""); // false
StringUtils.hasText("text"); // true
```

- Builder로 만들기(`import com.querydsl.core.BooleanBuilder;`)

```java
    @Override
    public List<MemberTeamDTO> searchByBuilder(MemberTeamSearchCondition memberTeamSearchCondition) {

        BooleanBuilder builder = new BooleanBuilder();
        // userName이 null이면 상관없는데 ""으로도 들어오는 경우가 많아서
        // StringUtils.hasText() 를 사용한다.
        if (StringUtils.hasText(memberTeamSearchCondition.getUserName())){
            builder.and(member.userName.eq(memberTeamSearchCondition.getUserName()));
        }
        if (StringUtils.hasText(memberTeamSearchCondition.getTeamName())){
            builder.and(member.team.name.eq(memberTeamSearchCondition.getTeamName()));
        }
        if (memberTeamSearchCondition.getAgeGoe() != null) {
            builder.and(member.age.goe(memberTeamSearchCondition.getAgeGoe()));
        }
        if (memberTeamSearchCondition.getAgeLoe() != null) {
            builder.and(member.age.loe(memberTeamSearchCondition.getAgeLoe()));
        }

        return jpaQueryFactory
                .select(new QMemberTeamDTO(
                        member.id.as("memberId"),
                        member.userName.as("memberName"),
                        member.age,
                        member.team.id.as("teamId"),
                        member.team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                    // 빌더 꼽으면 끝
                    builder
                )
                .fetch();
    }
```

- where로 구현해보기
- `import com.querydsl.core.types.dsl.BooleanExpression;` ⇒ 조합 가능한 타입

```java
@Override
    public List<MemberTeamDTO> searchByWhere(MemberTeamSearchCondition memberTeamSearchCondition) {
        return jpaQueryFactory
                .select(new QMemberTeamDTO(
                        member.id.as("memberId"),
                        member.userName.as("memberName"),
                        member.age,
                        member.team.id.as("teamId"),
                        member.team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        userNameEq(memberTeamSearchCondition.getUserName()),
                        teamNameEq(memberTeamSearchCondition.getTeamName()),
                        ageGoe(memberTeamSearchCondition.getAgeGoe()),
                        ageLoe(memberTeamSearchCondition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression userNameEq(String userName) {   
        return userName != null? member.userName.eq(userName) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return teamName != null? member.team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null? member.age.loe(ageLoe) : null;
    }
```

- 특히 조합이 가능한것으로 유효성 검사를 그냥 붙이면 되고 where절에서 chaing으로 .isValide() 하면된다.