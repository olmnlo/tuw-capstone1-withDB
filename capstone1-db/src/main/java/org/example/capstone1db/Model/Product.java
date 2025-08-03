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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name must not be empty")
    @Size(max = 20, message = "Name must be less than 20 characters")

    @Column(columnDefinition = "varchar(20) not null")
    private String name;

    @NotNull(message = "Price must not be empty")
    @Positive(message = "Price must be a positive number")

    @Column(columnDefinition = "double default(0)")
    private Double price;

    @NotNull(message = "Category ID must not be empty")
    @Column(columnDefinition = "int not null")
    private Integer categoryID;

    @Column(columnDefinition = "double default(0)")
    private Double productRate;

    @Column(columnDefinition = "boolean default (false)")
    private Boolean discount20;

    @Column(columnDefinition = "boolean default (false)")
    private Boolean isSeasonalProduct;

    @Column(columnDefinition = "double default(0)")
    private Double offer;

}