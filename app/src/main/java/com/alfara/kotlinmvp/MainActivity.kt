package com.alfara.kotlinmvp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfara.kotlinmvp.adapters.PostAdapter
import com.alfara.kotlinmvp.utils.Constants
import com.alfara.kotlinmvp.viewmodels.RecipeState
import com.alfara.kotlinmvp.viewmodels.RecipeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        isLoggedIn()
        setUpUI()
        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        recipeViewModel.getRecipes().observe(this, Observer {
            rv?.adapter?.let { a ->
                if (a is PostAdapter){
                    a.changelist(it)
                }
            }
        })
        recipeViewModel.getState().observer(this, Observer {
            when(it){
                is RecipeState.IsLoading -> {
                    if (it.state){
                        pb.visibility = View.VISIBLE
                        pb.isIndeterminate = true
                    }else{
                        pb.visibility = View.GONE
                        pb.isIndeterminate = false
                    }
                }
                is RecipeState.ShowToast -> Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        fab.setOnClickListener {
            startActivity(Intent(this@MainActivity, RecipeActivity::class.java).apply {
                putExtra("IS_INSERT", true)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val token = Constants.getToken(this@MainActivity)
        recipeViewModel.fetchAllRecipe(token)
    }

    private fun setUpUI(){
        rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PostAdapter(mutableListOf(), this@MainActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Constants.clearToken(this@MainActivity)
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isLoggedIn(){
        if(Constants.getToken(this@MainActivity).equals("UNDEFINED")){
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }).also { finish() }
        }
    }
}