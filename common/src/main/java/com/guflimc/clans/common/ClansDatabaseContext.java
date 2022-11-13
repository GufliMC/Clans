package com.guflimc.clans.common;

import com.guflimc.brick.orm.database.HibernateConfig;
import com.guflimc.brick.orm.database.HibernateDatabaseContext;
import com.guflimc.clans.common.domain.*;

public class ClansDatabaseContext extends HibernateDatabaseContext {

    public ClansDatabaseContext(HibernateConfig config) {
        super(config);
    }

    public ClansDatabaseContext(HibernateConfig config, int poolSize) {
        super(config, poolSize);
    }

    @Override
    protected Class<?>[] entityClasses() {
        return new Class[]{
                DClan.class,
                DClanProfile.class,
                DNexus.class,
                DProfile.class,
                DClanInvite.class,
                DClanProfilePermission.class,
                DAttack.class,
                DCrestTemplate.class
        };
    }

}
