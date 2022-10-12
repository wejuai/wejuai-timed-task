package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.ArticleDraft;
import com.wejuai.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleDraftRepository extends JpaRepository<ArticleDraft, String> {

    boolean existsByTextLike(String text);
}
