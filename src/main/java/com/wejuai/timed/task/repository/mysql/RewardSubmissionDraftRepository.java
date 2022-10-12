package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.RewardSubmissionDraft;
import com.wejuai.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardSubmissionDraftRepository extends JpaRepository<RewardSubmissionDraft, String> {

    boolean existsByTextLike(String text);
}
