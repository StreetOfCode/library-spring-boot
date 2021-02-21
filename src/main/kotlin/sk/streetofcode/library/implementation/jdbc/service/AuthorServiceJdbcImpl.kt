package sk.streetofcode.library.implementation.jdbc.service

import org.springframework.stereotype.Service
import sk.streetofcode.library.api.AuthorService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.AuthorAddRequest
import sk.streetofcode.library.domain.Author
import sk.streetofcode.library.implementation.jdbc.db.repository.AuthorJdbcRepository

@Service
class AuthorServiceJdbcImpl(
    private val authorRepository: AuthorJdbcRepository
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
            // TODO: Remove all books by this author
            authorRepository.delete(id)
        } else {
            throw ResourceNotFoundException("Author with id $id was not found")
        }
    }
}