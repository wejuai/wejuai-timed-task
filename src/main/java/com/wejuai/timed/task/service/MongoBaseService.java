package com.wejuai.timed.task.service;

import com.wejuai.timed.task.service.dto.MongoCount;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZM.Wang
 */
@Service
public class MongoBaseService {

    private final MongoTemplate mongoTemplate;

    public MongoBaseService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long getMongoCount(Criteria criteria, Class<?> clazz) {
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group().count().as("count")
        );
        AggregationResults<MongoCount> countAggregate = mongoTemplate.aggregate(countAggregation, clazz, MongoCount.class);
        MongoCount mongoCount = countAggregate.getUniqueMappedResult();
        return mongoCount == null ? 0 : mongoCount.getCount();
    }

    public long getMongoSum(Criteria criteria, String sumField, Class<?> clazz) {
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group().sum(sumField).as("count")
        );
        AggregationResults<MongoCount> countAggregate = mongoTemplate.aggregate(countAggregation, clazz, MongoCount.class);
        MongoCount mongoCount = countAggregate.getUniqueMappedResult();
        return mongoCount == null ? 0 : mongoCount.getCount();
    }

    <T> List<T> getList(Criteria criteria, long page, long size, Class<T> clazz, Sort.Direction sort, String... sortParam) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(sort, sortParam),
                Aggregation.skip(page * size),
                Aggregation.limit(size)
        );
        AggregationResults<T> aggregate = mongoTemplate.aggregate(aggregation, clazz, clazz);
        return aggregate.getMappedResults();
    }
}
