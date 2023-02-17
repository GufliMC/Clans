package com.guflimc.clans.common;

import com.guflimc.brick.orm.ebean.database.EbeanConfig;
import com.guflimc.config.common.ConfigComment;

public class ClansConfig {

    @ConfigComment("DO NOT TOUCH THIS! ADVANCED USAGE ONLY!")
    public EbeanConfig database;

    @Deprecated
    public transient String clanChatPrefix = "<insert:/clans info {clan_name}><hover:show_text:'{clan_display_name}'><gray>[{clan_display_tag}]</gray></hover></insert>";

    @Deprecated
    public transient String clanNametagPrefix = "<gray>[{clan_display_tag}]</gray> ";

    @Deprecated
    public transient String noClanDisplayName = "";

}
