package com.guflimc.clans.common;

import com.guflimc.brick.maths.database.api.AreaConverter;
import com.guflimc.brick.maths.database.api.LocationConverter;
import com.guflimc.brick.orm.ebean.database.EbeanConfig;
import com.guflimc.brick.orm.ebean.database.EbeanDatabaseContext;
import com.guflimc.brick.orm.ebean.database.EbeanMigrations;
import com.guflimc.clans.common.converters.CrestConfigConverter;
import com.guflimc.clans.common.converters.CrestTypeConverter;
import com.guflimc.clans.common.domain.*;
import io.ebean.annotation.Platform;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;

public class ClansDatabaseContext extends EbeanDatabaseContext {

    public ClansDatabaseContext(EbeanConfig config) {
        super(config, "Clans");
    }

    public ClansDatabaseContext(EbeanConfig config, int poolSize) {
        super(config, "Clans", poolSize);
    }

    @Override
    protected Class<?>[] applicableClasses() {
        return APPLICABLE_CLASSES;
    }

    private static final Class<?>[] APPLICABLE_CLASSES = new Class[]{
            DClan.class,
            DClanProfile.class,
            DNexus.class,
            DProfile.class,
            DClanInvite.class,
            DClanProfilePermission.class,
            DAttack.class,
            DCrestTemplate.class,

            AreaConverter.class,
            LocationConverter.class,
            CrestTypeConverter.class,
            CrestConfigConverter.class
    };

    public static void main(String[] args) throws IOException, SQLException {
        EbeanMigrations generator = new EbeanMigrations(
                "Clans",
                Path.of("Clans/common/src/main/resources"),
                Platform.H2, Platform.MYSQL
        );
        Arrays.stream(APPLICABLE_CLASSES).forEach(generator::addClass);
        generator.generate();
    }

}
