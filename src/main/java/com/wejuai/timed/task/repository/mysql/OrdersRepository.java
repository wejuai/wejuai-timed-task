package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.Orders;
import com.wejuai.timed.task.service.dto.StatisticsOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, String>, JpaSpecificationExecutor<Orders> {

    @Query(nativeQuery = true, value = "SELECT type,COUNT(id) `count`,sum(integral) integral from orders where type in('ARTICLE','SELECTED_REWARD','SYSTEM_ADD','SYSTEM_SUB') and nullify=false and created_at between ?1 and ?2 GROUP BY type")
    List<StatisticsOrders> statisticsOrders(Date start, Date end);

    @Query(nativeQuery = true, value = "select ifnull(sum(integral),0) from orders where user_id=?1 and income=true and nullify=false ")
    long sumIncomeByUser(String userId);

    @Query(nativeQuery = true, value = "select ifnull(sum(integral),0) from orders where user_id=?1 and income=false and nullify=false")
    long sumExpenditureByUser(String userId);
}
