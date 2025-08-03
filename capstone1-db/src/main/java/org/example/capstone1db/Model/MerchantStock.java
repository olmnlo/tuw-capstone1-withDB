package org.example.capstone1db.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class MerchantStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "productId must not be empty")
    @Column(columnDefinition = "int not null")
    private Integer productId;

    @NotNull(message = "merchantId must not be empty")
    @Column(columnDefinition = "int not null")
    private Integer merchantId;

    @Min(value = 11, message = "stock must be more than 10")
    @Column(columnDefinition = "int not null")
    private Integer stock;

    @Column(columnDefinition = "boolean default(false)")
    private Boolean isSeasonalProduct;

    @Column(columnDefinition = "int default(0)")
    private Integer soldProducts;

    @Column(columnDefinition = "int default(0.0)")
    private Double merchantRate;


}