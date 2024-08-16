package org.example.types;

public class IntegerType extends DataType {
    private final long value;

    public IntegerType(long value) {
        super();
        this.value = value;
    }

    @Override
    public RedisDataType getType() {
        return RedisDataType.INTEGER;
    }

    @Override
    public String getFormattedValue() {
        return ":" + value + "\r\n";
    }

    @Override
    public Long getRawValue() {
        return value;
    }
}
