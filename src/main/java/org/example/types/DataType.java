package org.example.types;

public abstract class DataType {
    abstract public TypeDataType getType();

    abstract public String getFormattedValue();

    abstract public Object getRawValue();

    public String toString() {
        return (getType() + "[" + getFormattedValue() + "]").replaceAll("\\r\\n", " ");
    }
}
