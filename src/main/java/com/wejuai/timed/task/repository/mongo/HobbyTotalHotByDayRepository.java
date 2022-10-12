package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.statistics.HobbyTotalHotByDay;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface HobbyTotalHotByDayRepository extends MongoRepository<HobbyTotalHotByDay, String> {
    HobbyTotalHotByDay findByDate(LocalDate localDate);
}
