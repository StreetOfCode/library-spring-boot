package sk.streetofcode.library

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
import sk.streetofcode.library.integration.IntegrationTest

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

    // TODO test for delete category will change book
}