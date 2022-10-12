package com.wejuai.timed.task.service;

import com.wejuai.entity.mongo.CelestialBodyType;
import com.wejuai.entity.mongo.UserPointRecord;
import com.wejuai.entity.mongo.UserPointType;
import com.wejuai.timed.task.repository.mongo.CelestialBodyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author ZM.Wang
 */
@Service
public class CelestialBodyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CelestialBodyRepository celestialBodyRepository;

    private final MongoBaseService mongoBaseService;

    public CelestialBodyService(CelestialBodyRepository celestialBodyRepository, MongoBaseService mongoBaseService) {
        this.celestialBodyRepository = celestialBodyRepository;
        this.mongoBaseService = mongoBaseService;
    }

    @Scheduled(cron = "0 30 1 * * ?")
    public void userPointStatistics() {
        logger.info("start---用户星球点数重新计算开始---start");
        celestialBodyRepository.findByType(CelestialBodyType.USER).forEach(celestialBody -> {
            Criteria criteria = new Criteria().and("user").is(celestialBody.getUser());
            long addPoint = mongoBaseService.getMongoSum(criteria.and("type").in(UserPointType.addTypes()), "point", UserPointRecord.class);
            long subPoint;
            try {
                subPoint = mongoBaseService.getMongoSum(criteria.and("type").in(UserPointType.subTypes()), "point", UserPointRecord.class);
            } catch (Exception e) {
                logger.warn("星球减分统计失败", e);
                subPoint = 0;
            }
            celestialBodyRepository.save(celestialBody.setPoint(addPoint - subPoint));
        });
        logger.info("end---用户星球点数重新计算结束---end");
    }
}
