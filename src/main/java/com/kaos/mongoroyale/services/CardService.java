package com.kaos.mongoroyale.services;

import com.kaos.mongoroyale.entites.projections.CardDefeats;
import com.kaos.mongoroyale.entites.projections.CardPercent;
import com.kaos.mongoroyale.entites.projections.DeckPercent;
import com.kaos.mongoroyale.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class CardService {

    @Autowired
    private PlayerRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;



    public CardPercent victoryAndDefeatsPercentByCard(String card, Instant startTime, Instant endTime){

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

        AggregationResults<CardPercent> results = mongoTemplate.aggregate(aggregation, "player", CardPercent.class);
        return results.getUniqueMappedResult();


    }

    public List<DeckPercent> deckVictoryRate(double rate, Instant startTime, Instant endTime){
        UnwindOperation unwindOperation = Aggregation.unwind("battles");

        MatchOperation battleTimeMatch = Aggregation.match(Criteria.where("battles.battleTime").gte(startTime).lte(endTime));

        GroupOperation groupOperation = Aggregation.group("battles.team.cards")
                .count().as("totalBattles")
                .sum(ConditionalOperators.when(Criteria.where("battles.result").is("VICTORY"))
                        .then(1).otherwise(0)).as("totalWins");

        AddFieldsOperation addFieldsOperation = Aggregation.addFields().addField("winRate")
                .withValue(ArithmeticOperators.Multiply.valueOf(
                        ArithmeticOperators.Divide.valueOf("totalWins").divideBy("totalBattles"))
                        .multiplyBy(100)).build();

        MatchOperation winRateMatch = Aggregation.match(Criteria.where("winRate").gte(rate));

        Aggregation aggregation = Aggregation.newAggregation(
                unwindOperation,
                battleTimeMatch,
                groupOperation,
                addFieldsOperation,
                winRateMatch,
                Aggregation.project()
                        .and("totalBattles").as("totalBattles")
                        .and("totalWins").as("totalWins")
                        .and("winRate").as("winRate")
                        .and("$_id").as("deck"),

                Aggregation.sort(Sort.Direction.DESC, "winRate")
        );

        AggregationResults<DeckPercent> results = mongoTemplate.aggregate(aggregation, "player", DeckPercent.class);
        return results.getMappedResults();

    }

    public CardDefeats defeatsByCardCombo (List<String> cards, Instant startTime, Instant endTime){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.unwind("battles"),
                Aggregation.match(Criteria.where("battles.team.cards").is(cards)
                        .and("battles.battleTime").gte(startTime).lte(endTime)),
                Aggregation.group()
                        .count().as("totalBattles")
                        .sum(ConditionalOperators.when(Criteria.where("battles.result").is("DEFEAT")).then(1).otherwise(0)).as("defeats"),
                Aggregation.project("totalBattles","defeats")
                        .andExclude("_id")
        );

        AggregationResults<CardDefeats> results = mongoTemplate.aggregate(aggregation, "player", CardDefeats.class);
        return results.getUniqueMappedResult();
    }
}
