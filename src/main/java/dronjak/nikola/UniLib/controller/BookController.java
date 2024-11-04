package dronjak.nikola.UniLib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dronjak.nikola.UniLib.dto.BookDTO;
import dronjak.nikola.UniLib.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

	@Autowired
	private BookService bookService;

	@GetMapping
	public ResponseEntity<?> getAllBooks() {
		return bookService.getAll();
	}

	@GetMapping("/{isbn}")
	public ResponseEntity<?> getBookByISBN(@PathVariable String isbn) {
		return bookService.getByISBN(isbn);
	}

	@PostMapping
	public ResponseEntity<?> addBook(@RequestBody BookDTO bookDTO) {
		return bookService.add(bookDTO);
	}

	@PutMapping("/{isbn}")
	public ResponseEntity<?> updateBook(@PathVariable String isbn, @RequestBody BookDTO bookDTO) {
		return bookService.update(isbn, bookDTO);
	}

	@DeleteMapping("/{isbn}")
	public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
		return bookService.delete(isbn);
	}
}
