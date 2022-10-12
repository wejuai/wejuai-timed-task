package com.wejuai.timed.task.repository.mongo;

import com.wejuai.entity.mongo.MerchantTransfer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MerchantTransferRepository extends MongoRepository<MerchantTransfer, String> {
}
