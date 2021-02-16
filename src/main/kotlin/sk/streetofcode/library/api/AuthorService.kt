package sk.streetofcode.library.api

import sk.streetofcode.library.api.request.AuthorAddRequest
import sk.streetofcode.library.domain.Author

interface AuthorService {
    fun get(id: Long): Author?
    fun getAll(): List<Author>
    fun add(request: AuthorAddRequest): Long?
    fun delete(id: Long)
    // TODO edit
}