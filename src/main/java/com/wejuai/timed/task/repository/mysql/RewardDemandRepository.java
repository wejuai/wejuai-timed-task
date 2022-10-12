package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.RewardDemand;
import com.wejuai.entity.mysql.RewardDemandStatus;
import com.wejuai.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RewardDemandRepository extends JpaRepository<RewardDemand, String> {

    List<RewardDemand> findByDeadlineAndStatus(LocalDate localDate, RewardDemandStatus status);

    boolean existsByTextLike(String text);
}
