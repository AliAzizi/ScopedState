package com.kotlinbyte.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.kotlinbyte.scoped_state.MutableScopedStateFlow
import com.kotlinbyte.scoped_state.ScopedStateFlow
import kotlinx.coroutines.launch

class ExampleViewModel : ViewModel() {
    /*

    */
    private val _scopedState: MutableScopedStateFlow<ExampleScope> =
        MutableScopedStateFlow.create<ExampleScope, ExampleScope.Initial>()

    val state: ScopedStateFlow<ExampleScope> = _scopedState

    fun fetchDateAndTime() = _scopedState.inScope<ExampleScope.FetchTime, FetchTimeState> {
        viewModelScope.launch {
            emit(FetchTimeState.Loading)
        }
    }

    fun fetchCurrencyManual() = _scopedState.inScope<ExampleScope.FetchCurrency, FetchTimeState> {
        viewModelScope.launch {
            emit(FetchTimeState.Loading)
        }
    }



    fun updateCurrencyAutomatically() = _scopedState.inScope<ExampleScope.FetchCurrency, FetchCurrencyState> {

    }
}

object DateTimeResponseDeserializer : ResponseDeserializable<DateTimeModel> {
    override fun deserialize(content: String) =
        jacksonObjectMapper().readValue<DateTimeModel>(content)
}
