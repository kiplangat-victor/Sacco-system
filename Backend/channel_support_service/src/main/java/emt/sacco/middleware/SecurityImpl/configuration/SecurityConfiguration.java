package emt.sacco.middleware.SecurityImpl.configuration;

import emt.sacco.middleware.SecurityImpl.SecImpl.services.UserDetailsServiceImpl;
import emt.sacco.middleware.SecurityImpl.filter.JwtAuthenticationEntryPoint;
import emt.sacco.middleware.SecurityImpl.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//    private JwtAuthorizationFilter jwtAuthorizationFilter;
//    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
//    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private UserDetailsServiceImpl userDetailsServiceImpl;
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @Autowired
//    public SecurityConfiguration(JwtAuthorizationFilter jwtAuthorizationFilter,
//                                 JwtAccessDeniedHandler jwtAccessDeniedHandler,
//                                 JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
//                                 @Qualifier("userDetailsServiceImpl")UserDetailsService userDetailsService,
//                                 BCryptPasswordEncoder bCryptPasswordEncoder) {
//        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
//        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
//        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
//       this.userDetailsServiceImpl = userDetailsServiceImpl;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }
////    @Bean
////    public UserDetailsService userDetailsService() {
////        JdbcDaoImpl userDetailsService = new JdbcDaoImpl();
////        // Configure userDetailsService as needed (e.g., set data source)
////        return userDetailsService;
////    }
////    @Bean
////    public UserService userService() {
////        return new UserServiceImpl();
////    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(bCryptPasswordEncoder);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().cors().and()
//                .sessionManagement().sessionCreationPolicy(STATELESS)
//                .and().authorizeRequests().antMatchers(PUBLIC_URLS).permitAll()
//                .anyRequest().permitAll()
//             ///   .antMatchers("/swagger-ui/**", "/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**").permitAll()
//
////                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .and()
//                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
//    }
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }


    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public JwtAuthorizationFilter authenticationJwtTokenFilter() {
        return new JwtAuthorizationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .antMatchers("/eureka/**").permitAll()
//				.anyRequest().fullyAuthenticated();
                .anyRequest().permitAll();
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
       // config.addAllowedOrigin(origin_local);
       // config.addAllowedOrigin(origin_52);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
