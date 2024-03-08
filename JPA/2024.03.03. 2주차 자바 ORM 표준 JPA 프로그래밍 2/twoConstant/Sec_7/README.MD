# 의문점

### 상속 관계 매핑을 왜 하는거야?

### 상속 관계 매핑 전략 종류

- **조인 전략**(JOINED): 엔티티를 상속관계로 생성 —> DB가 슈퍼, 서브 탑입으로 생성됨
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/9f5db462-089d-48f6-ac75-23058ca42ae0/Untitled.png)
    
    - 장점
        1. 친 DB 구조적인 설계…테이블 정규화…외래 키 참조 무결성 제약조건 활용…
        2. DB상 저장 공간이 효율적임
    - 단점
        1. 조회시 반드시 조인을 사용 할 수 밖에 없음 —> 성능 저하 + 쿼리가 복잡함
        2. 데이터 저장시 슈퍼, 서브 각각 1 쿼리 씩 총 2번의 insert 쿼리가 날라감
- **단일 테이블 전략**(SINGLE_TABLE) : 엔티티를 상속관계로 생성 → 통합 DB 테이블 하나가 생성, JPA 기본으로 세팅된 전략
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/8f7ec020-d950-4bd8-bff1-263c7ca727c0/Untitled.png)
    
    - 장점
        1. 조인이 1도 없음 —> 성능이 좋고 쿼리가 간단함
    - 단점
        1. 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야한다. —> DB 저장공잔의 비효율성
        2. 상황에 따라 테이블이 비효율적으로 너무 커서 조회 성능이 떨어질 수 있다.
- **구현 클래스마다 테이블 전략**(TABLE_PER_CLASS) —> 엔티티를 상속관계로 생성 → 클래스 하나당 “독립된” 테이블 하나가 매핑
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/e2542adf-cd63-4ad8-8cd3-e79846c2c300/Untitled.png)
    
    - 장점
        1. 서브타 타입을 명확하게 구분해서 처리할 때 효과적이다.
        2. not null 제약조건을 사용 할 수 있다.
    - 단점
        1. 여러 자식 테이블을 함께 조회할 때 성능이 느리다.(UNION SQL 필요)
        2. 자식 테이블을 통합해서 쿼리하기가 어렵다.

### 주요 애노테이션

- `@Inheritance(strategy=inheritanceType.xxx)`
    - Join 전략에서 사용, 부모 클래스 역할인 엔티티에 부여하면 된다. DB에 슈퍼 테이블이 생성되고 매핑까지된다.
- `@MappedSuperclass`
    - Table_Per_class 전략에 사용되며 해당 애노테이션이 붙은 클래스는 DB에 매핑되는 엔티티가 아니며 그냥 인터페이스이다.. 그냥 다른 공통 칼럼이 있는 엔티티들을 중복 작업없이 만들고 싶을 때 @MappedSuperclass 가 붙은 인터페이스를 임플받아 사용하면된다.
- `@DiscriminatorColumn(name="DTYPE")`
    - Single_Table 전략에서 사용. 통합 테이블에 매핑될 엔티티에 붙여주며 상속 관계에 있는 엔티티가 칼럼으로 들어올꺼야 라는걸 선언하는 역할
- `@DiscriminatorValue("Album")`
    - 통합 테이블에 칼럼으로 들어갈 상속 관계에 있는 자식 클래스에 붙여준다.
- 정리
    - 요약하자면, **`@Inheritance`**는 실제 엔티티 간의 상속 구조를 데이터베이스에 매핑할 때 사용되며, **`@MappedSuperclass`**는 공통 매핑 정보(필드와 메서드)를 상속할 때 사용되지만, 데이터베이스 테이블과는 직접적으로 매핑되지 않음.

### 슈퍼, 서브 테이블 구조 매핑하기

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/6012df84-cc76-4abd-bf38-537de2989973/Untitled.png)

```java
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
    
    // 기타 공통 속성 및 메서드
}

@Entity
public class Album extends Item {
    private String artist;
    // 앨범에 관련된 추가 속성 및 메서드
}

@Entity
public class Movie extends Item {
    private String director;
    private String actor;
    // 영화에 관련된 추가 속성 및 메서드
}

@Entity
public class Book extends Item {
    private String author;
    private String isbn;
    // 책에 관련된 추가 속성 및 메서드
}

```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/61d00dfb-37e4-404d-959f-aefe03743bc7/Untitled.png)

```java
import jakarta.persistence.*;

@MappedSuperclass
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
}

@Entity
public class Album extends Item {
    private String artist;
}

@Entity
public class Movie extends Item {
    private String director;
    private String actor;
}

@Entity
public class Book extends Item {
    private String author;
    private String isbn;
}
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/1e9e8251-b810-486b-9581-5464ada6206b/Untitled.png)

```java
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;

    // standard getters and setters
}

@Entity
@DiscriminatorValue("Album")
public class Album extends Item {
    private String artist;

    // standard getters and setters
}

@Entity
@DiscriminatorValue("Movie")
public class Movie extends Item {
    private String director;
    private String actor;

    // standard getters and setters
}

@Entity
@DiscriminatorValue("Book")
public class Book extends Item {
    private String author;
    private String isbn;

    // standard getters and setters
}

```

### Item 엔티티를 추상 클래스로 할 지 그냥 클래스로 할지 판단하기

- Item 단독으로 CURD 할 일이 있다 —> 클래스로 정의
- 그럴일이 없다 —> 추상 클래스로 정의