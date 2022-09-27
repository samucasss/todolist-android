package com.samuca.todolist

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.samuca.todolist.auth.Auth
import com.samuca.todolist.databinding.ActivityMainBinding
import com.samuca.todolist.model.Preferencias
import com.samuca.todolist.model.Tarefa
import com.samuca.todolist.model.TipoFiltro
import com.samuca.todolist.rest.NetworkUtils
import com.samuca.todolist.rest.PreferenciasRest
import com.samuca.todolist.rest.TarefaRest
import com.samuca.todolist.util.DataUtil
import com.samuca.todolist.view.*
import com.samuca.todolist.view.adapter.TarefaListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Locale.setDefault(Locale("pt", "BR"))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val isLogged: Boolean = Auth.getInstance(this).isLogged()
        helloMessage.isVisible = !isLogged
        listViewTarefas.isVisible = isLogged
        novo.isVisible = isLogged

        if (isLogged) {
            initTarefas()

        } else {
            helloMessage.text = "Bem-vindo ao sistema ToDoList"
        }

    }

    private fun initTarefas() {
        val itemOnClick: (Tarefa) -> Unit = { tarefa ->
            val intent = Intent(this, TarefaFormActivity::class.java)
            intent.putExtra("tarefa", tarefa as Serializable)
            startActivity(intent)
        }

        listViewTarefas.setLayoutManager(LinearLayoutManager(this))
        listViewTarefas.adapter = TarefaListAdapter(arrayListOf(),this, itemOnClick)

        novo.setOnClickListener {
            val intent = Intent(this, TarefaFormActivity::class.java)
            startActivity(intent)
        }

        getPreferencias()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val isLogged: Boolean = Auth.getInstance(this).isLogged()

        val itemPreferencias: MenuItem = menu.findItem(R.id.action_preferencias)
        val itemRegistro: MenuItem = menu.findItem(R.id.action_registro)
        val itemLogin: MenuItem = menu.findItem(R.id.action_login)
        val itemUser: MenuItem = menu.findItem(R.id.action_user)
        val itemSearch: MenuItem = menu.findItem(R.id.action_search)

        itemPreferencias.isVisible = isLogged
        itemRegistro.isVisible = !isLogged
        itemLogin.isVisible = !isLogged
        itemUser.isVisible = isLogged
        itemSearch.isVisible = isLogged

        initSearchView(itemSearch)

        return true
    }

    private fun initSearchView(itemSearch: MenuItem) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val mSearchView: SearchView? = itemSearch.actionView as SearchView

        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()))
            mSearchView.setIconifiedByDefault(false)

            val queryTextListener: SearchView.OnQueryTextListener =
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        println("query: ${query}")
                        find(query)
                        return true
                    }
                }

            mSearchView.setOnQueryTextListener(queryTextListener)
        }

        itemSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    return true;
                }
                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    recreate()
                    return true
                }
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
//            R.id.action_tecnologias -> {
//                val intent = Intent(this, TecnologiaActivity::class.java)
//                startActivity(intent)
//                true
//            }
            R.id.action_registro -> {
                val intent = Intent(this, UsuarioActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_sair -> {
                Auth.getInstance(this).logout()
                invalidateOptionsMenu()
                recreate()
                true
            }
            R.id.action_meusdados -> {
                val intent = Intent(this, UsuarioActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_preferencias -> {
                val intent = Intent(this, PreferenciasActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getPreferencias() {
        val auth: Auth = Auth.getInstance(baseContext)
        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val endpoint = retrofitClient.create(PreferenciasRest::class.java)
        val callback = endpoint.get("Bearer " + auth.authToken)

        callback.enqueue(object : Callback<Preferencias> {
            override fun onFailure(call: Call<Preferencias>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Preferencias>, response: Response<Preferencias>) {
                val preferencias: Preferencias? = response.body()
                println("preferencias: " + preferencias)

                getTarefas(preferencias)
            }
        })
    }

    private fun getTarefas(preferencias: Preferencias?) {
        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val auth: Auth = Auth.getInstance(baseContext)
        val endpoint = retrofitClient.create(TarefaRest::class.java)

        var periodo = TipoFiltro.TODOS.getPeriodo()
        if (preferencias != null) {
            val tipoFiltro = TipoFiltro.findById(preferencias.tipoFiltro)
            periodo = tipoFiltro!!.getPeriodo()
        }
        val inicioDate: Date = periodo.inicio
        val fimDate: Date =  periodo.fim

        val formatoRest = "yyyy-MM-dd"
        val inicio: String = DataUtil.format(inicioDate, formatoRest)
        val fim: String = DataUtil.format(fimDate, formatoRest)

        println("inicio: ${inicio} - fim: ${fim}")

        val callback = endpoint.findAll(inicio, fim, "Bearer " + auth.authToken)

        callback.enqueue(object : Callback<List<Tarefa>> {
            override fun onFailure(call: Call<List<Tarefa>>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Tarefa>>, response: Response<List<Tarefa>>) {
                var tarefaList: List<Tarefa>? = response.body()
                println("tarefaList: " + tarefaList)

                val done = if (preferencias != null) preferencias?.done else false
                if (tarefaList != null) {
                    tarefaList = tarefaList.filter {  it.done == done }

                    //ordena as tarefas
                    var tarefaSortList = tarefaList.sortedBy { it.data }

                    val adapter: TarefaListAdapter = listViewTarefas.adapter as TarefaListAdapter
                    adapter.addAll(tarefaSortList)
                }
            }
        })

    }

    private fun find(text: String) {
        val retrofitClient = NetworkUtils.getRetrofitInstance()

        val auth: Auth = Auth.getInstance(baseContext)
        val endpoint = retrofitClient.create(TarefaRest::class.java)

        val callback = endpoint.find(text, "Bearer " + auth.authToken)

        callback.enqueue(object : Callback<List<Tarefa>> {
            override fun onFailure(call: Call<List<Tarefa>>, t: Throwable) {
                println(t.message)
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Tarefa>>, response: Response<List<Tarefa>>) {
                var tarefaList: List<Tarefa>? = response.body()
                println("tarefaList: " + tarefaList)

                if (tarefaList != null) {
                    //ordena as tarefas
                    var tarefaSortList = tarefaList.sortedBy { it.data }

                    val adapter: TarefaListAdapter = listViewTarefas.adapter as TarefaListAdapter
                    adapter.clear()
                    adapter.addAll(tarefaSortList)
                }
            }
        })

    }

}