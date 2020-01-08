package com.alfara.kotlinmvp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alfara.kotlinmvp.models.Post
import com.alfara.kotlinmvp.utils.SingleLiveEvent
import com.alfara.kotlinmvp.utils.WrappedListResponse
import com.alfara.kotlinmvp.utils.WrappedResponse
import com.alfara.kotlinmvp.webservice.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewModel : ViewModel(){
    private var recipes = MutableLiveData<List<Post>>()
    private var state : SingleLiveEvent<RecipeState> = SingleLiveEvent()
    private var api  = ApiClient.instance()

    fun fetchAllRecipe(token : String){
        state.value = RecipeState.IsLoading(true)
        api.getAllPost(token).enqueue(object : Callback<WrappedListResponse<Post>>{
            override fun onFailure(call: Call<WrappedListResponse<Post>>, t: Throwable) {
                state.value = RecipeState.IsLoading(false)
                state.value = RecipeState.ShowToast(t.message.toString())
                println(""+t.message)

            }

            override fun onResponse(call: Call<WrappedListResponse<Post>>, response: Response<WrappedListResponse<Post>>) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedListResponse
                    if (body.status.equals("1")){
                        val data = body.data
                        recipes.postValue(data)
                    }
                }else{
                    state.value = RecipeState.ShowToast("tidak dapat mengambil data")
                }
                state.value =RecipeState.IsLoading(false)
            }
        })
    }

    fun createRecipe(token : String, title : String, content : String){
        state.value = RecipeState.IsLoading(true)
        api.create(token, title, content).enqueue(object : Callback<WrappedResponse<Post>>{
            override fun onFailure(call: Call<WrappedResponse<Post>>, t: Throwable) {
                state.value = RecipeState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedResponse<Post>>, response: Response<WrappedResponse<Post>>) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse
                    if (body.status.equals("1")){
                        state.value = RecipeState.ShowToast("berhasil menambah data")
                        state.value = RecipeState.Success
                    }else{
                        state.value = RecipeState.ShowToast("gagal menambah data")
                    }
                }else{
                    state.value = RecipeState.ShowToast("gagal menambahkan")
                }
                state.value = RecipeState.IsLoading(false)
            }
        })
    }

    fun updateRecipe(token : String, id : String, title: String, content: String){
        state.value = RecipeState.IsLoading(true)
        api.update(token, id, title, content).enqueue(object : Callback<WrappedResponse<Post>>{
            override fun onFailure(call: Call<WrappedResponse<Post>>, t: Throwable) {
                state.value = RecipeState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedResponse<Post>>, response: Response<WrappedResponse<Post>>) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse
                    if (body.status.equals("1")){
                        state.value = RecipeState.ShowToast("Berhasil mengupdate data")
                        state.value = RecipeState.Success
                    }else{
                        state.value = RecipeState.ShowToast("gagal mengupdate data")
                    }
                }else{
                    state.value = RecipeState.ShowToast("gagal update")
                }
                state.value = RecipeState.IsLoading(false)
            }

        })
    }

    fun deleteRecipe(token: String, id: String){
        state.value = RecipeState.IsLoading(true)
        api.delete(token, id).enqueue(object : Callback<WrappedResponse<Post>>{
            override fun onFailure(call: Call<WrappedResponse<Post>>, t: Throwable) {
                state.value = RecipeState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedResponse<Post>>, response: Response<WrappedResponse<Post>>) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse
                    if (body.status.equals("1")){
                        state.value = RecipeState.ShowToast("Berhasil Menghapus data")
                        state.value = RecipeState.Success
                    }else{
                        state.value = RecipeState.ShowToast("gagal menghapus data")
                    }
                }else{
                    state.value = RecipeState.ShowToast("gagal hapus")
                }
                state.value = RecipeState.IsLoading(false)
            }

        })
    }

    fun validate(title: String, content: String) : Boolean{
        state.value = RecipeState.Reset
        if (title.isEmpty() || content.isEmpty()){
            state.value = RecipeState.ShowToast("harap isi semua form")
            return false
        }
        if (title.length < 5){
            state.value = RecipeState.Validate(title = "title setidaknya 5 karakter")
            return false
        }
        if (content.length < 20){
            state.value = RecipeState.Validate(content = "content setidaknya 20 karakter")
            return false
        }
        return true
    }

    fun getState() = state
    fun getRecipes() = recipes
}

sealed class RecipeState{
    data class IsLoading(var state: Boolean = false) : RecipeState()
    data class ShowToast(var message : String) : RecipeState()
    object Success : RecipeState()
    data class Validate(
        var title : String ?= null,
        var content : String ?= null
    ) : RecipeState()
    object Reset : RecipeState()
}