# 4. 엔티티 매핑

# 객체와 테이블 매핑

## @Entity

- 역할 : JPA에게 이 클래스는 테이블과 매핑되는 니가 관리해야하는 클래스라걸 알려주는 역할
- 속성 정리
    - name :  JPA에서 사용할 엔티티 이름을 지정 할 수 있다.  기본값은 클래스 이름
- 주의사항
    - 기본 생성자 필수
    - final 클래스, enum, interface, inner 클래스는 사용하지 못한다.
    - 자징할 필드에 final 사용하지 않는다.

## @Table

- 역할 : 엔티티와 매핑할 테이블을 지정 할 수 있다.
- 매개변수 역할
- 주의사항

# 여러가지 매핑 어노테이션

## @Column

- 역할 : 필드와 컬럼을 매핑하고 필드의 제약 설정 할 수 있다.
- 매개변수 역할
    - name : 해당 필드와 매핑할 컬럼 지정
    - nullable : 널 가능 유무 결정
    - updatable : 해당 필드 변경 가능 여부 설정,
- 도표
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/29805ef4-ad1d-4072-b222-af2b7ffe45de/Untitled.png)
    

## @Enumerated

- 역할 : 자바의 enum 타입을 테이블이 인식 할 수 있는 타입으로 변경
- 주의사항
    - 절대 EnumType.ORDINAL 사용하지 말자 : enum 순서를 DB에 저장함
    - EnumType.STRING 을 사용해라: enum 이름을 DB에 저장함
- Enum을 굳이 사용하는이유는 일종의 제약조건을 걸어준것

GPT

### **예시 코드**

아래는 **`enum`** 타입을 사용하고, **`@Enumerated(EnumType.STRING)`**을 사용하여 데이터베이스에 매핑하는 예시입니다.

### Enum 타입 정의

```java
javaCopy code
public enum Status {
    ACTIVE,
    INACTIVE,
    DELETED;
}

```

### Entity 클래스에서 Enum 사용

```java
javaCopy code
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING) // EnumType.STRING 사용
    private Status status;

    // 생성자, 게터, 세터 생략
}

```

이 예시에서 **`User`** 엔티티의 **`status`** 필드는 **`Status`** **`enum`** 타입을 사용합니다. **`@Enumerated(EnumType.STRING)`** 어노테이션을 통해 **`status`** 필드의 값이 데이터베이스에 저장될 때 **`enum`**의 이름(**`"ACTIVE"`**, **`"INACTIVE"`**, **`"DELETED"`**)으로 저장되도록 설정합니다.

### **주의사항**

- **`EnumType.ORDINAL` 사용하지 않기**: **`EnumType.ORDINAL`**을 사용하면 **`enum`** 값의 순서가 데이터베이스에 정수 값으로 저장됩니다. 이 경우, **`enum`** 정의에 변경사항이 생길 때 데이터베이스의 값과 **`enum`** 값 사이의 매핑이 깨질 수 있습니다.
- **`EnumType.STRING` 사용하기**: **`EnumType.STRING`**을 사용하면 **`enum`**의 이름이 문자열로 데이터베이스에 저장되어, **`enum`** 정의가 변경되더라도 데이터의 일관성을 유지할 수 있습니다.

이 방식을 사용하면 **`enum`** 타입의 안전한 데이터베이스 저장 및 조회가 가능하며, 애플리케이션의 유지 보수성을 높일 수 있습니다.

https://lh3.googleusercontent.com/a/AEdFTp58_V3A04LwpYxFzb3ck9Ki1TkGZ6TyaoW87WiY=s96-c

**You**

위의 경우에 DB에 Status 필드 하나가 생기고 그 값으로는 ACTIVE, INACTIVE, DELETED만 온다는거지? DB 쿼리문도 알려줘

ChatGPT

**ChatGPT**

네, 맞습니다. 위의 경우에 **`User`** 테이블에 **`status`** 필드가 생성되며, 그 값으로는 **`ACTIVE`**, **`INACTIVE`**, **`DELETED`**만 저장될 수 있습니다. 이는 **`@Enumerated(EnumType.STRING)`** 어노테이션을 사용했기 때문에, **`enum`**의 이름이 문자열로 데이터베이스에 저장되는 방식입니다.

## @Temporal

- 역할 : 자바 객체의 시간 데이터 타입을 SQL 용 시간 데이터 타입으로 변경해줌
- 매개변수 역할
    
    value : 시간 형태를 지정해줌
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/eddf846e-a5ff-4830-b917-427d9c9567cc/Untitled.png)
    

MySql에 존재하는 시간 타입

### **1. DATE**

- **설명**: 날짜만을 저장합니다. 일반적으로 연, 월, 일 정보를 포함합니다.
- **예시**: **`2023-03-01`**

### **2. TIME**

- **설명**: 시간만을 저장합니다. 시, 분, 초 정보를 포함하며, 때에 따라 밀리초를 포함할 수도 있습니다.
- **예시**: **`15:45:30`**

### **3. TIMESTAMP**

- **설명**: 날짜와 시간을 모두 저장합니다. 연, 월, 일, 시, 분, 초 정보를 포함하며, 밀리초를 포함할 수도 있습니다. **`TIMESTAMP`**는 특정 시점을 정밀하게 표현하기 위해 사용됩니다.
- **예시**: **`2023-03-01 15:45:30`**

### **4. DATETIME**

- **설명**: **`TIMESTAMP`**와 유사하게 날짜와 시간을 모두 저장합니다. **`DATETIME`**과 **`TIMESTAMP`**의 주된 차이점은 데이터베이스 제품에 따라 **`TIMESTAMP`**가 UTC로 변환되어 저장되고 조회 시 시스템의 시간대로 변환될 수 있다는 점입니다. 반면, **`DATETIME`**은 변환 없이 저장된 값을 그대로 유지합니다.
- **예시**: **`2023-03-01 15:45:30`**
- 참고
    - LocalDate, LocalDateTime을 사용할때는 생략가능하다.(최신 하이버네이트 지원)

## @Lob

- 역할 : 그 긴 거 저장할 때 사용(String, byte[])
- GPT
    
    **`@Lob`** (Large Object) 어노테이션은 JPA에서 매우 큰 데이터를 저장할 때 사용됩니다. **`@Lob`**을 사용하면 데이터베이스의 **`BLOB`** (Binary Large Object) 또는 **`CLOB`** (Character Large Object) 타입으로 매핑하여 대용량의 바이너리 데이터나 긴 텍스트를 저장할 수 있습니다. 바이너리 데이터의 예로는 이미지, 비디오 파일 등이 있으며, 긴 텍스트의 예로는 긴 문서나 코드 스니펫 등이 있습니다.
    
    ### **`@Lob` 사용 예시**
    
    ### 바이너리 데이터 저장 예시 (BLOB)
    
    ```java
    javaCopy code
    import javax.persistence.Entity;
    import javax.persistence.GeneratedValue;
    import javax.persistence.GenerationType;
    import javax.persistence.Id;
    import javax.persistence.Lob;
    
    @Entity
    public class Document {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String name;
    
        @Lob
        private byte[] content; // 대용량 바이너리 데이터 저장
    
        // 생성자, 게터, 세터 생략
    }
    
    ```
    
    위의 예시에서 **`content`** 필드는 **`@Lob`** 어노테이션을 사용하여 대용량 바이너리 데이터를 저장하는 데 사용됩니다. 이 필드는 데이터베이스의 **`BLOB`** 타입으로 매핑됩니다.
    
    ### 텍스트 데이터 저장 예시 (CLOB)
    
    ```java
    javaCopy code
    import javax.persistence.Entity;
    import javax.persistence.GeneratedValue;
    import javax.persistence.GenerationType;
    import javax.persistence.Id;
    import javax.persistence.Lob;
    
    @Entity
    public class Article {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String title;
    
        @Lob
        private String content; // 대용량 텍스트 데이터 저장
    
        // 생성자, 게터, 세터 생략
    }
    
    ```
    
    위의 예시에서 **`content`** 필드는 **`@Lob`** 어노테이션을 사용하여 대용량 텍스트 데이터를 저장하는 데 사용됩니다. 이 필드는 데이터베이스의 **`CLOB`** 타입으로 매핑됩니다.
    
    ### **주의사항**
    
    - **`@Lob`**을 사용할 때는 해당 필드의 데이터 크기가 매우 클 수 있으므로, 데이터를 로드하거나 저장할 때 성능에 영향을 줄 수 있습니다. 필요한 경우에만 **`@Lob`**을 사용하고, 데이터 접근 전략을 신중히 고려해야 합니다.
    - 데이터베이스와 JPA 구현체에 따라 **`@Lob`** 매핑의 정확한 동작이 다를 수 있으므로, 사용 전에 관련 문서를 참조하는 것이 좋습니다.
    
    **`@Lob`** 어노테이션을 사용하면 JPA를 통해 대용량 데이터를 쉽게 다룰 수 있으며, 애플리케이션의 다양한 요구 사항을 충족시킬 수 있습니다.
    

## @Transient

- 역할 : DB에 저장할 필요는 없고 있으면 좋은 필드 설정할때. 그냥 메모리상에만 존재함

GPT

**`@Transient`** 어노테이션은 JPA(Java Persistence API)에서 사용되며, 특정 필드가 영속화 대상에서 제외되어야 할 때 사용됩니다. 즉, 해당 필드를 데이터베이스 테이블의 컬럼과 매핑하지 않겠다는 것을 나타냅니다. **`@Transient`**로 표시된 필드는 데이터베이스에 저장되지 않고, 조회될 때도 무시됩니다.

### **`@Transient` 사용 예시**

### 계산된 속성

클래스에 데이터베이스에 저장할 필요는 없지만, 런타임에 계산되어 사용되는 속성이 있을 경우 **`@Transient`**를 사용할 수 있습니다. 예를 들어, **`firstName`**과 **`lastName`** 필드를 합쳐 **`fullName`**을 생성하는 경우입니다.

```java
javaCopy code
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class User {

    @Id
    private Long id;
    private String firstName;
    private String lastName;

    @Transient
    private String fullName; // 데이터베이스에 저장할 필요 없는 계산된 속성

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // 생성자, 게터, 세터 생략
}

```

### 임시 데이터 보관

임시로 데이터를 보관해야 하지만 데이터베이스에는 저장할 필요가 없는 경우에 **`@Transient`**를 사용할 수 있습니다. 예를 들어, 사용자가 입력한 값을 임시로 검증하기 위한 필드 등이 이에 해당합니다.

### **`@Transient`가 필요한 이유**

1. **데이터베이스 독립성**: 모델의 모든 필드가 데이터베이스에 저장될 필요는 없습니다. 일부 필드는 애플리케이션 내부에서만 유용하며, 데이터베이스에 저장되면 오히려 스키마를 복잡하게 만들 수 있습니다.
2. **성능 최적화**: 데이터베이스에 불필요한 데이터를 저장하지 않음으로써, 저장 공간을 절약하고 성능을 최적화할 수 있습니다.
3. **애플리케이션 로직 지원**: 특정 비즈니스 로직을 지원하기 위해 임시 데이터를 저장하거나 계산된 속성을 관리해야 할 때 **`@Transient`** 어노테이션이 유용합니다.

결론적으로, **`@Transient`** 어노테이션은 JPA 엔티티의 일부 데이터를 데이터베이스와의 매핑에서 제외하고자 할 때 필요하며, 이를 통해 애플리케이션의 요구사항에 맞춰 더 유연하게 모델을 설계할 수 있습니다.

도표

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/64eeaad7-711a-466f-a2a4-bda16bfb202e/Untitled.png)

# 기본 키 매핑

## @Id

- 역할
- 매개변수 역할
- 주의사항

# 연관관계 매핑

## @ManyToOne

- 역할
- 매개변수 역할
    - 로딩 시점
    - 매핑
    - 영속성 전이
- 주의사항

## @JoinColumn

- 역할
- 매개변수 역할
- 주의사항

# 데이터베이스 스키마 자동 생성

### create

- 기존 테이블 삭제 후 새로 테이블 생성

### create-drop

- 프로그램 종료시점에 테이블 삭제. 프로그램 실행시 테이블 생성

### update

- 테이블에 변경이 있을 경우 반영해 테이블 생성

### validate

- 엔티티와 테이블이 정상 매핑되었는지 확인

GPT 정리 보기

### **validate 모드의 작동 방식**

애플리케이션 시작 시, JPA 구현체(예: Hibernate)는 각 엔티티 클래스에 정의된 매핑 정보(필드, 테이블 이름, 컬럼 이름, 관계 등)를 데이터베이스 스키마와 비교합니다. 만약 엔티티 클래스의 매핑 정보와 실제 데이터베이스 스키마 사이에 불일치가 발견되면, 애플리케이션은 오류 메시지와 함께 시작되지 않습니다.

### **구체적인 사례**

예를 들어, 다음과 같은 엔티티 클래스가 있다고 가정해봅시다.

```java
javaCopy code
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "age")
    private int age;
}

```

이 엔티티 클래스는 **`User`**라는 이름의 테이블에 매핑되며, **`id`**, **`username`**, **`age`**라는 컬럼을 가집니다.

이제 데이터베이스 스키마를 확인해봅시다. 올바른 스키마는 다음과 같아야 합니다.

```sql
sqlCopy code
CREATE TABLE User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    age INT
);

```

### 호환성 검사 실패 사례

만약 데이터베이스에 **`User`** 테이블이 다음과 같이 정의되어 있다면,

```sql
sqlCopy code
CREATE TABLE User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255), -- 엔티티와 컬럼 이름이 다릅니다.
    age INT
);

```

여기서 **`username`** 필드는 데이터베이스의 **`user_name`** 컬럼과 매핑되어야 하지만, 실제 데이터베이스 스키마에서는 컬럼 이름이 **`user_name`**으로 되어 있어 불일치합니다. 이 경우, **`validate`** 모드는 이 불일치를 감지하고 오류를 발생시킵니다. 결과적으로 애플리케이션은 시작되지 않고, 오류 메시지에서는 **`username`** 필드와 **`user_name`** 컬럼 사이의 매핑 문제를 지적하게 됩니다.

### **결론**

**`validate`** 모드는 개발 과정에서 엔티티 클래스와 데이터베이스 스키마 사이의 일관성을 유지하는 데 유용합니다. 이 모드를 사용하면, 애플리케이션 배포 전에 매핑 오류를 사전에 감지하고 수정할 수 있습니다. 그러나 실제 운영 환경에서는 스키마 변경이 필요한 경우가 많으므로, **`update`** 모드나 다른 데이터베이스 마이그레이션 도구를 사용하는 것이 일반적입니다.

### none