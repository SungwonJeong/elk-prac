package com.example.elkprac.user.service;

import com.example.elkprac.common.exception.CustomException;
import com.example.elkprac.common.message.ErrorMessage;
import com.example.elkprac.security.jwt.JwtTokenProvider;
import com.example.elkprac.user.dto.request.LoginRequestDto;
import com.example.elkprac.user.dto.request.SignupRequestDto;
import com.example.elkprac.user.dto.response.LoginResponseDto;
import com.example.elkprac.user.dto.response.SignupResponseDto;
import com.example.elkprac.user.entity.User;
import com.example.elkprac.user.entity.UserRole;
import com.example.elkprac.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        validateDuplicatedUsername(signupRequestDto.username());
        User user = createUser(signupRequestDto);
        userRepository.save(user);
        return SignupResponseDto.from(user);
    }

    private void validateDuplicatedUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(m -> {
                    log.warn("Username [{}] is already taken", username);  // 로그 추가
                    throw new CustomException(ErrorMessage.DUPLICATED_USERNAME);
                });
    }

    private User createUser(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.username();
        String password = passwordEncoder.encode(signupRequestDto.password());
        return signupRequestDto.toEntity(username, password, UserRole.USER);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        String username = loginRequestDto.username();
        String password = loginRequestDto.password();

        log.info("User [{}] is attempting to log in", username);  // 로그 추가

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorMessage.NOT_FOUND_USER)
        );

        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
        if (!passwordMatch) {
            log.warn("Invalid password attempt for user [{}]", username);  // 로그 추가
            throw new CustomException(ErrorMessage.NOT_VALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(username, user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(username, user.getRole());

        log.info("User [{}] successfully logged in", username);  // 로그 추가

        return LoginResponseDto.of(user, accessToken, refreshToken);
    }

//    private void generateAuthToken(HttpServletResponse servletResponse, User user) {
//        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole());
//        servletResponse.addHeader(JwtTokenProvider.ACCESS_TOKEN_HEADER, accessToken);
//
//        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRole());
//        servletResponse.addHeader(JwtTokenProvider.REFRESH_TOKEN_HEADER, refreshToken);
//
//        String redisRefreshTokenKey = REFRESH_PREFIX + customer.getEmail();
//        long refreshTokenExpiration = tokenProvider.getExpiryDuration(refreshToken, false) / 1000;
//        stringRedisTemplate.opsForValue().set(redisRefreshTokenKey, refreshToken, refreshTokenExpiration, TimeUnit.SECONDS);
//    }
}
