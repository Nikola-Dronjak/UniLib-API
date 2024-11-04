package dronjak.nikola.UniLib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dronjak.nikola.UniLib.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

}
