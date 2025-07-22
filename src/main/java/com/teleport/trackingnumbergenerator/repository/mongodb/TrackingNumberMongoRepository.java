package com.teleport.trackingnumbergenerator.repository.mongodb;

import com.teleport.trackingnumbergenerator.entity.EventLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrackingNumberMongoRepository extends MongoRepository<EventLog, String> {

}
