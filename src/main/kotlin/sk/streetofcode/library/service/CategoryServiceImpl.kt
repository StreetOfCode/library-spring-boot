package sk.streetofcode.library.service

import org.springframework.stereotype.Service
import sk.streetofcode.library.api.CategoryService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.CategoryAddRequest
import sk.streetofcode.library.db.repository.BookRepository
import sk.streetofcode.library.db.repository.CategoryRepository
import sk.streetofcode.library.domain.Category

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val bookRepository: BookRepository
) : CategoryService {

    override fun get(id: Long): Category? {
        return categoryRepository.get(id)
    }

    override fun getAll(): List<Category> {
        return categoryRepository.getAll()
    }

    override fun add(request: CategoryAddRequest): Long? {
        return categoryRepository.add(request)
    }

    override fun delete(id: Long) {
        if (get(id) != null) {
            // Set category in all books with this category id to null
            bookRepository.setCategoryToNullBy(id)
            categoryRepository.delete(id)
        } else {
            throw ResourceNotFoundException("Category with id $id was not found")
        }
    }
}