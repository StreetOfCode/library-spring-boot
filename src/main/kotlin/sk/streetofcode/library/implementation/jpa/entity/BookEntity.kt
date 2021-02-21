package sk.streetofcode.library.implementation.jpa.entity

import java.time.OffsetDateTime
import javax.persistence.*

@Entity(name = "book")
data class BookEntity(
    @Id
    @SequenceGenerator(name = "book_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_seq")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: AuthorEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    val category: CategoryEntity? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val createdAt: OffsetDateTime
) {
    constructor(name: String, author: AuthorEntity, category: CategoryEntity?, description: String)
            : this(null, author, category, name, description, OffsetDateTime.now())
}
