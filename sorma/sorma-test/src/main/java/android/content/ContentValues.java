package android.content;

import java.util.HashMap;

public final class ContentValues implements android.os.Parcelable {
    private HashMap<String, Object> map = new HashMap<String, Object>();
    
    public ContentValues() {
    }

    public ContentValues(int size) {
    }

    public ContentValues(android.content.ContentValues from) {
    }

    public boolean equals(java.lang.Object object) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        return map.hashCode();
    }

    public void put(java.lang.String key, java.lang.String value) {
        map.put(key, value);
    }

    public void putAll(android.content.ContentValues other) {
        map.putAll(getMap());
    }

    public void put(java.lang.String key, java.lang.Byte value) {
        map.put(key, value);
    }

    public void put(java.lang.String key, java.lang.Short value) {
        map.put(key, value);
    }

    public void put(java.lang.String key, java.lang.Integer value) {
        map.put(key, value);
    }

    public void put(java.lang.String key, java.lang.Long value) {
        map.put(key, value);
    }

    public void put(java.lang.String key, java.lang.Float value) {
        map.put(key, value);
    }

    public void put(java.lang.String key, java.lang.Double value) {
        map.put(key, value);
    }

    public void put(java.lang.String key, java.lang.Boolean value) {
        map.put(key, value);
    }

    public void put(java.lang.String key, byte[] value) {
        map.put(key, value);
    }

    public void putNull(java.lang.String key) {
        map.put(key, null);
    }

    public int size() {
        return map.size();
    }

    public void remove(java.lang.String key) {
        map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(java.lang.String key) {
        return map.containsKey(key);
    }

    public java.lang.Object get(java.lang.String key) {
        return map.get(key);
    }

    public java.lang.String getAsString(java.lang.String key) {
        return (String)map.get(key);
    }

    public java.lang.Long getAsLong(java.lang.String key) {
        return (Long)map.get(key);
    }

    public java.lang.Integer getAsInteger(java.lang.String key) {
        return (Integer)map.get(key);
    }

    public java.lang.Short getAsShort(java.lang.String key) {
        return (Short)map.get(key);
    }

    public java.lang.Byte getAsByte(java.lang.String key) {
        return (Byte)map.get(key);
    }

    public java.lang.Double getAsDouble(java.lang.String key) {
        return (Double)map.get(key);
    }

    public java.lang.Float getAsFloat(java.lang.String key) {
        return (Float)map.get(key);
    }

    public java.lang.Boolean getAsBoolean(java.lang.String key) {
        return (Boolean)map.get(key);
    }

    public byte[] getAsByteArray(java.lang.String key) {
        return (byte[])map.get(key);
    }

    public java.util.Set<java.util.Map.Entry<java.lang.String, java.lang.Object>> valueSet() {
        return map.entrySet();
    }

    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    public void writeToParcel(android.os.Parcel parcel, int flags) {
        throw new RuntimeException("Stub!");
    }

    public java.lang.String toString() {
        throw new RuntimeException("Stub!");
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

}
