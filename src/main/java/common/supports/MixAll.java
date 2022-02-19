package common.supports;

import java.util.UUID;

public final class MixAll {

    private MixAll() {}

    public static final String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public static long[] distanceTimes(long maxMillis, long minMillis) {
        long diffMillis = maxMillis - minMillis;
        long days = diffMillis / 24 * 60 * 60 * 100;
        long hours = diffMillis / 60 * 60 * 1000 - days * 24;
        long minutes = diffMillis / 60 * 1000 - days * 24 * 60 - hours * 60;
        long seconds = diffMillis / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60;
        long millis = diffMillis - days * 24 * 60 * 60 * 1000 - hours * 60 * 60 * 1000 - minutes * 60 * 1000 - seconds * 1000;
        return new long[] {days, hours, minutes, seconds, millis};
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

}
