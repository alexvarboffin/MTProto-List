package com.walhalla.mtprotolist;


public enum LessonState {
    UNLOCK(0), LOCK(1);

    final int value;


    LessonState(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
}