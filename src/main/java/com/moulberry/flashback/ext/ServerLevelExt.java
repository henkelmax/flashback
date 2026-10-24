package com.moulberry.flashback.ext;

public interface ServerLevelExt {

    boolean flashback$shouldSendChunk(long pos);
    void flashback$markChunkAsSendable(long pos);

    void flashback$setCanSpawnEntities(boolean canSpawnEntities);

}
