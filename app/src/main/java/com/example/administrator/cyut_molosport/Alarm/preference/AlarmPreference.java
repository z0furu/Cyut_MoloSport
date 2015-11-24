package com.example.administrator.cyut_molosport.Alarm.preference;

/**
 * Created by Administrator on 2015/10/14.
 */
public class AlarmPreference  {
    public enum Key{
        ALARM_NAME,
        ALARM_ACTIVE,
        ALARM_TIME,
        ALARM_REPEAT,
        ALARM_TONE,
        ALARM_VIBRATE,
        ALARM_DIFFICULTY
    }

    public enum Type{
        BOOLEAN,
        INTEGER,
        STRING,
        LIST,
        MULTIPLE_LIST,
        TIME
    }

    private Key key;
    private String title;
    private String summary;
    private Object value;
    private String[] options;
    private Type type;

    public AlarmPreference(Key key, Object value, Type type) {
        this(key,null,null,null, value, type);
    }

    public AlarmPreference(Key key,String title, String summary, String[] options, Object value, Type type) {
        setTitle(title);
        setSummary(summary);
        setOptions(options);
        setKey(key);
        setValue(value);
        setType(type);
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
