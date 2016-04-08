package com.epocrates.jenkins.amonix;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by tsaravana on 4/6/2016.
 */
public class ReleaseLabelReportPublisher extends Recorder {

    public static final String BUILD_LABEL = "BUILD_LABEL";
    public static final String BUILD = "BUILD";

    @DataBoundConstructor
    public ReleaseLabelReportPublisher() {
        super();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        listener.getLogger().println("AMONIX Executing...");

        // Show the link only when it's a PARAMETERIZED build.
        boolean shouldShowReport = false;
        List<ParametersAction> actions = build.getActions(ParametersAction.class);
        for (ParametersAction action : actions) {
            ParameterValue buildLabelParam = action.getParameter(BUILD_LABEL);
            if (buildLabelParam != null && buildLabelParam.getValue() != null) {
                String value = (String) buildLabelParam.getValue();
                if (!value.isEmpty()) {
                    shouldShowReport = true;
                }
            }
        }

        if (shouldShowReport) {
            listener.getLogger().println("AMONIX: Parameterized build");
            final String wikiPageUrl = getDescriptor().getWikiPageUrl();
            final long delayTime = getDescriptor().getDelayTime();
            final ReleaseLabelLinkAction buildAction = new ReleaseLabelLinkAction(build, listener, delayTime, wikiPageUrl);
            build.addAction(buildAction);
        }else{
            listener.getLogger().println("AMONIX: Not a Parameterized build");
        }

        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String wikiPageUrl = "https://intranet.athenahealth.com/wiki/node.esp?ID=102596";
        private long delayTime = 300;

        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Athena Release Label Report";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            delayTime = formData.getLong("delayTime");
            wikiPageUrl = formData.getString("wikiPageUrl");
            save();
            return super.configure(req, formData);
        }

        public String getWikiPageUrl() {
            return wikiPageUrl;
        }

        public void setWikiPageUrl(String wikiPageUrl) {
            this.wikiPageUrl = wikiPageUrl;
        }

        public long getDelayTime() {
            return delayTime;
        }

        public void setDelayTime(long delayTime) {
            this.delayTime = delayTime;
        }
    }
}
