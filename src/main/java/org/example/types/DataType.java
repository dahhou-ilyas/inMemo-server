package org.example.types;

public abstract class DataType {
    abstract public RedisDataType getType();

    abstract public String getFormattedValue();

    abstract public Object getRawValue();

    public String toString() {
        return (getType() + "[" + getFormattedValue() + "]").replaceAll("\\r\\n", " ");
    }
}
