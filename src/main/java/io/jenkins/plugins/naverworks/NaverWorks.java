package io.jenkins.plugins.naverworks;

import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.naverworks.auth.NaverWorksAuth;
import io.jenkins.plugins.naverworks.auth.Token;
import io.jenkins.plugins.naverworks.bot.Bot;
import io.jenkins.plugins.naverworks.bot.MessageService;
import io.jenkins.plugins.naverworks.bot.NaverWorksMessageService;
import io.jenkins.plugins.naverworks.bot.message.Message;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.apache.hc.core5.http.HttpStatus;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NaverWorks
        extends Builder
        implements SimpleBuildStep {

    private final String clientId;
    private final String clientSecret;
    private final String serviceAccount;
    private final String credentialId;
    private final String botId;
    private final String channelId;
    private String backgroundImageUrl;
    private final List<Map<String, String>> messages;
    private final String contentActionLabel;
    private final String contentActionLink;
    private final String notification;

    @DataBoundConstructor
    public NaverWorks(
            String clientId,
            String clientSecret,
            String serviceAccount,
            String credentialId,
            String botId,
            String channelId,
            List<Map<String, String>> messages,
            String contentActionLabel,
            String contentActionLink,
            String notification
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serviceAccount = serviceAccount;
        this.credentialId = credentialId;
        this.botId = botId;
        this.channelId = channelId;
        this.messages = messages;
        this.contentActionLabel = contentActionLabel;
        this.contentActionLink = contentActionLink;
        this.notification = notification;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getServiceAccount() {
        return serviceAccount;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public String getBotId() {
        return botId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    @DataBoundSetter
    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public String getContentActionLabel() {
        return contentActionLabel;
    }

    public String getContentActionLink() {
        return contentActionLink;
    }

    public String getNotification() {
        return notification;
    }

    @Override
    public String toString() {
        return "NaverWorks{" +
                "backgroundImageUrl='" + backgroundImageUrl + "'" +
                ", messages=" + messages +
                ", contentActionLabel='" + contentActionLabel + "'" +
                ", contentActionLink='" + contentActionLink + "'" +
                ", notification='" + notification + "'" +
                '}';
    }

    @Override
    public void perform(
            @NonNull Run<?, ?> run,
            @NonNull FilePath workspace,
            @NonNull EnvVars env,
            @NonNull Launcher launcher,
            TaskListener listener)
            throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();

        BasicSSHUserPrivateKey credential =
                CredentialsProvider.findCredentialById(
                        credentialId,
                        BasicSSHUserPrivateKey.class,
                        run,
                        (DomainRequirement) null);

        logger.println(this);

        String actionLink = contentActionLink;
        if ("BUILD_URL".equals(actionLink)) {
            actionLink = env.get("BUILD_URL");
        }

        if (credential == null) {
            throw new PrivateKeyCredentialsNotFoundException("Credential not found.");
        }

        final List<String> privateKeys = credential.getPrivateKeys();
        final App app = new App(clientId, clientSecret, serviceAccount, privateKeys.get(0));
        final NaverWorksAuth auth = new NaverWorksAuth();
        logger.println("Issue NAVER Works Token...");
        Token token = auth.requestNaverWorksToken(app);


        final Bot bot = new Bot(botId, channelId);
        final MessageService messageService = new NaverWorksMessageService();
        final UserConfiguration userConfiguration =
                new UserConfiguration(
                        messages,
                        backgroundImageUrl,
                        contentActionLabel,
                        actionLink,
                        notification
                );
        final Message message = messageService.write(userConfiguration);

        logger.println("Send NAVER Works Messages...");
        String response = messageService.send(token, bot, message);
        logger.println("Response..." + response);

        if (response.equals(String.valueOf(HttpStatus.SC_FORBIDDEN))) {
            // one more try
            token = auth.requestNaverWorksToken(app);
            response = messageService.send(token, bot, message);
            logger.println("Retry response..." + response);
        }
    }

    @Symbol("naver")
    @Extension
    @SuppressWarnings("unused")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @NonNull
        @Override
        public String getDisplayName() {
            return "NAVER Works";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public ListBoxModel doFillCredentialIdItems(@AncestorInPath Item item) {

            if (item == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                return new StandardListBoxModel();
            }
            if (item != null
                    && !item.hasPermission(Item.EXTENDED_READ) /*implied by Item.CONFIGURE*/
                    && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                return new StandardListBoxModel();
            }

            return new StandardListBoxModel()
                    .includeEmptyValue()
                    .includeMatchingAs(
                            ACL.SYSTEM,
                            item,
                            BasicSSHUserPrivateKey.class,
                            Collections.emptyList(),
                            CredentialsMatchers.instanceOf(BasicSSHUserPrivateKey.class)
                    );
        }

        public FormValidation doCheckClientId(@QueryParameter String value) {
            return isBlank(value, "Client ID is required.");
        }

        public FormValidation doCheckClientSecret(@QueryParameter String value) {
            return isBlank(value, "Client Secret is required.");
        }

        public FormValidation doCheckServiceAccount(@QueryParameter String value) {
            return isBlank(value, "Service Account is required.");
        }

        public FormValidation doCheckCredentialId(@QueryParameter String value) {
            return isBlank(value, "Credential ID is required.");
        }

        public FormValidation doCheckBotId(@QueryParameter String value) {
            return isBlank(value, "Bot ID is required.");
        }

        public FormValidation doCheckChannelId(@QueryParameter String value) {
            return isBlank(value, "Channel ID is required.");
        }

        public FormValidation doCheckContentActionLabel(@QueryParameter String value) {
            return isBlank(value, "Content Action Label is required.");
        }

        private FormValidation isBlank(String value, String message) {
            if (value.length() == 0) {
                return FormValidation.error(message);
            }
            return FormValidation.ok();
        }
    }

}
