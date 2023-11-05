package ru.netology.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.netology.repository.UserRepository;
import ru.netology.entity.DAOUser;
import ru.netology.entity.UserDTO;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	@Autowired
	FilesStorageService storageService;

	
	@Autowired
	private UserRepository userDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DAOUser user = userDao.findByLogin(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
				new ArrayList<>());
	}
	
	public DAOUser save(UserDTO user) {
		DAOUser newUser = new DAOUser();
		newUser.setLogin(user.getLogin());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		storageService.createHome(user.getLogin());
		return userDao.save(newUser);
	}
}
