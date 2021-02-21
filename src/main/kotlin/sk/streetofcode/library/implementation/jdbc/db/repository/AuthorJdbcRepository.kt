package sk.streetofcode.library.implementation.jdbc.db.repository

import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import sk.streetofcode.library.api.exception.InternalErrorException
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.AuthorAddRequest
import sk.streetofcode.library.implementation.jdbc.db.mapper.AuthorRowMapper
import sk.streetofcode.library.domain.Author
import java.sql.Connection
import java.sql.PreparedStatement

@Repository
class AuthorJdbcRepository(private val authorRowMapper: AuthorRowMapper, private val jdbcTemplate: JdbcTemplate) {
    companion object {
        private const val SELECT_BY_ID = "select * from author where author.id = ?"
        private const val SELECT_ALL = "select * from author"
        private const val INSERT = "insert into author(name, surname) values (?, ?)"
        private const val DELETE = "delete from author where author.id = ?"
    }

    fun get(id: Long): Author? {
        return try {
            jdbcTemplate.queryForObject(SELECT_BY_ID, authorRowMapper, id)
        } catch (e: EmptyResultDataAccessException) {
            throw ResourceNotFoundException("Author with id $id was not found")
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not get author with id $id")
        }
    }

    fun getAll(): List<Author> {
        return try {
            jdbcTemplate.query(SELECT_ALL, authorRowMapper)
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not get all authors")
        }
    }

    fun add(request: AuthorAddRequest): Long {
        return try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate.update({ connection: Connection ->
                val ps = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)
                ps.setString(1, request.name)
                ps.setString(2, request.surname)
                ps
            }, keyHolder)
            if (keyHolder.key != null) {
                keyHolder.key!!.toLong()
            } else {
                throw InternalErrorException("Could not add author, keyHolder was null")
            }
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not add author")
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
            throw InternalErrorException("Could not delete author with id $id")
        }
    }
}