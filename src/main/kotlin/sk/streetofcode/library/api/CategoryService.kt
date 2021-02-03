package sk.streetofcode.library.api

import sk.streetofcode.library.api.request.CategoryAddRequest
import sk.streetofcode.library.domain.Category

interface CategoryService {
    fun get(id: Long): Category?
    fun getAll(): List<Category>
    fun add(request: CategoryAddRequest): Long?
    fun delete(id: Long)
}