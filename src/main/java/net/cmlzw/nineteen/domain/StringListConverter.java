package net.cmlzw.nineteen.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    public static final String DELIMITER = "|";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        System.out.println("ConvertToDBColumn: " + String.join("-", attribute));
        if (attribute == null || attribute.size() == 0) {
            return "";
        }
        return String.join(DELIMITER, attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        System.out.println("ConvertToEntityAttribute: " + dbData);
        if (dbData == null || dbData.length() == 0) {
            return new ArrayList<>();
        }
        return Arrays.asList(dbData.split(DELIMITER));
    }
}
