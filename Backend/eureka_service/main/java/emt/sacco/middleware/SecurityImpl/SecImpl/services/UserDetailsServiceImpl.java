package emt.sacco.middleware.SecurityImpl.SecImpl.services;


import com.google.gson.Gson;
import emt.sacco.middleware.SecurityImpl.Sec.SwitchUsers;
import emt.sacco.middleware.SecurityImpl.repository.UserRepository;
import emt.sacco.middleware.Utils.CustomerInfo.UserDetailsRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository usersRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SwitchUsers switchUsers = usersRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		    Gson g = new Gson();
			UserDetailsRequestContext.setCurrentUserDetails(g.toJson(switchUsers));
		return UserDetailsImpl.build(switchUsers);
	}
}
