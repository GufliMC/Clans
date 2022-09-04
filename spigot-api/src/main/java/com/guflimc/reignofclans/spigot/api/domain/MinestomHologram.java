package com.guflimc.reignofclans.spigot.api.domain;

import com.guflimc.reignofclans.api.domain.Clan;
import net.minestom.server.item.ItemStack;

public interface MinestomHologram extends Clan {

    void despawn();

    void setItem(ItemStack itemStack);

    void unsetItem();

}
