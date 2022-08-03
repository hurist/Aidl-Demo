// IMyAidlInterface.aidl
package com.example.aidl;
import com.example.aidl.Callback;
import com.example.aidl.PriceCallback;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    int getPid();

    void pay(in Callback callback);

    void registerCallback(PriceCallback callback);
    void unregisterCallback(PriceCallback callback);
}