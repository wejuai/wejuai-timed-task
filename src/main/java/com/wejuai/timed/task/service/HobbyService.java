package com.wejuai.timed.task.service;

import com.wejuai.entity.mongo.statistics.HobbyHotByDay;
import com.wejuai.entity.mongo.statistics.HobbyTotalHot;
import com.wejuai.entity.mongo.statistics.HobbyTotalHotByDay;
import com.wejuai.timed.task.repository.mongo.HobbyHotByDayRepository;
import com.wejuai.timed.task.repository.mongo.HobbyTotalHotByDayRepository;
import com.wejuai.timed.task.repository.mongo.HobbyTotalHotRepository;
import com.wejuai.timed.task.repository.mysql.HobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.wejuai.timed.task.config.Constant.HOBBY_TOTAL_HOT_ID;

/**
 * @author ZM.Wang
 */
@Service
public class HobbyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HobbyTotalHotRepository hobbyTotalHotRepository;
    private final HobbyTotalHotByDayRepository hobbyTotalHotByDayRepository;

    private final MongoBaseService mongoBaseService;

    public HobbyService(HobbyHotByDayRepository hobbyHotByDayRepository, HobbyTotalHotByDayRepository hobbyTotalHotByDayRepository, HobbyTotalHotRepository hobbyTotalHotRepository, HobbyRepository hobbyRepository, MongoBaseService mongoBaseService) {
        this.hobbyTotalHotByDayRepository = hobbyTotalHotByDayRepository;
        this.hobbyTotalHotRepository = hobbyTotalHotRepository;
        this.mongoBaseService = mongoBaseService;
    }

    @Scheduled(cron = "0 40 1 * * ?")
    public void hobbyHotByDayEmptyProcessing() {
        logger.info("start---爱好热度统计空处理开始---start");
        LocalDate lastDay = LocalDate.now().plusDays(-1);
        Criteria criteria = new Criteria().and("date").is(lastDay);
        long watched = mongoBaseService.getMongoSum(criteria, "watched", HobbyHotByDay.class);
        long commented = mongoBaseService.getMongoSum(criteria, "commented", HobbyHotByDay.class);
        long created = mongoBaseService.getMongoSum(criteria, "created", HobbyHotByDay.class);
        long followed = mongoBaseService.getMongoSum(criteria, "followed", HobbyHotByDay.class);
        long unfollowed = mongoBaseService.getMongoSum(criteria, "unfollowed", HobbyHotByDay.class);

        HobbyTotalHotByDay hobbyTotalHotByDay = getHobbyTotalHotByDay(lastDay);
        hobbyTotalHotByDayRepository.save(hobbyTotalHotByDay
                .setWatched(watched).setCommented(commented).setCreated(created)
                .setFollowed(followed).setUnfollowed(unfollowed));

        criteria = new Criteria();
        long watchedTotal = mongoBaseService.getMongoSum(criteria, "watched", HobbyTotalHotByDay.class);
        long commentedTotal = mongoBaseService.getMongoSum(criteria, "commented", HobbyTotalHotByDay.class);
        long createdTotal = mongoBaseService.getMongoSum(criteria, "created", HobbyTotalHotByDay.class);
        long followedTotal = mongoBaseService.getMongoSum(criteria, "followed", HobbyTotalHotByDay.class);
        long unfollowedTotal = mongoBaseService.getMongoSum(criteria, "unfollowed", HobbyTotalHotByDay.class);

        HobbyTotalHot hobbyTotalHot = hobbyTotalHotRepository.findById(HOBBY_TOTAL_HOT_ID).orElse(new HobbyTotalHot(HOBBY_TOTAL_HOT_ID));
        hobbyTotalHotRepository.save(hobbyTotalHot
                .setCommented(commentedTotal).setCreated(createdTotal)
                .setWatched(watchedTotal).setFollowed(followedTotal - unfollowedTotal));
        logger.info("end---爱好热度统计空处理结束---end");
    }

    private HobbyTotalHotByDay getHobbyTotalHotByDay(LocalDate localDate) {
        HobbyTotalHotByDay hobbyTotalHotByDay = hobbyTotalHotByDayRepository.findByDate(localDate);
        if (hobbyTotalHotByDay == null) {
            hobbyTotalHotByDay = hobbyTotalHotByDayRepository.save(new HobbyTotalHotByDay(localDate));
        }
        return hobbyTotalHotByDay;
    }

}
