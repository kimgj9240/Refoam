package com.example.refoam.repository;

import com.example.refoam.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge,Long> {
    // 읽지 않은 배지(알림) 개수를 조회하는 커스텀 메서드
    // 반드시 boolean 필드명 앞에 Is나 IsFalse를 붙여서 count 메서드로 정의
    // 알림 개수 조회용 메서드
    // 기존: countByUserIdAndReadIsFalse → 변경:
    long countByUserIdAndIsReadFalse(Long userId);

    // 필요시 읽지 않은 알림 전체 조회
    List<Badge> findAllByUserIdAndIsReadFalse(Long userId);

}
