package com.project2.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SecurityUser extends User {

    @Getter
    private long id;
    @Getter
    private String nickname;

    public SecurityUser(long id, String email, String nickname, Collection<? extends GrantedAuthority> authorities) {
        super(email, "", authorities);
        this.id = id;
        this.nickname = nickname;
    }
}
