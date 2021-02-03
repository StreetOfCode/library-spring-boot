package sk.streetofcode.library.service

import org.springframework.stereotype.Service
import sk.streetofcode.library.api.AuthorService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.AuthorAddRequest
import sk.streetofcode.library.db.repository.AuthorRepository
import sk.streetofcode.library.db.repository.BookRepository
import sk.streetofcode.library.domain.Author

@Service
class AuthorServiceImpl(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) : AuthorService {

    override fun get(id: Long): Author? {
        return authorRepository.get(id)
    }

    override fun getAll(): List<Author> {
        return authorRepository.getAll()
    }

    override fun add(request: AuthorAddRequest): Long? {
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