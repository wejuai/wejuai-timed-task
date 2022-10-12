package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.CelestialBody;
import com.wejuai.entity.mongo.CelestialBodyType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CelestialBodyRepository extends MongoRepository<CelestialBody, String> {

    List<CelestialBody> findByType(CelestialBodyType type);
}
