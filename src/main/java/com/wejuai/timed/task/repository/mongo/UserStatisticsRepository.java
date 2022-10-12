package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.statistics.UserStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserStatisticsRepository extends MongoRepository<UserStatistics, String> {
}
