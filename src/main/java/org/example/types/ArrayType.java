package org.example.types;

public class ArrayType extends DataType {
    private final DataType[] values;

    public ArrayType(DataType[] values) {
        super();
        this.values = values;
    }

    @Override
    public RedisDataType getType() {
        return RedisDataType.ARRAY;
    }

    @Override
    public String getFormattedValue() {
        StringBuilder sb = new StringBuilder();
        sb.append("*");
        sb.append(values.length);
        sb.append("\r\n");
        for (DataType value : values) sb.append(value.getFormattedValue());
        return sb.toString();
    }

    @Override
    public DataType[] getRawValue() {
        return values;
    }
}
