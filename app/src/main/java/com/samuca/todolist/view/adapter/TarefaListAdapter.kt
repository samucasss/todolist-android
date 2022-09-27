package com.samuca.todolist.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.samuca.todolist.R
import com.samuca.todolist.model.Tarefa
import kotlinx.android.synthetic.main.tarefa_item.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TarefaListAdapter (private val tarefaList: MutableList<Tarefa>,
                         private val context: Context,
                         private val itemClickListener: (Tarefa) -> Unit):
    RecyclerView.Adapter<TarefaListAdapter.ViewHolder>() {

    fun clear() {
        this.tarefaList.clear()
    }

    fun addAll(tarefaList: List<Tarefa>) {
        this.tarefaList.addAll(tarefaList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val data = itemView.tarefa_data
        val nome = itemView.tarefa_nome
        val done = itemView.tarefa_done

        fun bind(tarefa: Tarefa, itemClickListener:(Tarefa)->Unit) {
            itemView.setOnClickListener { itemClickListener(tarefa) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.tarefa_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tarefa = tarefaList[position]

        val formatter = SimpleDateFormat("E, dd/MM/yyyy", Locale("pt", "BR"))
        holder.data.text =  formatter.format(tarefa.data)
        holder.nome.text = tarefa.nome
        holder.done.isVisible = tarefa.done

        holder.bind(tarefa, itemClickListener)
    }

    override fun getItemCount(): Int {
        return tarefaList.size
    }
}