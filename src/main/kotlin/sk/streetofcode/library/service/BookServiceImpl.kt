package sk.streetofcode.library.service

import org.springframework.stereotype.Service
import sk.streetofcode.library.api.BookService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.BookAddRequest
import sk.streetofcode.library.db.repository.AuthorRepository
import sk.streetofcode.library.db.repository.BookRepository
import sk.streetofcode.library.db.repository.CategoryRepository
import sk.streetofcode.library.domain.Book

@Service
class BookServiceImpl(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository,
    private val categoryRepository: CategoryRepository
) : BookService {

    override fun get(id: Long): Book? {
        return bookRepository.get(id)
    }

    override fun getAll(): List<Book> {
        return bookRepository.getAll()
    }

    override fun add(request: BookAddRequest): Long? {
        // Check that author exists, will throw exception if doesn't
        authorRepository.get(request.authorId)

        // Check that category exists (if it was sent), will throw exception if doesn't
        request.categoryId?.let { categoryRepository[request.categoryId] }

        return bookRepository.add(request)
    }

    override fun delete(id: Long) {
        if (get(id) != null) {
            bookRepository.delete(id)
        } else {
            throw ResourceNotFoundException("Book with id $id was not found")
        }
    }
}