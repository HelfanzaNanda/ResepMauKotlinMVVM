package com.alfara.kotlinmvp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alfara.kotlinmvp.models.Post
import com.alfara.kotlinmvp.utils.Constants
import com.alfara.kotlinmvp.viewmodels.RecipeState
import com.alfara.kotlinmvp.viewmodels.RecipeViewModel
import kotlinx.android.synthetic.main.activity_recipe.*

class RecipeActivity : AppCompatActivity() {
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        recipeViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })
        fill()
    }

    private fun handleUIState(it : RecipeState){
        when(it){
            is RecipeState.IsLoading -> {
                btn_submit.isEnabled = !it.state
            }
            is RecipeState.ShowToast -> Toast.makeText(this@RecipeActivity, it.message, Toast.LENGTH_SHORT).show()
            is RecipeState.Success -> finish()
            is RecipeState.Reset -> {
                setTitleError(null)
                setContentError(null)
            }
            is RecipeState.Validate -> {
                it.title?.let {
                    setTitleError(it)
                }
                it.content?.let {
                    setContentError(it)
                }
            }
        }
    }

    private fun getRecipe() = intent.getParcelableExtra<Post>("RECIPE")

    private fun setTitleError(err : String?){ in_title.error = err }
    private fun setContentError(err: String?){ in_content.error = err }
    private fun isInsert() = intent.getBooleanExtra("IS_INSERT", true)
    private fun fill(){
        if (!isInsert()){
            btn_submit.text = "Update"
            ed_title.setText(getRecipe().title)
            ed_content.setText(getRecipe().content)

            btn_submit.setOnClickListener {
                val title = ed_title.text.toString().trim()
                val content = ed_content.text.toString().trim()
                if (recipeViewModel.validate(title, content)){
                    recipeViewModel.updateRecipe(Constants.getToken(this@RecipeActivity), getRecipe().id.toString(), title, content)
                }
            }
        }else{
            btn_submit.text = "Save"
            btn_submit.setOnClickListener {
                val title = ed_title.text.toString().trim()
                val content = ed_content.text.toString().trim()
                if (recipeViewModel.validate(title, content)){
                    recipeViewModel.createRecipe(Constants.getToken(this@RecipeActivity), title, content)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!isInsert()){
            menuInflater.inflate(R.menu.menu_recipe, menu)
            return true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                recipeViewModel.deleteRecipe(Constants.getToken(this@RecipeActivity), getRecipe().id.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
