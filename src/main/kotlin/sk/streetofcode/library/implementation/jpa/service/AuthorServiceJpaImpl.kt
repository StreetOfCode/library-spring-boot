package sk.streetofcode.library.implementation.jpa.service

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import sk.streetofcode.library.api.AuthorService
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.AuthorAddRequest
import sk.streetofcode.library.domain.Author
import sk.streetofcode.library.implementation.jpa.db.repository.AuthorJpaRepository
import sk.streetofcode.library.implementation.jpa.entity.AuthorEntity

@Service
@Profile("jpa")
class AuthorServiceJpaImpl(
    private val authorRepository: AuthorJpaRepository
) : AuthorService {

    override fun get(id: Long): Author? {
        val maybeAuthor = authorRepository.findById(id)
        if (maybeAuthor.isPresent) {
            return mapEntityToDomain(maybeAuthor.get())
        } else {
            throw ResourceNotFoundException("Author with id $id was not found")
        }
    }

    override fun getAll(): List<Author> {
        return authorRepository.findAll().map { authorEntity -> mapEntityToDomain(authorEntity) }
    }

    override fun add(request: AuthorAddRequest): Long {
        return authorRepository.save(
            AuthorEntity(
                name = request.name,
                surname = request.surname
            )
        ).id!!
    }

    override fun delete(id: Long) {
        if (get(id) != null) {
            // TODO: Remove all books by this author
            authorRepository.deleteById(id)
        } else {
            throw ResourceNotFoundException("Author with id $id was not found")
        }
    }

    private fun mapEntityToDomain(authorEntity: AuthorEntity): Author {
        return Author(
            id = authorEntity.id!!,
            name = authorEntity.name,
            surname = authorEntity.surname
        )
    }
}