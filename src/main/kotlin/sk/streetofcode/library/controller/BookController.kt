package sk.streetofcode.library.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sk.streetofcode.library.api.BookService
import sk.streetofcode.library.api.request.BookAddRequest
import sk.streetofcode.library.domain.Book

@RestController
@RequestMapping("book")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Book>> {
        return ResponseEntity.ok(bookService.getAll())
    }

    @GetMapping("{id}")
    operator fun get(@PathVariable("id") id: Long): ResponseEntity<Book> {
        return ResponseEntity.ok(bookService.get(id)!!)
    }

    @PostMapping
    fun add(@RequestBody request: BookAddRequest): ResponseEntity<Long> {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.add(request)!!)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> {
        bookService.delete(id)
        return ResponseEntity.ok().build()
    }
}