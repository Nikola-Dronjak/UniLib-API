package dronjak.nikola.UniLib.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.dto.AuthorDTO;
import dronjak.nikola.UniLib.repository.AuthorRepository;

@SpringBootTest
class AuthorServiceTest {

	Author author1;

	Author author2;

	@Mock
	private AuthorRepository authorRepository;

	@InjectMocks
	private AuthorService authorService;

	@BeforeEach
	void setUp() throws Exception {
		author1 = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4));
		author2 = new Author(2, "Mika Mikic", new GregorianCalendar(2024, 10, 5), new GregorianCalendar(2024, 10, 6));
	}

	@AfterEach
	void tearDown() throws Exception {
		author1 = null;
		author2 = null;
	}

	@Test
	void testGetAllError() {
		when(authorRepository.findAll()).thenThrow(new RuntimeException("Something went wrong"));

		ResponseEntity<?> response = authorService.getAll();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Something went wrong", response.getBody());
	}

	@Test
	void testGetAll() {
		List<Author> authors = new ArrayList<Author>();
		authors.add(author1);
		authors.add(author2);
		when(authorRepository.findAll()).thenReturn(authors);

		ResponseEntity<?> response = authorService.getAll();
		List<AuthorDTO> authorDTOs = authors.stream().map(this::convertToDTO).collect(Collectors.toList());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(authorDTOs, response.getBody());
	}

	@Test
	void testGetByIdBadId() {
		when(authorRepository.findById(1)).thenReturn(Optional.empty());

		ResponseEntity<?> response = authorService.getById(1);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no author with the given id.", response.getBody());
	}

	@Test
	void testGetById() {
		when(authorRepository.findById(1)).thenReturn(Optional.of(author1));

		ResponseEntity<?> response = authorService.getById(1);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(author1), response.getBody());
	}

	@Test
	void testAddBadDateOfBirth() {
		when(authorRepository.save(any(Author.class)))
				.thenThrow(new RuntimeException("The author's date of birth has to be before his/hers date of death."));

		AuthorDTO authorDTO = convertToDTO(author1);
		GregorianCalendar birthDate = author1.getDateOfBirth();
		birthDate.add(Calendar.DAY_OF_MONTH, 1);
		authorDTO.setDateOfBirth(birthDate);

		ResponseEntity<?> response = authorService.add(authorDTO);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("The author's date of birth has to be before his/hers date of death.", response.getBody());
	}

	@Test
	void testAddBadDateOfDeath() {
		when(authorRepository.save(any(Author.class)))
				.thenThrow(new RuntimeException("The author's date of death has to be after his/hers date of birth."));

		AuthorDTO authorDTO = convertToDTO(author1);
		GregorianCalendar deathDate = author1.getDateOfDeath();
		deathDate.add(Calendar.DAY_OF_MONTH, 1);
		authorDTO.setDateOfBirth(deathDate);

		ResponseEntity<?> response = authorService.add(authorDTO);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("The author's date of death has to be after his/hers date of birth.", response.getBody());
	}

	@Test
	void testAddDuplicateAuthor() {
		when(authorRepository.findByName(author1.getName())).thenReturn(Optional.of(author1));

		ResponseEntity<?> response = authorService.add(convertToDTO(author1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("This author already exists.", response.getBody());
	}

	@Test
	void testAdd() {
		when(authorRepository.findByName(author1.getName())).thenReturn(Optional.empty());
		when(authorRepository.save(any(Author.class))).thenReturn(author1);

		ResponseEntity<?> response = authorService.add(convertToDTO(author1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(author1), response.getBody());
	}

	@Test
	void testUpdateBadId() {
		when(authorRepository.findById(author1.getAuthorId())).thenReturn(Optional.empty());

		ResponseEntity<?> response = authorService.update(1, convertToDTO(author1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no author with the given id.", response.getBody());
	}

	@Test
	void testUpdateBadDateOfBirth() {
		when(authorRepository.findById(1)).thenReturn(Optional.of(author1));
		when(authorRepository.save(any(Author.class)))
				.thenThrow(new RuntimeException("The author's date of birth has to be before his/hers date of death."));

		AuthorDTO authorDTO = convertToDTO(author1);
		GregorianCalendar birthDate = author1.getDateOfBirth();
		birthDate.add(Calendar.DAY_OF_MONTH, 1);
		authorDTO.setDateOfBirth(birthDate);

		ResponseEntity<?> response = authorService.add(authorDTO);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("The author's date of birth has to be before his/hers date of death.", response.getBody());
	}

	@Test
	void testUpdateBadDateOfDeath() {
		when(authorRepository.findById(1)).thenReturn(Optional.of(author1));
		when(authorRepository.save(any(Author.class)))
				.thenThrow(new RuntimeException("The author's date of death has to be after his/hers date of birth."));

		AuthorDTO authorDTO = convertToDTO(author1);
		GregorianCalendar deathDate = author1.getDateOfDeath();
		deathDate.add(Calendar.DAY_OF_MONTH, 1);
		authorDTO.setDateOfBirth(deathDate);

		ResponseEntity<?> response = authorService.add(authorDTO);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("The author's date of death has to be after his/hers date of birth.", response.getBody());
	}

	@Test
	void testUpdateDuplicateAuthor() {
		when(authorRepository.findById(author1.getAuthorId())).thenReturn(Optional.of(author1));
		when(authorRepository.findByName(author1.getName())).thenReturn(Optional.of(author1));
		when(authorRepository.save(any(Author.class))).thenThrow(new RuntimeException("This author already exists."));

		ResponseEntity<?> response = authorService.update(author1.getAuthorId(), convertToDTO(author1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("This author already exists.", response.getBody());
	}

	@Test
	void testUpdate() {
		when(authorRepository.findById(author1.getAuthorId())).thenReturn(Optional.of(author1));
		when(authorRepository.findByName(author1.getName())).thenReturn(Optional.empty());
		when(authorRepository.save(any(Author.class))).thenReturn(author1);

		ResponseEntity<?> response = authorService.update(1, convertToDTO(author1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(author1), response.getBody());
	}

	@Test
	void testDeleteBadId() {
		when(authorRepository.findById(author1.getAuthorId())).thenReturn(Optional.empty());

		ResponseEntity<?> response = authorService.delete(1);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no author with the given id.", response.getBody());
	}

	@Test
	void testDelete() {
		when(authorRepository.findById(author1.getAuthorId())).thenReturn(Optional.of(author1));

		ResponseEntity<?> response = authorService.delete(1);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(author1), response.getBody());
	}

	private AuthorDTO convertToDTO(Author author) {
		AuthorDTO authorDTO = new AuthorDTO();
		authorDTO.setName(author.getName());
		authorDTO.setDateOfBirth(author.getDateOfBirth());
		authorDTO.setDateOfDeath(author.getDateOfDeath());

		return authorDTO;
	}
}
