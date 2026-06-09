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
    @Column(length = 500) // Độ dài chuỗi JWT Token thường khá lớn
    private String id;

    @Column(name = "expiry_instant", nullable = false)
    private Instant expiryInstant; // Thời gian hết hạn gốc của Token để chạy Cron Job dọn dẹp dữ liệu rác
}