package sk.streetofcode.library.integration

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import sk.streetofcode.library.api.exception.ResourceNotFoundException
import sk.streetofcode.library.api.request.CategoryAddRequest
import sk.streetofcode.library.api.request.CategoryEditRequest
import sk.streetofcode.library.domain.Book
import sk.streetofcode.library.domain.Category

class CategoryIntegrationTests : IntegrationTest() {

    @Test
    fun getAllCategories_OK() {
        val categories = restTemplate.getForEntity<List<Category>>("/category")
        Assertions.assertEquals(HttpStatus.OK, categories.statusCode)
        Assertions.assertTrue(categories.body!!.size >= 2)
    }

    @Test
    fun getCategory_OK() {
        val categoryResponse = restTemplate.getForEntity<Category>("/category/1")
        Assertions.assertEquals(HttpStatus.OK, categoryResponse.statusCode)

        val category = categoryResponse.body!! // Throws NPE if null. Don't use in production code
        Assertions.assertEquals("Fantasy", category.name)
        Assertions.assertEquals("Fiction literature", category.description)
    }

    @Test
    fun getCategory_404() {
        val categoryResponse = restTemplate.getForEntity<ResourceNotFoundException>("/category/99")
        Assertions.assertEquals(HttpStatus.NOT_FOUND, categoryResponse.statusCode)
    }

    @Test
    fun addCategory_OK() {
        val categoryAddRequest = CategoryAddRequest("name", "description")
        val categoryResponse = restTemplate.postForEntity<String>("/category", categoryAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, categoryResponse.statusCode)

        val newCategoryId = categoryResponse.body!!.toLong()

        val getNewCategoryResponse = restTemplate.getForEntity<Category>("/category/${newCategoryId}")
        Assertions.assertEquals(HttpStatus.OK, getNewCategoryResponse.statusCode)

        val newCategory = getNewCategoryResponse.body!!
        Assertions.assertEquals(newCategoryId, newCategory.id)
        Assertions.assertEquals("name", newCategory.name)
        Assertions.assertEquals("description", newCategory.description)
    }

    @Test
    fun deleteNewCategory_OK() {
        // add category
        val categoryAddRequest = CategoryAddRequest("name", "description")
        val categoryResponse = restTemplate.postForEntity<String>("/category", categoryAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, categoryResponse.statusCode)
        val newCategoryId = categoryResponse.body!!.toLong()

        // new category exists
        val getNewCategoryResponse = restTemplate.getForEntity<Category>("/category/${newCategoryId}")
        Assertions.assertEquals(HttpStatus.OK, getNewCategoryResponse.statusCode)

        // delete category
        Assertions.assertEquals(
            HttpStatus.OK,
            restTemplate.exchange("/category/${newCategoryId}", HttpMethod.DELETE, null, Void::class.java).statusCode
        )

        // new category is removed
        Assertions.assertEquals(
            HttpStatus.NOT_FOUND,
            restTemplate.getForEntity<ResourceNotFoundException>("/category/${newCategoryId}").statusCode
        )
    }

    @Test
    fun deleteNonExistentCategory_404() {
        val response = restTemplate.exchange("/category/99", HttpMethod.DELETE, null, ResourceNotFoundException::class.java)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun deleteCategoryWillChangeBook_OK() {
        // delete category with id 1 (Fantasy) will change book with id 1 (Harry Potter) to category NULL

        // check that category and book exists and that book has categoryId 1 set
        Assertions.assertEquals(HttpStatus.OK, restTemplate.getForEntity<Category>("/category/1").statusCode)
        val bookResponse = restTemplate.getForEntity<Book>("/book/1")
        Assertions.assertEquals(HttpStatus.OK, bookResponse.statusCode)
        Assertions.assertEquals(1, bookResponse.body!!.categoryId)

        // delete category
        Assertions.assertEquals(
            HttpStatus.OK,
            restTemplate.exchange("/category/1", HttpMethod.DELETE, null, Void::class.java).statusCode
        )

        // check that category doesn't exist
        Assertions.assertEquals(
            HttpStatus.NOT_FOUND,
            restTemplate.getForEntity<ResourceNotFoundException>("/category/1").statusCode
        )
        // check that book has null categoryId
        val bookWithoutCategoryResponse = restTemplate.getForEntity<Book>("/book/1")
        Assertions.assertEquals(HttpStatus.OK, bookWithoutCategoryResponse.statusCode)
        Assertions.assertNull(bookWithoutCategoryResponse.body!!.categoryId)
    }

    @Test
    fun editNonExistentCategory_404() {
        val editRequest = CategoryEditRequest("changedName", "changedDescription")
        val response = restTemplate.exchange("/category/99", HttpMethod.PUT, HttpEntity(editRequest), ResourceNotFoundException::class.java)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun editNewCategory_OK() {
        // add category
        val categoryAddRequest = CategoryAddRequest("name", "description")
        val categoryResponse = restTemplate.postForEntity<String>("/category", categoryAddRequest)
        Assertions.assertEquals(HttpStatus.CREATED, categoryResponse.statusCode)
        val newCategoryId = categoryResponse.body!!.toLong()

        // edit new category
        val editRequest = CategoryEditRequest("changedName", "changedDescription")
        val response = restTemplate.exchange("/category/${newCategoryId}", HttpMethod.PUT, HttpEntity(editRequest), Void::class.java)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        // check if new category is changed
        val changedCategory = restTemplate.getForEntity<Category>("/category/${newCategoryId}")
        Assertions.assertEquals(HttpStatus.OK, changedCategory.statusCode)
        Assertions.assertEquals("changedName", changedCategory.body!!.name)
        Assertions.assertEquals("changedDescription", changedCategory.body!!.description)
    }
}