package com.dicoding.picodiploma.mystoryapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    var name: String? = null,
    var photo: String? = null,
    var description: String? = null
) : Parcelable