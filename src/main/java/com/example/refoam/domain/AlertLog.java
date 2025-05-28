package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 메시지 내용
    @Column(nullable = false)
    private String message;

    // 알림 확인 여부
    @Column(nullable = false)
    private boolean checked;

    // 알림 생성 시간
    private LocalDateTime createdDate;

    // 알림 확인 시간 (선택)
    private LocalDateTime readDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // 읽음 처리 메서드
    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checked) {
            this.readDate = LocalDateTime.now();
        }
    }
}
