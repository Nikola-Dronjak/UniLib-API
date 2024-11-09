package dronjak.nikola.UniLib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.domain.Book;
import dronjak.nikola.UniLib.domain.BookGenre;
import dronjak.nikola.UniLib.domain.BookLoan;
import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import dronjak.nikola.UniLib.dto.BookDTO;
import dronjak.nikola.UniLib.dto.BookLoanDTO;
import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.service.UserService;

@SpringBootTest
class UserControllerTest {

	User user1;

	User user2;

	Book book1;

	Book book2;

	Author author;

	Set<Author> authors;

	BookLoan bookLoan1;

	BookLoan bookLoan2;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	void setUp() throws Exception {
		user1 = new User(1, "Pera", "Peric", "pera@gmail.com", "test123", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 4));
		user2 = new User(2, "Mika", "Mikic", "mika@gmail.com", "test321", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 5));

		book1 = new Book("1234567890123", "Test1", BookGenre.NOVEL, 100, 5, true, new HashSet<Author>());
		book2 = new Book("1234567890123", "Test2", BookGenre.FICTION, 100, 0, false, new HashSet<Author>());

		author = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				new HashSet<>());

		authors = new HashSet<>();
		authors.add(author);

		book1.setAuthors(authors);
		book2.setAuthors(authors);

		bookLoan1 = new BookLoan(1, new GregorianCalendar(2024, 10, 3), null, user1, book1);
		bookLoan2 = new BookLoan(2, new GregorianCalendar(2024, 10, 4), new GregorianCalendar(2024, 10, 5), user1,
				book2);
	}

	@AfterEach
	void tearDown() throws Exception {
		user1 = null;
		user2 = null;

		book1 = null;
		book2 = null;

		author = null;

		authors = null;

		bookLoan1 = null;
		bookLoan2 = null;
	}

	void testGetAllBooksLoanedByUserError() throws Exception {
		try {
			lenient().when(userService.getAllBooksLoanedByUserId(1)).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
			mockMvc.perform(get("/api/users/myBooks/1").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testGetAllBooksLoanedByUser() throws Exception {
		List<BookLoan> bookLoans = new ArrayList<BookLoan>();
		bookLoans.add(bookLoan1);
		List<BookDTO> loanedBookDTOs = Arrays.asList(convertToDTO(book1));
		when(userService.getAllBooksLoanedByUserId(1)).thenReturn((ResponseEntity) ResponseEntity.ok(loanedBookDTOs));

		String loanedBooksJson = new ObjectMapper().writeValueAsString(loanedBookDTOs);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		mockMvc.perform(get("/api/users/myBooks/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(loanedBooksJson));
	}

	@Test
	void testLoanBookError() throws Exception {
		lenient().when(userService.loanBook(convertToDTO(bookLoan1))).thenThrow(new RuntimeException());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		mockMvc.perform(post("/api/users/loanBook").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testLoanBook() throws Exception {
		when(userService.loanBook(convertToDTO(bookLoan1))).thenReturn((ResponseEntity) ResponseEntity
				.ok("A copy of the book: " + book1 + " was loand out to user: " + user1));

		String bookLoanJson = new ObjectMapper().writeValueAsString(convertToDTO(bookLoan1));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		mockMvc.perform(post("/api/users/loanBook").contentType(MediaType.APPLICATION_JSON).content(bookLoanJson))
				.andExpect(status().isOk())
				.andExpect(content().string("A copy of the book: " + book1 + " was loand out to user: " + user1));
	}

	@Test
	void testReturnBookError() throws Exception {
		lenient().when(userService.returnBook(convertToDTO(bookLoan1))).thenThrow(new RuntimeException());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		mockMvc.perform(post("/api/users/returnBook").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testReturnBook() throws Exception {
		when(userService.returnBook(convertToDTO(bookLoan1)))
				.thenReturn((ResponseEntity) ResponseEntity.ok("User: " + user1 + " returned book: " + book1));

		String bookLoanJson = new ObjectMapper().writeValueAsString(convertToDTO(bookLoan1));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		mockMvc.perform(post("/api/users/returnBook").contentType(MediaType.APPLICATION_JSON).content(bookLoanJson))
				.andExpect(status().isOk()).andExpect(content().string("User: " + user1 + " returned book: " + book1));
	}

	@Test
	void testUpdateUserError() throws Exception {
		lenient().when(userService.update(1, convertToDTO(user1))).thenThrow(new RuntimeException());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testUpdateUser() throws Exception {
		when(userService.update(eq(1), any(UserDTO.class))).thenReturn((ResponseEntity) ResponseEntity.ok(user1));

		String userJson = new ObjectMapper().writeValueAsString(convertToDTO(user1));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(userJson))
				.andExpect(status().isOk()).andExpect(content().json(userJson));
	}

	@Test
	void testDeleteUserError() throws Exception {
		try {
			lenient().when(userService.delete(1)).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
			mockMvc.perform(delete("/api/users/1").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testDeleteUser() throws Exception {
		when(userService.delete(1)).thenReturn((ResponseEntity) ResponseEntity.ok(user1));

		String userJson = new ObjectMapper().writeValueAsString(user1);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		mockMvc.perform(delete("/api/users/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(userJson));
	}

	private BookDTO convertToDTO(Book book) {
		BookDTO bookDTO = new BookDTO();
		bookDTO.setIsbn(book.getIsbn());
		bookDTO.setTitle(book.getTitle());
		bookDTO.setGenre(book.getGenre());
		bookDTO.setNumberOfPages(book.getNumberOfPages());
		bookDTO.setNumberOfCopies(book.getNumberOfCopies());
		bookDTO.setAvailable(book.getAvailable());

		Set<Integer> authorIds = book.getAuthors().stream().map(Author::getAuthorId).collect(Collectors.toSet());
		bookDTO.setAuthorIds(authorIds);

		return bookDTO;
	}

	private UserDTO convertToDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(user.getUserId());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());
		userDTO.setEmail(user.getEmail());
		userDTO.setPassword(user.getPassword());
		userDTO.setRole(user.getRole());
		userDTO.setDateOfBirth(user.getDateOfBirth());

		return userDTO;
	}

	private BookLoanDTO convertToDTO(BookLoan bookLoan) {
		BookLoanDTO bookLoanDTO = new BookLoanDTO();
		bookLoanDTO.setUserId(bookLoan.getUser().getUserId());
		bookLoanDTO.setBookISBN(bookLoan.getBook().getIsbn());

		return bookLoanDTO;
	}
}
