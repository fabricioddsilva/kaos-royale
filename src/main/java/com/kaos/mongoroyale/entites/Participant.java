package com.kaos.mongoroyale.entites;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Participant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tag;
    private String name;
    private Integer startingTrophies;
    private List<String> cards = new ArrayList<>();

    public Participant() {
    }

    public Participant(String tag, String name, Integer startingTrophies) {
        this.tag = tag;
        this.name = name;
        this.startingTrophies = startingTrophies;
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

    public Participant(Integer startingTrophies) {
        this.startingTrophies = startingTrophies;
    }

    public Integer getStartingTrophies() {
        return startingTrophies;
    }

    public List<String> getCards() {
        return cards;
    }

    public void setStartingTrophies(Integer startingTrophies) {
        this.startingTrophies = startingTrophies;
    }

    public void addCard(String card) {
        cards.add(card);
    }

}
