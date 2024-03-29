package com.guflimc.clans.common;

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

    private final static String DATASOURCE_NAME = "Clans";

    public ClansDatabaseContext(EbeanConfig config) {
        super(config, DATASOURCE_NAME);
    }

    public ClansDatabaseContext(EbeanConfig config, int poolSize) {
        super(config, DATASOURCE_NAME, poolSize);
    }

    @Override
    protected Class<?>[] applicableClasses() {
        return APPLICABLE_CLASSES;
    }

    private static final Class<?>[] APPLICABLE_CLASSES = new Class[]{
            DClan.class,
            DClanProfile.class,
            DProfile.class,
            DClanInvite.class,
            DClanProfilePermission.class,
            DCrestTemplate.class,
            DClanAttribute.class,
            DProfileAttribute.class,

            CrestTypeConverter.class,
            CrestConfigConverter.class
    };

    public static void main(String[] args) throws IOException, SQLException {
        EbeanMigrations generator = new EbeanMigrations(
                DATASOURCE_NAME,
                Path.of("Clans/common/src/main/resources"),
                Platform.H2, Platform.MYSQL
        );
        Arrays.stream(APPLICABLE_CLASSES).forEach(generator::addClass);
        generator.generate();
    }

}
