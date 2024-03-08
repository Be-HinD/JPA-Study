package com.ssafy.querydsl.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberTeamSearchCondition {
// 회원명, 팀명, 나이(ageGoe, ageLoe)

    private String userName;
    private String teamName;
    private Integer ageGoe;    // Integer 사용이유는 값이 널로 들어올 수도 있어서
    private Integer ageLoe;

}
