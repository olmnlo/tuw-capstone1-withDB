package org.example.capstone1db.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "username must not be empty")
    @Size(min = 6,max = 15, message = "username must be more than 5 characters and less than 15")

    @Column(columnDefinition = "varchar(15) not null")
    private String username;

    @NotBlank(message = "password must not be empty")
    @Size(min = 7, max = 16,message = "password must be more than 6 characters and less than 16")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "password must contain letters and digits")

    @Column(columnDefinition = "varchar(16) not null")
    private String password;

    @NotBlank(message = "email must not be empty")
    @Email(message = "email must be a valid email")
    @Size(max = 20, message = "email must be  less than 20")

    @Column(columnDefinition = "varchar(20) not null")
    private String email;

    @NotBlank(message = "role must not be empty")
    @Pattern(regexp = "^(Admin|Customer)$", message = "role must be either 'Admin' or 'Customer'")

    @Column(columnDefinition = "varchar(8) not null")
    private String role;

    @NotNull(message = "balance must not be empty")
    @Positive(message = "balance must be a positive number")

    @Column(columnDefinition = "double default(0)")
    private Double balance;

    @Column(columnDefinition = "boolean default(false)")
    private Boolean subscribed = false;
}
