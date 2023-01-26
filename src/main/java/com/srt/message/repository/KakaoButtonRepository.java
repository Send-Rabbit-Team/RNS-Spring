package com.srt.message.repository;

import com.srt.message.domain.KakaoButton;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoButtonRepository extends JpaRepository<KakaoButton, Long> {
}
