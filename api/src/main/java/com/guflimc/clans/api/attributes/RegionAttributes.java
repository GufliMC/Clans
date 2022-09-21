package com.guflimc.clans.api.attributes;

import com.guflimc.brick.regions.api.attributes.AttributeKey;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.ClanAPI;

import java.util.UUID;

public class RegionAttributes {

    public final static AttributeKey<Clan> CLAN = new AttributeKey<>(
            "clan", Clan.class,
            (clan) -> clan.id().toString(),
            (str) -> ClanAPI.get().findClan(UUID.fromString(str)).orElse(null)
    );

}
