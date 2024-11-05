package dronjak.nikola.UniLib.service.authentication;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.repository.UserRepository;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(email);
		if (!user.isPresent())
			throw new UsernameNotFoundException("There is no user with the given email address.");

		return user.get();
	}
}
