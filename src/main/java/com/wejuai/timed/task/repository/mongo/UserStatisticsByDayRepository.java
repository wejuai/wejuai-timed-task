package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.statistics.UserStatisticsByDay;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface UserStatisticsByDayRepository extends MongoRepository<UserStatisticsByDay, String> {

    UserStatisticsByDay findByDate(LocalDate date);

}
