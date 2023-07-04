package com.test.View

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.Models.Product
import com.test.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Arrays

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private val productList = mutableListOf<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_recyclerview_itemview, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        Log.wtf("onBindViewHolder: ", Arrays.deepToString(arrayOf(product)) )
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun submitList(list: List<Product>) {
        productList.clear()
        productList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewProduct: CircleImageView = itemView.findViewById(R.id.imageViewProduct)
        private val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
        private val textViewProductType: TextView = itemView.findViewById(R.id.textViewProductType)
        private val textViewProductPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        private val textViewProductTax: TextView = itemView.findViewById(R.id.textViewProductTax)

        fun bind(product: Product) {
            // Bind data to the views
            // Example:
             Glide.with(itemView.context)
                 .load(product.image)
                 .error(R.drawable.baseline_refresh_24)
                 .into(imageViewProduct)
             textViewProductName.text = product.productName
             textViewProductType.text = product.productType
             textViewProductPrice.text = "Price: ₹${product.price}"
             textViewProductTax.text = "Tax: ₹${product.tax}"
        }
    }
}