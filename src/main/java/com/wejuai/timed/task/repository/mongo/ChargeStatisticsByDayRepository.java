package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.statistics.ChargeStatisticsByDay;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface ChargeStatisticsByDayRepository extends MongoRepository<ChargeStatisticsByDay, String> {

    ChargeStatisticsByDay findByDate(LocalDate date);
}
