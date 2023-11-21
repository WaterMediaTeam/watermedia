package me.srrapero720.watermedia.api.config.values;

public class StringValue implements Value<String> {

    final String key;
    String value;
    public StringValue(String key, String defaultValue) {
        this.key = key;
        this.value = defaultValue;
    }

    public String get() { return value; }

    public void set(String value) { this.value = value; }

    @Override
    public String getStringValue() {
        return key + "=" + value;
    }
}