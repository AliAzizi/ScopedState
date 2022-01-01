package com.kotlinbyte.example

import com.kotlinbyte.scoped_state.StateWatcher

sealed class ExampleScope {
    object Initial : ExampleScope()
    object FetchTime : ExampleScope()
    object FetchCurrency : ExampleScope()
    object FetchCurrencyManually : ExampleScope()
}

sealed class FetchTimeState : StateWatcher.BaseState {
    object Loading : FetchTimeState()
    data class Data(val dateTime: String, val timezone: String) : FetchTimeState()
}


sealed class FetchCurrencyState : StateWatcher.BaseState {
    object Loading : FetchCurrencyState()
    data class Data(val dateTime: String, val timezone: String) : FetchCurrencyState()
    data class Error(val reason: String) : FetchCurrencyState()
}
