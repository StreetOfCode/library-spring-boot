package sk.streetofcode.library.implementation.jpa.db.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sk.streetofcode.library.implementation.jpa.entity.BookEntity

@Repository
interface BookJpaRepository : CrudRepository<BookEntity, Long> {
    fun findByAuthorId(authorId: Long): List<BookEntity>

    fun findByNameIgnoreCaseContaining(name: String): List<BookEntity>
}