package com.kaos.mongoroyale.entites.projections;

public class ResultPercent {
    private Integer totalBattles;
    private Double victoryPercentage;
    private Double defeatPercentage;

    public ResultPercent() {
    }

    public ResultPercent(Integer totalBattles, Double victoryPercentage, Double defeatPercentage) {
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
