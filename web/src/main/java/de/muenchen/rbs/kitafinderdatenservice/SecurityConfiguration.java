package de.muenchen.rbs.kitafinderdatenservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;

/**
 * The central class for configuration of all security aspects.
 * 
 * @author m.zollbrecht
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

	private static final String AUD_CLAIM = "aud";

	public static final String SCOPE_LHM_EXTENDED = "LHM_Extended";
	public static final String SCOPE_ROLES = "roles";
	public static final String SCOPE_OPENID = "openid";

	/**
	 * Security configuration for OAuth protected parts of the application Excluding
	 * Swagger UI and spring actuators.
	 */
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtDecoder decoder,
			JwtAuthenticationConverter authConverter) throws Exception {
		http.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/actuator/info", "/actuator/health/**", "/swagger-ui/**", "/v3/api-docs/**")
				.permitAll().anyRequest().authenticated()).oauth2ResourceServer(
						(oauth2) -> oauth2.jwt(jwt -> jwt.decoder(decoder).jwtAuthenticationConverter(authConverter)));
		http.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable());
		return http.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtAuthoritiesConverter());
		return jwtAuthenticationConverter;
	}

	@Bean
	JwtDecoder jwtDecoder(@Value("${app.security.issuer-url}") String issuerUri,
			@Value("${app.security.client-id}") String requiredAudience) {
		log.info("Initializing security with issuer-uri {} and audience {}.", issuerUri, requiredAudience);
		
		NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);

		OAuth2TokenValidator<Jwt> audienceValidator = audienceValidator(requiredAudience);
		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
		OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

		jwtDecoder.setJwtValidator(withAudience);

		return jwtDecoder;
	}

	OAuth2TokenValidator<Jwt> audienceValidator(String requiredAudience) {
		return new JwtClaimValidator<List<String>>(AUD_CLAIM, aud -> aud.contains(requiredAudience));
	}

	/** Swagger-API config for security */
	@Configuration
	@SecurityScheme(name = "ApiClient", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(clientCredentials = @OAuthFlow(tokenUrl = "${app.security.token-url}", scopes = {
			@OAuthScope(name = SCOPE_LHM_EXTENDED), @OAuthScope(name = SCOPE_ROLES) })))
	public class SpringdocConfig {
	}

}