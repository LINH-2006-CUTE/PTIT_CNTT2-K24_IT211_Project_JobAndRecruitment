package com.example.jobandrecruitment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedToken {

    @Id
    @Column(length = 100, nullable = false, unique = true)
    private String id;

    @Column(name = "expiry_instant", nullable = false)
    private Instant expiryInstant;
}