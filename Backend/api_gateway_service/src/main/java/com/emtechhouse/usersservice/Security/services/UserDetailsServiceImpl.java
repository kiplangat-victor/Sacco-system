package com.emtechhouse.usersservice.Security.services;


import com.emtechhouse.usersservice.Users.Users;
import com.emtechhouse.usersservice.Users.UsersRepository;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserDetailsRequestContext;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UsersRepository usersRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = usersRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		    Gson g = new Gson();
			UserDetailsRequestContext.setCurrentUserDetails(g.toJson(user));
		return UserDetailsImpl.build(user);
	}
}
