package sk.streetofcode.library.implementation.jpa.service

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import sk.streetofcode.library.api.CategoryService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.CategoryAddRequest
import sk.streetofcode.library.api.request.CategoryEditRequest
import sk.streetofcode.library.domain.Category
import sk.streetofcode.library.implementation.jpa.db.repository.BookJpaRepository
import sk.streetofcode.library.implementation.jpa.db.repository.CategoryJpaRepository
import sk.streetofcode.library.implementation.jpa.entity.CategoryEntity

@Service
@Profile("jpa")
class CategoryServiceJpaImpl(
    private val categoryRepository: CategoryJpaRepository,
    private val bookRepository: BookJpaRepository
) : CategoryService {

    override fun get(id: Long): Category? {
        val maybeCategory = categoryRepository.findById(id)
        if (maybeCategory.isPresent) {
            return mapEntityToDomain(maybeCategory.get())
        } else {
            throw ResourceNotFoundException("Category with id $id was not found")
        }
    }

    override fun getAll(): List<Category> {
        return categoryRepository.findAll().map { categoryEntity -> mapEntityToDomain(categoryEntity) }
    }

    override fun add(request: CategoryAddRequest): Long {
        return categoryRepository.save(
            CategoryEntity(
                name = request.name,
                description = request.description
            )
        ).id!!
    }

    override fun delete(id: Long) {
        if (get(id) != null) {
            bookRepository.setBooksCategoryToNullByCategoryId(id)
            categoryRepository.deleteById(id)
        } else {
            throw ResourceNotFoundException("Category with id $id was not found")
        }
    }

    override fun edit(id: Long, request: CategoryEditRequest) {
        val maybeCategory = categoryRepository.findById(id)
        if (maybeCategory.isPresent) {
            val categoryEntity = maybeCategory.get()
            categoryEntity.name = request.name
            categoryEntity.description = request.description
            categoryRepository.save(categoryEntity)
        } else {
            throw ResourceNotFoundException("Category with id $id was not found")
        }
    }

    private fun mapEntityToDomain(categoryEntity: CategoryEntity): Category {
        return Category(
            id = categoryEntity.id!!,
            name = categoryEntity.name,
            description = categoryEntity.description
        )
    }
}