package sk.streetofcode.library.implementation.jdbc.db.mapper

import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import sk.streetofcode.library.domain.Category
import java.sql.ResultSet

@Component
class CategoryRowMapper : RowMapper<Category> {
    override fun mapRow(p0: ResultSet, p1: Int): Category {
        return Category(
            p0.getLong("id"),
            p0.getString("name"),
            p0.getString("description")
        )
    }
}