package dronjak.nikola.UniLib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/loanBook/{userId}/{isbn}")
	public ResponseEntity<?> loanBook(@PathVariable Integer userId, @PathVariable String isbn) {
		return userService.loanBook(userId, isbn);
	}

	@PostMapping("/returnBook/{}")
	public ResponseEntity<?> returnBook(@PathVariable String isbn) {
		return userService.returnBook(isbn);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserDTO userDTO) {
		return userService.update(id, userDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
		return userService.delete(id);
	}
}
