package com.test.AddProducts.View

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.atwa.filepicker.core.FilePicker
import com.bumptech.glide.Glide
import com.test.Controllers.ProductService
import com.test.GetProducts.View.GetProducts
import com.test.R
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AddProducts : Fragment() {
    private lateinit var imageViewProduct: CircleImageView
    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductType: EditText
    private lateinit var editTextProductPrice: EditText
    private lateinit var editTextProductTax: EditText
    private lateinit var buttonUpload: Button
    private lateinit var buttonCancel: Button
    private lateinit var productService: ProductService
    private val filePicker = FilePicker.getInstance(this)
    private var file: File? = null // Set this to the actual File if available, otherwise keep it null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews(view)
        setClicks()
        initRetrofit()
    }
    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/api/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productService = retrofit.create(ProductService::class.java)
    }

    private fun setViews(view: View) {
        imageViewProduct = view.findViewById(R.id.imageViewProduct)
        editTextProductName = view.findViewById(R.id.editTextProductName)
        editTextProductType = view.findViewById(R.id.editTextProductType)
        editTextProductPrice = view.findViewById(R.id.editTextProductPrice)
        editTextProductTax = view.findViewById(R.id.editTextProductTax)
        buttonUpload = view.findViewById(R.id.buttonUpload)
        buttonCancel = view.findViewById(R.id.buttonCancel)
    }

    private fun setClicks() {
        imageViewProduct.setOnClickListener { SelectImage() }

        buttonUpload.setOnClickListener {uploadProduct()}

        buttonCancel.setOnClickListener {CancelUpload()}
    }


    private fun uploadProduct() {
        val productName =
            editTextProductName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val productType =
            editTextProductType.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val price =
            editTextProductPrice.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val tax = editTextProductTax.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())


        val requestFile = file?.let {
            it.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        }

        val multipartBody = requestFile?.let {
            MultipartBody.Part.createFormData("files[]", file?.name, it)
        }
        Log.e("uploadProduct: ", productName.toString())

        val call = productService.addProduct(productName, productType, price, tax, multipartBody)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Handle success response
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Process the response body
                    val getProductFragment = GetProducts() // Create an instance of the AddProductFragment
                    val fragmentManager = requireActivity().supportFragmentManager // Get the fragment manager
                    val fragmentTransaction = fragmentManager.beginTransaction() // Start a fragment transaction
                    fragmentTransaction.replace(R.id.fragment_container, getProductFragment) // Replace the current fragment with the AddProductFragment
                    fragmentTransaction.addToBackStack(null) // Add the transaction to the back stack
                    fragmentTransaction.commit() // Commit the transaction

                } else {
                    Log.wtf("onResponse: ",response.body().toString() )
                    Log.wtf("onResponse: ",response.errorBody().toString() )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle network failure or API call failure
                Log.wtf( "onFailure: ", t.toString())
            }
        })
    }

    private fun CancelUpload()
    {
        val getProductFragment = GetProducts() // Create an instance of the AddProductFragment
        val fragmentManager = requireActivity().supportFragmentManager // Get the fragment manager
        val fragmentTransaction = fragmentManager.beginTransaction() // Start a fragment transaction
        fragmentTransaction.replace(R.id.fragment_container, getProductFragment) // Replace the current fragment with the AddProductFragment
        fragmentTransaction.addToBackStack(null) // Add the transaction to the back stack
        fragmentTransaction.commit() // Commit the transaction
    }

    private fun SelectImage()
    {
        filePicker.pickImage { meta ->
            val file : File? = meta?.file
            val bitmap : Bitmap? = meta?.bitmap
            // Set the bitmap to the CircleImageView using Glide
            Glide.with(requireContext())
                .load(bitmap)
                .error(R.drawable.baseline_add_photo_alternate_24)
                .into(imageViewProduct)

            this.file = file
        }
    }
}