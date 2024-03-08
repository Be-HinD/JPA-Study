# JPA가 인식하는 값타임

### 값 타입 분류

- 엔티티 타입
- 값 타입
    - 기본값 타입
        - 자바 기본 타입 : int, double
        - 래퍼 클래스 : Integer, Long
        - String
    - 임베디드 타입 : embedded type, 복합 값 타입
        - 클래스 처럼 묶어서 값을 관리하면서 사용하고 싶은데 엔티티간 값이 공유가 되지 않음
        - 객체로 값타입을 관리하면서 객체의 특성인 메서드도 활용 할 수 있다는 장점이 있음
        - 클래스 지정하는 곳에 Embadable 매핑하는곳에 Embaded
    - 컬렉션 값 타입 : collection value type

### 값 타입을 왜 알아야되는데?

- 사용하는 값 타입에 공유가 되고 안되는 성질이 있어 상황에 맞게 개발하기 위해서 알아야한다.

# 엔티티 타입

> @Entity로 저으이하는 객체로 데이터가 변해도 식별자로 추적이 가능하다.
> 

<aside>
💡 엔티티 타입은 각자의 번호가 적힌 옷을 입고 있는 상황이다. 아무리 성형으로 얼굴을 뜯어 고쳐도 옷에 적힌 번호를 보고 아 너 누구구나 하고 알아볼 수 있다.

</aside>

# 값 타입

> 따로 식별자가 없고 그냥 값 그 자체가 식별자로 작용하는 타임.
> 

<aside>
💡 1이면 1이지 100이겠느냐~ 100이면 100이지 1이겠느냐~

</aside>

### 기본값 타입

- String, int …
- 생명 주기를 엔티티에 의존함. 즉 엔티티가 사라지면 같이 사라짐
- 값 타입은 엔티티끼리 절대 공유돼선 안된다.(아래의 경우는 공유가 됨으로 대참사가 일어나는 경우)

```java
Member memberA = new Member("상수");
memberB = memberA;  // 얇은 복사
memberB.SetName("민규");

System.out.println("memberA의 이름 : " + memberA.GetName());   // 민규
System.out.println("memberB의 이름 : " + memberB.GetName());   // 민규
```

- 위의 경우 member는 참조값을 가지는 객체이지만(스택에 참조값이 힙에 실질 객체의 정보가 존재) 자바의 primitive type은 애초에 스택 영역에 찍히기 때문에 공유 될 수가 없음.

### 임베디드 타입

> 같은 유형의 필드를 한 번에 객체로 묶어서 관리하게 해주는 JPA의 값타입.
> 

### 왜 사용하는데?

- 같은 유형의 테이블의 필드값을 자바 사이드에서 묶어서 관리 할 수 있게 되어 코드의 가독성, 재사용성을 높일 수 있다.
- 또한 임베디드 클래스 내에 별도의 메서드를 정의해서 사용 할 수 있다.

### 임베디드 타입 도입 전후 비교하기

- 임베디드 타입 사용 x

```java
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "STARTDATE", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "ENDDATE", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "STREET", nullable = false)
    private String street;

    @Column(name = "ZIPCODE", nullable = false)
    private String zipcode;

}
```

임베디드 타입 사용 

```java
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Embedded
    private WorkPeriod workPeriod;
    
    @Embedded
    private Address address;

}

@Embeddable
@NoArgsConstructor
@Getter
public class Address {

    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "STREET", nullable = false)
    private String street;

    @Column(name = "ZIPCODE", nullable = false)
    private String zipcode;
    
    // 인스턴스 매서드 정의도 가능하다.
    public void toStringAddress() {
        System.out.println(this.city + " " + this.street + " " + this.zipcode);
    }
}

@Embeddable
@NoArgsConstructor
@Getter
public class WorkPeriod {

    @Column(name = "STARTDATE", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "ENDDATE", nullable = false)
    private LocalDateTime endDate;

}
```

- 임베디드 타입을 사용하는 경우 같은 유형의 필드 값을 묶어서 관리 할 수 있게 되면서 Memer 엔티티의 구조가 간단하게 변했음을 확인 할 수 있다.
- 또한 객체이기 때문에 별도의 인스턴스 메서드를 선언해서 사용 할 수 있다. 그러면 필드에 대한 다양한 메서드를 관리하기가 용이해진다.
    
    ```java
    Member member = new Member();
    Address address = new Address("뉴욕", "강서로", "3842");
    member.setAddress(address);
    member.getAddress().toStringAddress();   // "뉴욕 강서로 3842"
    ```
    

### 어떻게 사용하는건데?

1. 임베디드 타입 클래스 생성 → `@Enbeddable` 붙이고 기본 생성자`@NoArgsConstructor` 붙여주기 `@Setter` 설정 하지 말기

```java
@Embeddable
@NoArgsConstructor
@Getter
public class WorkPeriod {

    @Column(name = "STARTDATE", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "ENDDATE", nullable = false)
    private LocalDateTime endDate;

}
```

1. 임베디드 타입 클래스를 엔티티에 사용하기 → 임베더블 타입 사용한다는 뜻으로 `@Embedded` 붙여주고 선언해주기(근본적으로 값 타입이기 때문에 `private String name` 선언하는 것과 같다.)

```java
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Embedded
    private WorkPeriod workPeriod;
    
    @Embedded
    private Address address;

}
```

### 주의점

- 임베디드 타입은 참조값을 가지는 클래스이기 때문에 자칫 잘못하면 서로 다른 엔티티가 동일한 임베디드 클래스를 공유할 수 있다. 따라서 임베디드 타입 설계시 @Setter 설정 자체를 없애 혹시라도 공유가 됐을 경우 해당 클래스의 값을 변경 불가로 만들어 컴파일 에러를 띄워야한다. 아니면 오류도 안 나기 때문에 답이없다.
- 그러면 기존의 엔티티에 임베디드 타입으로 설정된 값을 변경할 수 없잖아?
    - 그냥 해당 엔티티에 새로운 임베디드 타입 객체를 생성해서 다시 넣어주는게 유지보수 차원에서 낫다.