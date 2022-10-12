package com.wejuai.timed.task.service;

import com.endofmaster.commons.util.DateUtil;
import com.wejuai.entity.mongo.statistics.OrdersStatisticsByDay;
import com.wejuai.entity.mysql.OrdersType;
import com.wejuai.timed.task.repository.mongo.OrdersStatisticsByDayRepository;
import com.wejuai.timed.task.repository.mysql.OrdersRepository;
import com.wejuai.timed.task.repository.mysql.UserRepository;
import com.wejuai.timed.task.service.dto.StatisticsOrders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * @author ZM.Wang
 */
@Service
public class OrdersService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final OrdersStatisticsByDayRepository ordersStatisticsByDayRepository;

    public OrdersService(UserRepository userRepository, OrdersRepository ordersRepository, OrdersStatisticsByDayRepository ordersStatisticsByDayRepository) {
        this.userRepository = userRepository;
        this.ordersRepository = ordersRepository;
        this.ordersStatisticsByDayRepository = ordersStatisticsByDayRepository;
    }

    @Scheduled(cron = "0 20 1 * * ?")
    public void statisticsOrders() {
        logger.info("start---统计每日积分参数任务开始---start");
        Date start = DateUtil.localDateTime2Date(LocalDate.now().plusDays(-1), LocalTime.MIN);
        Date end = DateUtil.localDateTime2Date(LocalDate.now().plusDays(-1), LocalTime.MAX);
        List<StatisticsOrders> statisticsOrders = ordersRepository.statisticsOrders(start, end);
        long articleCount = 0, articleAmount = 0, rewardDemandCount = 0, rewardDemandAmount = 0, transferAddCount = 0, transferAddAmount = 0, transferSubCount = 0, transferSubAmount = 0;
        for (StatisticsOrders statistics : statisticsOrders) {
            if (statistics.getType() == OrdersType.ARTICLE) {
                articleCount = statistics.getCount();
                articleAmount = statistics.getIntegral();
            }
            if (statistics.getType() == OrdersType.SELECTED_REWARD) {
                rewardDemandCount = statistics.getCount();
                rewardDemandAmount = statistics.getIntegral();
            }
            if (statistics.getType() == OrdersType.SYSTEM_ADD) {
                transferAddCount = statistics.getCount();
                transferAddAmount = statistics.getIntegral();
            }
            if (statistics.getType() == OrdersType.SYSTEM_SUB) {
                transferSubCount = statistics.getCount();
                transferSubAmount = statistics.getIntegral();
            }
        }
        OrdersStatisticsByDay ordersStatisticsByDay = ordersStatisticsByDayRepository.findByDate(LocalDate.now().plusDays(-1));
        if (ordersStatisticsByDay == null) {
            ordersStatisticsByDay = new OrdersStatisticsByDay(articleCount, articleAmount, rewardDemandCount, rewardDemandAmount, transferAddCount, transferAddAmount, transferSubCount, transferSubAmount);
        } else {
            ordersStatisticsByDay = ordersStatisticsByDay.update(articleCount, articleAmount, rewardDemandCount, rewardDemandAmount, transferAddCount, transferAddAmount, transferSubCount, transferSubAmount);
        }
        ordersStatisticsByDayRepository.save(ordersStatisticsByDay);
        logger.info("end---统计每日积分参数任务结束---end");
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void userIntegralStatistics() {
        logger.info("start---重新根据订单计算用户积分真实性任务开始---start");
        userRepository.findAll().forEach(user -> {
            long income = ordersRepository.sumIncomeByUser(user.getId());
            long expenditure = ordersRepository.sumExpenditureByUser(user.getId());
            userRepository.save(user.setIntegral(income - expenditure));
        });
        logger.info("end---重新根据订单计算用户积分真实性任务结束---end");
    }
}
