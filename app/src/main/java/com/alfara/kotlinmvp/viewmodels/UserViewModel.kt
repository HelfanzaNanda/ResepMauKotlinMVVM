package com.alfara.kotlinmvp.viewmodels

import androidx.lifecycle.ViewModel
import com.alfara.kotlinmvp.models.User
import com.alfara.kotlinmvp.utils.Constants
import com.alfara.kotlinmvp.utils.SingleLiveEvent
import com.alfara.kotlinmvp.utils.WrappedResponse
import com.alfara.kotlinmvp.webservice.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel(){
    private var state : SingleLiveEvent<UserState> = SingleLiveEvent()
    private var api = ApiClient.instance()

    fun login(email : String, password: String){
        state.value = UserState.IsLoading(true)
        api.login(email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message.toString())
                state.value = UserState.ShowToast(t.message.toString())
                state.value = UserState.IsLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse
                    if (body.status.equals("1")){
                        val token = body.data!!.api_token
                        println("isi dari token vm adalah $token")
                        state.value = UserState.ShowToast("selamat datang $email")
                        state.value = UserState.Success(token!!)
                    }else{
                        state.value = UserState.ShowToast("Tidak Dapat Login!")
                    }
                }else{
                    state.value = UserState.ShowToast("Login Gagal")
                }
                state.value = UserState.IsLoading(false)
            }
        })
    }

    fun register (name : String, email: String, password: String){
        state.value = UserState.IsLoading(true)
        api.register(name, email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                state.value = UserState.ShowToast(t.message.toString())
                state.value = UserState.IsLoading(false)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse
                    if (body.status.equals("1")){
                        val token = body.data!!.api_token
                        state.value = UserState.ShowToast("selamat datang $email")
                        state.value = UserState.Success(token!!)
                    }else{
                        state.value = UserState.ShowToast("Tidak dapat login")
                    }
                }else{
                    state.value = UserState.ShowToast("Login Gagal")
                }
                state.value = UserState.IsLoading(false)
            }
        })
    }

    fun validate(name : String?, email : String, password : String) : Boolean{
        state.value = UserState.Reset
        if (name != null ){
            if (name.isEmpty()){
                state.value = UserState.ShowToast("nama tidak boleh kosong")
                return false
            }
            if (name.length < 5){
                state.value = UserState.Validate(name = "nama setidaknya 5 karakter")
                return false
            }

        }
        if (email.isEmpty() || password.isEmpty()){
            state.value = UserState.ShowToast("mohon isi semua form")
            return false
        }
        if (!Constants.isValidEmail(email)){
            state.value = UserState.Validate(email = "email tidak valid")
            return false
        }
        if (!Constants.isValidPassword(password)){
            state.value = UserState.Validate(password = "password tidak valid")
            return false
        }
        return true
    }
    fun getState() = state
}

sealed class UserState{
    data class IsLoading (var state: Boolean = false) : UserState()
    object Reset : UserState()
    data class ShowToast(var message : String) : UserState()

    data class Validate(
        var name : String ?= null,
        var password : String ?= null,
        var email : String ?= null
    ) : UserState()

    data class Success(var token : String) : UserState()

}