package com.kotlinbyte.example

import com.kotlinbyte.scoped_state.StateWatcher

sealed class CurrencyScope {
    object Initial : CurrencyScope()
    object FetchTime : CurrencyScope()
    object FetchCurrency : CurrencyScope()
    object FetchCurrencyManually : CurrencyScope()
    object CounterScope : CurrencyScope()
    object CounterScope2 : CurrencyScope()
}

sealed class FetchTimeState : StateWatcher.BaseState {
    object Loading : FetchTimeState()
    data class Data(val dateTime: String, val timezone: String) : FetchTimeState()
    data class Error(val reason: String) : FetchTimeState()
}


sealed class FetchCurrencyState : StateWatcher.BaseState {
    object Loading : FetchCurrencyState()
    data class Data(val currencyDto: CurrencyDto) : FetchCurrencyState()
    data class Error(val reason: String) : FetchCurrencyState()
}

sealed class FetchCurrencyManuallyState : StateWatcher.BaseState {
    object Loading : FetchCurrencyManuallyState()
    data class Data(val currencyDto: CurrencyDtoItem) : FetchCurrencyManuallyState()
    data class Error(val reason: String) : FetchCurrencyManuallyState()
}

sealed class CounterState : StateWatcher.BaseState {
    data class Changed(val value: Int) : CounterState()
}

sealed class CounterState2 : StateWatcher.BaseState {
    data class Changed(val value: Int) : CounterState2()
}