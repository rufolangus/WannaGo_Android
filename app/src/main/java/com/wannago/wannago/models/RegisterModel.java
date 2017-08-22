package com.wannago.wannago.models;

/**
 * Created by Rafael Valle on 3/22/2017.
 */

public class RegisterModel
{
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String image;
    public int gender;
    public String dateOfBirth;

    public RegisterModel(String username, String firstName, String LastName,
                         String email, String password, String imageURL, String birthDay, int Gender)
    {
        this.username = username;
        this.firstName = firstName;
        this.gender = Gender;
        lastName = LastName;
        this.email = email;
        this.password = password;
        this.image = imageURL;
        this.dateOfBirth = birthDay;
    }
}
