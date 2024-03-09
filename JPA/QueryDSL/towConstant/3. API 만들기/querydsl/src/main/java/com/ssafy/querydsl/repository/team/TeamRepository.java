package com.ssafy.querydsl.repository.team;

import com.ssafy.querydsl.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
