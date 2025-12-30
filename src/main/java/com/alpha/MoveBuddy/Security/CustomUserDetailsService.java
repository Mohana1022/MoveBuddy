package com.alpha.MoveBuddy.Security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.alpha.MoveBuddy.Repository.UsersRepo;
import com.alpha.MoveBuddy.entity.Users;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String mobileNo)
            throws UsernameNotFoundException {

        Users user = usersRepo.findByUsermobileNo(Long.parseLong(mobileNo))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(
                String.valueOf(user.getUsermobileNo()),
                user.getUserPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole())) // CUSTOMER / DRIVER
        );
    }
}
