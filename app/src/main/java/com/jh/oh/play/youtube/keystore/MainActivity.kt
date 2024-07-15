package com.jh.oh.play.youtube.keystore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.jh.oh.play.youtube.keystore.databinding.ActivityMainBinding
import com.jh.oh.play.youtube.keystore.utils.KeyStoreUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        ).apply {
            lifecycleOwner = this@MainActivity
        }

        binding.btnEncode.setOnClickListener {
            KeyStoreUtils.encrypt(binding.editEncode.text.toString()).apply {
                binding.textEncode.text = this
            }
        }

        binding.btnDecode.setOnClickListener {
            if(binding.textEncode.text.toString().isNotEmpty()) {
                KeyStoreUtils.decrypt(binding.textEncode.text.toString()).apply {
                    Snackbar.make(
                        binding.root,
                        this,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}