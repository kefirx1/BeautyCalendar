package pl.dev.beautycalendar

data class CustomerInfo(
    var date: Long,
    var service: String,
    val surname: String,
    val name: String,
    val telephone: String
)
