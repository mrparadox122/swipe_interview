package com.test.GetProducts.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.test.AddProducts.View.AddProducts
import com.test.Controllers.ProductService
import com.test.GetProducts.Model.Product
import com.test.R
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetProducts : Fragment() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var search: EditText
    private lateinit var adapter: ProductAdapter
    private lateinit var AddProductButton: ImageButton
    private var productList: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_get_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetViews(view)
    }
    private fun SetViews(view: View)
    {
        toolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        AddProductButton = view.findViewById(R.id.AddProduct)
        search = view.findViewById(R.id.Search)
        search.addTextChangedListener { text ->
            val query = text?.toString()?.trim()
            filterProducts(query)
        }
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = ProductAdapter()
        recyclerView.adapter = adapter
        SetClicks()
    }

    private fun SetClicks()
    {
        AddProductButton.setOnClickListener { AddProduct() }
        callApi()
    }

    private fun callApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/api/public/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val productService = retrofit.create(ProductService::class.java)

        lifecycleScope.launch {
            try {
                productList = productService.getProducts()
                adapter.submitList(productList)
            } catch (e: Exception) {
                // Handle error
                Toast.makeText(requireContext(), "Internal Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterProducts(query: String?) {
        if (query.isNullOrEmpty()) {
            adapter.submitList(productList)
        } else {
            val filteredList = productList.filter { product ->
                product.productName.contains(query, ignoreCase = true)
            }
            adapter.submitList(filteredList)
        }
    }

    private fun AddProduct() {
        val addProductFragment = AddProducts() // Create an instance of the AddProductFragment
        val fragmentManager = requireActivity().supportFragmentManager // Get the fragment manager
        val fragmentTransaction = fragmentManager.beginTransaction() // Start a fragment transaction
        fragmentTransaction.replace(R.id.fragment_container, addProductFragment) // Replace the current fragment with the AddProductFragment
        fragmentTransaction.addToBackStack(null) // Add the transaction to the back stack
        fragmentTransaction.commit() // Commit the transaction
    }
}