package com.guflimc.reignofclans.common;

import com.guflimc.reignofclans.common.domain.DClan;
import com.guflimc.brick.orm.database.HibernateConfig;
import com.guflimc.brick.orm.database.HibernateDatabaseContext;
import com.guflimc.reignofclans.common.domain.DClanProfile;
import com.guflimc.reignofclans.common.domain.DCrest;
import com.guflimc.reignofclans.common.domain.DProfile;

public class ReignOfClansDatabaseContext extends HibernateDatabaseContext {

    public ReignOfClansDatabaseContext(HibernateConfig config) {
        super(config);
    }

    public ReignOfClansDatabaseContext(HibernateConfig config, int poolSize) {
        super(config, poolSize);
    }

    @Override
    protected Class<?>[] entityClasses() {
        return new Class[]{
                DClan.class,
                DClanProfile.class,
                DCrest.class,
                DProfile.class
        };
    }

}
