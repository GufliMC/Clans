package com.guflimc.clans.common.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guflimc.clans.api.crest.CrestConfig;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CrestConfigConverter implements AttributeConverter<CrestConfig, String> {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(RecordTypeAdapterFactory.builder().allowMissingComponentValues().create())
            .create();

    @Override
    public String convertToDatabaseColumn(CrestConfig attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public CrestConfig convertToEntityAttribute(String dbData) {
        return gson.fromJson(dbData, CrestConfig.class);
    }
}