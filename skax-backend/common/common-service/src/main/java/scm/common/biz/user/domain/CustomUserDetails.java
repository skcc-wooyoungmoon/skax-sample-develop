package scm.common.biz.user.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // role/userrole 샘플 패키지를 제거했으므로, 현재는 권한을 DB에서 로딩하지 않습니다.
        // 필요 시 JWT claims에 권한을 넣거나, 별도의 권한 서비스/테이블을 연동해야 합니다.
        return Collections.emptySet();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public User getDomainUser() {
        return user;
    } // 필요 시 도메인 객체 접근
}
