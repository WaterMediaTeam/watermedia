package me.srrapero720.watermedia.api.config.values;

public class IntegerValue implements Value<Integer> {
    final String key;
    Integer value;
    public IntegerValue(String key, Integer defaultValue) {
        this.key = key;
        this.value = defaultValue;
    }

    public Integer get() { return value; }

    public void set(Integer value) { this.value = value; }

    @Override
    public String getStringValue() {
        return key + "=" + value.toString();
    }
}