package sk.streetofcode.library.implementation.jdbc.service

import org.springframework.stereotype.Service
import sk.streetofcode.library.api.CategoryService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.CategoryAddRequest
import sk.streetofcode.library.api.request.CategoryEditRequest
import sk.streetofcode.library.domain.Category
import sk.streetofcode.library.implementation.jdbc.db.repository.BookJdbcRepository
import sk.streetofcode.library.implementation.jdbc.db.repository.CategoryJdbcRepository

@Service
class CategoryServiceJdbcImpl(
    private val categoryRepository: CategoryJdbcRepository,
    private val bookRepository: BookJdbcRepository
) : CategoryService {

    override fun get(id: Long): Category? {
        return categoryRepository.get(id)
    }

    override fun getAll(): List<Category> {
        return categoryRepository.getAll()
    }

    override fun add(request: CategoryAddRequest): Long {
        return categoryRepository.add(request)
    }

    override fun delete(id: Long) {
        if (get(id) != null) {
            bookRepository.setCategoryToNullBy(id)
            categoryRepository.delete(id)
        } else {
            throw ResourceNotFoundException("Category with id $id was not found")
        }
    }

    override fun edit(id:Long, request: CategoryEditRequest) {
        if (get(id) != null) {
            categoryRepository.edit(id, request.name, request.description)
        } else {
            throw ResourceNotFoundException("Category with id $id was not found")
        }
    }
}