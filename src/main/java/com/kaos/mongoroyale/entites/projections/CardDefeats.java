package com.kaos.mongoroyale.entites.projections;

import java.io.Serial;
import java.io.Serializable;

public class CardDefeats implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

}
