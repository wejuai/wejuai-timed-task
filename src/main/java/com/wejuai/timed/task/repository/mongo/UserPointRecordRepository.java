package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.UserPointRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPointRecordRepository extends MongoRepository<UserPointRecord, String> {
}
