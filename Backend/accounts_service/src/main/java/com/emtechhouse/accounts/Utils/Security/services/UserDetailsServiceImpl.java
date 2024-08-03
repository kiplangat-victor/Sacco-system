package com.emtechhouse.accounts.Utils.Security.services;

import com.emtechhouse.accounts.Utils.HttpInterceptor.UserDetailsRequestContext;
import com.emtechhouse.accounts.Utils.Security.Usersdata;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@SneakyThrows
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		JSONObject jo = new JSONObject(UserDetailsRequestContext.getcurrentUserDetails());
		Gson gson = new Gson();
		Usersdata users = gson.fromJson(String.valueOf(jo), Usersdata.class);
		return UserDetailsImpl.build(users);
	}
}
