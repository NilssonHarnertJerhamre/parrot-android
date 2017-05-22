package comnilssonharnertjerhamre.httpsgithub.parrot_android;

/**
 * Created by henriknh on 5/18/17.
 *
 * Public static class that hold a variable representing id of user.
 * Getters and setters to access variable.
 */

public class DataHolder {
    private static int data;

    public static int getData() {
        return data;
    }

    public static int setData(int data) {
        DataHolder.data = data;
        return data;
    }
}