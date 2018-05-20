package de.unikl.hci.abbas.behaviometric.TouchLogger.gestures;

public enum Gesture {
    Tap,
    LongPress,
    Scroll,
    Swipe,
    TwoFinger,
    Unidentified;

    public boolean isSupported() {
        return this == Tap || this == LongPress || this == Swipe || this == Scroll;
    }
}
