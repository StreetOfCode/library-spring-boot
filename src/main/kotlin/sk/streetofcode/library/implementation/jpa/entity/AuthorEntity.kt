package sk.streetofcode.library.implementation.jpa.entity

import javax.persistence.*

@Entity(name = "author")
data class AuthorEntity(
    @Id
    @SequenceGenerator(name = "author_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_seq")
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val surname: String,

    @OneToMany(
        mappedBy = "author",
        cascade = [CascadeType.PERSIST],
        fetch = FetchType.LAZY
    )
    val books: MutableSet<BookEntity> = mutableSetOf()
) {
    constructor(name: String, surname: String)
            : this(null, name, surname, mutableSetOf())
}
