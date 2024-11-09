package dronjak.nikola.UniLib.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dronjak.nikola.UniLib.domain.BookLoan;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Integer> {

	@Query("SELECT bl FROM BookLoan bl WHERE bl.user.id = :userId AND bl.returnDate IS NULL")
	List<BookLoan> findActiveBookLoansByUserId(@Param("userId") Integer userId);

	@Query("SELECT bl FROM BookLoan bl WHERE bl.user.id = :userId AND bl.book.isbn = :bookISBN AND bl.returnDate IS NULL")
	Optional<BookLoan> findActiveLoanByUserAndBook(@Param("userId") Integer userId, @Param("bookISBN") String bookISBN);
}
