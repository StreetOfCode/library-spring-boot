package sk.streetofcode.library.domain

import java.time.OffsetDateTime

data class Book(
    val id: Long,
    val authorId: Long,
    val categoryId: Long? = null,
    val name: String,
    val description: String,
    val createdAt: OffsetDateTime
)
