package br.com.impacta.parte2_laboratorio19

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.impacta.parte2_laboratorio19.data.Contato
import br.com.impacta.parte2_laboratorio19.databinding.ContatoItemBinding

class Adaptador(val listaContatos: MutableList<Contato>): RecyclerView.Adapter<Adaptador.ContatoViewHolder>() {

    inner class ContatoViewHolder(val binding: ContatoItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoViewHolder {
        val binding = ContatoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContatoViewHolder, position: Int) {
        holder.binding.contato = listaContatos[position]
    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }
}