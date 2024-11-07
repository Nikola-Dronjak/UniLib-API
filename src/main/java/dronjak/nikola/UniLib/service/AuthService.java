package dronjak.nikola.UniLib.service;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.repository.UserRepository;
import dronjak.nikola.UniLib.service.authentication.JwtService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Service
public class AuthService {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	private Validator validator;

	public AuthService() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}

	public ResponseEntity<?> register(UserDTO userDTO) {
		try {
			User user = convertFromDTO(userDTO);
			Set<ConstraintViolation<User>> violations = validator.validate(user);
			if (!violations.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				for (ConstraintViolation<User> violation : violations) {
					errors.put(violation.getPropertyPath().toString(), violation.getMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}

			if (userDTO.getDateOfBirth().after(new GregorianCalendar()))
				throw new RuntimeException("The users's date of birth has to be in the future.");

			if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
				throw new RuntimeException("This user already exists.");

			user.setPassword(new BCryptPasswordEncoder(12).encode(userDTO.getPassword()));
			user.setRole(UserRole.STUDENT);
			userRepository.save(user);
			return ResponseEntity.ok(jwtService.generateToken(userDTO.getEmail()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> login(UserDTO userDTO) {
		try {
			Authentication user = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));

			if (user.isAuthenticated())
				return ResponseEntity.ok(jwtService.generateToken(userDTO.getEmail()));

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	private User convertFromDTO(UserDTO userDTO) {
		User user = new User();
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
		user.setDateOfBirth(userDTO.getDateOfBirth());

		return user;
	}
}
