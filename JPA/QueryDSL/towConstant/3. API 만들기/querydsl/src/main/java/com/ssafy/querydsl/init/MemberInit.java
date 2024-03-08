package com.ssafy.querydsl.init;

import com.ssafy.querydsl.entity.Member;
import com.ssafy.querydsl.entity.Team;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")    // 실행 환경이 local인 경우 실행하겠다.
@Component    // 빈 주입이느 클래스별로 필요한 작업이다..
@RequiredArgsConstructor
public class MemberInit {
    private final InitMemberService initMemberService;

    @PostConstruct
    public void init(){
        initMemberService.inIt();
    }

    @Component
    static class InitMemberService{

        @PersistenceContext
        EntityManager em;

        @Transactional
        public void inIt() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Member member = new Member("user" + i, i);
                if(i % 2 == 0){
                    member.setTeam(teamA);
                } else {
                    member.setTeam(teamB);
                }
                em.persist(member);
            }
        }
    }
}
