package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.Article;
import com.wejuai.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String> {

    List<Article> findByCreatedAtBefore(Date date);

    boolean existsByTextLike(String text);
}
