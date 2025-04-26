package Controller.Dao.UtilsDao;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class TokenUtils {

    private static String  signkey= "QGtradingsystem";
    private static Long expire = 43200000L;

    //生成token令牌
    public static String generateJWT(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signkey)
                .setExpiration(new Date(System.currentTimeMillis()+expire))
                .compact();
        return jwt;
    }

    //解析jwt令牌
    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(signkey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}
