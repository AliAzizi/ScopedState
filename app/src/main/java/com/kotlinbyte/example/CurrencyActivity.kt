package com.kotlinbyte.example

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlinbyte.example.databinding.ActivityCurrencyBinding
import com.kotlinbyte.scoped_state.StateWatcher

class CurrencyActivity : AppCompatActivity() {
    lateinit var viewModel: CurrencyViewModel
    lateinit var binding: ActivityCurrencyBinding
    lateinit var currencyAdapter: CurrencyListAdapter

    companion object {
        const val TAG = "CurrencyActivity"
    }

    var counter: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CurrencyViewModel::class.java]
        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupStateWatcher()
        viewModel.fetchDateAndTime()
        viewModel.updateCurrencyAutomatically()

    }

    private fun setupRecyclerView() {
        currencyAdapter = CurrencyListAdapter()
        with(binding.currencyListRecycler) {
            adapter = currencyAdapter
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupStateWatcher() = StateWatcher.watch(viewModel.state) {
        attach(lifecycle)

        scope<CurrencyScope.FetchTime, FetchTimeState> {
            with(binding) {

                state<FetchTimeState.Data> {
                    timeProgress.visibility = View.GONE
                    time.text = it.dateTime
                    timeZone.text = it.timezone
                }

                state<FetchTimeState.Loading> {
                    timeProgress.visibility = View.VISIBLE
                }

                state<FetchTimeState.Error> {
                    Toast.makeText(applicationContext, it.reason, Toast.LENGTH_LONG).show()
                }



                scope<CurrencyScope.FetchCurrency, FetchCurrencyState> {

                    state<FetchCurrencyState.Data> {
                        currencyListRecycler.visibility = View.VISIBLE
                        currencyProgress.visibility = View.INVISIBLE
                        currencyAdapter.collection = it.currencyDto
                    }

                    state<FetchCurrencyState.Loading> {
                        currencyListRecycler.visibility = View.INVISIBLE
                        currencyProgress.visibility = View.VISIBLE
                    }

                    state<FetchCurrencyState.Error> {
                        Toast.makeText(applicationContext, it.reason, Toast.LENGTH_LONG).show()
                    }
                }

                scope<CurrencyScope.FetchCurrencyManually, FetchCurrencyManuallyState> {

                    state<FetchCurrencyManuallyState.Data> {
                        manualProgress.visibility = View.INVISIBLE
                        btc.text = it.currencyDto.price
                    }

                    state<FetchCurrencyManuallyState.Loading> {
                        manualProgress.visibility = View.VISIBLE
                    }

                    state<FetchCurrencyManuallyState.Error> {
                        Toast.makeText(applicationContext, it.reason, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


}