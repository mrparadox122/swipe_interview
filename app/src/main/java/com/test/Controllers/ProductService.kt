package com.test.Controllers

import com.test.Models.Product
import retrofit2.http.GET

interface ProductService {
    @GET("get")
    suspend fun getProducts(): List<Product>
}
