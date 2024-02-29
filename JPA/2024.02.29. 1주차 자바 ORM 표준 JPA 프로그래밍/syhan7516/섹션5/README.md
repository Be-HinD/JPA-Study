## 연관관계

- 연관관계가 필요한 이유
    - 객체를 테이블에 맞추어 모델링
        - 테이블은 참조 대신 외래 키를 사용, 객체는 참조 사용
        - 연관관계가 없는 객체
            - 예) 회원, 팀
        - 저장 : 외래 키 식별자를 직접 다룸
            - 예) 회원의 팀을 식별자로 정의
            - 예) Member.setTeamId(team.getId( ));
        - 조회 : 식별자로 조회
            - 예) findMember = em.find(Member.class, member.getId( ));
            - 예) findTeam = em.find(Team.class, team.getId( ));
    - **테이블과 객체 사이에 간격이 존재해 협력 관계 만드는 것이 불가능 !!**
- 단방향 연관관계
    - 객체지향적인 모델링
        - 객체 연관관계 사용
            - 객체의 참조와 테이블의 외래 키를 매핑
        - 저장 : 연관관계 저장
            - 회원의 팀을 팀 객체로 정의
            - 예) Member.setTeam(team);
        - 조회 : 객체 그래프 탐색으로 조회
            - 예) findMember = em.find(Member.class, member.getId( ));
            - 예) findTeam = findMember.getTeam( );
- 양방향 연관관계와 연관관계의 주인
    - 1 : N 양방향 매핑
        - 1 쪽은 동일
        - 예) 회원 - JoinColumn(name = “TEAM_ID”)
        - 예) private Team team;
        
        ---
        
        - N 쪽은 컬렉션 추가
        - 예) 팀 - OneToMany(mappedBy = “team”)
        - 예) List<Member> members = new ArrayList<Member>( );
        
        ---
        
        - 양방향으로 객체 그래프 탐색 가능
        - 예) findTeam = em.find(Team.class, team.getId( ));
        - 예) MemberSize = findTeam.getMembers( ).size( );
    - 연관관계의 주인과 mappedBy
        - 양방향 매핑 ?
            - 양방향 관계는 서로 다른 단방향 관계 2개
            - 외래 키 하나로 두 테이블의 연관관계 관리 가능
            - 양쪽으로 조인 가능, 둘 중 하나를 주인 설정 필수
        - 양뱡향 매핑 규칙 ?
            - 연관관계 주인만이 외래 키를 수정 가능하며, 나머지는 읽기만 !
            - 주인은 mappedBy 속성 불가, 반대는 속성으로 주인 지정
        - 양방향 매핑 주인 ?
            - 외래 키가 존재하는 곳이 주인
            - N, Many 쪽이 항상 주인
            - 주인에 값을 입력은 필수, 양쪽 값을 입력하는 것을 추천 (편의 메소드 이용)