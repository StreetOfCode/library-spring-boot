package sk.streetofcode.library.implementation.jpa.db.repository

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sk.streetofcode.library.implementation.jpa.entity.BookEntity
import javax.transaction.Transactional

@Repository
interface BookJpaRepository : CrudRepository<BookEntity, Long> {
    @Transactional
    fun deleteByAuthorId(authorId: Long)

    @Query(value = "update book set book.category_id = NULL where book.category_id = ?", nativeQuery = true)
    @Modifying
    @Transactional
    fun setBooksCategoryToNullByCategoryId(categoryId: Long)

    fun findByAuthorId(authorId: Long): List<BookEntity>

    fun findByNameIgnoreCaseContaining(name: String): List<BookEntity>
}