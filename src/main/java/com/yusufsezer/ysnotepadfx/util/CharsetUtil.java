package com.yusufsezer.ysnotepadfx.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CharsetUtil {

    public static Charset determineCharset(byte[] value) {

        Charset defaultCharset = StandardCharsets.ISO_8859_1;

        if (isUTF16LE(value)) {
            return StandardCharsets.UTF_16LE;
        }

        if (isUTF16BE(value)) {
            return StandardCharsets.UTF_16BE;
        }

        if (isUTF8BOM(value) || isTextUnicode(value)) {
            return StandardCharsets.UTF_8;
        }

        return defaultCharset;
    }

    private static boolean isUTF16(final byte[] value) {
        return value != null && value.length > 2;
    }

    private static boolean isUTF16LE(final byte[] value) {
        return isUTF16(value)
                && value[0] == 0xffffffff
                && value[1] == 0xfffffffe;
    }

    private static boolean isUTF16BE(final byte[] value) {
        return isUTF16(value)
                && value[0] == 0xfffffffe
                && value[1] == 0xffffffff;
    }

    private static boolean isUTF8BOM(final byte[] value) {
        return value != null
                && value.length > 3
                && value[0] == 0xffffffef
                && value[1] == 0xffffffbb
                && value[2] == 0xffffffbf;
    }

    private static boolean isTextUnicode(final byte[] value) {
        final String newString = new String(value, StandardCharsets.UTF_8);
        final byte[] newBytes = newString.getBytes(StandardCharsets.UTF_8);
        return Arrays.equals(value, newBytes);
    }

}
