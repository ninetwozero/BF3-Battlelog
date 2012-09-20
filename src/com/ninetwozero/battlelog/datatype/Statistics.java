package com.ninetwozero.battlelog.datatype;

public class Statistics {

    private int title;
    private String value;
    private int style;

    public Statistics() {
    }

    public Statistics(int title, String value, int style){
        this.title = title;
        this.value = value;
        this.style = style;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}
