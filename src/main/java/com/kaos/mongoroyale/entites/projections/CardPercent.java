package com.kaos.mongoroyale.entites.projections;

import java.io.Serial;
import java.io.Serializable;

public class CardPercent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer totalBattles;
    private Double victoryPercentage;
    private Double defeatPercentage;

    public CardPercent() {
    }

    public CardPercent(Integer totalBattles, Double victoryPercentage, Double defeatPercentage) {
        this.totalBattles = totalBattles;
        this.victoryPercentage = victoryPercentage;
        this.defeatPercentage = defeatPercentage;
    }

    public Integer getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(Integer totalBattles) {
        this.totalBattles = totalBattles;
    }

    public Double getVictoryPercentage() {
        return victoryPercentage;
    }

    public void setVictoryPercentage(Double victoryPercentage) {
        this.victoryPercentage = victoryPercentage;
    }

    public Double getDefeatPercentage() {
        return defeatPercentage;
    }

    public void setDefeatPercentage(Double defeatPercentage) {
        this.defeatPercentage = defeatPercentage;
    }

}
