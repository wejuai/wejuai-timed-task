package com.wejuai.timed.task.service;

import com.endofmaster.commons.util.DateUtil;
import com.wejuai.entity.mongo.SendMessage;
import com.wejuai.entity.mongo.statistics.UserStatistics;
import com.wejuai.entity.mongo.statistics.UserStatisticsByDay;
import com.wejuai.timed.task.repository.mongo.UserStatisticsByDayRepository;
import com.wejuai.timed.task.repository.mongo.UserStatisticsRepository;
import com.wejuai.timed.task.repository.mysql.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.wejuai.timed.task.config.Constant.USER_STATISTICS_ID;

/**
 * @author ZM.Wang
 */
@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserStatisticsByDayRepository userStatisticsByDayRepository;

    private final MongoBaseService mongoBaseService;

    public UserService(UserRepository userRepository, UserStatisticsRepository userStatisticsRepository, UserStatisticsByDayRepository userStatisticsByDayRepository, MongoBaseService mongoBaseService) {
        this.userRepository = userRepository;
        this.userStatisticsRepository = userStatisticsRepository;
        this.userStatisticsByDayRepository = userStatisticsByDayRepository;
        this.mongoBaseService = mongoBaseService;
    }

    @Scheduled(cron = "0 10 1 * * ?")
    public void statistics() {
        logger.info("start---统计每日用户参数任务开始---start");
        LocalDateTime start = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MAX);
        Criteria criteria = new Criteria().and("createdAt").gte(start).lte(end);
        long msgCount = mongoBaseService.getMongoCount(criteria, SendMessage.class);
        long newUserCount = userRepository.countByCreatedAtBetween(DateUtil.localDateTime2Date(start), DateUtil.localDateTime2Date(end));
        UserStatisticsByDay userStatisticsByDay = userStatisticsByDayRepository.findByDate(LocalDate.now().plusDays(-1));
        if (userStatisticsByDay == null) {
            userStatisticsByDay = new UserStatisticsByDay(newUserCount, msgCount);
        } else {
            userStatisticsByDay = userStatisticsByDay.update(newUserCount, msgCount);
        }
        userStatisticsByDayRepository.save(userStatisticsByDay);
        UserStatistics userStatistics = userStatisticsRepository.findById(USER_STATISTICS_ID)
                .orElse(new UserStatistics(USER_STATISTICS_ID));
        userStatisticsRepository.save(userStatistics.addImMsg(msgCount).addUserRegister(newUserCount));
        logger.info("end---统计每日用户参数任务结束---end");
    }
}
