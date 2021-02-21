package sk.streetofcode.library.implementation.jpa.entity

import javax.persistence.*

@Entity(name = "category")
data class CategoryEntity(
    @Id
    @SequenceGenerator(name = "category_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_seq")
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @OneToMany(
        mappedBy = "category",
        cascade = [CascadeType.PERSIST],
        fetch = FetchType.LAZY
    )
    val books: MutableSet<BookEntity> = mutableSetOf()

) {
    constructor(name: String, description: String)
            : this(null, name, description, mutableSetOf())
}
