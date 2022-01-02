package com.kotlinbyte.example


import com.fasterxml.jackson.annotation.JsonProperty

data class DateTimeDto(
    @JsonProperty("abbreviation")
    val abbreviation: String,
    @JsonProperty("client_ip")
    val clientIp: String,
    @JsonProperty("datetime")
    val datetime: String,
    @JsonProperty("day_of_week")
    val dayOfWeek: Int,
    @JsonProperty("day_of_year")
    val dayOfYear: Int,
    @JsonProperty("dst")
    val dst: Boolean,
    @JsonProperty("dst_from")
    val dstFrom: Any?,
    @JsonProperty("dst_offset")
    val dstOffset: Int,
    @JsonProperty("dst_until")
    val dstUntil: Any?,
    @JsonProperty("raw_offset")
    val rawOffset: Int,
    @JsonProperty("timezone")
    val timezone: String,
    @JsonProperty("unixtime")
    val unixtime: Int,
    @JsonProperty("utc_datetime")
    val utcDatetime: String,
    @JsonProperty("utc_offset")
    val utcOffset: String,
    @JsonProperty("week_number")
    val weekNumber: Int
)