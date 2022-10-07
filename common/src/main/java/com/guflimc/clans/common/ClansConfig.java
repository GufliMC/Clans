package com.guflimc.clans.common;

import com.guflimc.brick.orm.database.HibernateConfig;

import java.util.List;

public class ClansConfig {

    public HibernateConfig database;

    public String clanChatPrefix;
    public String clanNametagPrefix;

    public SidebarTemplate attackSidebar;

    public static class SidebarTemplate {

        public String title;
        public List<String> lines;

    }

}
