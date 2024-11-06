package dronjak.nikola.UniLib.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.repository.UserRepository;

@SpringBootTest
class UserServiceTest {

	User user1;

	User user2;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

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
	void testUpdateBadId() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.empty());

		ResponseEntity<?> response = userService.update(1, convertToDTO(user1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no user with the given id.", response.getBody());
	}

	@Test
	void testUpdateBadDateOfBirth() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		when(userRepository.save(any(User.class)))
				.thenThrow(new RuntimeException("The user's date of birth has to be before his/hers date of death."));

		UserDTO userDTO = convertToDTO(user1);
		GregorianCalendar birthDate = user1.getDateOfBirth();
		birthDate.add(Calendar.DAY_OF_MONTH, 1);
		userDTO.setDateOfBirth(birthDate);

		ResponseEntity<?> response = userService.update(1, userDTO);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("The user's date of birth has to be before his/hers date of death.", response.getBody());
	}

	@Test
	void testUpdateDuplicateAuthor() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
		when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
		when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("This user already exists."));

		ResponseEntity<?> response = userService.update(user1.getUserId(), convertToDTO(user1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("This user already exists.", response.getBody());
	}

	@Test
	void testUpdate() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
		when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(user1);

		ResponseEntity<?> response = userService.update(1, convertToDTO(user1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(user1), response.getBody());
	}

	@Test
	void testDeleteBadId() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.empty());

		ResponseEntity<?> response = userService.delete(1);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no user with the given id.", response.getBody());
	}

	@Test
	void testDelete() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));

		ResponseEntity<?> response = userService.delete(1);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(user1), response.getBody());
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
