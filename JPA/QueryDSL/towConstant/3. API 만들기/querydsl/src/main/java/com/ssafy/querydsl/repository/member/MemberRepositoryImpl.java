package com.ssafy.querydsl.repository.member;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.querydsl.dto.request.MemberTeamSearchCondition;
import com.ssafy.querydsl.dto.response.MemberTeamDTO;
import com.ssafy.querydsl.dto.response.QMemberTeamDTO;
import com.ssafy.querydsl.entity.Member;
import com.ssafy.querydsl.entity.QMember;
import com.ssafy.querydsl.entity.QTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.ssafy.querydsl.entity.QMember.member;


@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Member> findAll() {
        return jpaQueryFactory
                    .selectFrom(member)
                    .fetch();
    }

    @Override
    public List<Member> findByUserName(String userName) {
        return jpaQueryFactory
                .selectFrom(member)
                .where(member.userName.eq(userName))
                .fetch();
    }

    @Override
    public List<MemberTeamDTO> searchByBuilder(MemberTeamSearchCondition memberTeamSearchCondition) {

        BooleanBuilder builder = new BooleanBuilder();
        // userName이 null이면 상관없는데 ""으로도 들어오는 경우가 많아서
        // StringUtils.hasText() 를 사용한다.
        if (StringUtils.hasText(memberTeamSearchCondition.getUserName())){
            builder.and(member.userName.eq(memberTeamSearchCondition.getUserName()));
        }
        if (StringUtils.hasText(memberTeamSearchCondition.getTeamName())){
            builder.and(member.team.name.eq(memberTeamSearchCondition.getTeamName()));
        }
        if (memberTeamSearchCondition.getAgeGoe() != null) {
            builder.and(member.age.goe(memberTeamSearchCondition.getAgeGoe()));
        }
        if (memberTeamSearchCondition.getAgeLoe() != null) {
            builder.and(member.age.loe(memberTeamSearchCondition.getAgeLoe()));
        }

        return jpaQueryFactory
                .select(new QMemberTeamDTO(
                        member.id.as("memberId"),
                        member.userName.as("memberName"),
                        member.age,
                        member.team.id.as("teamId"),
                        member.team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                    // 빌더 꼽으면 끝
                    builder
                )
                .fetch();
    }

    @Override
    public List<MemberTeamDTO> searchByWhere(MemberTeamSearchCondition memberTeamSearchCondition) {
        return jpaQueryFactory
                .select(new QMemberTeamDTO(
                        member.id.as("memberId"),
                        member.userName.as("memberName"),
                        member.age,
                        member.team.id.as("teamId"),
                        member.team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        userNameEq(memberTeamSearchCondition.getUserName()),
                        teamNameEq(memberTeamSearchCondition.getTeamName()),
                        ageGoe(memberTeamSearchCondition.getAgeGoe()),
                        ageLoe(memberTeamSearchCondition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression userNameEq(String userName) {
        return userName != null? member.userName.eq(userName) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return teamName != null? member.team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null? member.age.loe(ageLoe) : null;
    }


}
