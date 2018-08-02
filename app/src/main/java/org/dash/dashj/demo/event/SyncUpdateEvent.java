package org.dash.dashj.demo.event;

import java.util.Date;

public class SyncUpdateEvent {

    private double pct;
    private int blocksSoFar;
    private Date date;

    public SyncUpdateEvent(double pct, int blocksSoFar, Date date) {
        this.pct = pct;
        this.blocksSoFar = blocksSoFar;
        this.date = date;
    }

    public double getPct() {
        return pct;
    }

    public int getBlocksSoFar() {
        return blocksSoFar;
    }

    public Date getDate() {
        return date;
    }
}
