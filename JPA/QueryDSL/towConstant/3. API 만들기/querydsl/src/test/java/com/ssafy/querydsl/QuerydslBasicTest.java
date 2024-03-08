package com.ssafy.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.querydsl.entity.Member;
import com.ssafy.querydsl.entity.QMember;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.ssafy.querydsl.entity.QMember.*;
import static com.ssafy.querydsl.entity.QTeam.team;

// 테스트가 아닌 스프링 전체 빙과 트리거가 다 동작함 그래서 위에
@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    JPAQueryFactory jpaQueryFactory;;



    @Test
    public void startJPQL() {
        // member1을 찾아라
        Member findByJPQL = em.createQuery("select m from Member m WHERE m.userName = :username", Member.class)
                .setParameter("username", "user1").getSingleResult();
        Assertions.assertThat(findByJPQL.getUserName()).isEqualTo("user1");
    }

    @Test
    public void startQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        QMember m = member;
        Member findMember = jpaQueryFactory
                .select(m)
                .from(m)
                .where(m.userName.eq("user1"))
                .fetchOne();

        Assertions.assertThat(findMember.getUserName()).isEqualTo("user1");
    }

    @Test
    public void search() {
        // 이름이 user1이면서 나이가 10살인 사람을 조회하는 쿼리를 날린다.
        // 만약에 비어있으면 알아서 null을 날린다.
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        Member findMember = jpaQueryFactory
                .selectFrom(member)
                .where(member.userName.eq("user1"),
                        member.age.eq(10))
                .fetchOne();
        Assertions.assertThat(findMember.getUserName().isBlank());
    }

    // 정렬 테스트 sktt1 팬인 사람의 나이순 오름자순, 이름순으로 내림차순 정렬을 수행해라
    @Test
    public void sort() {

        List<Member> findMembers = jpaQueryFactory
                .selectFrom(member)
                .where(member.team.name.eq("SKT"))
                .orderBy(member.age.asc(),
                        member.userName.desc())
                .fetch();

    }

    /**
     * 팀 SKT에 소속된 모든 회원
     */
    @Test
    public void join(){
        List<Member> memberList= jpaQueryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("SKT"))
                .fetch();
    }

    @Test
    public void theta_join(){
        List<Member> memberList= jpaQueryFactory
                .select(member)
                .from(member, team)
                .where(team.name.eq("SKT"))
                .fetch();
    }

}
