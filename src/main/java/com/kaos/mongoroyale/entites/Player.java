package com.kaos.mongoroyale.entites;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document
public class Player implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String tag;
    private String name;
    private Integer expLevel;
    private Integer trophies;

    private List<Battle> battles = new ArrayList<>();

    public Player() {
    }

    public Player(String tag, String name, Integer expLevel, Integer trophies) {
        this.tag = tag;
        this.name = name;
        this.expLevel = expLevel;
        this.trophies = trophies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getExpLevel() {
        return expLevel;
    }

    public void setExpLevel(Integer expLevel) {
        this.expLevel = expLevel;
    }

    public Integer getTrophies() {
        return trophies;
    }

    public void setTrophies(Integer trophies) {
        this.trophies = trophies;
    }

    public List<Battle> getBattles() {
        return battles;
    }

    public void addBattle(Battle battle){
        battles.add(battle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(getTag(), player.getTag());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTag());
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", tag='" + tag + '\'' +
                ", expLevel=" + expLevel +
                ", trophies=" + trophies +
                ", battles=" + battles +
                '}';
    }
}
