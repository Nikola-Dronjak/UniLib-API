package dronjak.nikola.UniLib.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dronjak.nikola.UniLib.domain.BookLoan;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Integer> {

	Optional<BookLoan> findByBook_Isbn(String isbn);
}
