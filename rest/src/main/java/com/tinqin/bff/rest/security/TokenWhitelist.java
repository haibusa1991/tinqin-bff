package com.tinqin.bff.rest.security;

public enum TokenWhitelist {
    GET(new String[]{
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/items"
    }),

    POST(new String[]{
            "/auth/login",
            "/auth/register"
    });

    public final String[] values;

    private TokenWhitelist(String[] values) {
        this.values = values;
    }
}
