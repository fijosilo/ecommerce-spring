package com.fijosilo.ecommerce.configuration;

import com.fijosilo.ecommerce.dto.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ImplementationUserDetails implements UserDetails {
    private final Client client;
    private final UserRole userRole;
    private final Set<? extends GrantedAuthority> grantedAuthorities;

    public ImplementationUserDetails(Client client) {
        this.client = client;
        userRole = UserRole.valueOf(client.getRole());
        grantedAuthorities = getGrantedAuthorities();
    }

    private Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> authorities = userRole.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + client.getRole()));
        return authorities;
    }

    @Override
    public String getUsername() {
        // username is actually modified in the SecurityConfig.java to be the user email
        return client.getEmail();
    }

    @Override
    public String getPassword() {
        return client.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public boolean isEnabled() {
        return client.isEnabled();
    }

    @Override
    public boolean isAccountNonExpired() {
        return client.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return client.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return client.isEnabled();
    }

}
