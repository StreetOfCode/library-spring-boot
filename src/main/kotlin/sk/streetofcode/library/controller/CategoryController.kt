package sk.streetofcode.library.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sk.streetofcode.library.api.CategoryService
import sk.streetofcode.library.api.request.CategoryAddRequest
import sk.streetofcode.library.api.request.CategoryEditRequest
import sk.streetofcode.library.domain.Author
import sk.streetofcode.library.domain.Book
import sk.streetofcode.library.domain.Category

@RestController
@RequestMapping("category")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryService.getAll())
    }

    @GetMapping("{id}")
    fun get(@PathVariable("id") id: Long): ResponseEntity<Category> {
        return ResponseEntity.ok(categoryService.get(id)!!)
    }

    @PostMapping
    fun add(@RequestBody request: CategoryAddRequest): ResponseEntity<Long> {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.add(request))
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> {
        categoryService.delete(id)
        return ResponseEntity.ok().build()
    }

    @PutMapping("{id}")
    fun edit(@PathVariable("id") id: Long, @RequestBody request: CategoryEditRequest): ResponseEntity<Void> {
        categoryService.edit(id, request)
        return ResponseEntity.ok().build()
    }
}