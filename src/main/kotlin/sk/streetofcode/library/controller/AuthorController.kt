package sk.streetofcode.library.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sk.streetofcode.library.api.AuthorService
import sk.streetofcode.library.api.request.AuthorAddRequest
import sk.streetofcode.library.domain.Author

@RestController
@RequestMapping("author")
class AuthorController(private val authorService: AuthorService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Author>> {
        return ResponseEntity.ok(authorService.getAll())
    }

    @GetMapping("{id}")
    operator fun get(@PathVariable("id") id: Long): ResponseEntity<Author> {
        return ResponseEntity.ok(authorService.get(id)!!)
    }

    @PostMapping
    fun add(@RequestBody request: AuthorAddRequest): ResponseEntity<Long> {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.add(request)!!)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> {
        authorService.delete(id)
        return ResponseEntity.ok().build()
    }
}