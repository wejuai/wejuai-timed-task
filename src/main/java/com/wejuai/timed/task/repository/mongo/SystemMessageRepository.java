package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.SystemMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemMessageRepository extends MongoRepository<SystemMessage, String> {
}
