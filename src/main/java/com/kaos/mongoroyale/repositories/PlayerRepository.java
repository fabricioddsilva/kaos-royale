package com.kaos.mongoroyale.repositories;

import com.kaos.mongoroyale.entites.Player;
import com.kaos.mongoroyale.entites.projections.CardPercent;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {

}
