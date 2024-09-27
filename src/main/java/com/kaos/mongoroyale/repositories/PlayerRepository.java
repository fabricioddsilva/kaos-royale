package com.kaos.mongoroyale.repositories;

import com.kaos.mongoroyale.entites.Player;
import com.kaos.mongoroyale.entites.projections.ResultPercent;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {

    @Aggregation(pipeline = {"{$unwind: '$battles'}," +
            "{$match: {'battles.team.cards': 'Goblin Drill', 'battles.battleTime': {$gte: ISODate('2024-09-25T00:00:00Z'), $lte: ISODate('2024-09-25T06:59:59Z')}}}," +
            "{$group: {_id: null, totalBattles: {$sum: 1}, victories: {$sum: {$cond: [ {$eq: [ '$battles.result', 'VICTORY' ]}, 1, 0 ]} }, defeats: {$sum: {$cond: [ {$eq: [ '$battles.result', 'DEFEAT' ]}, 1, 0]}}   }}," +
            "{$project: {_id: 0, totalBattles: 1, victoryPercentage: {$multiply: [ {$divide: [ '$victories', '$totalBattles' ]}, 100 ]}, defeatPercentage: {$multiply: [ {$divide: [ '$defeats', '$totalBattles' ]}, 100 ]}}}"})
    AggregationResults<ResultPercent> victoryAndDefeatsPercentByCard(
//            String card, Instant initialTimeStamp, Instant finalTimeStamp
    );
}
