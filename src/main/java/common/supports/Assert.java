package common.supports;

public class Assert {

    // ~ ===============================================================================================================

    public static void nonNull(Object object, String code, String msg, String... args) {
        if(object == null) {
            new Throwables(code, msg, args).throwEx();
        }
    }
    public static void nonNull(Object object, String code) {
        if(object == null) {
            new Throwables(code).throwEx();
        }
    }
    public static void nonNull(Object object, String code, String msg) {
        if(object == null) {
            new Throwables(code, msg).throwEx();
        }
    }

    public static void isTrue(boolean bool, String code, String msg, String... args) {
        if(!bool) {
            new Throwables(code, msg, args).throwEx();
        }
    }
    public static void isTrue(boolean bool, String code) {
        if(!bool) {
            new Throwables(code).throwEx();
        }
    }
    public static void isTrue(boolean bool, String code, String msg) {
        if(!bool) {
            new Throwables(code, msg).throwEx();
        }
    }

}
