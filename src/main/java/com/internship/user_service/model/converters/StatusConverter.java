package com.internship.user_service.model.converters;

import com.internship.user_service.model.enums.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Status attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public Status convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return Status.fromCode(dbData);
    }
}
