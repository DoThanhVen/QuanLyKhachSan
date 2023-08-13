package com.poly.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// CSRF, CORS
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable())
		// Phân quyền sử dụng
//		http.authorizeRequests().antMatchers("/home/index", "/auth/login/**", "/login/oauth2/code/**","/account/**").permitAll()
//				.anyRequest().authenticated();
				.authorizeHttpRequests((request) -> request.anyRequest().permitAll());
		// Giao diện đăng nhập
		http.formLogin(f -> f.loginPage("/sign-in").loginProcessingUrl("/auth/login")// ACTION
				.defaultSuccessUrl("/auth/login/success", false).failureHandler((request, response, exception) -> {
					HttpSession session = request.getSession();
					session.setAttribute("username", request.getParameter("username"));
					session.setAttribute("password", request.getParameter("password"));
					response.sendRedirect("/auth/login/error");
				}).successHandler((request, response, exception) -> {
					HttpSession session = request.getSession();
					session.setAttribute("username", request.getParameter("username"));
					session.setAttribute("password", request.getParameter("password"));
					response.sendRedirect("/auth/login/success");
				}));

		http.rememberMe(rememberme -> rememberme.rememberMeParameter("remember"));

		// Đăng xuất
		http.logout(logout -> logout.logoutUrl("/auth/logoff").logoutSuccessUrl("/auth/logoff/success"));

		// Oauth2 - Đăng nhập từ mạng xã hội
		http.oauth2Login(oauth -> oauth.loginPage("/auth/login/form").defaultSuccessUrl("/oauth2/login/success", true)
				.failureUrl("/oauth2/login/error").authorizationEndpoint(
						authorizationEndpoint -> authorizationEndpoint.baseUri("/oauth2/authorization")));
		return http.build();
	}

	@Bean
	public AuthorizationRequestRepository<OAuth2AuthorizationRequest> getRepository() {
		return new HttpSessionOAuth2AuthorizationRequestRepository();
	}

	@Bean
	public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> getToken() {
		return new DefaultAuthorizationCodeTokenResponseClient();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
