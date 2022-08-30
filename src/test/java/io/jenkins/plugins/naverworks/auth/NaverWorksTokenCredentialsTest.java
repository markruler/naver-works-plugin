package io.jenkins.plugins.naverworks.auth;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import hudson.security.ACL;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NaverWorksTokenCredentialsTest {

    private static final String UPDATED_CRED_ID = "custom-id-updated";
    private static final String CRED_ID = "custom-ID";

    // MUST JUnit4
    // org.junit.Rule
    // org.junit.Test
    @Rule
    public final JenkinsRule jenkinsRule = new JenkinsRule();

    private CredentialsStore store;

    @Before
    public void setup() {
        store = CredentialsProvider.lookupStores(jenkinsRule.jenkins).iterator().next();
    }

    @Test
    public void crud_naver_works_token_credential()
            throws IOException {

        assertThat(jenkinsRule).isNotNull();

        Token token = new Token();
        token.setAccessToken("test-access-token");

        NaverWorksTokenCredentials credential = new NaverWorksTokenCredentials(CredentialsScope.GLOBAL, CRED_ID, "description", token);
        NaverWorksTokenCredentials updatedCredential = new NaverWorksTokenCredentials(credential.getScope(), UPDATED_CRED_ID, credential.getDescription(), credential.getToken());

        testCreateUpdateDelete(credential, updatedCredential);
    }

    /**
     * Creates, updates and deletes credentials and perform different assertions
     *
     * @param credential        the credential to create
     * @param updatedCredential the credential that will replace the first one during update
     * @throws IOException if the change could not be persisted.
     * @see <a href="https://github.com/jenkinsci/plain-credentials-plugin/blob/83be2ca41c/src/test/java/org/jenkinsci/plugins/plaincredentials/BaseTest.java">plain-credentials test</a>
     */
    private <T extends BaseStandardCredentials> void testCreateUpdateDelete(T credential, T updatedCredential)
            throws IOException {

        // Add a credential
        store.addCredentials(Domain.global(), credential);

        // Look up all credentials
        List<BaseStandardCredentials> credentials =
                CredentialsProvider.lookupCredentials(
                        BaseStandardCredentials.class,
                        jenkinsRule.jenkins,
                        ACL.SYSTEM,
                        Collections.<DomainRequirement>emptyList()
                );

        // There is one credential
        assertThat(credentials).hasSize(1);
        BaseStandardCredentials cred = credentials.get(0);
        assertThat(cred).isInstanceOf(credential.getClass());
        assertThat(cred.getId()).isEqualTo(CRED_ID);
        assertThat(cred.getId()).isEqualTo(CRED_ID);

        // Update credential
        store.updateCredentials(Domain.global(), cred, updatedCredential);

        // Look up all credentials again
        credentials = CredentialsProvider.lookupCredentials(
                BaseStandardCredentials.class,
                jenkinsRule.jenkins,
                ACL.SYSTEM,
                Collections.<DomainRequirement>emptyList()
        );

        // There is still 1 credential but the ID has been updated
        assertThat(credentials).hasSize(1);
        cred = credentials.get(0);
        assertThat(cred).isInstanceOf(credential.getClass());
        assertThat(cred.getId()).isEqualTo(UPDATED_CRED_ID);

        // Delete credential
        store.removeCredentials(Domain.global(), cred);

        // Look up all credentials again
        credentials = CredentialsProvider.lookupCredentials(
                BaseStandardCredentials.class,
                jenkinsRule.jenkins,
                ACL.SYSTEM,
                Collections.<DomainRequirement>emptyList()
        );

        // There are no credentials anymore
        assertThat(credentials).isEmpty();
    }
}
