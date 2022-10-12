package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.statistics.OrdersStatisticsByDay;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface OrdersStatisticsByDayRepository extends MongoRepository<OrdersStatisticsByDay, String> {

    OrdersStatisticsByDay findByDate(LocalDate date);
}
