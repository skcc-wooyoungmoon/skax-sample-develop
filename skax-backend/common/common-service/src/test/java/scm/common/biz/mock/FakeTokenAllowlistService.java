package scm.common.biz.mock;

import scm.common.app.auth.TokenAllowlistService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeTokenAllowlistService implements TokenAllowlistService {

    private final Map<String, String> uidToToken = new ConcurrentHashMap<>();

    @Override
    public void allow(String uid, String token) {
        uidToToken.put(uid, token);
    }

    @Override
    public boolean isAllowed(String uid, String token) {
        return token != null && token.equals(uidToToken.get(uid));
    }

    @Override
    public void revoke(String uid) {
        uidToToken.remove(uid);
    }
}
