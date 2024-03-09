package com.ssafy.querydsl.comtroller;

import com.ssafy.querydsl.dto.request.MemberTeamSearchCondition;
import com.ssafy.querydsl.dto.response.MemberTeamDTO;
import com.ssafy.querydsl.repository.member.MemberRepository;
import com.ssafy.querydsl.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @GetMapping("v1/members")
    public List<MemberTeamDTO> searchMemberV1(MemberTeamSearchCondition memberTeamSearchCondition) {
        return memberRepository.searchByWhere(memberTeamSearchCondition);
    }

}

