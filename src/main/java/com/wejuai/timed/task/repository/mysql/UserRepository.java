package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface UserRepository extends JpaRepository<User, String> {

    long countByCreatedAtBetween(Date start, Date end);
}
