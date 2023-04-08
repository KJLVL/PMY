package com.example.myaquarium.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {
    private String login;
    private String userName;
    private String surname;
    private String avatar;
    private String aquariumVolume;
    private String city;
    private String phone;
}
