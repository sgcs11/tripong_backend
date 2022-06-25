package tripong.backend.config.auth.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import tripong.backend.config.auth.PrincipalDetail;
import tripong.backend.entity.user.User;
import tripong.backend.repository.user.UserRepository;
import tripong.backend.service.account.AccountService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuthInfo oAuthInfo = null;
        String providerName = userRequest.getClientRegistration().getRegistrationId();
        if(providerName.equals("google")){
            oAuthInfo = new GoogleUser(oAuth2User.getAttributes());
        }
        else{
            log.info("설계된 provider 없음");
        }

        Optional<User> user = userRepository.findByLoginId(oAuthInfo.getProviderName() + "_" + oAuthInfo.getNickName() + oAuthInfo.getProviderId());
        if(user.isPresent()){
            return new PrincipalDetail(user.get(), oAuth2User.getAttributes());
        }
        else{
            User user1 = accountService.oauthJoin((OAuthInfo) oAuthInfo);
            return new PrincipalDetail(user1, oAuth2User.getAttributes());
        }
    }
}
