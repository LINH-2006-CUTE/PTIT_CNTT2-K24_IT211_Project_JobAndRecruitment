package com.example.jobandrecruitment.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.jobandrecruitment.repository.RevokedTokenRepository;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final RevokedTokenRepository revokedTokenRepository;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, RevokedTokenRepository revokedTokenRepository) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.revokedTokenRepository = revokedTokenRepository;
	}

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			try {
				username = jwtService.extractUsername(token);
			} catch (Exception ex) {
				logger.debug("Failed to parse JWT: {}", ex.getMessage());
			}
		}

		// If token exists in revoked tokens blacklist, block the request immediately
		if (token != null && revokedTokenRepository != null && revokedTokenRepository.existsById(token)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json;charset=UTF-8");
			String json = "{\"success\":false,\"message\":\"Token đã bị thu hồi\",\"data\":null,\"errors\":null,\"httpStatus\":\"UNAUTHORIZED\"}";
			response.getWriter().write(json);
			return;
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (jwtService.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		filterChain.doFilter(request, response);
	}
}
