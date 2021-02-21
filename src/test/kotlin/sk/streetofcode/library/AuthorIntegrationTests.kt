package sk.streetofcode.library.integration

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.AuthorAddRequest
import sk.streetofcode.library.domain.Author
import sk.streetofcode.library.domain.Book

class AuthorIntegrationTests : IntegrationTest() {

    @Test
    fun getAllAuthors_OK() {
        val authors = restTemplate.getForEntity<List<Author>>("/author")
        Assertions.assertEquals(HttpStatus.OK, authors.statusCode)
        Assertions.assertTrue(authors.body!!.size >= 4)
    }

    @Test
    fun getAuthor_OK() {
        val authorResponse = restTemplate.getForEntity<Author>("/author/1")
        Assertions.assertEquals(HttpStatus.OK, authorResponse.statusCode)

        val author = authorResponse.body!! // Throws NPE if null. Don't use in production code
        Assertions.assertEquals("J.K.", author.name)
        Assertions.assertEquals("Rowling", author.surname)
    }

    @Test
    fun getAuthor_404() {
        val authorResponse = restTemplate.getForEntity<ResourceNotFoundException>("/author/99")
        Assertions.assertEquals(HttpStatus.NOT_FOUND, authorResponse.statusCode)
    }

    @Test
    fun addAuthor_OK() {
        val authorAddRequest = AuthorAddRequest("john", "bool")
        val authorResponse = restTemplate.postForEntity<String>("/author", authorAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, authorResponse.statusCode)

        val newAuthorId = authorResponse.body!!.toLong()

        val getNewAuthorResponse = restTemplate.getForEntity<Author>("/author/${newAuthorId}")
        Assertions.assertEquals(HttpStatus.OK, getNewAuthorResponse.statusCode)

        val newAuthor = getNewAuthorResponse.body!!
        Assertions.assertEquals(newAuthorId, newAuthor.id)
        Assertions.assertEquals("john", newAuthor.name)
        Assertions.assertEquals("bool", newAuthor.surname)
    }

    @Test
    fun deleteNewAuthor_OK() {
        // add author
        val authorAddRequest = AuthorAddRequest("john", "bool")
        val authorResponse = restTemplate.postForEntity<String>("/author", authorAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, authorResponse.statusCode)
        val newAuthorId = authorResponse.body!!.toLong()

        // new author exists
        val getNewAuthorResponse = restTemplate.getForEntity<Author>("/author/${newAuthorId}")
        Assertions.assertEquals(HttpStatus.OK, getNewAuthorResponse.statusCode)

        // delete author
        Assertions.assertEquals(
            HttpStatus.OK,
            restTemplate.exchange("/author/${newAuthorId}", HttpMethod.DELETE, null, Void::class.java).statusCode
        )

        // new author is removed
        Assertions.assertEquals(
            HttpStatus.NOT_FOUND,
            restTemplate.getForEntity<ResourceNotFoundException>("/author/${newAuthorId}").statusCode
        )
    }

    @Test
    fun deleteNonExistentAuthor_404() {
        val response = restTemplate.exchange("/author/99", HttpMethod.DELETE, null, ResourceNotFoundException::class.java)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    // TODO test which will delete book when author will be deleted
}