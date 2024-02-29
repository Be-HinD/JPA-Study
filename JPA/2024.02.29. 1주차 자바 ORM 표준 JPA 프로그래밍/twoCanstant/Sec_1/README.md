# 1. JPA소개

# 목차

# 수업 전 나의 의문

## JPA는 뭐고 하이버네이트는 뭐야?

> Java 진영의 ORM 표준 기술(인터페이스). Hibarnate는 JPA의 구현체
> 

<aside>
💡 JPA는 개발자가 자바 객체 조작만으로 DB에 대한 CRUD 조작을 가능하게 했다.

</aside>

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/21ea046b-715b-42b0-90a0-f2452fd0bcac/Untitled.png)

## 기존의 어떤 문제를 JPA가 해결한거야?

### JPA 이전 개발의 문제점

- CRUD 과정 자체가 SQL, JAVA를 넘나들면서 작업하기 때문에 번거롭다는 단점
    - 자바 객체 —> SQL로 원하는 row추출 —> 잡바객체로 변환하는 과정을 **개발자가 직접 수행**
- 객체와 관계형 DB의 근본적 차이가 존재한다.
    - 상속 ←→ 슈퍼 서브 타입
    - 참조 ←→ 외래 키
    - 데이터 타입
    - 데이터 식별 방법…
- 계층 분할이 어렵다.

### 왜 JPA를 사용해야하는가?

- Java 객체 조작만으로 DB에 대한 CRUD가 가능하다. Java — JPA — SQL
- SQL 중심적인 개발에서 자바 객체 중심의 개발이 가능하다.
- 성능이 좋다.
    - 중간 계층(JPA)가 있음으로 매 요청마다 쿼리를 치는게 아닌 일정부분 모았다가 쿼리를 쳐 시간이 많이 드는 DB 접근 횟수를 줄인다.
    - 이미 JAP에 있는 데이터에대해선 DB에 접근하지 않고 바로 반환해 줄 수 있다.(like 캐시처럼 활용가능)
- 표준기술이다.

# SQL Mapper를 활용한 개발  VS JPA를 활용한 개발

### SQL Mapper

- 장점
    - SQL을 직접 작성함으로써 복잡한 쿼리나 데이터베이스의 기능을 최대한 활용 할 수 있다.
- 단점
    - SQL에 대한 별도의 지식이 있어야하며 데이터간 호환성을 위한 개발자의 추가적인 작업이 필요하다.
    - 그냥 다 수동이다. 사실상 java와 sql 그냥 남남이고 java 사이드나 sql 사이드에서 변화가 일어났을때 각각 변화에 대한 업데이트를 수동을 해줘야됨.

### JPA(ORM)를 활용한 개발

- 장점
    - 매핑만 잘 하면 그냥 자바 객체를 다루는 지식만 있으면 DB 조작이 가능하다.
    - 객체의 특성을 활용해 작업을 단순화 할 수 있다..
        - 객체 그래프 탐색
    - 버퍼 영역이 생김으로 성능 최적화를 할 수 있다.
        - 지연로딩, 즉시로등, 캐싱과 같은 작업을 통해 DB 접근을 최소화 할 수 있다.
- 단점
    - ORM을 끼고하기 때문에 디테일한 쿼리 처리가 힘들다.
    - 자동으로 이식 된 쿼리가 나가기 때문에 쿼리 단 건에 대한 최적화가 힘들다.
    - 개념 학습이 어렵다.

# jpa 이전과 이후를 객체 그래프 탐색과 연관지어 설명하시오!

- **`User`** 객체가 여러 **`Order`** 객체를 가지고 있을 때 특정 user의 모든 order를 조회할때
    - 낭만의 시대 : user_id로 user 객체 가져옴, user_id로 해당 orders 가져옴(이와중에 SQL문도 각각 따로 작성해야됨) + 테이블과 엔티티 연동이 안되어있기 때문에 해당 user에 orders 업데이트까지….
        
        ```java
            public User getUserWithOrders(Long userId) {
                // User 조회
                User user = userMapper.selectUserById(userId);
                // 해당 User의 Order들 조회
                List<Order> orders = orderMapper.selectOrdersByUserId(userId);
                user.setOrders(orders); // User 객체에 Order 리스트 설정(테이블과 객체가 연동x)
                return user;
            }
        ```
        
    - JPA 활용 : 그냥 user_id로 user 조회함. user.getOrder()로 바로 orders 가져옴 그리고 포문돌림. 이와중에 SQL 쓸 필요도 없음. 객체 관계를 자동으로 업데이트 해줘서 별도의 업데이트도 필요없음.
        
        ```java
        User user = entityManager.find(User.class, userId); // User 조회
        List<Order> orders = user.getOrders(); // 연관된 Order 객체들을 자연스럽게 탐색
        for(Order order : orders) {
            System.out.println(order.getOrderDetails());
        }
        ```
        

### JPA는 어떻게 동작해?

### DB 저장

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/500d6b24-8399-4a40-af61-a73e1a702922/Untitled.png)

1. MemberDAO : Data Access Object로 repository 구현 클래스를 의미한다.
    
    ```java
    public interface MemberRepository extends JpaRepository<Member, Long> {
    }
    ```
    
2. JPA가 전달받은 객체(Entity)를 분석하고 적절한 쿼리문 작성함.
3. JDBC API(Java DataBase Connectivity API)가 때가 되면(Transactional) DB에 연결하고 JPA가 작성한 쿼리를 실행하고 실행 결과를 받아온다.

### DB조회

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/a7bd8d78-8915-47c3-b2fd-82d98e9045e0/Untitled.png)

1. DAO를 통해 JPA에 필요정보를 전달
2. JPA는 쿼리문을 작성
3. JDBC API는 때가 되면 DB에 연결하고 쿼리문을 실행하여 raw 를 반환받음
4. JPA가 raw를 java 객체로 바꿔 MemberDAO에 전달.