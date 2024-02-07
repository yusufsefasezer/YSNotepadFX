package com.yusufsezer.enumration;

public enum EOL {

    LF("Unix", "\n"),
    CRLF("Windows", "\r\n"),
    CR("Old Mac", "\r");

    private final String style;
    private final String seperator;

    private EOL(String style, String seperator) {
        this.style = style;
        this.seperator = seperator;
    }

    public String getStyle() {
        return style;
    }

    public String getSeperator() {
        return seperator;
    }

    public static EOL stringToEOL(String eolString) {
        if (LF.getSeperator().equals(eolString)) {
            return LF;
        } else if (CRLF.getSeperator().equals(eolString)) {
            return CRLF;
        } else if (CR.getSeperator().equals(eolString)) {
            return CR;
        }
        return null;
    }

    public static EOL getEOLFromString(String text) {
        int prev = -1;
        for (char ch : text.toCharArray()) {
            if (ch == '\n') {
                return prev == '\r'
                        ? CRLF : LF;
            } else if (prev == '\r') {
                return CR;
            }
            prev = ch;
        }
        return null;
    }

}
