package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.statistics.HobbyTotalHot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HobbyTotalHotRepository extends MongoRepository<HobbyTotalHot, String> {
}
