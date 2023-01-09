package com.srt.message.repository;

import com.srt.message.domain.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}
