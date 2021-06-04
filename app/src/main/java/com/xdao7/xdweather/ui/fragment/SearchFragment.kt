package com.xdao7.xdweather.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xdao7.xdweather.R
import com.xdao7.xdweather.databinding.FragmentSearchBinding
import com.xdao7.xdweather.ui.adapter.SearchAdapter
import com.xdao7.xdweather.ui.viewmodel.SearchViewModel
import com.xdao7.xdweather.utils.getStatusBarHeight

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.clActionbar.setPadding(0, getStatusBarHeight(), 0, 0)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViews()
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        binding.apply {
            adapter = SearchAdapter(this@SearchFragment, viewModel.placeList)
            rvPlace.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = this@SearchFragment.adapter
            }

            btnSearch.setOnClickListener {
                search()
            }

            etSearch.setOnEditorActionListener { _, i, _ ->
                return@setOnEditorActionListener if (i == EditorInfo.IME_ACTION_SEARCH) {
                    search()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun initData() {
        viewModel.apply {
            placeLiveData.observe(viewLifecycleOwner) { result ->
                val places = result.getOrNull()
                if (places != null) {
                    binding.rvPlace.visibility = View.VISIBLE
                    placeList.clear()
                    placeList.addAll(places)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(activity, R.string.str_no_search, Toast.LENGTH_SHORT).show()
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
        }
    }

    private fun search() {
        val content = binding.etSearch.text.toString()
        if (content.isNotEmpty()) {
            viewModel.searchPlaces(content)
        } else {
            binding.rvPlace.visibility = View.GONE
            viewModel.placeList.clear()
            adapter.notifyDataSetChanged()
        }
    }
}