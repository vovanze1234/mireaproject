package com.example.proga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proga.JsonPlaceholderApi
import com.example.proga.Post
import com.example.proga.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExampleFragment : Fragment() {

    private lateinit var textViewResult: TextView
    private lateinit var jsonPlaceholderApi: JsonPlaceholderApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_example, container, false)
        textViewResult = view.findViewById(R.id.text_view_result)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi::class.java)

        getPost()
    }

    private fun getPost() {
        val call: Call<Post> = jsonPlaceholderApi.getPost(1)

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    textViewResult.text = "Code: ${response.code()}"
                    return
                }

                val post = response.body()

                var content = ""
                content += "ID: ${post?.id}\n"
                content += "User ID: ${post?.userId}\n"
                content += "Title: ${post?.title}\n"
                content += "Text: ${post?.text}\n\n"

                textViewResult.append(content)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                textViewResult.text = t.message
            }
        })
    }
}
