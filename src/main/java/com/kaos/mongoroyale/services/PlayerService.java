package com.kaos.mongoroyale.services;

import com.kaos.mongoroyale.entites.Battle;
import com.kaos.mongoroyale.entites.Participant;
import com.kaos.mongoroyale.entites.Player;
import com.kaos.mongoroyale.entites.enums.Result;
import com.kaos.mongoroyale.repositories.PlayerRepository;
import com.kaos.mongoroyale.services.exceptions.ConnectionNotSucessfulException;
import com.kaos.mongoroyale.services.exceptions.LoadBattlesException;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONException;
import kong.unirest.core.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository repository;

    private final String api_url = "https://api.clashroyale.com/v1/players/";

    @Value("${api_key}")
    private String api_key;

    public Player insert(String tag){
        tag = tag.replaceAll("(?i)#", "%23");

        String url = api_url + tag;

        System.out.println(url);

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
}
