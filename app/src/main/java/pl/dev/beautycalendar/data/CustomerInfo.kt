package pl.dev.beautycalendar.data

data class CustomerInfo(
    var date: Long,
    val name: String,
    var service: String,
    val surname: String,
    val telephone: String
)