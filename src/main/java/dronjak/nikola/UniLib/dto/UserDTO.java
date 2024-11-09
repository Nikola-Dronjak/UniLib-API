package dronjak.nikola.UniLib.dto;

import java.util.GregorianCalendar;
import java.util.Objects;

import dronjak.nikola.UniLib.domain.UserRole;

public class UserDTO {

	private int userId;

	private String firstName;

	private String lastName;

	private String email;

	private String password;

	private UserRole role;

	private GregorianCalendar dateOfBirth;

	public UserDTO() {

	}

	public UserDTO(int userId, String firstName, String lastName, String email, String password, UserRole role,
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	@Override
	public String toString() {
		return "UserDTO [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", role=" + role + ", dateOfBirth=" + dateOfBirth + "]";
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
		UserDTO other = (UserDTO) obj;
		return Objects.equals(dateOfBirth, other.dateOfBirth) && Objects.equals(email, other.email)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(password, other.password) && role == other.role && userId == other.userId;
	}
}
