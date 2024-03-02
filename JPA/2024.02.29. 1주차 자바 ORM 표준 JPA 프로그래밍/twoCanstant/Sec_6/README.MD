# 6. 다양한 연관관계 매핑

# 연관관계  매핑시 고려사항 3가지

### 1. 다중성

- `N:1` : 가장 많이 사용함. 무조건 구현 할 수 있어야됨
- `N:M` : 쓰지말고 1 : N, N : 1로 나눠서 구현해라
- `1:1`

### 2. 단방향, 양방향

- 기본적으로는 단방향으로 다 해결 가능 —> ERD와 그냥 동일한 상태
- 양방향은 조회가 필요한 경우에 추 후에 `OneToMany(maapedBy = " ")` 설정해줘도 늦지않음

### 3. 연관관계 주인

- 2번참고

# 다양한 연관관계 매핑 방법

### N : 1 단 방향

![Member : Team = N : 1](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/03262103-b845-46c5-9ed3-03b4cd601b7a/Untitled.png)

Member : Team = N : 1

- 외래키 : N
- 단방향 : [member.team](http://member.team) 만 가능(team.member 불가능)

### N : 1 양방향

- 외래키 부여 : N
- 연관관계 주인 설정

### 1 : N 단방향(권장하지 않음 하지만 객체다운 방법임)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/aa169f87-bcbc-44d0-ba32-e657b1151282/Untitled.png)

- 외래키 부여 : 1  단 DB 상의 외래키(FK)는 변하지 않는다. 단지 매핑되는 객체가 다른 쪽일 뿐.
- 단방향
- Member(N), Team(1)코드 확인하기

```java
@Entity
public class Member {
		// 연관관계 영역
	  // 단방향이기 때문에 생략
}
```

```java
@Entity
public class Team{
		// 연관관계
		@OneToMany
		@JoinColumn("TEAM_ID")
		private List<Member> memberList = new ArrayList<>();
}
```

- 왜 이게 객체다운 방법인가?
    - 일반적으로 바구니에 공을 넣지 공이 바구니를 정하지 않는다.
    - 외래키의 위치에 독립적으로 서비스 상에서 꼭 필요한 단방향 관계만 설정 할 수 있기 때문에 메모리상으로 유리한 이점을 가져갈 수 있다.
        - ex) team.memberList의 참조 방향만 필요한 경우 기존의 정석적인 방법이라면 member.team은 외래키 설정 때문에 반드시 생길 수 밖에 없지만 1:N 단방향 매핑은 외래키의 위치에 독립적이기 때문에 필요한 참조 관계만 설정 할 수 있다.

```java
// N 쪽에 외래키 주인도 N쪽인경우(정석적인 상황)
Member memberA = new Member();
Team teamA = new Team();

memberA.SetTeam(teamA);  // 공이 바구니를 정하는 격

// N쪽에 외래키 설정, 주인은 1쪽인 경우(권장하지 않는 방식)
Member memberB = new Member();
Team teamB = new Team();

teamB.memberList.add(memberB);    // 바구니에 공을 담는 격 --> 자연스럽게 받아드려짐
```

- 왜 권장하지 않는가?
    - 개발자가 예측한 쿼리가 나가지 않음 → 내가 다룬 엔티티에 대한 쿼리가 나가는게 아닌 다른 엔티티의 쿼리가 나감 —> 헷갈림
    - 아래의 로직을 보면 1쪽이 외래키를 관리하는 경우(권장하지 않는 방식) `teamB.memberList.add(memberB)` 를 한 경우 team 테이블에 대한 쿼리가 나가는게 일반적인 개발자이 받아드리는 사고방식 근데 내가 건드리지도 않은 Member 테이블에대한 쿼리가 나가면서 이런 JPA 세팅을 모르는 개발자를 불안하게 만듦 더군다나 현업에서는 맞물려 돌아가는 테이블이 많기 때문에 이러한 업데이트 쿼리가 여러 테이블에 걸쳐 나가면 이거 뭐 사고친건가? 라는 생각이 날 수 밖에 없음 그래서 협업을 고려했을 때 권장하는 방식이 아님

```java
// N 쪽에 외래키를 주인도 N쪽인경우(정석적인 상황)
Member memberA = new Member(); //  Member 테이블 변경 insert 쿼리
Team teamA = new Team();   // Team 테이블 변경 insert 쿼리

memberA.SetTeam(teamA);  // Member 테이블 변경 update 쿼리
// 나가는 쿼리
// member 테이블에 memberA 추가
// Team 테이블에 teamA 추가
// Member 테이블에 memberA의 team_id 를 teamA로 설정

// N쪽에 외래키 설정, 주인은 1쪽인 경우(권장하지 않는 방식)
Member memberB = new Member();    // Member 테이블 변경 insert 쿼리
Team teamB = new Team();    // Team 테이블 변경 insert 쿼리

teamB.memberList.add(memberB);    // Team이아닌 Member 테이블 변경 update 쿼리
// member 테이블에 memberA 추가
// Team 테이블에 teamA 추가
// Member 테이블에 memberA의 team_id 를 teamA로 설정
```

### 1 : N 양방향(굳이 할 필요가 없음)

- 외래 키 : 1이 연관관계의 주인(1쪽이 외래키를 관리하겠다.)
- 그냥 ㅈㄴ 억지임 어차피 양방향으로 설정될꺼 그냥 정석적인 방법으로 설계하면됨
    - 차라리 1 : N 단방향은 메모리적으로나 논리적이거나 장점은 있는데 이건 그것조차 없음

### Member(N)

```java
@ManyToOne
@JoinColumn("TEAM_ID", insertable = false, updatabel = false)
private Team team;
```

- 일단 정상적인 방식으로 그대로 쓰고 `insertable = false, updatabel = false`  속성 설정을 통해 해당 객체를 읽기 전용으로 강제로 바꿔주면 됨
- 일기 전용으로 설정을 안 하면 Member, Team 둘 다 연관관계의 주인이 되면서 그냥 엄대엄 되는거임—> JPA가 바보가됨(쿼리가 실행이 안되거나 둘 다 되거나 예측 불가능하게 동작하게 됨)

### Team(1)

```java
@OneToMany
@JoinColumn("TEAM_ID")
private List<Member> memberList = new ArrayList<>();
```

### 1 : 1 단방향 주테이블에 외래키 부여

<aside>
💡 모든 1 : 1 관계는 일대일 대응 관계를 유지하기 위해서 유니크 제약조건이 필수!(근데 JPA 가 알아서 OneToOne은 유니크 제약 걸어줌)

</aside>

외래키 : 그냥 아무나한테 줘도 상관없음 근데 유니크 제약조건 걸어줘야됨

- 유니크 제약조건 걸어주는 이유 : 당연히 한 사람당 사물함 하나여야지 유니크 제약이 없으면 야 너두 사물함 번호 1번이야? 나두~ 가된다.

### Locker

```java
@Id
@Generate...
@Column(name = "LOCKER_ID")
private Long lockerId;
```

### Member

```java
@OneToOne
@JoinColumn("LOCKER_ID", )
private Locker locker;
```

### 1 : 1 양방향 주테이블에 외래키 부여(권장)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/51302bf3-328c-478a-93af-80f6238b2025/Untitled.png)

- 주테이블 : 테이블 상의 PK 의 위치와 같은 형태로 설게하는 경우(정석적인 방식)
- Member와 Locker 엔티티 코드(한 사람당 한 개의 사물함 배정)

### Locker

```java
@Id
@Generate...
@Column(name = "LOCKER_ID")
private Long lockerId;

@OneToOne(mappedBy = "member")
private Member member
```

### Member

```java
@OneToOne
@JoinColumn("LOCKER_ID", )
private Locker locker;
```

### 1 : 1 단방향 대상테이블에 외래키 부여

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/6dad0eac-1f03-4631-8cff-00beb63fd1ed/Untitled.png)

- 테이블의 FK 의 위치와 객체의 주인이 다른경우 —> JPA 에서 지원 안함

### 1 : 1 양방향 대상테이블에 외래키 부여

- 그냥 정석적인 방법으로 하면됨

### 1 : 1 관계에서 고민해야할것 ⇒ 외래키를 누가 관리한거냐?

- 이게 정말 미래에도 1 : 1일까?
- 회원 당 사물함이 여러개로 바뀐다면?
- 사물함당 여러 회원이 사용하는 걸로 바뀐다면?

### N : M(쓰지마라)

- 반드시 연결용 테이블에 매핑할 엔티티를 추가해줘 1 : N , N : 1 로 만들어 줘야한다.
- 왜 쓰면 안되나?
    - 일단 기본적으로 관계형 데이터베이스는 정규화된(필드당 데이터 하나) 테이블 2개로 다대다 관계를 표현 할 수 가 없다. 다대다면 필연적으로 한 필드에 2개 이상의 데이터를 넣어야된느 상황이 온다. 그게 구현이 안된다는거임
    - 단순 다대다로 연결된 DB의 데이터 이외의 데이터가 필요한데(최소 발생시간, 수정시간…) 그 추가 기록을 지원하지 못함
    - 2개의 엔티티로 구현 한다해도 JPA 내부적으로 중계테이블을 하나 만들고 어떤 작업에 대해 예상치 못한 쿼리가 나갈 수 있다.