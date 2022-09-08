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
import io.jenkins.plugins.naverworks.auth.NaverWorksCredential;
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

    private final String credentialId;
    private final String botId;
    private final String channelId;
    private String backgroundImageUrl;
    private final List<Map<String, String>> messages;
    private final String contentActionLabel;
    private final String contentActionLink;
    private final String simpleMessage;
    private final String messageType;

    @DataBoundConstructor
    public NaverWorks(
            String credentialId,
            String botId,
            String channelId,
            List<Map<String, String>> messages,
            String contentActionLabel,
            String contentActionLink,
            String simpleMessage,
            String messageType
    ) {
        this.credentialId = credentialId;
        this.botId = botId;
        this.channelId = channelId;
        this.messages = messages;
        this.contentActionLabel = contentActionLabel;
        this.contentActionLink = contentActionLink;
        this.simpleMessage = simpleMessage;
        this.messageType = messageType;
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

    public String getSimpleMessage() {
        return simpleMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return "NaverWorks{" +
                "backgroundImageUrl='" + backgroundImageUrl + "'" +
                ", messages=" + messages +
                ", contentActionLabel='" + contentActionLabel + "'" +
                ", contentActionLink='" + contentActionLink + "'" +
                ", simpleMessage='" + simpleMessage + "'" +
                ", messageType='" + messageType + "'" +
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

        NaverWorksCredential credential =
                CredentialsProvider.findCredentialById(
                        credentialId,
                        NaverWorksCredential.class,
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

        logger.println("Issue NAVER Works Token...");
        final NaverWorksAuth auth = new NaverWorksAuth();
        Token token = auth.requestNaverWorksToken(credential);

        final Bot bot = new Bot(botId, channelId);
        final MessageService messageService = new NaverWorksMessageService();
        final UserConfiguration userConfiguration =
                new UserConfiguration(
                        messages,
                        backgroundImageUrl,
                        contentActionLabel,
                        actionLink,
                        simpleMessage,
                        messageType);
        final Message message = messageService.write(userConfiguration);

        logger.println("Send NAVER Works Messages...");
        String response = messageService.send(token, bot, message);
        logger.println("Response..." + response);

        if (response.equals(String.valueOf(HttpStatus.SC_FORBIDDEN))) {
            // one more try
            token = auth.requestNaverWorksToken(credential);
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

        public FormValidation doCheckCredentialId(@QueryParameter String value) {
            return isBlank(value, "Credential ID is required.");
        }

        public FormValidation doCheckBotId(@QueryParameter String value) {
            return isBlank(value, "Bot ID is required.");
        }

        public FormValidation doCheckChannelId(@QueryParameter String value) {
            return isBlank(value, "Channel ID is required.");
        }

        public FormValidation doCheckMessageType(@QueryParameter String value) {
            return isBlank(value, "Message Type is required.");
        }

        private FormValidation isBlank(String value, String message) {
            if (value.length() == 0) {
                return FormValidation.error(message);
            }
            return FormValidation.ok();
        }
    }

}
