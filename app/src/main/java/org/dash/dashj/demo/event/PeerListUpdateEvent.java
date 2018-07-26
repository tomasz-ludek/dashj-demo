package org.dash.dashj.demo.event;

import org.bitcoinj.core.Peer;

import java.util.List;

public class PeerListUpdateEvent {

    private List<Peer> peerList;

    public PeerListUpdateEvent(List<Peer> peerList) {
        this.peerList = peerList;
    }

    public List<Peer> getPeerList() {
        return peerList;
    }
}
