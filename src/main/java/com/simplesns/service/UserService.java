package com.simplesns.service;

import com.simplesns.exception.ErrorCode;
import com.simplesns.exception.SimpleSnsApplicationException;
import com.simplesns.model.Alarm;
import com.simplesns.model.User;
import com.simplesns.model.entity.UserEntity;
import com.simplesns.repository.AlarmEntityRepository;
import com.simplesns.repository.UserCacheRepository;
import com.simplesns.repository.UserEntityRepository;
import com.simplesns.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userRepository;
    private final AlarmEntityRepository alarmRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserCacheRepository redisRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    /**
     * 사용자 이름으로 사용자 정보를 조회
     * Redis 캐시에서 먼저 조회 후, 없을 경우 DB 에서 불러오기
     */
    public User loadUserByUserName(String userName) throws UsernameNotFoundException {
        return redisRepository.getUser(userName).orElseGet(
                () -> userRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(
                        () -> new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("userName is %s", userName))
                ));
    }

    /**
     * 로그인 처리
     * 비밀번호 검증 후 JWT 토큰 반환
     */
    public String login(String userName, String password) {
        // 사용자 정보 로드
        User savedUser = loadUserByUserName(userName);

        // 사용자 정보를 Redis 캐시에 저장
        redisRepository.setUser(savedUser);

        // 비밀번호 검증
        if (!encoder.matches(password, savedUser.getPassword())) {
            throw new SimpleSnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // JWT 토큰 생성 후 반환
        return JwtTokenUtils.generateAccessToken(userName, secretKey, expiredTimeMs);
    }

    /**
     * 회원가입 처리
     * 중복된 사용자 이름이 없을 경우, 새 사용자 정보 저장
     */
    @Transactional
    public User join(String userName, String password) {
        // 사용자 이름 중복 체크
        userRepository.findByUserName(userName).ifPresent(it -> {
            throw new SimpleSnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName is %s", userName));
        });

        // 새로운 사용자 정보 저장
        UserEntity userEntity = userRepository.save(UserEntity.of(userName, encoder.encode(password)));

        // 저장된 Entity 를 User 객체로 반환
        return User.fromEntity(userEntity);
    }

    /**
     * 사용자 알림 리스트를 페이징 처리하여 조회
     */
    @Transactional
    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
        return alarmRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    }

}
