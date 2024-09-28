package com.kaos.mongoroyale.resources;

import com.kaos.mongoroyale.entites.projections.CardPercent;
import com.kaos.mongoroyale.entites.projections.DeckPercent;
import com.kaos.mongoroyale.resources.util.URL;
import com.kaos.mongoroyale.services.CardService;
import com.kaos.mongoroyale.services.exceptions.ObjectNotFoundException;
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

    @GetMapping(value = "/cardPercents")
    public ResponseEntity<String>
    victoryAndDefeatsPercentByCard(
            @RequestParam(value = "card", defaultValue = "") String card,
            @RequestParam(value = "initialTimeStamp", defaultValue = "") String initialTimeStamp,
            @RequestParam(value = "finalTimeStamp", defaultValue = "") String finalTimeStamp
    ){
        card = URL.decodeParam(card);
        Instant initialTime = URL.convertDate(initialTimeStamp, new Date(0L).toInstant());
        Instant finalTime = URL.convertDate(finalTimeStamp, Instant.now());
        CardPercent rp = service.victoryAndDefeatsPercentByCard(card, initialTime, finalTime);

        if(rp == null){
            throw new ObjectNotFoundException("Verifique o intervalo de tempo ou o nome da carta");
        }

        return ResponseEntity.ok().body(rp.toString());
    }

    @GetMapping(value = "/deckPercents")
    public ResponseEntity<String>
    deckVictoryRate(
            @RequestParam(value = "rate", defaultValue = "") String rate_text,
            @RequestParam(value = "initialTimeStamp", defaultValue = "") String initialTimeStamp,
            @RequestParam(value = "finalTimeStamp", defaultValue = "") String finalTimeStamp
    ){
        double rate = Double.parseDouble(URL.decodeParam(rate_text));
        Instant initialTime = URL.convertDate(initialTimeStamp, new Date(0L).toInstant());
        Instant finalTime = URL.convertDate(finalTimeStamp, Instant.now());
        String body = "";

        List<DeckPercent> results = service.deckVictoryRate(rate, initialTime, finalTime);

        if(results.isEmpty()){
            throw new ObjectNotFoundException("Verifique o intervalo de tempo ou a porcentagem inserida");
        }

        return ResponseEntity.ok().body(results.toString());


    }


}
