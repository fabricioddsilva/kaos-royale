package com.kaos.mongoroyale.entites.projections;

public class CardDefeats {
    private Integer totalBattles;
    private Integer defeats;

    public CardDefeats(Integer totalBattles, Integer defeats) {
        this.totalBattles = totalBattles;
        this.defeats = defeats;
    }

    public Integer getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(Integer totalBattles) {
        this.totalBattles = totalBattles;
    }

    public Integer getDefeats() {
        return defeats;
    }

    public void setDefeats(Integer defeats) {
        this.defeats = defeats;
    }

    @Override
    public String toString() {
        return "Total de Batalhas: " + totalBattles + "\n" +
                "Derrotas: " + defeats;
    }
}
