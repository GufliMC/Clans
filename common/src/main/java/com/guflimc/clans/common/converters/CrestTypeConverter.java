package com.guflimc.clans.common.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guflimc.clans.api.cosmetic.CrestType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

@Converter
public class CrestTypeConverter implements AttributeConverter<CrestType, String> {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(RecordTypeAdapterFactory.builder().allowMissingComponentValues().create())
            .create();

    @Override
    public String convertToDatabaseColumn(CrestType attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public CrestType convertToEntityAttribute(String dbData) {
        return gson.fromJson(dbData, CrestType.class);
    }
}