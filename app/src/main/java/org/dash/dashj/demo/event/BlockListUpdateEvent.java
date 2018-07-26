package org.dash.dashj.demo.event;

import org.bitcoinj.core.StoredBlock;

import java.util.List;

public class BlockListUpdateEvent {

    private List<StoredBlock> blockList;

    public BlockListUpdateEvent(List<StoredBlock> blockList) {
        this.blockList = blockList;
    }

    public List<StoredBlock> getBlockList() {
        return blockList;
    }
}
