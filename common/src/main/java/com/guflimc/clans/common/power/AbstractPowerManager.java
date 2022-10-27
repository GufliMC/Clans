package com.guflimc.clans.common.power;

import com.guflimc.clans.common.ClansConfig;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractPowerManager {

    private final Map<UUID, Instant> tracked = new ConcurrentHashMap<>();

    protected AbstractPowerManager(ClansConfig config, Scheduler scheduler) {

    }

    public void track(UUID entity) {
        tracked.put(entity, Instant.now());
    }

    public void clear(UUID entity) {
        tracked.remove(entity);
    }

}
