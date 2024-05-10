package com.example.proga

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proga.databinding.FragmentProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class   ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

        // Пример сохранения данных в SharedPreferences
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString().toInt()

            with(sharedPreferences.edit()) {
                putString("name", name)
                putInt("age", age)
                apply()
            }
        }

        // Пример чтения данных из SharedPreferences и заполнение полей
        val savedName = sharedPreferences.getString("name", "")
        val savedAge = sharedPreferences.getInt("age", 0)

        binding.etName.setText(savedName)
        binding.etAge.setText(savedAge.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}