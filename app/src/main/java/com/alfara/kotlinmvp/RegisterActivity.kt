package com.alfara.kotlinmvp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alfara.kotlinmvp.utils.Constants
import com.alfara.kotlinmvp.viewmodels.UserState
import com.alfara.kotlinmvp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })
        doRegister()
    }

    private fun handleUIState(it : UserState){
        when(it){
            is UserState.IsLoading -> {
                if (it.state){
                    pb_register.visibility = View.VISIBLE
                    btn_register.isEnabled = false
                    pb_register.isIndeterminate = true
                }else{
                    pb_register.isIndeterminate = false
                    btn_register.isEnabled = true
                    pb_register.visibility = View.GONE
                }
            }
            is UserState.ShowToast -> Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()

            is UserState.Success -> {
                println("isi dari token adalah "+it.token)
                Constants.setToken(this@RegisterActivity,"Bearer ${it.token}")
                startActivity(Intent(this@RegisterActivity, MainActivity::class.java)).also { finish() }
            }

            is UserState.Reset -> {
                setNameError(null)
                setEmailError(null)
                setPasswordError(null)
            }

            is UserState.Validate -> {
                it.name?.let {
                    setNameError(it)
                }
                it.email?.let {
                    setEmailError(it)
                }
                it.password?.let {
                    setPasswordError(it)
                }
            }
        }
    }

    private fun setNameError(err : String?){
        in_name_register.error = err
    }

    private fun setEmailError(err : String?){
        in_email_register.error = err
    }

    private fun setPasswordError(err : String?){
        in_password_regigster.error = err
    }

    private fun doRegister(){
        btn_register.setOnClickListener {
            val name = ed_name_register.text.toString().trim()
            val email = ed_email_register.text.toString().trim()
            val pass = ed_password_register.text.toString().trim()
            if (userViewModel.validate(name, email, pass)){
                userViewModel.register(name, email, pass)
            }
        }
    }
}
