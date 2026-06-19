package com.Bank.bank_system.config;

/**
 * CORS agora é gerenciado centralmente pelo Spring Security em
 * {@link com.Bank.bank_system.security.SecurityConfig#corsConfigurationSource()}.
 *
 * Esta classe foi intencionalmente esvaziada para evitar cabeçalhos
 * Access-Control-Allow-Origin duplicados (um do WebMvc + outro do Security).
 */
public class CorsConfig {
}
