package com.sky.api.weatherapiservice.security.jwt;

import com.sky.api.weatherapicommon.entity.User;
import com.sky.api.weatherapiservice.Location.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private UserRepository repo;

    @Autowired
    public CustomUserDetailService(UserRepository repo) {
        this.repo=repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user=repo.findByUsername(username);
        if(!user.isPresent())
        {
            throw new UsernameNotFoundException("No user found with the given user name");
        }
        return new CustomerUserDetail(user.get());
    }
}
