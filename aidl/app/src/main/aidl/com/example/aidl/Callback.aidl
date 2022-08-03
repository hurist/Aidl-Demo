// Callback.aidl
package com.example.aidl;
import com.example.aidl.CallbackEntity;

interface Callback {
    void call(in CallbackEntity entity);
}
