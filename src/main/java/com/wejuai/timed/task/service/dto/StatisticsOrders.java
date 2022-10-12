package com.wejuai.timed.task.service.dto;

import com.wejuai.entity.mysql.OrdersType;

/**
 * @author ZM.Wang
 */
public class StatisticsOrders {

    private OrdersType type;

    private long count;

    private long integral;

    public OrdersType getType() {
        return type;
    }

    public long getCount() {
        return count;
    }

    public long getIntegral() {
        return integral;
    }
}
