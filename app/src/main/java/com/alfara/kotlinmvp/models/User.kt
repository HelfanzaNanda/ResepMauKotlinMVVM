package com.alfara.kotlinmvp.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") var id : Int ?= null,
    @SerializedName("name") var name : String ?= null,
    @SerializedName("email") var email : String ?= null,
    @SerializedName("api_token") var api_token : String? = null

)