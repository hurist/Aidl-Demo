package com.example.aidl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallbackEntity(
    val message: String,
    val code: Int
) : Parcelable