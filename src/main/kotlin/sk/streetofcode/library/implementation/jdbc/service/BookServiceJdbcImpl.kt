package sk.streetofcode.library.implementation.jdbc.service

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import sk.streetofcode.library.api.BookService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.BookAddRequest
import sk.streetofcode.library.domain.Book
import sk.streetofcode.library.implementation.jdbc.db.repository.AuthorJdbcRepository
import sk.streetofcode.library.implementation.jdbc.db.repository.BookJdbcRepository
import sk.streetofcode.library.implementation.jdbc.db.repository.CategoryJdbcRepository

@Service
class BookServiceJdbcImpl(
    private val authorRepository: AuthorJdbcRepository,
    private val bookRepository: BookJdbcRepository,
    private val categoryRepository: CategoryJdbcRepository
) : BookService {

    override fun get(id: Long): Book? {
        return bookRepository.get(id)
    }

    override fun getAll(): List<Book> {
        return bookRepository.getAll()
    }

    override fun add(request: BookAddRequest): Long {
        // Check that author exists, will throw exception if doesn't
        authorRepository.get(request.authorId)

        // Check that category exists (if it was sent)
        if (request.categoryId != null) categoryRepository.get(request.categoryId)

        return bookRepository.add(request)
    }

    override fun delete(id: Long) {
        if (get(id) != null) {
            bookRepository.delete(id)
        } else {
            throw ResourceNotFoundException("Book with id $id was not found")
        }
    }

    override fun findByAuthorId(authorId: Long): List<Book> {
        return bookRepository.findByAuthorId(authorId)
    }

    override fun searchBooksByName(search: String): List<Book> {
        return bookRepository.searchByName(search)
    }
}