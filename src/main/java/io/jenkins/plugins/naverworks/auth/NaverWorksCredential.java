package io.jenkins.plugins.naverworks.auth;

import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

// JENKINS_HOME/credentials.xml
@NameWith(StandardCredentials.NameProvider.class)
public class NaverWorksCredential extends BasicSSHUserPrivateKey {

    private static final long serialVersionUID = 11L;

    private Token token;
    private final Secret clientId;
    private final Secret clientSecret;
    private final Secret serviceAccount;

    @DataBoundConstructor
    public NaverWorksCredential(
            @NonNull CredentialsScope scope,
            @NonNull String id,
            @Nullable Token token,
            @Nullable BasicSSHUserPrivateKey.PrivateKeySource privateKeySource,
            @Nullable Secret passphrase,
            @Nullable String description,
            @NonNull String clientId,
            @NonNull String clientSecret,
            @NonNull String serviceAccount
    ) {
        super(scope, id, null, privateKeySource, Secret.toString(passphrase), description);
        this.token = token;
        this.clientId = Secret.fromString(clientId);
        this.clientSecret = Secret.fromString(clientSecret);
        this.serviceAccount = Secret.fromString(serviceAccount);
    }

    @Nullable
    public Token getToken() {
        return token;
    }

    @DataBoundSetter
    public void setToken(Token token) {
        this.token = token;
    }

    @NonNull
    public String getClientId() {
        return Secret.toString(clientId);
    }

    @NonNull
    public String getClientSecret() {
        return Secret.toString(clientSecret);
    }

    @NonNull
    public String getServiceAccount() {
        return Secret.toString(serviceAccount);
    }

    @Extension
    @SuppressWarnings("unused")
    public static class DescriptorImpl extends BasicSSHUserPrivateKey.DescriptorImpl {

        @Override
        @NonNull
        public String getDisplayName() {
            return "NAVER Works API v2.0 Client";
        }

    }
}
