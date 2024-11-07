package dronjak.nikola.UniLib.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.repository.UserRepository;
import dronjak.nikola.UniLib.service.authentication.JwtService;

@SpringBootTest
class AuthServiceTest {

	User user1;

	User user2;

	@Mock
	private JwtService jwtService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AuthService authService;

	@BeforeEach
	void setUp() throws Exception {
		user1 = new User(1, "Pera", "Peric", "pera@gmail.com", "test123", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 4));
		user2 = new User(2, "Mika", "Mikic", "mika@gmail.com", "test321", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 5));
	}

	@AfterEach
	void tearDown() throws Exception {
		user1 = null;
		user2 = null;
	}

	@Test
	void testRegisterBadDateOfBirth() {
		when(userRepository.save(any(User.class)))
				.thenThrow(new RuntimeException("The user's date of birth has to be after his/hers date of birth."));

		UserDTO userDTO = convertToDTO(user1);
		GregorianCalendar deathDate = user1.getDateOfBirth();
		deathDate.add(Calendar.DAY_OF_MONTH, 1);
		userDTO.setDateOfBirth(deathDate);

		ResponseEntity<?> response = authService.register(userDTO);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("The user's date of birth has to be after his/hers date of birth.", response.getBody());
	}

	@Test
	void testRegisterDuplicateUser() {
		when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

		ResponseEntity<?> response = authService.register(convertToDTO(user1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("This user already exists.", response.getBody());
	}

	@Test
	void testRegister() {
		when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(user1);

		ResponseEntity<?> response = authService.register(convertToDTO(user1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(jwtService.generateToken(user1.getEmail()), response.getBody());
	}

	@Test
	void testLoginNotAuthenticated() {
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Invalid username or password."));

		ResponseEntity<?> response = authService.login(convertToDTO(user1));

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals("Invalid username or password.", response.getBody());
	}

	@Test
	void testLogin() {
		Authentication authentication = mock(Authentication.class);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);

		when(authentication.isAuthenticated()).thenReturn(true);

		String expectedToken = "mocked-jwt-token";
		when(jwtService.generateToken(user1.getEmail())).thenReturn(expectedToken);

		ResponseEntity<?> response = authService.login(convertToDTO(user1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedToken, response.getBody());
	}

	private UserDTO convertToDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());
		userDTO.setEmail(user.getEmail());
		userDTO.setPassword(user.getPassword());
		userDTO.setDateOfBirth(user.getDateOfBirth());

		return userDTO;
	}
}
