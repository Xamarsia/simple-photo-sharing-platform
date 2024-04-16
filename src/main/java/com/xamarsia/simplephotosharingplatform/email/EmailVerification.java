package com.xamarsia.simplephotosharingplatform.email;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(nullable = false)
    public Integer verificationCode;

    @Column(nullable = false)
    public final LocalDateTime creationDateTime = LocalDateTime.now();

    @Column(nullable = false)
    public String email;

    @Column(nullable = false)
    @Builder.Default
    public Boolean isUsed = false;
}


