package com.kaos.mongoroyale.entites;

import com.kaos.mongoroyale.entites.enums.Result;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class Battle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Instant battleTime;
    private Participant team;
    private Participant opponent;
    private Result result;

    public Battle() {
    }

    public Battle(Instant battleTime, Participant team, Participant opponent) {
        this.battleTime = battleTime;
        this.team = team;
        this.opponent = opponent;
    }

    public Instant getBattleTime() {
        return battleTime;
    }

    public void setBattleTime(Instant battleTime) {
        this.battleTime = battleTime;
    }

    public Participant getTeam() {
        return team;
    }

    public void setTeam(Participant team) {
        this.team = team;
    }

    public Participant getOpponent() {
        return opponent;
    }

    public void setOpponent(Participant opponent) {
        this.opponent = opponent;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
