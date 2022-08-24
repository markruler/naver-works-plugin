package io.jenkins.plugins.naverworks;

import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
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
import io.jenkins.plugins.naverworks.bot.Action;
import io.jenkins.plugins.naverworks.bot.Bot;
import io.jenkins.plugins.naverworks.bot.CoverData;
import io.jenkins.plugins.naverworks.bot.Element;
import io.jenkins.plugins.naverworks.bot.ListTemplateContent;
import io.jenkins.plugins.naverworks.bot.ListTemplateMessage;
import io.jenkins.plugins.naverworks.bot.MessageService;
import io.jenkins.plugins.naverworks.bot.NaverWorksMessageService;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @DataBoundConstructor
    public NaverWorks(String clientId, String clientSecret, String serviceAccount, String credentialId, String botId, String channelId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serviceAccount = serviceAccount;
        this.credentialId = credentialId;
        this.botId = botId;
        this.channelId = channelId;
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

    @Override
    public void perform(
            @Nonnull Run<?, ?> run,
            @Nonnull FilePath workspace,
            @Nonnull EnvVars env,
            @Nonnull Launcher launcher,
            TaskListener listener)
            throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();

        BasicSSHUserPrivateKey credential = CredentialsProvider.findCredentialById(
                credentialId,
                BasicSSHUserPrivateKey.class,
                run,
                (DomainRequirement) null);

        assert credential != null;
        List<String> privateKeys = credential.getPrivateKeys();

        final App app = new App(clientId, clientSecret, serviceAccount, privateKeys.get(0));
        final NaverWorksAuth auth = new NaverWorksAuth();

        final Token token;
        try {
            token = auth.requestNaverWorksToken(app);
        } catch (URISyntaxException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        logger.println("Token >>> " + token);

        Bot bot = new Bot(botId, channelId);
        MessageService messageService = new NaverWorksMessageService();

        ListTemplateContent content = new ListTemplateContent();
        content.setCoverData(new CoverData(backgroundImageUrl));

        List<Element> elements = new ArrayList<>();
        Action action = new Action("uri", "more", "https://example.com/");
        elements.add(new Element("JIRA-KEY", "이슈 제목", action));
        content.setElements(elements);

        List<Action> nestedActions = new ArrayList<>();
        nestedActions.add(new Action("uri", "Go to Jenkins", "https://www.jenkins.io/"));
        List<List<Action>> actions = new ArrayList<>();
        actions.add(nestedActions);
        content.setActions(actions);

        final ListTemplateMessage message = new ListTemplateMessage(content);
        try {
            messageService.sendMessage(token, bot, message);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

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

        private FormValidation isBlank(String value, String message) {
            if (value.length() == 0) {
                return FormValidation.error(message);
            }
            return FormValidation.ok();
        }
    }

}
