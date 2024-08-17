package org.example.types;

public class SimpleStringType extends DataType {
    private final String value;

    public SimpleStringType(String value) {
        this.value = value;
    }

    @Override
    public TypeDataType getType() {
        return TypeDataType.SIMPLE_STRING;
    }

    @Override
    public String getFormattedValue() {
        return "+" + value + "\r\n";
    }

    @Override
    public String getRawValue() {
        return value;
    }
}
