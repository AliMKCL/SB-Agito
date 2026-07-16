package com.staj.gatewayserver.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    // Read the role information form the jwt access token.
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        // Accesses "realm_access" key's value inside the payload.
        Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return new ArrayList<>();
        }

        // Get the "roles" key's value inside realm_access from the payload.
        Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles"))
                .stream().map(roleName -> "ROLE_" + roleName) // Add a prefix to the roles, because hasRole() requires it.
                .map(SimpleGrantedAuthority::new) // Convert String to SGA.
                .collect(Collectors.toList());
        return returnValue;
    }
}
