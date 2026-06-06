package com.revcart.analytics.repository;

import com.revcart.analytics.model.Analytics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalyticsRepository extends MongoRepository<Analytics, String> {
}
