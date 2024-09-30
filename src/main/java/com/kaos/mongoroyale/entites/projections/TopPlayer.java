package com.kaos.mongoroyale.entites.projections;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TopPlayer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tag;
    private String name;
    private Double winPercentage;
    private Integer totalVictories;
    private Integer totalBattles;
    private List<String> deck = new ArrayList<>();
    private Integer deckVictories;

    public TopPlayer() {
    }

    public TopPlayer(String id, String name, Double winPercentage, Integer totalVictories, Integer totalBattles, List<String> deck, Integer deckVictories) {
        this.tag = id;
        this.name = name;
        this.winPercentage = winPercentage;
        this.totalVictories = totalVictories;
        this.totalBattles = totalBattles;
        this.deck = deck;
        this.deckVictories = deckVictories;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(Double winPercentage) {
        this.winPercentage = winPercentage;
    }

    public Integer getTotalVictories() {
        return totalVictories;
    }

    public void setTotalVictories(Integer totalVictories) {
        this.totalVictories = totalVictories;
    }

    public Integer getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(Integer totalBattles) {
        this.totalBattles = totalBattles;
    }

    public List<String> getDeck() {
        return deck;
    }

    public void setDeck(List<String> deck) {
        this.deck = deck;
    }

    public Integer getDeckVictories() {
        return deckVictories;
    }

    public void setDeckVictories(Integer deckVictories) {
        this.deckVictories = deckVictories;
    }

//    @Override
//    public String toString() {
//        return
//                "Tag: " + tag + "\n" +
//                "Nick: " + name + "\n" +
//                "Taxa de Vitória: " + String.format("%.2f", winPercentage) + "% \n" +
//                "Total de Vitórias: " + totalVictories + "\n" +
//                "Total de Batalhas: " + totalBattles + "\n" +
//                "Deck: " + deck + "\n" +
//                "Vitórias com o Deck: " + deckVictories;
//    }
}
