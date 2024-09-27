package com.kaos.mongoroyale.services;

import com.kaos.mongoroyale.entites.Player;
import com.kaos.mongoroyale.entites.projections.ResultPercent;
import com.kaos.mongoroyale.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class CardService {

    @Autowired
    private PlayerRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;



    public ResultPercent victoryAndDefeatsPercentByCard(String card, Instant startTime, Instant endTime){

        UnwindOperation unwindBattles = Aggregation.unwind("battles");
        MatchOperation matchGoblinDrill = Aggregation.match(
                Criteria.where("battles.team.cards").is(card)
                        .and("battles.battleTime").gte(startTime).lte(endTime)
        );

        Aggregation aggregation = Aggregation.newAggregation(
                unwindBattles,
                matchGoblinDrill,
                Aggregation.group()
                        .count().as("totalBattles")
                        .sum(ConditionalOperators.when(Criteria.where("battles.result").is("VICTORY")).then(1).otherwise(0)).as("victories")
                        .sum(ConditionalOperators.when(Criteria.where("battles.result").is("DEFEAT")).then(1).otherwise(0)).as("defeats"),


                Aggregation.project("totalBattles", "victories", "defeats")
                        .andExpression("victories / totalBattles * 100").as("victoryPercentage")
                        .andExpression("defeats / totalBattles * 100").as("defeatPercentage")
        );

        AggregationResults<ResultPercent> results = mongoTemplate.aggregate(aggregation, "player", ResultPercent.class);
        return results.getUniqueMappedResult();


    }
}
