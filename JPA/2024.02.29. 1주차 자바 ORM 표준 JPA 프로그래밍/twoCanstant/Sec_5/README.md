# 5. 연관관계 매핑 기초

# 목차

# 객체와 테이블 연관관계 차이

<aside>
💡 객체에는 방향이 존재하지만 테이블에는 방향의 개념이 없다.

</aside>

# 단방향 연관관계

> 한쪽에서만 연관관계에 있는 객체에 접근가능 한 경우. 객체의 경우 참조에 의해 방향성이 있기 문에 테이블 관계에는 없는 객체의 특성으로 생김
> 

### 예시 Member → Team

```java
 @Entity
 public class Member { 
		 @Id @GeneratedValue
		 private Long id;
		 @Column(name = "USERNAME")
		 private String name;
		 private int age;
		
		 @ManyToOne
		 @JoinColumn(name = "TEAM_ID")
		 private Team team;
}
```

```java
 @Entity
 public class Team { 
		 @Id @GeneratedValue
		 private Long id;
		 @Column(name = "name")
		 private String name;
}
```

설명

- Member의 경우 멤버변수로 team 객체를 가지고있다. 그렇기 때문에 member.GetTeam()을 통해서 team에 접근이 가능하다.
- Team의 경우 멤버변수로 member를 가지고있지 않기 때문에 team을 통해서 member에 접근 할 수 없다.
- Member와 Team의 관계 수정도 오로지 Member를 통해서만 이루어진다.

# 객체지향 모델링의 장점

- 객체 그래프 탐색 : 복잡한 SQL문을 칠 필요 없이 참조를 통해 자유롭게 다른 엔티티로 이동하여 CRUD 가 가능하다.
- 예시(특정 멤버의 팀을 조회하는 경우)
    - 객체 그래프 탐색을 통한 경우
    
    ```java
    /조회
     Member findMember = em.find(Member.class, member.getId()); 
    //참조를 사용해서 연관관계 조회
     Team findTeam = findMember.getTeam();
    ```
    
    - sql을 통해서 작업해야하는 경우
    
    ```java
    SELECT Team.*
    FROM Member
    JOIN Team ON Member.team_id = Team.id
    WHERE Member.id = 1;
    ```
    
- 예시 2 객체 그래프 탐색 극대화 사례
    - 블로그의 경우 작석자, 게시글, 댓글이 있고 엔티티의 연관관계는 아래와 같을 때 특정 게시글에 대한 모든 코멘트를 조회하고 각 코멘트의 저자 정보까지 추출해오는 경우
    
    Post → Comment → Author
    
    - JPA 그래프 탐색을 활용한경우
    
    ```java
    Post post = entityManager.find(Post.class, postId);
    System.out.println("Post Title: " + post.getTitle());
    for(Comment comment : post.getComments()) { // 지연 로딩으로 Comment 조회
        System.out.println("Comment: " + comment.getText() + ",
    												Author: " + comment.getAuthor().getName()); // 각 Comment의 Author도 지연 로딩으로 조회
    }
    ```
    
    - SQL 매퍼 사용시
    
    ```sql
    <mapper namespace="com.example.mapper.PostMapper">
        <select id="selectPostWithComments" resultMap="PostCommentMap">
            SELECT p.id as post_id, p.title, p.content, 
                   c.id as comment_id, c.text, 
                   a.id as author_id, a.name
            FROM Post p
            LEFT JOIN Comment c ON p.id = c.post_id
            LEFT JOIN Author a ON c.author_id = a.id
            WHERE p.id = #{postId}
        </select>
        
        <resultMap id="PostCommentMap" type="Post">
            <id property="id" column="post_id"/>
            <result property="title" column="title"/>
            <result property="content" column="content"/>
            <collection property="comments" ofType="Comment">
                <id property="id" column="comment_id"/>
                <result property="text" column="text"/>
                <association property="author" javaType="Author">
                    <id property="id" column="author_id"/>
                    <result property="name" column="name"/>
                </association>
            </collection>
        </resultMap>
    </mapper>
    ```
    

# 양방향 연관관계

### 연관계의 주인

- 양방향 참조 관계에서 수정 권한을 가지고 있는 객체를 의미한다.

### 왜 연관관계의 주인이 필요한가?

- 수정 권한이 양측에 다 있다면 데이터 무결성이 깨질 수가 있다.
- 이런 이유가 일어나는 이유는 DB에서는 외래키 설정만하면 Join을 활용해서 양방향 조회가 가능하며 자동으로 무결성(양측의 외래키로 연결된 데이터의 일치성)을 관리해주지만 객체세계에서는 양방향 참조 설정만 하면 양쪽 엔티티에서 서로 수정이 가능하기때문에 데이터 무결성이 깨질 수 있기 때문에 한쪽에서만 수정할 수 있게 수정 권한이 있는 객체를 따로 지정해 줘야한다.

### 양방향 연관관계란 무엇인가?

- A.GetB(), B.GetA()가 가능한 관계 즉 서로가 서로를 멤버변수로 가지고 있기 때문에 양측에서 서로를 참조할 수 있는 관계를 양방향 연관관계라고한다.

### 어느 객체를 연관관계 주인으로 설정하면되나?

- 외래 키를 가진 객체를 연관관계 주인으로 설정한다. 주로 N:1 에서 N쪽의 객체에 외래키가 주어지고 연관관계의 주인이된다.

### 어떻게 주인으로 설정을 하나?

- @MappedBy(”A”)를 통해 해당 멤버변수는 A를 통해서 매핑(생성 및 수정) 됨을 지정 할 수 있다. —> **A.SetB 를 통해서만 A가 정상적으로 DB에 반영된다.**

### @JoinColumn(”기본키 명”)과 @MappedBy(”연관관계 주인”)

- @JoinColumn : 객체가 아닌 DB 관계에서 봤을 때 외래키를 지정해주는 애노테이션
- @MpppedBy(”A”) : A 객체에 의해서 맵핑된 B의 의미로 결국 연관관계 주인은 B라는것을 의미 연관관계 주인이 아닌 객체에게 부여한다.
    
    ```java
    public class Member {
    		@OneToMany(mappedBy = "team")
    		List<Member> memberList = new ArrayList<>();
    }
    ```
    
    ```java
    public class Team {
    			@ManyToOne(fetch = Fetch.Lazy)
    			@JoinColumn("team_id")
    			Team team;
    }
    ```
    

# 실습

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/621d4656-344d-4b71-b0b2-7be0110ad70b/Untitled.png)

- 연관관계 설정하기 : N대1, Member(Many), Team(One)
- 외래키 부여하기 : Member 객체에 team_id 가 존재
- 연관관계 주인설정하기

### 연관관계 설정하기

- Member
    
    ```java
    public class Member {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "member_id")
        private Long id;
    
        @ManyToOne
        private Team team;
    
        @Column(name = "user_name")
        private String userName;
    }
    ```
    
- Team
    
    ```java
    public class Team {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "team_id")
        private Long id;
    
        @Column(name = "name")
        private String name;
    
        @OneToMany
        private List<Member> memberList = new ArrayList<>();
    }
    ```
    

### Member(Many side)객체에 외래키 부여

<aside>
💡 여기까지는 일반적은 DB 설계와 동일하다.

</aside>

- Member
    
    ```java
    public class Member {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "member_id")
        private Long id;
    
        @ManyToOne
        @JoinColumn(name = "team_id")
        private Team team;
    
        @Column(name = "user_name")
        private String userName;
    }
    ```
    

### 연관관계 주인 설정 ⇒ 외래키를 가진놈이 연관관계의 주인

- 외래키로 설정된 Member.team이 연관관계의 주인이고 Team.memberList는 노예임 즉 Team.memberList is mapped by Member.team임
    
    ```java
    public class Team {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "team_id")
        private Long id;
    
        @Column(name = "name")
        private String name;
    
        @OneToMany(mappedBy = "team")
        private List<Member> memberList = new ArrayList<>();
    }
    ```
    

### 지연 로딩 설정

- @ManyToOne 는 지연 로딩으로 설정 해줘야 한다.
    
    ```java
    public class Member {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "member_id")
        private Long id;
    
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "team_id")
        private Team team;
    
        @Column(name = "user_name")
        private String userName;
    }
    ```
    

### 최종

- Member
    
    ```java
    public class Member {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "member_id")
        private Long id;
    
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "team_id")
        private Team team;
    
        @Column(name = "user_name")
        private String userName;
    }
    ```
    
- Team
    
    ```java
    public class Team {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "team_id")
        private Long id;
    
        @Column(name = "name")
        private String name;
    
        @OneToMany(mappedBy = "team")
        private List<Member> memberList = new ArrayList<>();
    }
    ```
    

### 외래키를 가지고있고 연관관계의 주인인 Member 객체를 통해서만 Team의 데이터가 DB에 반영된다.

- Team 객체를 통해서 memberList에 member를 추가하는 경우는 DB에 반영이 되지않는다.
    
    ```java
    Team team = new Team();
    team.setName("TeamA);
    em.persist(team);
    
    Member member = new Member();
    member.setName("member1");
    
    // Team 객체를 통해 member 객체를 추가하는 경우
    team.getMemberList.add(member); //--> DB에 반영이 되지 않는다. member1 null
    
    // Member 객체를 통해 Team을 설정하는 경우
    member.SetTeam(team);  //--> DB에 반영이 된다. member1 2
    ```
    

# 양방향 연관관계 주의사항

### 순수 객체 상태를 고려하여 항상 양쪽에 값을 설정하자

- 외래키를 설정하고 mappedBy를 한다고해서 자동으로 엔티티에 자동 갱신되는건 아니다.(Transaction 기준으로 DB에 업데이트되고 그 뒤에 불러오면 갱신되어있음)
- 한 컨트롤러 내에서 작업을 할 때 바로 DB에 갱신되지 않기때문에 작업 중에는 양방행 둘다 엔티티에 엔티티를 값을 설정해줘야한다.
- 예시
    
    ```java
    Member member = new Member();
    member.SetName("이상수");
    
    Team team = new Team();
    team.SetName("빅샷");
    
    member.SetTeam(team);  // --> DB에 반영
    team.memberList.add(member);    // --> DB에 반영 X But 권장
    ```
    

### 연관관계 편의 메소드를 생성하자

- 별도로 연관관계의 주인인 엔티티에 객체를 업데이트 할 때 자동으로 반대편 엔티티에도 추가가될 수 있도록하는 메서드를 활용한다.
    
    ```java
    public class Member {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "member_id")
        private Long id;
    
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "team_id")
        private Team team;
    
        @Column(name = "user_name")
        private String userName;
    
    		public void setTeam(Team team) {
    				this.team = team;
    				team.memberList.add(team);
    		}
    }
    ```
    
- Member의 인스턴스 메서드에 setTeam();을 만들어서 한번에 양방향 객체 설정 처리를 할 수 있다. 즉
    
    ```java
    Member member = new Member();
    member.SetName("이상수");
    
    Team team = new Team();
    team.SetName("빅샷");
    
    member.setTeam(team);    //--> 인스턴스 메서드에의해 양방향 객체 할당 수행
    ```
    

### 양방향 매핑시 롬복을 이용한 toString(); 메서드나 직렬화 사용시 순환참조로 stackOverFlow 에러가 날 수 있다.

### 일단 단방향으로만 테이블을 설계하자

- 단방향 연관관계 매핑만해도 이미 테이블 설계는 끝난것이다.
- 개발 중 양방향으로 조회가 필요할 경우에 추가하고 mappedBy 설정을 해주면 된다.