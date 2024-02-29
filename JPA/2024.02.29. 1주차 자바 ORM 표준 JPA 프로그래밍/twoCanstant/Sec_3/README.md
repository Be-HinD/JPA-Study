# 목차

# JPA를 공부하는데 가장 중요한 2가지

## 1. 객체와 관계형 데이터베이스 매핑 그자체

## 2. 영속성 컨텍스트

# 영속성 컨텍스트

> 엔티티를 영구 저장하는 환경(논리적인 공간). EntityManger.persist(entity)
> 

<aside>
💡 영속성 컨텍스트 = EntityManager가 만드는 Java와 DB 사이의 버퍼 영역

</aside>

## 엔티티 매니저 팩토리와 엔티티 매니저

- 클라이언트에서 요청이 왔을때 엔티티 팩토리에서 엔티티 매니저를 생성하고 엔티티 매니저가 DB와 커넥션 풀을 이용해 DB 작업을 수행한다. 엔티티는 엔티티 매니저를 통해서만 영속속 컨택스트에 들어갈 수 있다.
    - 
        
        ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/448dc875-b750-46e6-aa0f-88f8ffe4209a/Untitled.png)
        

## 엔티티의 생명주기

- new/transient(비영속)
- manage(영속) : 영속성 컨텍스트 영영으로 입갤 —> Transaction이 끝나면 쿼리 날라가서 DB에 반영
- detach(준영속) : 영속성 컨텍스트 영역 밖으로 제외 —> Transaction이 끝나는 시점에 영속성 컨텍스트 영역 밖에 존재하기 때문에 해당 엔티티에대한 쿼리문자체가 생성되지 않고 더티체킹도 되지 않음
    
    ```java
    tx.begin()
    
    Member member = em.find(Member.class, "memberA");  // 영속상태
    member.SetName("tkdtn");
    em.detach(member);
    
    tx.commit();
    ```
    
- removed(삭제된 상태) : DB에서도 삭제한다는 뜻

![생명 주기별 EntityManger method](https://prod-files-secure.s3.us-west-2.amazonaws.com/e8dc9b1a-ebd7-4746-8b87-278cc7cc5d56/e8ea6b13-46c5-41dd-b573-970dc1ca6ccd/Untitled.png)

생명 주기별 EntityManger method

- 생명 주기 한 루틴
    
    ```java
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(??)
    EntityManger em = emf.createEntityManger();
    EntityTransaction tx = em.getTransaction();
    
    tx.begin(); // Transaction 시작 구간
    
    Member member = new Member();
    member.SetId(100L);
    member.SetName("상수");
    ---------------------------------------------JPA와 아무 관련 없는 비영속 상태
    em.persist(member);
    ---------------------------------------------영속성 컨택스트 입갤(DB에 저장된 상태 아님)
    em.detach(member);
    ---------------------------------------------영속성 컨택스트에서 아웃
    em.persist(member);
    ---------------------------------------------영속성 컨택스트 입갤(DB에 저장된 상태 아님)
    tx.commit();
    ---------------------------------------------Transaction 종료, 쿼리쳐서 DB에 반영
    em.close();
    ---------------------------------------------영속성 컨택스트 영업 종료(모든 엔티티가 준영속상태로 변경)
    
    ```
    

### 영속성 컨텍스트의 이점

<aside>
💡 버퍼링과 캐싱이 핵심

</aside>

- 1차 캐시 : 같은 Transaction 내에서 필요한 엔티티가 영속성 컨택스트 영역에 있다면 DB 조회까지 할 필요도 없이 그놈 꺼내서 반환해줌—> DB 조회를 안 하기 때문에 빠름
    
    ```java
    tx.begin();
    Member member = new Member();
    member.SetId(100L);
    member.SetNaem("상수");
    em.find(member);
    tx.commit();
    ```
    
- 동일성 보장 —> 같은 Transaction 내에서 동일선을 보장함 == 으로 비교해도 같음
    - Transaction 내에서 똑같은놈 조회해서 가져온게 같다는 판정을 한다는거임
- 트랜잭션을 지원하는 쓰기 지연 —> 엔티티의 정보가 변경 될때마다 쿼리를 쳐서 DB에 반영하는게아닌 쿼리 날릴거 좀 모아놨다가 한방에 처리함 —> DB 조회 횟수가 감소하기 때문에 속도가 빨라짐
- 변경 감지 —> 영속성 컨택스트 내에있는 엔티티는 그냥 알아서 변경감지를 통해 DB에 반영이됨, 참고로 DB에서 꺼내온 객체는 이미 영속성 컨택스트 영역에 포함이 되어있는 상태임 그래서 persist따로 할 필요 없다.
    
    ```java
    tx.begin();
    Member memberA = em.find(Member.class, "memberA");    // memberA는 영속성 컨택스트에 포함
    member.SetName("tkdtn");
    tx.commit();
    ```
    
- 지연 로딩 —> DB에서 데이터를 가져오는 작업을 진짜 그 데이터가 필요한 시점까지 미루는 작업

### 자동으로 flush()가 되는 시점

- `em.flush();` 는 일단 쿼리는 나가고 commit은 안된 시점이다. 현재까지의 DB 변경분이 반영은 됐지만 롤백은 가능한 상태 그리고 영속성 컨택스트의 저장 상태도 유지된다.
- 트랜잭션 커밋 - 플러시 자동 호출
- JPQL 쿼리 실행 - 플러시 날리고 JPQL 실행함

### 준영속 상태로 만드는 방법

- `em.detach(Entity)`;
- `em.clear();` : 영속성 컨택스트 영역을 그냥 싹 비움
- `em.close();` : 영속성 컨택스트 자체가 사라짐