package com.ian.android.geoquiz

import androidx.annotation.StringRes

// @StringRes告訴編譯器該參數為資源ID(R裡面的ID)
data class Question(@StringRes val textResId: Int, val answer: Boolean)