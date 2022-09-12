package com.cpcd.microservices.app.oauth.serviciooauthcas.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class UserInfo implements UserDetails {
    private String name;
    private String surname;
    private String email;
    private String avatar;
    private String username;
    private String password;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;
    private List<GrantedAuthority> authorities = new ArrayList<>();
    private String cadenLTI;



    public UserInfo(String username, String password ,boolean isEnabled, boolean isAccountNonExpired,  boolean isCredentialsNonExpired,
                    boolean isAccountNonLocked, List<GrantedAuthority> authorities, String cadenLTI) {

        this.username = username;
        this.password = password;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
        this.authorities = authorities;
        this.cadenLTI = cadenLTI;
    }

    public String getCadenLTI() {
        return cadenLTI;
    }

    public void setCadenLTI(String cadenLTI) {
        this.cadenLTI = cadenLTI;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setUsername(String string) {
        this.username=string;
    }

    public void setName(String string) {
        this.name=string;

    }
    public String getName() {
        return this.name;

    }




    public void setAccountNonExpired(boolean isAccountNonExpired) {
        this.isAccountNonExpired = isAccountNonExpired;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
