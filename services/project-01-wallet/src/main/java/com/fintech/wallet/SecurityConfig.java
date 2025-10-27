package com.fintech.wallet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final AuthenticationEntryPoint authEntryPoint;
	
	// Inject the custom entry point using its component name
	public SecurityConfig(@Qualifier("customAuthenticationEntryPoint") AuthenticationEntryPoint authEntryPoint) {
		this.authEntryPoint = authEntryPoint;
	}
	
	@Bean
	 SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(authorize -> authorize
					.requestMatchers("/api/ledgers").hasAuthority("SCOPE_admin")
					.requestMatchers("/api/ledger_accounts").hasAuthority("SCOPE_admin")
					.requestMatchers("/api/journals").hasAuthority("SCOPE_admin")
					.requestMatchers("/api/ledgers/{id}").hasAuthority("SCOPE_admin")
					.requestMatchers("/admin/transactions/{transactionId}").hasAuthority("SCOPE_admin")
					.requestMatchers("/me").authenticated()
					.requestMatchers("/wallet").authenticated()
					.requestMatchers("/transfers").authenticated()
					.anyRequest().authenticated()
					)
			.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(authEntryPoint) // Set the custom entry point here
					)
			.oauth2ResourceServer(oauth2 -> oauth2
					.jwt(Customizer.withDefaults()));
		return http.build();
	}
}
