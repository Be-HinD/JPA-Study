package com.ssafy.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(of = {"id", "userName", "age"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "userName")
    private String userName;

    @Column(name = "age")
    private int age;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    public Member(String userName) {
        this(userName, 0);
    }
    public Member(String userName, int age) {
        this(userName, age, null);
    }
    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }
    public void changeTeam(Team team) {
        this.team = team;
        team.getMemberList().add(this);
    }

}
