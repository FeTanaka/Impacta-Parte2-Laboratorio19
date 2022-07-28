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
import java.net.URLEncoder


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
            val json = gerarJSONObjectLista(lista)
            val asyncTask = minhaAsyncTask()
            asyncTask.execute(json)
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

    fun gerarJSONObjectLista(lista: MutableList<Contato>): JSONObject {
        var jsonArray = JSONArray()
        lista.forEach {
            jsonArray.put(it.toJSONObject())
        }
        val json = JSONObject()
        json.put("contatos", jsonArray)
        return json
    }

    fun gerarContatoJSONObject(json: JSONObject): Contato {
        val idContato = if (json.has("idcontato")) json.getLong("idcontato") else null
        val nome = json.getString("nome")
        val idade = if (json.has("idade")) json.getInt("idade") else null
        return Contato(idContato, nome, idade)
    }

    fun recuperarListaContatos(json: JSONObject): MutableList<Contato> {
        val lista = mutableListOf<Contato>()
        val array = json.getJSONArray("contatos")
        for (i in 0 until array.length()) {
            lista.add(gerarContatoJSONObject(array[i] as JSONObject))
        }
        return lista
    }

    inner class minhaAsyncTask() : AsyncTask<JSONObject, Void, JSONObject>() {
        override fun doInBackground(vararg p0: JSONObject?): JSONObject {
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
                val formatados = "${URLEncoder.encode("json", "UTF-8")}=${
                    URLEncoder.encode(
                        p0[0].toString(),
                        "UTF-8"
                    )
                }"
                writer.write(formatados.toString())
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

        override fun onPostExecute(result: JSONObject?) {
            super.onPostExecute(result)

            val adapter = Adaptador(recuperarListaContatos(result!!))
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }


}