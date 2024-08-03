package emt.sacco.middleware.SecurityImpl.SecImpl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import emt.sacco.middleware.SecurityImpl.Sec.UserDetailsImpl;
import emt.sacco.middleware.SecurityImpl.SecImpl.services.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static emt.sacco.middleware.SecurityImpl.constants.SecurityConstants.*;
import static java.util.Arrays.stream;
@Component
public class JWTTokenProvider {
    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private UserDetailsImpl userDetails;
//    private JwtTokenProvider jwtTokenProvider;


//    public String generateJwtToken(String username ){
////        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
//       // String[] claims=getClaimsFromUser(userName);
//        //UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(username);
//        List<GrantedAuthority> userAuthorities = // Obtain user authorities from somewhere
//                String[] userClaims = getClaimsFromUser(username);
//
//        return JWT.create().withIssuer(GET_ARRAYS_LLC).withAudience(GET_ARRAYS_ADMINISTRATION)
//                .withSubject(userDetails.getUsername())
//                .withIssuedAt(new Date())
//                .withIssuedAt(new Date()).withSubject(use)
//               // .withArrayClaim(AUTHORITIES, claims).withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
//                .sign(HMAC512(secret.getBytes(StandardCharsets.UTF_8)));
//     }


    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Assuming you have UserDetailsServiceImpl as your UserDetailsService implementation

    public String generateJwtToken(String username) {
        // Load user details based on the username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Extract authorities from the user details
        List<GrantedAuthority> userAuthorities = (List<GrantedAuthority>) userDetails.getAuthorities();
        String[] userClaims = userAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        // Generate JWT token using the loaded user details and authorities
        return JWT.create()
                .withIssuer(GET_ARRAYS_LLC)
                .withAudience(GET_ARRAYS_ADMINISTRATION)
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withArrayClaim(AUTHORITIES, userClaims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes(StandardCharsets.UTF_8)));
    }
    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    public String generateJwtTokens(String username) {
        return Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public List<GrantedAuthority> getAurhorities(String token){
        String[] claims=getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
     }
     public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request){
         UsernamePasswordAuthenticationToken usernamePasswordAuthToken=new UsernamePasswordAuthenticationToken(username,null,authorities);
         usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
         return usernamePasswordAuthToken;
     }
     public boolean isTokenValid(String token){
        JWTVerifier verifier=getJWTVerifier();
        return StringUtils.isNotEmpty(token) && !isTokenExpired(verifier,token);

     }
     public String getSubject(String token){
         JWTVerifier verifier=getJWTVerifier();
        return verifier.verify(token).getSubject();
     }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration=verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier=getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try{
            Algorithm algorithm=HMAC512(secret);
            verifier=JWT.require(algorithm).withIssuer(GET_ARRAYS_LLC).build();


        }catch(JWTVerificationException exception){
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);

        }
        return verifier;
    }

    private String[] getClaimsFromUser(String  userName) {
        List<String> authorities=new ArrayList<>();
        for(GrantedAuthority grantedAuthority: userDetails.getAuthorities()){

            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
}
