package com.kaos.mongoroyale.entites.projections;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeckPercent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer totalBattles;
    private Integer totalWins;
    private Double winRate;

    private List<String> deck = new ArrayList<>();

    public DeckPercent() {
    }

    public DeckPercent(Integer totalBattles, Integer totalWins, Double winRate, List<String> deck) {
        this.totalBattles = totalBattles;
        this.totalWins = totalWins;
        this.winRate = winRate;
        this.deck = deck;
    }

    public Integer getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(Integer totalBattles) {
        this.totalBattles = totalBattles;
    }

    public Integer getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(Integer totalWins) {
        this.totalWins = totalWins;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public List<String> getDeck() {
        return deck;
    }

    public void setDeck(List<String> deck) {
        this.deck = deck;
    }

}
