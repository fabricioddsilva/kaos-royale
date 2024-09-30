package com.kaos.mongoroyale.resources;

import com.google.gson.Gson;
import com.kaos.mongoroyale.entites.Player;
import com.kaos.mongoroyale.entites.projections.TopPlayer;
import com.kaos.mongoroyale.resources.util.URL;
import com.kaos.mongoroyale.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/players")
public class PlayerResource {

    @Autowired
    private PlayerService service;

    @GetMapping
    public ResponseEntity<String> home(){
        return ResponseEntity.ok().body("funciona");
    }

    @PostMapping(value = "/insert")
    public ResponseEntity<Void> insert(@RequestParam(value = "tag") String tag){
        tag = URL.decodeParam(tag);
        Player player = service.insert(tag);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(player.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = "/topPlayer")
    public ResponseEntity<TopPlayer> findTopPlayer(){
        return ResponseEntity.ok().body(service.findTopPlayer());
    }

//    @PostMapping(value = "/addAll")
//    public ResponseEntity<Void> addAll(){
//
//        service.insertMultiplePlayers();
//        return ResponseEntity.ok().build();
//    }


}
