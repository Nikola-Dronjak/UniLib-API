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

import dronjak.nikola.UniLib.dto.BookLoanDTO;
import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/myBooks/{userId}")
	public ResponseEntity<?> getAllBooksLoanedByUser(@PathVariable Integer userId) {
		return userService.getAllBooksLoanedByUserId(userId);
	}
	
	@GetMapping("/{email}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
		return userService.getByEmail(email);
	}

	@PostMapping("/loanBook")
	public ResponseEntity<?> loanBook(@RequestBody BookLoanDTO bookLoanDTO) {
		return userService.loanBook(bookLoanDTO);
	}

	@PostMapping("/returnBook")
	public ResponseEntity<?> returnBook(@RequestBody BookLoanDTO bookLoanDTO) {
		return userService.returnBook(bookLoanDTO);
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
