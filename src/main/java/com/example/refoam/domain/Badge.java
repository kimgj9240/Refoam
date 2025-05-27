package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Badge {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String message;

    // 1) 필드명을 read → isRead 로 변경
    // 2) @Column 으로 is_read 컬럼과 매핑
    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.isRead = true;
    }

}
