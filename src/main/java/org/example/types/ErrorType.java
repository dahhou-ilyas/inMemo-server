package org.example.types;

public class ErrorType extends DataType {
    private final String value;

    public ErrorType(String value) {
        super();
        this.value = value;
    }

    @Override
    public RedisDataType getType() {
        return RedisDataType.ERROR;
    }

    @Override
    public String getFormattedValue() {
        return "-" + value + "\r\n";
    }

    @Override
    public String getRawValue() {
        return value;
    }
}
