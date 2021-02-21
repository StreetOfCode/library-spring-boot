package sk.streetofcode.library.implementation.jdbc.db.repository

import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import sk.streetofcode.library.api.exception.InternalErrorException
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.CategoryAddRequest
import sk.streetofcode.library.implementation.jdbc.db.mapper.CategoryRowMapper
import sk.streetofcode.library.domain.Category
import java.sql.Connection
import java.sql.PreparedStatement

@Repository
class CategoryJdbcRepository(private val categoryRowMapper: CategoryRowMapper, private val jdbcTemplate: JdbcTemplate) {
    companion object {
        private const val SELECT_BY_ID = "select * from category where category.id = ?"
        private const val SELECT_ALL = "select * from category"
        private const val INSERT = "insert into category(name, description) values (?, ?)"
        private const val DELETE = "delete from category where category.id = ?"
        private const val EDIT = "update category set name = ?, description = ? where category.id = ?"

    }

    operator fun get(id: Long): Category? {
        return try {
            jdbcTemplate.queryForObject(SELECT_BY_ID, categoryRowMapper, id)
        } catch (e: EmptyResultDataAccessException) {
            throw ResourceNotFoundException("Category with id $id was not found")
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not get category with id $id")
        }
    }

    fun getAll(): List<Category> {
        return try {
            jdbcTemplate.query(SELECT_ALL, categoryRowMapper)
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not get all categories")
        }
    }

    fun add(request: CategoryAddRequest): Long {
        return try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate.update({ connection: Connection ->
                val ps = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)
                ps.setString(1, request.name)
                ps.setString(2, request.description)
                ps
            }, keyHolder)
            if (keyHolder.key != null) {
                keyHolder.key!!.toLong()
            } else {
                throw InternalErrorException("Could not add category, keyHolder is null")
            }
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not add category")
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
            throw InternalErrorException("Could not delete category with id $id")
        }
    }

    fun edit(id: Long, name: String, description: String) {
        try {
            jdbcTemplate.update { connection: Connection ->
                val ps = connection.prepareStatement(EDIT)
                ps.setString(1, name)
                ps.setString(2, description)
                ps.setLong(3, id)
                ps
            }
        } catch (e: DataAccessException) {
            throw InternalErrorException("Could not update category with id $id")
        }
    }
}