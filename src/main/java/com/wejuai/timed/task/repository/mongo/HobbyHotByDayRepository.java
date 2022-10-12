package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.statistics.HobbyHotByDay;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

/**
 * @author ZM.Wang
 */
public interface HobbyHotByDayRepository extends MongoRepository<HobbyHotByDay, String> {

    HobbyHotByDay findByDateAndHobbyId(LocalDate createdAt, String hobbyId);
}
