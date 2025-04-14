package com.capricon.User_Service.service;

import com.capricon.User_Service.exception.UserException;
import com.capricon.User_Service.exception.enums.UserErrorCode;
import com.capricon.User_Service.model.User;
import com.capricon.User_Service.model.UserPrincipal;
import com.capricon.User_Service.repository.jpa.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found", UserErrorCode.USER_NOT_FOUND));

        return new UserPrincipal(user);

    }
}
