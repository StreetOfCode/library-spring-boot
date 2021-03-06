package sk.streetofcode.library.implementation.jdbc.service

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import sk.streetofcode.library.api.AuthorService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.AuthorAddRequest
import sk.streetofcode.library.implementation.jdbc.db.repository.AuthorJdbcRepository
import sk.streetofcode.library.implementation.jdbc.db.repository.BookJdbcRepository
import sk.streetofcode.library.domain.Author

@Service
@Profile("jdbc")
class AuthorServiceJdbcImpl(
    private val authorRepository: AuthorJdbcRepository,
    private val bookRepository: BookJdbcRepository
) : AuthorService {

    override fun get(id: Long): Author? {
        return authorRepository.get(id)
    }

    override fun getAll(): List<Author> {
        return authorRepository.getAll()
    }

    override fun add(request: AuthorAddRequest): Long {
        return authorRepository.add(request)
    }

    override fun delete(id: Long) {
        if (get(id) != null) {
            // Remove all books by this author
            bookRepository.deleteByAuthor(id)
            authorRepository.delete(id)
        } else {
            throw ResourceNotFoundException("Author with id $id was not found")
        }
    }
}