package sk.streetofcode.library.db.repository

import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import sk.streetofcode.library.api.exception.InternalErrorException
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.BookAddRequest
import sk.streetofcode.library.db.mapper.BookRowMapper
import sk.streetofcode.library.domain.Book
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types
import java.time.OffsetDateTime

@Repository
class BookRepository(private val bookRowMapper: BookRowMapper, private val jdbcTemplate: JdbcTemplate) {
    companion object {
        private const val SELECT_BY_ID = "select * from book where book.id = ?"
        private const val SELECT_ALL = "select * from book"
        private const val INSERT =
            "insert into book(author_id, category_id, name, description, created_at) values (?, ?, ?, ?, ?)"
        private const val DELETE = "delete from book where book.id = ?"
        private const val DELETE_BY_AUTHOR_ID = "delete from book where book.author_id = ?"
        private const val SET_CATEGORY_NULL = "update book set book.category_id = NULL where book.category_id = ?"

    }

    operator fun get(id: Long): Book? {
        return try {
            jdbcTemplate.queryForObject(SELECT_BY_ID, bookRowMapper, id)
        } catch (e: EmptyResultDataAccessException) {
            throw ResourceNotFoundException("Book with id $id was not found")
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not get book with id $id")
        }
    }

    fun getAll(): List<Book> {
        return try {
            jdbcTemplate.query(SELECT_ALL, bookRowMapper)
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not get all books")
        }
    }

    fun add(request: BookAddRequest): Long? {
        return try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate.update({ connection: Connection ->
                val ps = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)
                ps.setLong(1, request.authorId)
                if (request.categoryId == null) ps.setNull(2, Types.BIGINT) else ps.setLong(2, request.categoryId)
                ps.setString(3, request.name)
                ps.setString(4, request.description)
                ps.setTimestamp(5, Timestamp.from(OffsetDateTime.now().toInstant()))
                ps
            }, keyHolder)
            if (keyHolder.key != null) {
                keyHolder.key!!.toLong()
            } else {
                throw InternalErrorException("Could not add book, keyHolder is null")
            }
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not add book")
        }
    }

    fun delete(id: Long) {
        try {
            jdbcTemplate.update { connection: Connection ->
                val ps = connection.prepareStatement(DELETE)
                ps.setLong(1, id)
                ps
            }
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not delete book with id $id")
        }
    }

    fun deleteByAuthor(authorId: Long) {
        try {
            jdbcTemplate.update { connection: Connection ->
                val ps = connection.prepareStatement(DELETE_BY_AUTHOR_ID)
                ps.setLong(1, authorId)
                ps
            }
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not remove book by authorId")
        }
    }

    fun setCategoryToNullBy(categoryId: Long) {
        try {
            jdbcTemplate.update { connection: Connection ->
                val ps = connection.prepareStatement(SET_CATEGORY_NULL)
                ps.setLong(1, categoryId)
                ps
            }
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not set category to null")
        }
    }
}