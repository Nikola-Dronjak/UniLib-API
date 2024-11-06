package dronjak.nikola.UniLib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.GregorianCalendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.service.AuthService;

@SpringBootTest
class AuthControllerTest {

	User user1;

	User user2;

	@Mock
	private AuthService authService;

	@InjectMocks
	private AuthController authController;

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
	void testRegisterError() throws Exception {
		lenient().when(authService.register(convertToDTO(user1))).thenThrow(new RuntimeException());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
		mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testRegisterUser() throws Exception {
		when(authService.register(any(UserDTO.class)))
				.thenReturn((ResponseEntity) ResponseEntity.ok("Account successfully created."));

		String userJson = new ObjectMapper().writeValueAsString(convertToDTO(user1));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
		mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON).content(userJson))
				.andExpect(status().isOk()).andExpect(content().string("Account successfully created."));
	}

	@Test
	void testLoginError() throws Exception {
		try {
			lenient().when(authService.login(convertToDTO(user1))).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
			mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testLoginUser() throws Exception {
		when(authService.login(convertToDTO(user1))).thenReturn((ResponseEntity) ResponseEntity.ok("x-mock-token"));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
		mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("x-mock-token"));
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
