package br.com.impacta.parte2_laboratorio19

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.impacta.parte2_laboratorio19.data.Contato
import br.com.impacta.parte2_laboratorio19.databinding.FragmentHomeBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lista = gerarListaContatos(5)
        binding.button.setOnClickListener {
            binding.recyclerView.adapter = Adaptador(lista)
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
        }
        binding.button2.setOnClickListener {
            val jsonArray = gerarJSONArray(lista)
            val asyncTask  = minhaAsyncTask()
            asyncTask.execute(jsonArray)
        }
    }

    fun gerarListaContatos(quantidade: Int): MutableList<Contato> {
        val lista = mutableListOf<Contato>()

        for (i in 0 until quantidade) {
            val contato = Contato(i.toLong(), "Nome - $i", i * 2)
            lista.add(contato)
        }

        return lista
    }

    fun gerarJSONArray(lista: MutableList<Contato>): JSONArray {
        var jsonArray = JSONArray()
        lista.forEach {
            jsonArray.put(it.toJSONObject())
        }
        return jsonArray
    }

    fun gerarContatoJSONObject(json: JSONObject): Contato {
        val idContato = json.getLong("idContato")
        val nome = json.getString("nome")
        val idade = json.getInt("idade")
        return Contato(idContato, nome, idade)
    }

    fun recuperarListaContatos(json: JSONArray): MutableList<Contato> {
        val lista = mutableListOf<Contato>()
        for (i in 0 until json.length()) {
            lista.add(gerarContatoJSONObject(json[i] as JSONObject))
        }
        return lista
    }

    inner class minhaAsyncTask(): AsyncTask<JSONArray, Void, JSONObject>() {
        override fun doInBackground(vararg p0: JSONArray?): JSONObject {
            val apiUrl = "http://www.nmsystems.com.br/testecarga.php"
            val url = URL(apiUrl)
            var resposta = ""

            (url.openConnection() as? HttpURLConnection)?.let { conexao ->
                conexao.requestMethod = "POST"
                conexao.doInput = true
                conexao.doOutput = true
                conexao.connectTimeout = 15000
                conexao.readTimeout = 15000
                conexao.connect()

                val os = conexao.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os))
                writer.write(p0[0].toString())
                writer.flush()

                val inputStream = if (conexao.responseCode == HttpURLConnection.HTTP_OK) {
                    conexao.inputStream
                } else {
                    conexao.errorStream
                }

                resposta = inputStream.reader().readText()
            }
            return JSONObject(resposta)
        }
    }


}