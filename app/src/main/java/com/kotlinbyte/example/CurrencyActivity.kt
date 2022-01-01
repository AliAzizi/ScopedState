package com.kotlinbyte.example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kotlinbyte.example.databinding.ActivityCurrencyBinding
import com.kotlinbyte.scoped_state.StateWatcher

class CurrencyActivity : AppCompatActivity() {
    lateinit var viewModel: ExampleViewModel
    lateinit var binding: ActivityCurrencyBinding

    companion object {
        const val TAG = "CurrencyActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ExampleViewModel::class.java]
        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupStateWatcher()
        //viewModel.init()
        viewModel.fetchDateAndTime()
    }

    private fun setupStateWatcher() = StateWatcher.watch(viewModel.state) {
        attach(lifecycle)

        scope<ExampleScope.FetchTime, FetchTimeState> {
            state<FetchTimeState.Data> {
                binding.timeProgress.visibility = View.GONE
                binding.time.text = it.dateTime
                binding.timeZone.text = it.timezone
            }
            state<FetchTimeState.Loading> {
                binding.timeProgress.visibility = View.VISIBLE
            }
        }
    }


}