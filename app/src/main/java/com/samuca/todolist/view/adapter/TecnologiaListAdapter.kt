package com.samuca.todolist.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samuca.todolist.model.Tecnologia
import com.samuca.todolist.R
import kotlinx.android.synthetic.main.tecnologia_item.view.*

class TecnologiaListAdapter (private val tecnologiaList: MutableList<Tecnologia>,
                             private val context: Context):
    RecyclerView.Adapter<TecnologiaListAdapter.ViewHolder>() {

    fun addAll(tecnologiaList: List<Tecnologia>) {
        this.tecnologiaList.addAll(tecnologiaList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome = itemView.tecnologia_nome
        val tipo = itemView.tecnologia_tipo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.tecnologia_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tecnologia = tecnologiaList[position]
        holder.nome.text = tecnologia.nome
        holder.tipo.text = tecnologia.tipo
    }

    override fun getItemCount(): Int {
        return tecnologiaList.size
    }
}