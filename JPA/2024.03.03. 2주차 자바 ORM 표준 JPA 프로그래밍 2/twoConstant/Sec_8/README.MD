# 프록시

> 엔티티와 동일한 필드를 가진 껍데기 엔티티를 프록시라고한다.(그냥 빈 껍데기)
> 

<aside>
💡 프록시 == 지연로딩

</aside>

### 왜 프록시가 필요해?

- 엔티티의 연관된 다른 엔티티를 필요할 때까지 로드하지 않도록 해준다. —> 지연 로딩 → 성능 최적화
    - member.getName()을 했을때 member 고유의 모든 속성에 대한 데이터는 조회되어 넘어오지만 연관관계에 있는 Team에 대한 데이터는 가져오지 않는다.
    - 특정 필드만 가져오는건 아니다.

### 사용법 ⇒ 그냥 엔티티 사용하는거랑 똑같이 사용

```java
// member 엔티티가 아닌 프록시 member를 반환
Member member = em.getReference(Member.class, "id1");
// 프록시는 진짜 member 엔티티를 참조하기 때문에 메소드 호출시 진짜 member 엔티티의 메서드 호출
member.getName();
```

- `em.getReferece()`를 통해 프록시 객체를 반환 받는다.
    - 만약 영속성 컨텍스트에 이미 해당 엔티티가 존재한다면 진짜 엔티티를 반환한다.

### 주의사항

- 엔티티가 준 영속 상태일때 프록시를 초기화하면 `LazyInitializationException` 예외 터짐
- 프록시를 사용하는 이유가 지연로딩의 이점을 누리기 위함인데 지연 로딩이 가능하려면 해당 엔티티를 통해 언제든지 DB에서 필요한 데이터를 가져올 수 있어야함(영속 상태). 근데 준 영속 상태로 가버리면 해당 엔티티로 DB에 접근 하지 못하기 때문에 지연 로딩 또한 하지 못함. 그래서 프록시도 초기화 하지 못하는것.
- 영속 상태
    - 엔티티가 '영속성 컨텍스트'와 연결되어 있어, 언제든지 필요할 때 데이터베이스와 상호작용(데이터 로드 및 저장 등)이 가능한 상태 —> 지연 로딩 가능 —> 프록시 초기화 가능
- 준 영속 상태
    - 엔티티가 영속성 컨텍스트에서 분리되어, 더 이상 데이터베이스와 자동으로 상호작용하지 않는 상태입니다. 즉, 이 상태에서는 엔티티의 데이터를 가져오거나 변경사항을 데이터베이스에 반영할 수 없습니다. —> 지연 로딩 불가능—> 프록시 초기화 불가능

### 어떻게 이게 가능한가?

- 프록시는 객체는 실제 객체의 참조값을 가지고있음
- 참조값을 가지고있다가 실제로 메서드 호출이 일어났을 때 진짜 엔티티로 연결해서 메서드를 동작시킴

### 프록시를 이용해 member.GetName(); 했을 때 내부에서 일어나는 로직

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/d778d681-81c4-4a4b-b992-bbf500098a98/Untitled.png)

1. 프록시 초기화 : 프록시는 데이터베이스에 실제 엔티티 데이터를 조회하는 요청하고 해당 엔티티의 참조 값으로 갱신됨.
2. 실제 엔티티 로드 : DB 조회를 통해 진짜 엔티티를 생성
3. 초기화 된 프록시를 통해 실제 엔티티의 메서드가 실행되며 요구한 값이 반환됨

### 프록시의 단점 : 지연 로딩 vs 즉시 로딩

```java
String name = member.GetName();
Tema team = member.GetTeam();
```

- 프록시를 통한 지연 로딩의 경우 쿼리가 2 번 나가고 DB 조회가 2번 일어남
    - member.GetName() : Member와 연관관계에 있는 Team엔티티는 빼고 모든 속성 데이터를 끌고와서 member 엔티티를 생성하고 .GetName() 메서드 동작
    - member.GetTeam(); : 기존의 member 엔티티의 속성 정보에 team이 없기 때문에 다시 한번 쿼리 쳐서 member에 team 정보를 채워주고 GetTeam() 메서드 실행해서 팀정보를 반환
- 프록시를 사용하지 않은 즉시 로딩의 경우 쿼리가 1번 나가고 DB 조회 또한 1번 일어남
    - member.GetName() : Member의 고유 속성정보와 연관관계에 있는 모든 엔티티에 대한 정보를 한번에 다 끌어옴
    - member.GetTeam() : 이미 앞의 요청으로 member에 대한 모든 속성 정보가 있기 때문에 DB에 조회할 필요 없이 team을 반환 함.
- 만약에 member.GetTeam()을 하고 member.GetName()을 하면 지연로딩이라도 DB에 1번만 조회하지 않을까?
    - 아님. 그냥 지연로딩 설정 자체가 연관관계에 있는 엔티티를 조회할때 딱 그 정보만 찾아오기 때문에 member.GetName() 할 때 다시 DB 조회가 필요함.

# 즉시 로딩과 지연 로딩

> 연관관계 설정이 되어있는 엔티티의 로딩 시점을 설정 할 수 있다.
> 

### 즉시 로딩

- `fetch = FetchType.EAGER`
- 즉시 로딩으로 설정된 필드는 항상 해당 엔티티가 조회될 때 “같이” DB에서 조회되어 정보가 반환 됨.
- 최적화를 한다면 DB 접근을 최적화 하면서 성능을 이끌어 낼 수 있지만 쿼리가 이상하게 나가 신뢰성의 문제가 있어 실무에선 사용하지 않는다고 함.
- OneToOne, ManyToOne과 같이 One으로 끝나는 애들은 로딩 디폴트 값이 즉시 로딩으로 설정 되어있음 =⇒ 그래서 지연로딩으로 로딩 설정 바꿔줘야함

### 지연 로딩

- `fetch = FetchType.LAZY`
- 지연 로딩으로 설정된 필드는 해당 필드를 꼭 집어 조회하지 않으면 DB에서 정보를 가져오지않음
- 심지어 꼭 집이 조회해도 딱 해당 필드의 정보만 반환하는 싸가지 없는 로딩 설정 하지만 쿼리의 신뢰성이 높아서 실무에서 사용.
- OneToMany, ManyToMany는 기본이 지연로딩

### N + 1 문제

- 즉시 로딩시 발생하는 N + 1
    - 기본적으로 JPQL 사용시 발생함
    - Member, Team이 즉시 로딩 관계일때
    - 만약 모든 유저의 이름을 조회하는 JPQL을 작성했다고 가정
    - 그냥 유저정보만 조회하면 끝나는데 즉시 로딩이 되었있기 때문에 불필요한 각 유저별로 팀을 조회하는 쿼리까지 나가게 되어 비효율적임
- 지연 로딩시 발생하는 N + 1
    - 한방에 가져오면 DB 1회 조회를 한 결과로 이후에 필요한 정보를 사용하면 되는데
    - 그게 안됨 무조건 연관관계에 있는 엔티티 조회 당 1회의 쿼리와 DB 조회가 발생함.
- 해결책
    - JPQL 페치 조인(Fetch Join)
    - @EntityGraph
    - 로딩 전략 변경

# 영속성 전이

> 연관 관계로 설정된 엔티티가 영속성 상태로 들어갈 때 연관 관계에 있는 엔티티 또한 영속성 상태로 들어가도록 설정하는 것. 말 그대로 영속성이 전이되는 것.
> 

<aside>
💡 연관관계 설정하는건 그냥 자바 객체로 테이블 만드는 것에서 끝이다. 테이블 데이터를 변경하는건 관련된 엔티티가 영속성 상태에 들어 있어야 DB에 반영이됨.

</aside>

### 왜 사용하는데?

- 근본적으로 개발자의 실수에(영속성 상태 관리 부실)의해 데이터 무결성이 깨지는 것을 막아준다.
    - 연관관계에 있는 애들 잘 관리 해줄 자신 있으면 영속성 전이 사용안해도 됨
- 반복 작업을 줄여 코드가 간결해진다.

### 영속성 전이 비사용 vs 영속성 전이 사용

```java
@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    // 영속성 전이 설정 없음
    @OneToMany(mappedBy = "parent")
    private List<Child> children = new ArrayList<>();
}

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
}

// 엔티티 저장 코드
Parent parent = new Parent();
Child child1 = new Child();
Child child2 = new Child();

child1.setParent(parent);
child2.setParent(parent);
parent.getChildren().add(child1);
parent.getChildren().add(child2);

// 변경 사항이 있는 모든 연관관계의 엔티티를 영속성 상태로 변경해야 변경사항이 DB에 반영됨
em.persist(parent);
em.persist(child1);
em.persist(child2);
```

- 문제점 : 만약 em.persist(child2); 이거 빠뜨리면 Childe 테이블에 child2 자체가 추가가 안됨. 그러면 이후에 DB에서 parent를 찾아서 모든 child를 조회해도 child1만 나오고 child2는 나오지 않는 개 ㅈ된 상황이 발생하게됨.

```java
@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    // 영속성 전이 설정
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<Child> children = new ArrayList<>();
}

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
}

// 엔티티 저장 코드
Parent parent = new Parent();
Child child1 = new Child();
Child child2 = new Child();

child1.setParent(parent);
child2.setParent(parent);
parent.getChildren().add(child1);
parent.getChildren().add(child2);

em.persist(parent); // 여기서 parent와 연관된 child1, child2도 함께 저장됨
```

- 장점
    - 그냥 연관관계에서 1인 엔티티를 영속상태로 변경해주기만 하면 연관관계에 있는 엔티티가 알아서 영속 상태에 들어가면서 변경 정보가 반영됨.

### 전략

> 주로 1 : N 관계에서 1의 엔티티에 연관관계가 설정된 엔티티에 걸어준다.(@OneToMany() 의 cascade속성으로 걸어주면 됨)
> 
- `cascade = CascadeType.PERSIST`
    - 팀이 사라진다고 해서 그 팀에 있는 멤버가 멤버 테이블에서 없어지지 않음 그냥 멤버의 팀 필드의 값에 수정이 있을 뿐인 관계

```java
@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    // 영속성 전이 설정
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<Child> children = new ArrayList<>();
}

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
}

// 엔티티 저장 코드
Parent parent = new Parent();
Child child1 = new Child();
Child child2 = new Child();

child1.setParent(parent);
child2.setParent(parent);
parent.getChildren().add(child1);
parent.getChildren().add(child2);

em.persist(parent); // 여기서 parent와 연관된 child1, child2도 함께 저장됨

```

- `cascade = CascadeType.REMOVE`
    - 팀이 사라지면 그 팀에 있는 멤버도 멤버 테이블에서 삭제되는 관계

```java
@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    // 영속성 전이 설정
    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Child> children = new ArrayList<>();
}

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
}

// 엔티티 삭제 코드
Parent parent = em.find(Parent.class, parentId);

// cascade = CascadeType.REMOVE 안되어 있는 경우
// for (Child child : parent.getChildren()) {
//     em.remove(child); // 각 Child 엔티티를 명시적으로 삭제
// }
//

em.remove(parent); // Parent 엔티티를 삭제할 때 연관된 Child 엔티티들도 함께 삭제됨

```

- `cascade = CascadeType.PERSIST`+ `orphanRemoval = true`
    - 팀이 사라진경우 해당 멤버의 팀이 사라진 팀 밖에 없었던경우 해당 멤버도 멤버 테이블에서 사라지는 관계 고아 객체가 되어도 해당 테이블에서 사라지게 하지 않으려면  `orphanRemoval = true` 이거 설정 하면 안됨

```java
 @Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Child> children = new ArrayList<>();

    // Helper method to manage bi-directional relationship
    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Child child) {
        children.remove(child);
        child.setParent(null);
    }
}

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
}

// 엔티티 저장 코드
Parent parent = new Parent();
Child child1 = new Child();
Child child2 = new Child();

parent.addChild(child1);
parent.addChild(child2);

em.persist(parent);

// 엔티티 삭제 코드
// 고아 객체 제거를 활용하여 Child 엔티티 삭제
parent.removeChild(child1); // 이 Child 인스턴스는 고아 객체가 되며, 데이터베이스에서 자동으로 삭제됩니다.

```

# 실전 예제