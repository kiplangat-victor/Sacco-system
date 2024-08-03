package emt.sacco.middleware.Security.jwt;//package emt.sacco.middleware.Security.jwt;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // Disable CSRF (cross site request forgery)
//        http.csrf().disable();
//
//        // No session management required
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        // Permit access to login and signup endpoints
//        http.authorizeRequests()
//                .antMatchers("/api/auth/login").permitAll()
//                .antMatchers("/api/auth/signup").permitAll()
//                .anyRequest().authenticated();
//
//        // Apply JWT filter
//        http.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//    }
//
//    // Other configurations (e.g., authentication manager, password encoder, etc.)
//
//}
