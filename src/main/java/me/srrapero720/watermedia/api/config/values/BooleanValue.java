package me.srrapero720.watermedia.api.config.values;

public class BooleanValue implements Value<Boolean> {
    final String key;
    Boolean value;
    public BooleanValue(String key, Boolean defaultValue) {
        this.key = key;
        this.value = defaultValue;
    }

    public Boolean get() { return value; }

    public void set(Boolean value) { this.value = value; }

    @Override
    public String getStringValue() {
        return key + "=" + value.toString();
    }
}