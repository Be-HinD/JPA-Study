package com.ssafy.querydsl.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@NoArgsConstructor
public class MemberTeamDTO {

    private Long memberId;
    private String memberName;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection
    public MemberTeamDTO(Long memberId, String memberName, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
