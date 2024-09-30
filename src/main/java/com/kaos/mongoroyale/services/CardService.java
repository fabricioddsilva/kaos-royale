package com.kaos.mongoroyale.services;

import com.kaos.mongoroyale.entites.projections.CardDefeats;
import com.kaos.mongoroyale.entites.projections.CardPercent;
import com.kaos.mongoroyale.entites.projections.DeckPercent;
import com.kaos.mongoroyale.entites.projections.TopPlayer;
import com.kaos.mongoroyale.repositories.PlayerRepository;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class CardService {

    @Value("${db_uri}")
    public String uri;
    @Autowired
    private PlayerRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;




    public CardPercent victoryAndDefeatsPercentByCard(String card, Instant startTime, Instant endTime) {

        UnwindOperation unwindBattles = unwind("battles");
        MatchOperation matchGoblinDrill = match(
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


                project("totalBattles", "victories", "defeats")
                        .andExpression("victories / totalBattles * 100").as("victoryPercentage")
                        .andExpression("defeats / totalBattles * 100").as("defeatPercentage")

        );

        AggregationResults<CardPercent> results = mongoTemplate.aggregate(aggregation, "player", CardPercent.class);
        return results.getUniqueMappedResult();


    }

    public List<DeckPercent> deckVictoryRate(double rate, Instant startTime, Instant endTime) {
        UnwindOperation unwindOperation = unwind("battles");

        MatchOperation battleTimeMatch = match(Criteria.where("battles.battleTime").gte(startTime).lte(endTime));

        GroupOperation groupOperation = Aggregation.group("battles.team.cards")
                .count().as("totalBattles")
                .sum(ConditionalOperators.when(Criteria.where("battles.result").is("VICTORY"))
                        .then(1).otherwise(0)).as("totalWins");

        AddFieldsOperation addFieldsOperation = Aggregation.addFields().addField("winRate")
                .withValue(ArithmeticOperators.Multiply.valueOf(
                                ArithmeticOperators.Divide.valueOf("totalWins").divideBy("totalBattles"))
                        .multiplyBy(100)).build();

        MatchOperation winRateMatch = match(Criteria.where("winRate").gte(rate));

        Aggregation aggregation = Aggregation.newAggregation(
                unwindOperation,
                battleTimeMatch,
                groupOperation,
                addFieldsOperation,
                winRateMatch,
                project()
                        .and("totalBattles").as("totalBattles")
                        .and("totalWins").as("totalWins")
                        .and("winRate").as("winRate")
                        .and("$_id").as("deck"),

                Aggregation.sort(Sort.Direction.ASC, "winRate"),
                limit(10)

        );

        AggregationResults<DeckPercent> results = mongoTemplate.aggregate(aggregation, "player", DeckPercent.class);
        return results.getMappedResults();

    }

    public CardDefeats defeatsByCardCombo(List<String> cards, Instant startTime, Instant endTime) {
        Aggregation aggregation = Aggregation.newAggregation(
                unwind("battles"),
                match(Criteria.where("battles.team.cards").is(cards)
                        .and("battles.battleTime").gte(startTime).lte(endTime)),
                Aggregation.group()
                        .count().as("totalBattles")
                        .sum(ConditionalOperators.when(Criteria.where("battles.result").is("DEFEAT")).then(1).otherwise(0)).as("defeats"),
                project("totalBattles", "defeats")
                        .andExclude("_id")
        );

        AggregationResults<CardDefeats> results = mongoTemplate.aggregate(aggregation, "player", CardDefeats.class);
        return results.getUniqueMappedResult();
    }


}
