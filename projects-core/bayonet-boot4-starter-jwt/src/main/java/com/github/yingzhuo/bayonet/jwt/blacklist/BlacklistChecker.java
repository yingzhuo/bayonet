package com.github.yingzhuo.bayonet.jwt.blacklist;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface BlacklistChecker {

    boolean isBlacklisted(String rawToken, DecodedJWT decodedToken);

}
