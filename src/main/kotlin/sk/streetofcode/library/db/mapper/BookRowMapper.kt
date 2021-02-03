package sk.streetofcode.library.db.mapper

import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import sk.streetofcode.library.domain.Author
import sk.streetofcode.library.domain.Book
import sk.streetofcode.library.domain.Category
import java.sql.ResultSet
import java.time.ZoneOffset

@Component
class BookRowMapper: RowMapper<Book>  {
    override fun mapRow(p0: ResultSet, p1: Int): Book {
         return Book(
             p0.getLong("id"),
             p0.getLong("author_id"),
             if (p0.getObject("category_id") != null) p0.getLong("category_id") else null,
             p0.getString("name"),
             p0.getString("description"),
             p0.getTimestamp("created_at").toLocalDateTime().atOffset(ZoneOffset.UTC)
         )
    }
}