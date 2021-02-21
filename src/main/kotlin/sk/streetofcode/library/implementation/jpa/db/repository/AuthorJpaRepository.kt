package sk.streetofcode.library.implementation.jpa.db.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sk.streetofcode.library.implementation.jpa.entity.AuthorEntity

@Repository
interface AuthorJpaRepository : CrudRepository<AuthorEntity, Long> {
}