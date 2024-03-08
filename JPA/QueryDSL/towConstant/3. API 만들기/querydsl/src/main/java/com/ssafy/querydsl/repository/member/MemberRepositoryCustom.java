package com.ssafy.querydsl.repository.member;

import com.ssafy.querydsl.dto.request.MemberTeamSearchCondition;
import com.ssafy.querydsl.dto.response.MemberTeamDTO;
import com.ssafy.querydsl.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findAll();
    List<Member> findByUserName(String userName);

    List<MemberTeamDTO> searchByBuilder(MemberTeamSearchCondition memberTeamSearchCondition);

    List<MemberTeamDTO> searchByWhere(MemberTeamSearchCondition memberTeamSearchCondition);
}
