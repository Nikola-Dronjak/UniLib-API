package dronjak.nikola.UniLib.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@Table(name = "Users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;

	@NotBlank(message = "The first name of the user is required.")
	@Size(min = 2, message = "The first name of the user has to have at least 2 characters.")
	private String firstName;

	@NotBlank(message = "The last name of the user is required.")
	@Size(min = 2, message = "The last name of the user has to have at least 2 characters.")
	private String lastName;

	@NotBlank(message = "The email of the user is required.")
	@Email(message = "The email address of the user must be valid.")
	@Column(unique = true)
	private String email;

	@NotBlank(message = "The password of the user is required.")
	@Size(min = 5, message = "The password of the user has to have at least 5 characters.")
	private String password;

	private UserRole role;

	@NotNull(message = "The user's date of birth is required.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private GregorianCalendar dateOfBirth;

	public User() {

	}

	public User(int userId, String firstName, String lastName, String email, String password, UserRole role,
			GregorianCalendar dateOfBirth) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.role = role;
		this.dateOfBirth = dateOfBirth;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public GregorianCalendar getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(GregorianCalendar dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", password=" + password
				+ ", role=" + role + ", dateOfBirth=" + dateOfBirth.getTime() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateOfBirth, email, firstName, lastName, password, role, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(dateOfBirth, other.dateOfBirth) && Objects.equals(email, other.email)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(password, other.password) && role == other.role && userId == other.userId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role.toString()));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}
}
