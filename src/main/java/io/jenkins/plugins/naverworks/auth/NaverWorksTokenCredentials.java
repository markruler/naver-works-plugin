package io.jenkins.plugins.naverworks.auth;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;

// JENKINS_HOME/credentials.xml
@NameWith(StandardCredentials.NameProvider.class)
public class NaverWorksTokenCredentials extends BaseStandardCredentials {

    private static final long serialVersionUID = 1L;

    private final @NonNull Token token;

    @DataBoundConstructor
    public NaverWorksTokenCredentials(
            CredentialsScope scope,
            String id,
            String description,
            @NonNull Token token) {
        super(scope, id, description);
        this.token = token;
    }

    @NonNull
    public Token getToken() {
        return token;
    }

    @Extension
    @SuppressWarnings("unused")
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {

        @Override
        @NonNull
        public String getDisplayName() {
            return "NAVER Works Token";
        }

    }
}
