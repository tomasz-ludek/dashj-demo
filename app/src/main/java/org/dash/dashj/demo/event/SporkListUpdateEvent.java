package org.dash.dashj.demo.event;

import org.bitcoinj.core.SporkMessage;

public class SporkListUpdateEvent {

    private SporkMessage spork;

    public SporkListUpdateEvent(SporkMessage spork) {
        this.spork = spork;
    }

    public SporkMessage getSpork() {
        return spork;
    }
}
