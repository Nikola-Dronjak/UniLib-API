package dronjak.nikola.UniLib.seeder;

import java.util.GregorianCalendar;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.domain.Book;
import dronjak.nikola.UniLib.domain.BookGenre;
import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import dronjak.nikola.UniLib.repository.AuthorRepository;
import dronjak.nikola.UniLib.repository.BookRepository;
import dronjak.nikola.UniLib.repository.UserRepository;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@Override
	public void run(String... args) throws Exception {
		if (userRepository.count() == 0) {
			userRepository.save(new User(1, "Petar", "Petrović", "admin@gmail.com",
					new BCryptPasswordEncoder(12).encode("admin123"), UserRole.ADMINISTRATOR,
					new GregorianCalendar(2000, 1, 1)));
			userRepository.save(new User(2, "Jovan", "Jovanović", "student@gmail.com",
					new BCryptPasswordEncoder(12).encode("student123"), UserRole.STUDENT,
					new GregorianCalendar(2000, 1, 1)));
		}

		Author archibaldReiss = new Author(1, "Archibald Reiss", new GregorianCalendar(1875, 6, 8),
				new GregorianCalendar(1929, 7, 8), new HashSet<>());
		Author niccoloMachiavelli = new Author(2, "Niccolo Machiavelli", new GregorianCalendar(1469, 4, 3),
				new GregorianCalendar(1527, 5, 21), new HashSet<>());
		Author albertCamus = new Author(3, "Albert Camus", new GregorianCalendar(1913, 10, 7),
				new GregorianCalendar(1960, 0, 4), new HashSet<>());
		Author ivoAndric = new Author(4, "Ivo Andrić", new GregorianCalendar(1892, 9, 10),
				new GregorianCalendar(1975, 2, 13), new HashSet<>());
		Author halilInalcik = new Author(5, "Halil Inalcik", new GregorianCalendar(1916, 8, 7),
				new GregorianCalendar(2016, 6, 25), new HashSet<>());
		Author fyodorDostoevsky = new Author(6, "Fyodor Dostoevsky", new GregorianCalendar(1828, 8, 9),
				new GregorianCalendar(1910, 10, 20), new HashSet<>());
		Author jimKurose = new Author(7, "Jim Kurose", new GregorianCalendar(1956, 8, 13), null, new HashSet<>());
		Author keithRoss = new Author(8, "Keith Ross", new GregorianCalendar(1945, 5, 6), null, new HashSet<>());
		if (authorRepository.count() == 0) {
			authorRepository.save(archibaldReiss);
			authorRepository.save(niccoloMachiavelli);
			authorRepository.save(albertCamus);
			authorRepository.save(ivoAndric);
			authorRepository.save(halilInalcik);
			authorRepository.save(fyodorDostoevsky);
			authorRepository.save(jimKurose);
			authorRepository.save(keithRoss);
		}

		Book book1 = new Book("9788610031300", "Čujte Srbi! Čuvajte se sebe", BookGenre.NOVEL, 128, 5, true,
				new HashSet<>());
		Book book2 = new Book("9781847493231", "The Prince", BookGenre.NOVEL, 100, 5, true, new HashSet<>());
		Book book3 = new Book("9788689203752", "The Stranger", BookGenre.FICTION, 102, 5, true, new HashSet<>());
		Book book4 = new Book("9788674706404", "Na Drini ćuprija", BookGenre.FICTION, 333, 5, true, new HashSet<>());
		Book book5 = new Book("9788674706398", "Prokleta avlija", BookGenre.FICTION, 85, 5, true, new HashSet<>());
		Book book6 = new Book("9786058301184",
				"The Ottoman Empire And Europe: The Ottoman Empire and Its Place in European History",
				BookGenre.HISTORY, 272, 5, true, new HashSet<>());
		Book book7 = new Book("9789752430327", "The Ottoman Empire - Sultan, Society and Economy", BookGenre.HISTORY,
				512, 5, true, new HashSet<>());
		Book book8 = new Book("9781840224306", "Crime and Punishment", BookGenre.NOVEL, 528, 5, true, new HashSet<>());
		Book book9 = new Book("9780133594140", "Computer Networking: A Top-Down Approach", BookGenre.FICTION, 864, 5,
				true, new HashSet<>());

		book1.getAuthors().add(archibaldReiss);
		book2.getAuthors().add(niccoloMachiavelli);
		book3.getAuthors().add(albertCamus);
		book4.getAuthors().add(ivoAndric);
		book5.getAuthors().add(ivoAndric);
		book6.getAuthors().add(halilInalcik);
		book7.getAuthors().add(halilInalcik);
		book8.getAuthors().add(fyodorDostoevsky);
		book9.getAuthors().add(jimKurose);
		book9.getAuthors().add(keithRoss);

		bookRepository.save(book1);
		bookRepository.save(book2);
		bookRepository.save(book3);
		bookRepository.save(book4);
		bookRepository.save(book5);
		bookRepository.save(book6);
		bookRepository.save(book7);
		bookRepository.save(book8);
		bookRepository.save(book9);

		archibaldReiss.getBooks().add(book1);
		niccoloMachiavelli.getBooks().add(book2);
		albertCamus.getBooks().add(book3);
		ivoAndric.getBooks().add(book4);
		ivoAndric.getBooks().add(book5);
		halilInalcik.getBooks().add(book6);
		halilInalcik.getBooks().add(book7);
		fyodorDostoevsky.getBooks().add(book8);
		jimKurose.getBooks().add(book9);
		keithRoss.getBooks().add(book9);

		authorRepository.save(archibaldReiss);
		authorRepository.save(niccoloMachiavelli);
		authorRepository.save(albertCamus);
		authorRepository.save(ivoAndric);
		authorRepository.save(halilInalcik);
		authorRepository.save(fyodorDostoevsky);
		authorRepository.save(jimKurose);
		authorRepository.save(keithRoss);
	}
}
