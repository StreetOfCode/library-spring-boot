package sk.streetofcode.library.api

import sk.streetofcode.library.api.request.BookAddRequest
import sk.streetofcode.library.domain.Book

interface BookService {
    fun get(id: Long): Book?
    fun getAll(): List<Book>
    fun add(request: BookAddRequest): Long?
    fun delete(id: Long)
}