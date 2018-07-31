package org.dash.dashj.demo.event;

public class MasternodeListUpdateEvent {

    private int syncStatus;

    public MasternodeListUpdateEvent(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getSyncStatus() {
        return syncStatus;
    }
}
