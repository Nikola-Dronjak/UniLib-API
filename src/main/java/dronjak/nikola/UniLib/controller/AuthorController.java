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

import dronjak.nikola.UniLib.dto.AuthorDTO;
import dronjak.nikola.UniLib.service.AuthorService;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

	@Autowired
	private AuthorService authorService;

	@GetMapping
	public ResponseEntity<?> getAllAuthors() {
		return authorService.getAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getAuthorById(@PathVariable Integer id) {
		return authorService.getById(id);
	}

	@PostMapping
	public ResponseEntity<?> addAuthor(@RequestBody AuthorDTO authorDTO) {
		return authorService.add(authorDTO);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateAuthor(@PathVariable Integer id, @RequestBody AuthorDTO authorDTO) {
		return authorService.update(id, authorDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAuthor(@PathVariable Integer id) {
		return authorService.delete(id);
	}
}
