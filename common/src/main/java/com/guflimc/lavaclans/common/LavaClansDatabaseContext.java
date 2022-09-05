package com.guflimc.lavaclans.common;

import com.guflimc.lavaclans.common.domain.*;
import com.guflimc.brick.orm.database.HibernateConfig;
import com.guflimc.brick.orm.database.HibernateDatabaseContext;

public class LavaClansDatabaseContext extends HibernateDatabaseContext {

    public LavaClansDatabaseContext(HibernateConfig config) {
        super(config);
    }

    public LavaClansDatabaseContext(HibernateConfig config, int poolSize) {
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
                DClanJoinRequest.class
        };
    }

}
