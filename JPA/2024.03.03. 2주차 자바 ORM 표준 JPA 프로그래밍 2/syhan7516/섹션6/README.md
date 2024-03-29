## 다중성

- JPA의 모든 애노테이션은 DB 맵핑을 위해 만들어졌기 때문에 DB 관점에서 고민 필요
- 다대일, 일대다, 일대일, 다대다 존재
- 다대일
    - @ManyToOne
    - 다대일 단방향
        - 예) 객체 : 멤버 → 팀
        - 예) 테이블 : 멤버 → 팀
        - 멤버 엔티티에 팀이 존재
    - 다대일 양방향
        - 예) 객체 : 멤버 ↔ 팀
        - 예) 테이블 : 멤버 → 팀
        - 멤버 엔티티에 팀이 존재, 팀에 멤버 리스트 존재
        - **테이블은 외래 키 하나로 모두 참조 가능하여 영향 X**
- 일대다
    - @OneToMany
    - 일대다 단방향
        - 예) 객체 : 팀 → 멤버
        - 예) 테이블 : 멤버 → 팀
        - 팀에 멤버 리스트 존재
        - **객체 팀 업데이트 경우 멤버 테이블이 업데이트가 되어 불필요한 업데이트 쿼리 발생**
        - → **테이블이 많아지면 헷갈리며, 운영이 어려워지기 때문에 권장 X**
        - → **@JoinColumn 사용 필수이며, 어길 경우 자동으로 중간 테이블이 생성**
        - → **참조에 대한 손해를 조금 보더라도 다대일 양방향 매핑 사용 권장 !**
    - 일대다 양방향
        - 예) 객체 : 팀 ↔ 멤버
        - 예) 테이블 : 멤버 → 팀
        - **멤버가 주인이 아니기 때문에 주인처럼 만들어주되, 쓰기 & 업데이트 막기 작업 필요**
        - → **@JoinColumn(insertable=false, updatable=false)**
        - → **공식적으로 존재 X**
        - → **이상한 거 사용말고, 다대일 양방향 매핑 권장 !**
- 일대일
    - @OneToOne
    - 외래 키 DB 유일 제약 조건 추가 필요
    - 일대일 관계는 반대도 일대일이기 때문에 주, 대상 테이블에서 외래 키 선택 가능
        - 주 테이블에 외래 키 단방향
            - 예) 객체 : 멤버 → 사물함
            - 예) 테이블 : 멤버 → 사물함
            - **다대일 단방향과 유사**
        - 주 테이블에 외래 키 양방향
            - 예) 객체 : 멤버 ↔ 사물함
            - 예) 테이블 : 멤버 → 사물함
            - **다대일 양방향처럼 외래 키가 있는 곳이 주인**
        - 대상 테이블에 외래 키 단방향
            - 예) 객체 : 멤버 → 사물함
            - 예) 테이블 : 사물함 → 멤버
            - **지원도 안되고, 그냥 불가능 ! (양방향은 지원)**
        - 대상 테이블에 외래 키 양방향
            - 예) 객체 : 멤버 ↔ 사물함
            - 예) 테이블 : 사물함 → 멤버
            - **사물함 값이 있는지 없는지 알려면 멤버 테이블을 조인하여 확인해야 조회가 가능 !**
            - **주 테이블에 외래 키 양방향 매핑 방법과 동일**
    - 주인이 어디든 각각의 장, 단점 존재
        - 주 테이블 외래 키
            - 장점 : JPA 매핑 편리, 주 테이블만 조회해도 대상 테이블에 데이터 확인 가능
            - 단점 : 값이 없으면 외래 키에 Null 허용
        - 대상 테이블 외래 키
            - 장점 : 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 구조 유지
            - 단점 : 단방향 지원 X (양방향 가능), 지연 로딩 의미 X
- 다대다
    - @ManyToMany
    - **객체는 가능, 관계형 데이터베이스는 정규화된 테이블 2개를 다대다 관계 표현 불가능 !**
    - → **자동으로 테이블을 만들어주지만, 필드 추가 불가능하여 실무 권장 X**
    - → **연결 테이블을 추가해서 엔티티티로 승격하여 일대다, 다대일 관계로 해소**
    - → **@ManyToMany ⇒ @OneToMany, @ManyToOne**

## 단방향, 양방향

- 테이블
    - 외래 키 하나로 양쪽 조인이 가능하기 때문에 방향 개념이 없음
- 객체
    - 참조용 필드가 있는 쪽으로만 참고 가능
    - 단방향 : 한쪽만 참조
    - 양방향 : 양쪽으로 서로 단방향 참조

## 연관관계의 주인

- 연관관계 주인의 필요성
    - 테이블과 다르게 객체의 양방향 관계는 참조가 2군데 존재
    - 테이블의 외래 키를 관리할 곳 지정 필요
    - 주인 : 외래 키를 관리
    - 반대 : 외래 키에 영향을 주지 않으며, 단순 조회 가능 (업데이트 불가)