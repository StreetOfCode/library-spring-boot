package sk.streetofcode.library.integration

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.BookAddRequest
import sk.streetofcode.library.domain.Book
import java.time.OffsetDateTime

class BookIntegrationTests : IntegrationTest() {

    @Test
    fun getAllBooks_OK() {
        val books = restTemplate.getForEntity<List<Book>>("/book")
        Assertions.assertEquals(HttpStatus.OK, books.statusCode)
        Assertions.assertTrue(books.body!!.size >= 4)
    }

    @Test
    fun getBook_OK() {
        val bookResponse = restTemplate.getForEntity<Book>("/book/1")
        Assertions.assertEquals(HttpStatus.OK, bookResponse.statusCode)

        val book = bookResponse.body!!
        Assertions.assertEquals(1, book.authorId)
        Assertions.assertEquals(1, book.categoryId)
        Assertions.assertEquals("Harry Potter", book.name)
        Assertions.assertEquals("Story about young wizard", book.description)
        Assertions.assertNotNull(book.createdAt)
    }

    @Test
    fun getBook_404() {
        val bookResponse = restTemplate.getForEntity<ResourceNotFoundException>("/book/99")
        Assertions.assertEquals(HttpStatus.NOT_FOUND, bookResponse.statusCode)
    }

    @Test
    fun addBook_OK() {
        val timeMinusOneMinute = OffsetDateTime.now().minusMinutes(1)

        val bookAddRequest = BookAddRequest(1, 1, "Harry Potter 2", "Tajomna komnata")
        val bookResponse = restTemplate.postForEntity<String>("/book", bookAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, bookResponse.statusCode)

        val newBookId = bookResponse.body!!.toLong()

        val getNewBookResponse = restTemplate.getForEntity<Book>("/book/${newBookId}")
        Assertions.assertEquals(HttpStatus.OK, getNewBookResponse.statusCode)

        val newBook = getNewBookResponse.body!!
        Assertions.assertEquals(newBookId, newBook.id)
        Assertions.assertEquals(1, newBook.authorId)
        Assertions.assertEquals(1, newBook.categoryId)
        Assertions.assertEquals("Harry Potter 2", newBook.name)
        Assertions.assertEquals("Tajomna komnata", newBook.description)
        Assertions.assertTrue(newBook.createdAt.isAfter(timeMinusOneMinute))
    }

    @Test
    fun addBookWithNullCategory_OK() {
        val timeMinusOneMinute = OffsetDateTime.now().minusMinutes(1)

        val bookAddRequest = BookAddRequest(1, null, "Harry Potter 2", "Tajomna komnata")
        val bookResponse = restTemplate.postForEntity<String>("/book", bookAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, bookResponse.statusCode)

        val newBookId = bookResponse.body!!.toLong()

        val getNewBookResponse = restTemplate.getForEntity<Book>("/book/${newBookId}")
        Assertions.assertEquals(HttpStatus.OK, getNewBookResponse.statusCode)

        val newBook = getNewBookResponse.body!!
        Assertions.assertEquals(newBookId, newBook.id)
        Assertions.assertEquals(1, newBook.authorId)
        Assertions.assertEquals(null, newBook.categoryId)
        Assertions.assertEquals("Harry Potter 2", newBook.name)
        Assertions.assertEquals("Tajomna komnata", newBook.description)
        Assertions.assertTrue(newBook.createdAt.isAfter(timeMinusOneMinute))
    }

    @Test
    fun addBookWithNonExistentAuthor_404() {
        val bookAddRequest = BookAddRequest(99, 11, "Harry Potter 2", "Tajomna komnata")
        val bookResponse = restTemplate.postForEntity<String>("/book", bookAddRequest)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, bookResponse.statusCode)
    }

    @Test
    fun addBookWithNonExistentCategory_404() {
        val bookAddRequest = BookAddRequest(1, 99, "Harry Potter 2", "Tajomna komnata")
        val bookResponse = restTemplate.postForEntity<String>("/book", bookAddRequest)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, bookResponse.statusCode)
    }

    @Test
    fun deleteNewBook_OK() {
        // add book
        val bookAddRequest = BookAddRequest(1, 1, "Harry Potter 2", "Tajomna komnata")
        val bookResponse = restTemplate.postForEntity<String>("/book", bookAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, bookResponse.statusCode)
        val newBookId = bookResponse.body!!.toLong()

        // new book exists
        val getNewBookResponse = restTemplate.getForEntity<Book>("/book/${newBookId}")
        Assertions.assertEquals(HttpStatus.OK, getNewBookResponse.statusCode)

        // delete book
        Assertions.assertEquals(
            HttpStatus.OK,
            restTemplate.exchange("/book/${newBookId}", HttpMethod.DELETE, null, Void::class.java).statusCode
        )

        // new book is removed
        Assertions.assertEquals(
            HttpStatus.NOT_FOUND,
            restTemplate.getForEntity<ResourceNotFoundException>("/book/${newBookId}").statusCode
        )
    }

    @Test
    fun deleteNonExistentBook_404() {
        val response =
            restTemplate.exchange("/book/99", HttpMethod.DELETE, null, ResourceNotFoundException::class.java)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun searchBooks_OK() {
        val searchString = "ing" // should return 'The Fellowship of the ring' as well as 'Thinking, Fast and Slow'
        val booksResponse = restTemplate.exchange("/book/search", HttpMethod.POST, HttpEntity(searchString), object: ParameterizedTypeReference<List<Book>> () {})

        Assertions.assertEquals(HttpStatus.OK, booksResponse.statusCode)

        val books = booksResponse.body!!
        Assertions.assertEquals(2, books.size)

        Assertions.assertEquals(1, books.filter { book -> book.name == "The Fellowship of the ring" }.size)
        Assertions.assertEquals(1, books.filter { book -> book.name == "Thinking, Fast and Slow" }.size)
    }

    @Test
    fun getBooksByAuthor_OK() {
        // add book by J.K. Rowling
        val bookAddRequest = BookAddRequest(1, 1, "Harry Potter 2", "Tajomna komnata")
        val bookResponse = restTemplate.postForEntity<String>("/book", bookAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, bookResponse.statusCode)

        val bookByAuthorResponse = restTemplate.exchange("/book?authorId=1", HttpMethod.GET, null, object: ParameterizedTypeReference<List<Book>> () {})
        Assertions.assertEquals(HttpStatus.OK, bookByAuthorResponse.statusCode)

        val harryPotterBooks = bookByAuthorResponse.body!!
        Assertions.assertEquals(2, harryPotterBooks.size)

        Assertions.assertEquals(1, harryPotterBooks.filter { book -> book.name == "Harry Potter" }.size)
        Assertions.assertEquals(1, harryPotterBooks.filter { book -> book.name == "Harry Potter 2" }.size)

    }

    @Test
    fun getBooksByAuthor_Empty() {
        val bookResponse = restTemplate.getForEntity<List<Book>>("/book?authorId=99")
        Assertions.assertEquals(HttpStatus.OK, bookResponse.statusCode)
        Assertions.assertEquals(0, bookResponse.body!!.size)
    }
}