package dronjak.nikola.UniLib.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dronjak.nikola.UniLib.domain.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
	
	Optional<Author> findByName(String name);
}
