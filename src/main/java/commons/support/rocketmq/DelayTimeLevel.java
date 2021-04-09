package commons.support.rocketmq;

import commons.support.MixAll;

public final class DelayTimeLevel {

    public static final int LEVEL_1S = 1;
    public static final int LEVEL_5S = 2;
    public static final int LEVEL_10S = 3;
    public static final int LEVEL_30S = 4;
    public static final int LEVEL_1M = 5;
    public static final int LEVEL_2M = 6;
    public static final int LEVEL_3M = 7;
    public static final int LEVEL_4M = 8;
    public static final int LEVEL_5M = 9;
    public static final int LEVEL_6M = 10;
    public static final int LEVEL_7M = 11;
    public static final int LEVEL_8M = 12;
    public static final int LEVEL_9M = 13;
    public static final int LEVEL_10M = 14;
    public static final int LEVEL_20M = 15;
    public static final int LEVEL_30M = 16;
    public static final int LEVEL_1H = 17;
    public static final int LEVEL_2H = 18;

    public static int getDelayTimeLevel(long maxMillis, long minMillis) {
        long[] longs = MixAll.distanceTimes(maxMillis, minMillis);
        for(int i = 0; i < 5; i++) {
            long value = longs[i];
            if(value > 0) {
                if(i == 0) {
                    return LEVEL_2H;
                }
                if(i == 1) {
                    if(value >= 2) {
                        return LEVEL_2H;
                    }
                    return LEVEL_1H;
                }
                if(i == 2) {
                    if(value >= 30) {
                        return LEVEL_30M;
                    }
                    if(value >= 20) {
                        return LEVEL_20M;
                    }
                    if(value >= 10) {
                        return LEVEL_10M;
                    }
                    if(value >= 9) {
                        return LEVEL_9M;
                    }
                    if(value >= 8) {
                        return LEVEL_8M;
                    }
                    if(value >= 7) {
                        return LEVEL_7M;
                    }
                    if(value >= 6) {
                        return LEVEL_6M;
                    }
                    if(value >= 5) {
                        return LEVEL_5M;
                    }
                    if(value >= 4) {
                        return LEVEL_4M;
                    }
                    if(value >= 3) {
                        return LEVEL_3M;
                    }
                    if(value >= 2) {
                        return LEVEL_2M;
                    }
                    return LEVEL_1M;
                }
                if(i == 3) {
                    if(value >= 30) {
                        return LEVEL_30S;
                    }
                    if(value >= 10) {
                        return LEVEL_10S;
                    }
                    if(value >= 5) {
                        return LEVEL_5S;
                    }
                    return LEVEL_1S;
                }
            }
        }
        return LEVEL_1S;
    }

}
