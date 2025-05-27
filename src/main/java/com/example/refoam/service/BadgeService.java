package com.example.refoam.service;

import com.example.refoam.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public long getUnreadCount(Long userId) {
        // 메서드명을 Repository에 맞춰 수정
        return badgeRepository.countByUserIdAndIsReadFalse(userId);
    }
}

