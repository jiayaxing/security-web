package com.jiayaxing.securityweb.security;

import com.jiayaxing.securityweb.entity.UserEntity;
import com.jiayaxing.securityweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userService.findByUsername(username);
        String password = userEntity.getEncodePassword();
        String role = userEntity.getRole();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(username).append(",").append(userEntity.getUuid()).append(",").append(userEntity.getGroupName());

        Collection<GrantedAuthority> grantedAuthorities =  AuthorityUtils.commaSeparatedStringToAuthorityList(role);
        UserDetails userDetails = User.withUsername(stringBuilder.toString())
                .password(password)
                .authorities(grantedAuthorities)
                .disabled(userEntity.getDeleteFlag()==1?true:false)
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .build();


        return userDetails;

    }
}
