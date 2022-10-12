package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.trade.Charge;
import com.wejuai.entity.mongo.trade.TradeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ChargeRepository extends MongoRepository<Charge, String> {

}
