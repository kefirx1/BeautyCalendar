package pl.dev.beautycalendar.data

data class CustomerInfo(
    var active: Int,
    var dateOf: ArrayList<VisitsDate>,
    val name: String,
    val surname: String,
    val telephone: String
)
