package com.guflimc.clans.common.power;

import com.guflimc.brick.scheduler.api.Scheduler;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.common.ClansConfig;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AbstractPowerManager {

    private final Map<Profile, Long> tracker = new ConcurrentHashMap<>();

    protected AbstractPowerManager(ClansConfig config, Scheduler scheduler) {
        int seconds = config.powerGiftInterval * 60;
        scheduler.asyncRepeating(() -> {
            Collection<Profile> cached = ClanAPI.get().cachedProfiles();

            // remove old profiles
            new HashSet<>(tracker.keySet()).stream()
                    .filter(profile -> !cached.contains(profile))
                    .forEach(tracker::remove);

            cached.forEach(profile -> {
                tracker.putIfAbsent(profile, profile.playTime());
                long previous = tracker.get(profile);
                long diff = profile.playTime() - previous;

                if (diff >= seconds) {
                    tracker.put(profile, previous + seconds);
                    profile.setPower(profile.power() + config.powerGiftAmount);
                    ClanAPI.get().update(profile);
                }
            });

        }, 1, TimeUnit.SECONDS);
    }

}
