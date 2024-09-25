package com.kaos.mongoroyale.repositories;

import com.kaos.mongoroyale.entites.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {
}
