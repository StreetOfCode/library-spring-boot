package sk.streetofcode.library.implementation.jdbc.db.mapper

import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import sk.streetofcode.library.domain.Author
import java.sql.ResultSet

@Component
class AuthorRowMapper: RowMapper<Author>  {
    override fun mapRow(p0: ResultSet, p1: Int): Author {
         return Author(
             p0.getLong("id"),
             p0.getString("name"),
             p0.getString("surname")
         )
    }
}