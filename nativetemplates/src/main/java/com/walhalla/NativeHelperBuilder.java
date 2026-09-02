package com.walhalla;

public class NativeHelperBuilder {

    private int var0 = 1;
    private int var1 = 3;
    private String id;

    public NativeHelperBuilder setNumberOfAds(int var0) {
        this.var0 = var0;
        return this;
    }


    public NativeHelperBuilder setMaxFetchAttempt(int var0) {
        this.var1 = var0;
        return this;
    }

    public NativeHelperBuilder setUnitId(String var0) {
        this.id = var0;
        return this;
    }

    public NativeHelper create() {
        return new NativeHelper(var0, var1, id);
    }
}
