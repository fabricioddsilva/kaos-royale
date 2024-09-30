package com.kaos.mongoroyale.services;

import com.kaos.mongoroyale.entites.Battle;
import com.kaos.mongoroyale.entites.Participant;
import com.kaos.mongoroyale.entites.Player;
import com.kaos.mongoroyale.entites.enums.Result;
import com.kaos.mongoroyale.entites.projections.TopPlayer;
import com.kaos.mongoroyale.repositories.PlayerRepository;
import com.kaos.mongoroyale.services.exceptions.ConnectionNotSucessfulException;
import com.kaos.mongoroyale.services.exceptions.LoadBattlesException;
import com.mongodb.client.*;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONException;
import kong.unirest.core.json.JSONObject;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String api_url = "https://api.clashroyale.com/v1/players/";

    @Value("${api_key}")
    private String api_key;

    @Value("${db_uri}")
    public String uri;

    public Player insert(String tag){
        tag = tag.replaceAll("(?i)#", "%23");

        String url = api_url + tag;

        HttpResponse<JsonNode> player_data = Unirest.get(url)
                .header("Authorization", api_key)
                .asJson();

        if(player_data.isSuccess()){

            Player player = loadPlayer(player_data);

            try {

                JSONArray battle_data = Unirest.get(url + "/battlelog")
                        .header("Authorization", api_key)
                        .asJson()
                        .getBody()
                        .getArray();

                List<Battle> battles = loadBattles(battle_data);

                for (Battle battle : battles) {
                    player.addBattle(battle);
                }

                repository.save(player);

                System.out.println("Player Registrado: " + player.getTag());

                return player;
            }catch (JSONException e){
                throw new LoadBattlesException(e.getMessage());
            }

        } else {
            throw new ConnectionNotSucessfulException("Não foi possível se conectar com a API");
        }
    }


    public Player loadPlayer(HttpResponse<JsonNode> data){
        return new Player(
                data.getBody().getObject().getString("tag"),
                data.getBody().getObject().getString("name"),
                data.getBody().getObject().getInt("expLevel"),
                data.getBody().getObject().getInt("trophies")
        );
    }

    public List<Battle> loadBattles(JSONArray data){
        List<Battle> battles = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject battle = data.getJSONObject(i);

            Battle newBattle = new Battle();

            String stringTime = battle.getString("battleTime");
            ZonedDateTime time = ZonedDateTime.parse(stringTime, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSX"));

            newBattle.setBattleTime(Instant.parse(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"))));

            JSONArray teams_data = new JSONArray();
            teams_data.put(battle.getJSONArray("team").getJSONObject(0));

            JSONArray opponent_data = new JSONArray();
            opponent_data.put(battle.getJSONArray("opponent").getJSONObject(0));

            List<Participant> teams = loadParticipant(teams_data);
            List<Participant> opponents = loadParticipant(opponent_data);

            if (!teams.isEmpty()) {
                newBattle.setTeam(teams.get(0)); // Set the first participant as the team
            }

            if (!opponents.isEmpty()) {
                newBattle.setOpponent(opponents.get(0)); // Set the first participant as the opponent
            }


            if (i < data.length() - 1) {
                JSONObject nextBattle = data.getJSONObject(i + 1);
                int nextStartingTrophies = nextBattle.getJSONArray("team").getJSONObject(0).getInt("startingTrophies");
                int currentStartingTrophies = newBattle.getTeam().getStartingTrophies();

                if (nextStartingTrophies > currentStartingTrophies) {
                    newBattle.setResult(Result.VICTORY);
                } else {
                    newBattle.setResult(Result.DEFEAT);
                }
            }

            battles.add(newBattle);
        }

        return battles;
    }

    public List<Participant> loadParticipant(JSONArray data){
        List<Participant> participants = new ArrayList<>();
        for (int i = 0; i < data.length(); i++){
            JSONObject fighter = data.getJSONObject(i);

            Participant participant = new Participant(fighter.getString("tag"),fighter.getString("name"),fighter.getInt("startingTrophies"));

            JSONArray cards = fighter.getJSONArray("cards");
            for(int j = 0; j < cards.length(); j++){
                JSONObject card = cards.getJSONObject(j);
                participant.addCard(card.getString("name"));
            }

            participants.add(participant);
        }

        return participants;
    }

    public TopPlayer findTopPlayer() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("royale");
            MongoCollection<Document> collection = database.getCollection("player");

            AggregateIterable<Document> result = collection.aggregate(
                    Arrays.asList(new Document("$unwind", "$battles"),
                            new Document("$group",
                                    new Document("_id",
                                            new Document("playerId", "$_id")
                                                    .append("tag", "$tag")
                                                    .append("name", "$name")
                                                    .append("deck", "$battles.team.cards"))
                                            .append("victories",
                                                    new Document("$sum",
                                                            new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$battles.result", "VICTORY")), 1L, 0L))))
                                            .append("totalBattles",
                                                    new Document("$sum", 1L))),
                            new Document("$group",
                                    new Document("_id",
                                            new Document("playerId", "$_id.playerId")
                                                    .append("name", "$_id.name")
                                                    .append("tag", "$_id.tag"))
                                            .append("totalVictories",
                                                    new Document("$sum", "$victories"))
                                            .append("totalBattles",
                                                    new Document("$sum", "$totalBattles"))
                                            .append("decks",
                                                    new Document("$push",
                                                            new Document("deck", "$_id.deck")
                                                                    .append("victories", "$victories")))),
                            new Document("$addFields",
                                    new Document("winPercentage",
                                            new Document("$multiply", Arrays.asList(new Document("$divide", Arrays.asList("$totalVictories", "$totalBattles")), 100L)))),
                            new Document("$sort",
                                    new Document("totalVictories", -1L)),
                            new Document("$limit", 1L),
                            new Document("$unwind", "$decks"),
                            new Document("$sort",
                                    new Document("decks.victories", -1L)),
                            new Document("$group",
                                    new Document("_id",
                                            new Document("playerId", "$_id.playerId")
                                                    .append("name", "$_id.name")
                                                    .append("tag", "$_id.tag")
                                                    .append("totalVictories", "$totalVictories")
                                                    .append("totalBattles", "$totalBattles")
                                                    .append("winPercentage", "$winPercentage"))
                                            .append("topDeck",
                                                    new Document("$first", "$decks.deck"))
                                            .append("deckVictories",
                                                    new Document("$first", "$decks.victories"))),
                            new Document("$project",
                                    new Document("_id", 0L)
                                            .append("name", "$_id.name")
                                            .append("tag", "$_id.tag")
                                            .append("totalVictories", "$_id.totalVictories")
                                            .append("totalBattles", "$_id.totalBattles")
                                            .append("winPercentage", "$_id.winPercentage")
                                            .append("deck", "$topDeck")
                                            .append("deckVictories", "$deckVictories")))
            );

            TopPlayer topPlayer = new TopPlayer();

            for(Document data : result){
                topPlayer.setTag(data.getString("tag"));
                topPlayer.setName(data.getString("name"));
                topPlayer.setTotalVictories(data.getLong("totalVictories").intValue());
                topPlayer.setTotalBattles(data.getLong("totalBattles").intValue());
                topPlayer.setWinPercentage(data.getDouble("winPercentage"));
                topPlayer.setDeck(data.getList("deck", String.class));
                topPlayer.setDeckVictories(data.getLong("deckVictories").intValue());
            }

            return topPlayer;
        }
    }

//    public void insertMultiplePlayers(){
//        String path = "C:\\temp\\dados.txt";
//        try(BufferedReader br = new BufferedReader(new FileReader(path))){
//            String line = br.readLine();
//            while (line != null) {
//                try{
//                    insert(line);
//                    line = br.readLine();
//                    } catch (Exception e){
//                        System.err.println("Erro na tag: " + line );
//                        line = br.readLine();
//                }
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }




}
