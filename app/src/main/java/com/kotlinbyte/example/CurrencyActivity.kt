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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CurrencyViewModel::class.java]
        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupButtons()
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

    private fun setupButtons() {
        binding.increment.setOnClickListener {
            viewModel.increment()
        }
        binding.decrement.setOnClickListener {
            viewModel.decrement()
        }

        binding.increment2.setOnClickListener {
            viewModel.increment2()
        }
        binding.decrement2.setOnClickListener {
            viewModel.decrement2()
        }
    }

    private fun setupStateWatcher() = StateWatcher.watch(viewModel.state) {
        attach(lifecycle)

        with(binding) {
            scope<CurrencyScope.FetchTime, FetchTimeState> {

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

            scope<CurrencyScope.CounterScope, CounterState> {

                state<CounterState.Changed> {
                    counter.text = it.value.toString()
                }

            }

            scope<CurrencyScope.CounterScope2, CounterState2> {

                state<CounterState2.Changed> {
                    counter2.text = it.value.toString()
                }

            }
        }
    }


}