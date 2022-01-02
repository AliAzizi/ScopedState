package com.kotlinbyte.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import com.kotlinbyte.scoped_state.MutableScopedStateFlow
import com.kotlinbyte.scoped_state.ScopedStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrencyViewModel : ViewModel() {
    var counter = 0
    private val _scopedState: MutableScopedStateFlow<CurrencyScope> =
        MutableScopedStateFlow.create<CurrencyScope, CurrencyScope.Initial>()

    val state: ScopedStateFlow<CurrencyScope> = _scopedState

    fun fetchDateAndTime() = _scopedState.withScope<CurrencyScope.FetchTime, FetchTimeState> {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(3000)
                    emit(FetchTimeState.Loading)
                    Fuel.get("http://worldtimeapi.org/api/timezone/Asia/Tehran")
                        .awaitObjectResult(DateTimeResponseDeserializer)
                        .fold(
                            { data -> emit(FetchTimeState.Data(data.datetime, data.timezone)) },
                            { error -> emit(FetchTimeState.Error("An error of type ${error.exception} happened: ${error.message}")) }
                        )
                }
            }

        }
    }


    fun updateCurrencyAutomatically() =
        _scopedState.withScope<CurrencyScope.FetchCurrency, FetchCurrencyState> {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    while (true) {
                        delay(3000)
                        emit(FetchCurrencyState.Loading)
                        Fuel.get("https://api.binance.com/api/v3/ticker/price")
                            .awaitObjectResult(CurrencyResponseDeserializer)
                            .fold(
                                { data -> emit(FetchCurrencyState.Data(data)) },
                                { error -> emit(FetchCurrencyState.Error("An error of type ${error.exception} happened: ${error.message}")) }
                            )
                    }
                }

            }
        }
}

object DateTimeResponseDeserializer : ResponseDeserializable<DateTimeDto> {
    override fun deserialize(content: String) =
        jacksonObjectMapper().readValue<DateTimeDto>(content)
}

object CurrencyResponseDeserializer : ResponseDeserializable<CurrencyDto> {
    override fun deserialize(content: String) =
        jacksonObjectMapper().readValue<CurrencyDto>(content)
}