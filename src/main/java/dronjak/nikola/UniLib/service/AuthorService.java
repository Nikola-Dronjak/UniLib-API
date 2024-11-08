package dronjak.nikola.UniLib.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.dto.AuthorDTO;
import dronjak.nikola.UniLib.repository.AuthorRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;

	private Validator validator;

	public AuthorService() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}

	public ResponseEntity<?> getAll() {
		try {
			List<Author> authors = authorRepository.findAll();
			List<AuthorDTO> authorDTOs = authors.stream().map(this::convertToDTO).collect(Collectors.toList());
			return ResponseEntity.ok(authorDTOs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	public ResponseEntity<?> getById(Integer id) {
		try {
			Optional<Author> authorFromDb = authorRepository.findById(id);
			if (!authorFromDb.isPresent())
				throw new RuntimeException("There is no author with the given id.");

			AuthorDTO authorDTO = convertToDTO(authorFromDb.get());
			return ResponseEntity.ok(authorDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> add(AuthorDTO authorDTO) {
		try {
			Author author = convertFromDTO(authorDTO);
			Set<ConstraintViolation<Author>> violations = validator.validate(author);
			if (!violations.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				for (ConstraintViolation<Author> violation : violations) {
					errors.put(violation.getPropertyPath().toString(), violation.getMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}

			if (authorDTO.getDateOfBirth().after(authorDTO.getDateOfDeath()))
				throw new RuntimeException("The author's date of birth has to be before his/hers date of death.");

			if (authorDTO.getDateOfDeath().before(authorDTO.getDateOfBirth()))
				throw new RuntimeException("The author's date of death has to be after hist/hers date of birth.");

			if (authorRepository.findByName(authorDTO.getName()).isPresent())
				throw new RuntimeException("This author already exists.");

			Author newAuthor = authorRepository.save(author);
			AuthorDTO newAuthorDTO = convertToDTO(newAuthor);
			return ResponseEntity.ok(newAuthorDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> update(Integer id, AuthorDTO authorDTO) {
		try {
			Optional<Author> authorFromDb = authorRepository.findById(id);
			if (!authorFromDb.isPresent())
				throw new RuntimeException("There is no author with the given id.");

			Author author = convertFromDTO(authorDTO);
			Set<ConstraintViolation<Author>> violations = validator.validate(author);
			if (!violations.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				for (ConstraintViolation<Author> violation : violations) {
					errors.put(violation.getPropertyPath().toString(), violation.getMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}

			if (authorDTO.getDateOfBirth().after(authorDTO.getDateOfDeath()))
				throw new RuntimeException("The author's date of birth has to be before his/hers date of death.");

			if (authorDTO.getDateOfDeath().before(authorDTO.getDateOfBirth()))
				throw new RuntimeException("The author's date of death has to be after hist/hers date of birth.");

			if (!authorFromDb.get().getName().equals(authorDTO.getName())) {
				Optional<Author> existingAuthor = authorRepository.findByName(authorDTO.getName());
				if (existingAuthor.isPresent() && existingAuthor.get().getAuthorId() != id) {
					throw new RuntimeException("This author already exists.");
				}
			}

			author.setAuthorId(id);
			Author updatedAuthor = authorRepository.save(author);
			AuthorDTO updatedAuthorDTO = convertToDTO(updatedAuthor);
			return ResponseEntity.ok(updatedAuthorDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> delete(Integer id) {
		try {
			Optional<Author> authorFromDb = authorRepository.findById(id);
			if (!authorFromDb.isPresent())
				throw new RuntimeException("There is no author with the given id.");

			authorFromDb.get().getBooks().forEach(book -> book.getAuthors().remove(authorFromDb.get()));
			authorRepository.deleteById(id);
			AuthorDTO deletedAuthorDTO = convertToDTO(authorFromDb.get());
			return ResponseEntity.ok(deletedAuthorDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	private AuthorDTO convertToDTO(Author author) {
		AuthorDTO authorDTO = new AuthorDTO();
		authorDTO.setAuthorId(author.getAuthorId());
		authorDTO.setName(author.getName());
		authorDTO.setDateOfBirth(author.getDateOfBirth());
		authorDTO.setDateOfDeath(author.getDateOfDeath());

		return authorDTO;
	}

	private Author convertFromDTO(AuthorDTO authorDTO) {
		Author author = new Author();
		author.setName(authorDTO.getName());
		author.setDateOfBirth(authorDTO.getDateOfBirth());
		author.setDateOfDeath(authorDTO.getDateOfDeath());

		return author;
	}
}
