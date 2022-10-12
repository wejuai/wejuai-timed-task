package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.RewardDemand;
import com.wejuai.entity.mysql.RewardSubmission;
import com.wejuai.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardSubmissionRepository extends JpaRepository<RewardSubmission, String> {

    boolean existsByTextLike(String text);

    long countByRewardDemand(RewardDemand rewardDemand);

    List<RewardSubmission> findByRewardDemand(RewardDemand rewardDemand);
}
