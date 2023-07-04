package com.test.GetProducts.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.R

class Launcher : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Replace the fragment container with BlankFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GetProducts())
            .commit()
    }
}
