package io.jenkins.plugins.naverworks.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jenkins.plugins.naverworks.App;
import org.junit.Before;
import org.junit.Test;

import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NaverWorksAuthTest {

    // ssh-keygen -m pkcs8 -f test.pem -N ""
    final String pkcs8EncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIG/gIBADANBgkqhkiG9w0BAQEFAASCBugwggbkAgEAAoIBgQC2VQkxEoKe7y+H\n" +
            "Gv87rHWdUF7sPyF741+popCqVIaiS62F/xcUBj9CZF+v51rWn+tsQ6pPONy9Qish\n" +
            "wQ7Kufj7JkEI5jvmx5ISgvkc7jzJ7iL3cU+Kh9WVi+INsdQfVyt//+9PoCAX7sYG\n" +
            "hfhQ/JPb1Lq21RGYwe0jHpR4crgiCwNmN9C8TGRW4zszki/X4zQcJs604b4ZLc3K\n" +
            "k5fCvE+pE+NmAwWec4RPxUm7GkcJ30Rbv97j5FkoXyTJKPiGsDHTxqmJvcKu0YvQ\n" +
            "Ad/2H5napgR1Rvkr7t1bqsXW/8aSCXGWgdYqT9SYa0MGepT2+mkaF1+bk/yBFItF\n" +
            "BsUMlNr+uuHRyS2KbpF369/N6uqLFirOYiYMG8vqgfbDrWI2ljeqm/m94Fhb6hit\n" +
            "e1LewSWKCPWcmrnZEVTOqb49w1rkexNLu6niL6IbHsQ7ZTOZnieVrBFV9JrlstVa\n" +
            "xWw+C30te+td8CbB8z2962HPOfFBLXAyTd3A4KWeOxhAXYIxuXsCAwEAAQKCAYAr\n" +
            "1ReHl4aVRLTtSt0u8E+6CNc4/hhDmy6lLjKO8BEJtR27B875fmL4x/6E4B5jWECV\n" +
            "X1LzBp1yVhTeYXX34GttnQ2nYQnnefSwxJG0lcVBWiFH0RKpH+yFrgIi/qwa+K65\n" +
            "b8KNJtZwSnOKn+xJM98ryuE0ENbRHVWvyqswpYuEi5c+nI5upgK9LJVP2YXs5W7i\n" +
            "kaXBsD8t+Gfvmjmyk+imoeRZ4sIYDHczYh9OfouzYNwjHmYr7PpODUlfnpzYbES1\n" +
            "8ijzPAt/d69rvnEedzgj1mS5qoCLPI6qXjZhSLK47Ad3P2TmOsCSip0296dGpjjc\n" +
            "wId+CwxrZw2JzP7tXtTj5AR88+3/O9dWiZmlw1bHj6hW6P5oXy/Vx+LaveX2njtv\n" +
            "PqzfVkUYQ11wNuIJGxC4tilikNDRb3DFlEBbK1mT0GyBE0EO+d4Y+yxOdH6eg1vq\n" +
            "Ry7OA53IsJyvkX1PS3HO3bSQmTcguQGTqGYWodIaJizovPU28Onih4Qj/wxs/kEC\n" +
            "gcEA7IF6+sAUW/+bMgfgBN2IjuQW3DLbSAPQ3S13AutxfgEAnD28QkMsdoMd4w2k\n" +
            "wlsiSqUpnMAjn272mEh3kjIb9bFl1X9SSNC/BI9wBzDj+6tOZMF+osh1H5ff5SDo\n" +
            "n/HUp8nsBwMfKi03OUdY8PSrIPHsZcMb+wnqjWUflbbCW5IHu15iu/o+B+xsBxvG\n" +
            "c+UWtQb14FHCz7rBzPeUwPMItMY9KV9IFd3gKxRdON2XXahLtMZ8mZp05HcbZXm+\n" +
            "IPvbAoHBAMVcb4PP1Mav9yiMc9i/1vpf9HQ4+12nELx7Da3Lw9pBmSwzbXKb1Agu\n" +
            "kkWuH+QRmExs9WWoTyIIfPyEL2EbQ3OMq8o8QczGIEa6ArEuU2cIMyfhI+uM0u8f\n" +
            "DNVnmE56XdHkwQk196d4Ht1w/IuOil7WwQ7E+RmslLJPhhTW+pf9PL2vrGh4759K\n" +
            "HKRj/fC8Y9VmMMZWTJne1fv32M7ts+K1ZbK90Ycl0hlpt19cKRPruGeYWBJsxJD9\n" +
            "pNOwO+p64QKBwFTQAhgV9bcgLLl+VXmpMCV96izBGB31MFuAyGMFp8d1+JAPG4nv\n" +
            "qOw/DOK1q9X+9IcsP4X06VFta4Ukx6xnx9sN6OTtKcbvBPFPtu79O2SvBAfh09Xw\n" +
            "aQARaLs0V/ezs30QTlIdbnmjiWBDE8QlGZFQ8GN12nzmC3T1RKRdy8kmzxsEOAgN\n" +
            "L12MDRlnfn1lNAVrH5V3/JKd+I3WY0PmY8LgLGK/xowA6zjHrr6LyJJPngaP/3Le\n" +
            "Dvk3NV3okKQnbQKBwQCHSsxvMYONCIeH1nB7v1ahoDG1knJOSv5gFoLUBwEllx5n\n" +
            "fIGktUnuqe4/shPOhnk+utqma3CDxgnYGM1Z5hShG2eQZSo+M/tgQJcj2rv1v1tC\n" +
            "FUZIbOsUXNaxXCi+c4fpIMVriQFSXjfaWV/mUzprtQ/3DfQtM29jpLwngQ+cLU78\n" +
            "QXcQzwaEzYtA7gLgimRtn3YEIJi2SlmqNsb7Y1uTKAKlOxZtbPHP1faMemL2cvqg\n" +
            "mYCWcNV3B0l8jC0mmkECgcEA3hL3797EiJL9E+LFqsdHyMYOsLhQEXqiRPv+E39v\n" +
            "XCW1obzm6sfkt/5ghS7ETZ/+gDxermCXt84hSKSI7/KHrJzGPfnt7Rassfr/2EJa\n" +
            "SaRghTJiaSU0XWt6WSjnmDgCeiLTW8ejFmpYFwRCq87+m43LewgtQhRoSu3T9tK+\n" +
            "Qi/QZAzLIAXjEVETRPT/2XXfhqYs1ojVDC/carvWwIDGIE+9WpPKDSkiONQeahMA\n" +
            "M62cxtZJq/pL4qItMTq7C7rf\n" +
            "-----END PRIVATE KEY-----";

    private final String issuer = "clientId";
    private final String subject = "sa";
    private NaverWorksAuth auth;

    @Before
    public void setUp() {
        auth = new NaverWorksAuth();
    }

    @Test
    public void generate_jwt() throws GeneralSecurityException {
        App app = new App(issuer, "secret", subject, pkcs8EncodedPrivateKey);
        String jwt = auth.generateJwtWithServiceAccount(app);
        DecodedJWT decodedJWT = JWT.decode(jwt);

        // https://developers.worksmobile.com/kr/reference/authorization-sa
        assertThat(decodedJWT).isNotNull();
        assertThat(decodedJWT.getAlgorithm()).isEqualTo("RS256");
        assertThat(decodedJWT.getType()).isEqualTo("JWT");
        assertThat(decodedJWT.getIssuer()).isEqualTo(issuer);
        assertThat(decodedJWT.getSubject()).isEqualTo(subject);
    }

    @Test
    public void throws_invalid_key_spec_exception() {
        App app = new App(issuer, "secret", subject, "invalid private Key");

        assertThatThrownBy(() -> auth.generateJwtWithServiceAccount(app))
                .isInstanceOf(InvalidKeySpecException.class);
    }

}
