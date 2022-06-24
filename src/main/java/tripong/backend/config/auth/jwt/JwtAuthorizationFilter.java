package tripong.backend.config.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import tripong.backend.config.auth.PrincipalDetail;
import tripong.backend.entity.user.User;
import tripong.backend.repository.user.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository){
        super(authenticationManager);
        this.userRepository=userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("시작: JwtAuthorizationFilter");

        String header = request.getHeader(JwtProperties.HEADER_STRING);
        if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)){
            log.info("종료: JwtAuthorizationFilter - 인가 불가(jwt 토큰 없음)");
            chain.doFilter(request,response);
            return;
        }

        String token = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");
        String loginId = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                .getClaim("loginId").asString();

        if(loginId != null){
            User user = userRepository.findByLoginId(loginId)
                    .orElseThrow(()->{
                        log.info("종료: JwtAuthorizationFilter - 인가 불가(DB 사용자 없음)");
                        return new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : "+ loginId);
                    });
            PrincipalDetail principal = new PrincipalDetail(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        log.info("종료: JwtAuthorizationFilter - 인가 ok");
        chain.doFilter(request, response);
    }
}
