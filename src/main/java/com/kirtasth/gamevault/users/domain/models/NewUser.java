package com.kirtasth.gamevault.users.domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUser {

    private String username;

    private String email;

    private String password;

    private String avatarUrl;

    private String bio;

}
