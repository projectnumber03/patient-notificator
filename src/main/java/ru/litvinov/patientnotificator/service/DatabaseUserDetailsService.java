package ru.litvinov.patientnotificator.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.litvinov.patientnotificator.model.UserPrincipal;

@Service
@AllArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final var user = userService.findByLogin(username);
        if (user.isEmpty()) throw new UsernameNotFoundException(username);
        return new UserPrincipal(user.get());
    }

}
