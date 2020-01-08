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
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })
        doLogin()
        gotoRegister()
    }

    private fun handleUIState(it : UserState){
        when(it){
            is UserState.IsLoading -> {
                if (it.state){
                    pb_login.visibility = View.VISIBLE
                    btn_login.isEnabled = false
                }else{
                    btn_login.isEnabled = true
                    pb_login.visibility = View.GONE
                }
            }
            is UserState.ShowToast -> Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()

            is UserState.Success -> {
                println("isi dari token adalah "+it.token)
                Constants.setToken(this@LoginActivity,"Bearer ${it.token}")
                startActivity(Intent(this@LoginActivity, MainActivity::class.java)).also { finish() }
            }

            is UserState.Reset -> {
                setEmailError(null)
                setPasswordError(null)
            }

            is UserState.Validate -> {
                it.email?.let {
                    setEmailError(it)
                }
                it.password?.let {
                    setPasswordError(it)
                }
            }
        }
    }

    private fun doLogin(){
        btn_login.setOnClickListener {
            val email = ed_email_login.text.toString().trim()
            val password = ed_password_login.text.toString().trim()
            if (userViewModel.validate(null, email, password)){
                userViewModel.login(email, password)
            }
        }
    }

    private fun gotoRegister(){
        btn_to_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun setEmailError(err : String?){ in_email_login.error = err }

    private fun setPasswordError(err : String?){ in_password_login.error = err }

    override fun onResume() {
        super.onResume()
        if(!Constants.getToken(this).equals("UNDEFINED")){
            startActivity(Intent(this, MainActivity::class.java)).also { finish() }
        }
    }
}
