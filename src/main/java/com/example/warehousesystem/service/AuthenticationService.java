package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.AuthenticationRequest;
import com.example.warehousesystem.dto.request.IntrospectRequest;
import com.example.warehousesystem.dto.response.AuthenticationResponse;
import com.example.warehousesystem.entity.InvalidatedToken;
import com.example.warehousesystem.entity.User;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.repository.InvalidatedTokenRepository;
import com.example.warehousesystem.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    public final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid_duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refresh_token}")
    protected long REFRESH_INTERVAL;

    public String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("WarehouseSystem.com")
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope", buildScope(user))
                .claim("type", "access_token")
                .build();
        return getSignerToken(jwsHeader, jwtClaimsSet);
    }

    public String generateRefreshToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("WarehouseSystem.com")
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(REFRESH_INTERVAL, ChronoUnit.DAYS).toEpochMilli()))
                .claim("type", "refresh_token")
                .build();
        return getSignerToken(jwsHeader, jwtClaimsSet);
    }

    private String getSignerToken(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet) {
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "No Bin with available capacity"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "No Bin with available capacity");
        }

        var token = generateToken(user);
        var refreshToken = generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public String buildScope(User user) {
        StringJoiner joiner = new StringJoiner(" ");
        String role = String.valueOf(user.getRole());
        if (role != null && !role.trim().isEmpty()) {
            joiner.add("ROLE_" + role.trim());
        }
        return joiner.toString();
    }

    public void logout(IntrospectRequest request, String refreshToken) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken(), false);
        String accessTokenJti = signToken.getJWTClaimsSet().getJWTID();
        Date accessTokenExpiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        invalidatedTokenRepository.save(InvalidatedToken.builder()
                .id(accessTokenJti)
                .expiryTime(accessTokenExpiryTime)
                .build());

        if (refreshToken != null) {
            var signRefreshToken = verifyToken(refreshToken, true);
            String refreshTokenJti = signRefreshToken.getJWTClaimsSet().getJWTID();
            Date refreshTokenExpiryTime = signRefreshToken.getJWTClaimsSet().getExpirationTime();

            invalidatedTokenRepository.save(InvalidatedToken.builder()
                    .id(refreshTokenJti)
                    .expiryTime(refreshTokenExpiryTime)
                    .build());
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        if (!signedJWT.verify(verifier)) throw new AppException(ErrorCode.UNAUTHENTICATED, "No Bin with available capacity");
        if (signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED, "No Bin with available capacity");
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED, "No Bin with available capacity");

        if (isRefresh) {
            String type = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!"refresh_token".equals(type)) throw new AppException(ErrorCode.UNAUTHENTICATED, "No Bin with available capacity");
        }

        return signedJWT;
    }

    public AuthenticationResponse refreshAccessToken(String refreshToken) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(refreshToken, true);
        String username = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "No Bin with available capacity"));

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(generateToken(user))
                .refreshToken(refreshToken)
                .build();
    }
}
