package sk.streetofcode.library.api.request

data class BookAddRequest(
    val authorId: Long,
    val categoryId: Long?,
    val name: String,
    val description: String
)