package com.soutenance.security;

import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouve"));

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())
                .disabled(!user.isEnabled())
                .build();
    }
}
