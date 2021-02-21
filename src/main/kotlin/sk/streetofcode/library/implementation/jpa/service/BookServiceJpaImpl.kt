package sk.streetofcode.library.implementation.jpa.service

import org.springframework.context.annotation.Profile
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import sk.streetofcode.library.api.BookService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.BookAddRequest
import sk.streetofcode.library.domain.Book
import sk.streetofcode.library.implementation.jpa.db.repository.AuthorJpaRepository
import sk.streetofcode.library.implementation.jpa.db.repository.BookJpaRepository
import sk.streetofcode.library.implementation.jpa.db.repository.CategoryJpaRepository
import sk.streetofcode.library.implementation.jpa.entity.BookEntity
import sk.streetofcode.library.implementation.jpa.entity.CategoryEntity

@Service
@Profile("jpa")
class BookServiceJpaImpl(
    private val authorRepository: AuthorJpaRepository,
    private val bookRepository: BookJpaRepository,
    private val categoryRepository: CategoryJpaRepository
) : BookService {

    override fun get(id: Long): Book? {
        val maybeBook = bookRepository.findById(id)
        if (maybeBook.isPresent) {
            return mapEntityToDomain(maybeBook.get())
        } else {
            throw ResourceNotFoundException("Book with id $id was not found")
        }
    }

    override fun getAll(): List<Book> {
        return bookRepository.findAll().map { bookEntity -> mapEntityToDomain(bookEntity) }
    }

    override fun add(request: BookAddRequest): Long {
        val authorEntity = authorRepository.findByIdOrNull(request.authorId)
            ?: throw ResourceNotFoundException("Author with id ${request.authorId} was not found")

        // Check that category exists (if it was sent)
        var categoryEntity: CategoryEntity? = null
        if (request.categoryId != null) {
            categoryEntity = categoryRepository.findByIdOrNull(request.categoryId)
                ?: throw ResourceNotFoundException("Category with id ${request.authorId} was not found")
        }

        return bookRepository.save(
            BookEntity(
                name = request.name,
                description = request.description,
                author = authorEntity,
                category = categoryEntity
            )
        ).id!!
    }

    override fun delete(id: Long) {
        if (get(id) != null) {
            bookRepository.deleteById(id)
        } else {
            throw ResourceNotFoundException("Book with id $id was not found")
        }
    }

    override fun findByAuthorId(authorId: Long): List<Book> {
        return bookRepository.findByAuthorId(authorId).map { bookEntity -> mapEntityToDomain(bookEntity) }
    }

    override fun searchBooksByName(search: String): List<Book> {
        return bookRepository.findByNameIgnoreCaseContaining(search).map { bookEntity -> mapEntityToDomain(bookEntity) }
    }

    private fun mapEntityToDomain(bookEntity: BookEntity): Book {
        return Book(
            id = bookEntity.id!!,
            name = bookEntity.name,
            description = bookEntity.description,
            createdAt = bookEntity.createdAt,
            authorId = bookEntity.author.id!!,
            categoryId = bookEntity.category?.id
        )
    }
}