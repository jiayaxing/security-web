package com.jiayaxing.securityweb.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class LoginService {

    // 令牌秘钥,至少4位
    @Value("${token.secret:abcdefg}")
    private String secret;

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid)
    {
        String verifyKey = "captcha_codes" + uuid;
        // 用户验证
        Authentication authentication = null;
        try
        {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                throw new RuntimeException("aa");
            }
            else
            {
                throw new RuntimeException(e.getMessage());
            }
        }

        String principal = authentication.getPrincipal().toString();
        String[] split = principal.split(",");

        List<String> authorityList = new ArrayList<>();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>)authentication.getAuthorities();
        for (int i = 0; i < authorities.size(); i++) {
            GrantedAuthority grantedAuthority = authorities.get(i);
            String authority = grantedAuthority.getAuthority();
            System.out.println(authority);
            authorityList.add(authority);
        }

        HashMap<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("username",split[0]);
        userInfoMap.put("uuid",split[1]);
        userInfoMap.put("groupName",split[2]);
        userInfoMap.put("role",authorityList.get(0));

        String randomUuid = UUID.randomUUID().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("login_user_key", randomUuid);
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        // 生成token
        return token;
    }

}
