package com.kotlinbyte.example


import com.fasterxml.jackson.annotation.JsonProperty

data class CurrencyDtoItem(
    @JsonProperty("price")
    val price: String,
    @JsonProperty("symbol")
    val symbol: String
)