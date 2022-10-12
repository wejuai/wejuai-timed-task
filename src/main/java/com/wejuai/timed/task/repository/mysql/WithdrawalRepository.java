package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.ApplyStatus;
import com.wejuai.entity.mysql.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, String>, JpaSpecificationExecutor {

    @Query(nativeQuery = true, value = "select ifnull(sum(integral),0) integral from withdrawal where status='PASS' and created_at between ?1 and ?2")
    long sumIntegralByCreatedAtAndStatus(Date start, Date end);

    long countByStatusAndCreatedAtBetween(ApplyStatus status, Date start, Date end);
}
