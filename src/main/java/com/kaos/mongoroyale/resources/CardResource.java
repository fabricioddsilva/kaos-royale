package com.kaos.mongoroyale.resources;

import com.kaos.mongoroyale.entites.Player;
import com.kaos.mongoroyale.entites.projections.ResultPercent;
import com.kaos.mongoroyale.resources.util.URL;
import com.kaos.mongoroyale.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/cards")
public class CardResource {

    @Autowired
    private CardService service;

    @GetMapping(value = "/percents")
    public ResponseEntity<String>
    victoryAndDefeatsPercentByCard(
            @RequestParam(value = "card", defaultValue = "") String card,
            @RequestParam(value = "initialTimeStamp", defaultValue = "") String initialTimeStamp,
            @RequestParam(value = "finalTimeStamp", defaultValue = "") String finalTimeStamp
    ){

        System.out.println(initialTimeStamp);
        System.out.println(finalTimeStamp);
        card = URL.decodeParam(card);
        Instant initialTime = URL.convertDate(initialTimeStamp, new Date(0L).toInstant());
        Instant finalTime = URL.convertDate(finalTimeStamp, Instant.now());
        String body = "";
        ResultPercent rp = service.victoryAndDefeatsPercentByCard(card, initialTime, finalTime);

        if(rp == null){
            body = "Não foi possível encontrar registros nesse intervalo de tempo.";
        } else {
            body = "Total de Batalhas: " + rp.getTotalBattles() + "\n" +
                    "Vitórias: " + rp.getVictoryPercentage() + "% \n" +
                    "Derrotas: " + rp.getDefeatPercentage() + "% \n";
        }
        return ResponseEntity.ok().body(body);
    }


}
