package com.budgetmap.test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class TestJWE {
    public static void main(String[] args) {
        String secret = "miClaveSecretaMuyLargaYParaBudgetMap2024SeguraConMasCaracteres1234567890";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Let's see if this compiles in a java script, but I can't run it directly without compiling.
    }
}
