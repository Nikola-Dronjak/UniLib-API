package dronjak.nikola.UniLib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.service.AuthService;

@RestController
@RequestMapping("/api/users")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
		return authService.register(userDTO);
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
		return authService.login(userDTO);
	}
}
