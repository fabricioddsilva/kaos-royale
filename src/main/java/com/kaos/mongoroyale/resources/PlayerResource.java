package com.kaos.mongoroyale.resources;

import com.kaos.mongoroyale.entites.Player;
import com.kaos.mongoroyale.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/{tag}")
    public ResponseEntity<Void> insert(@PathVariable String tag){
        Player player = service.insert(tag);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(player.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }
}
