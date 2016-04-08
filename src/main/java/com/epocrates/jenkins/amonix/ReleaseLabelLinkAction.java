package com.epocrates.jenkins.amonix;

import hudson.model.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by tsaravana on 4/7/2016.
 */
public class ReleaseLabelLinkAction implements Action {

    private static final Calendar CALENDAR = Calendar.getInstance();
    public static final String AMP = "&";

    private String build;
    private String label;
    private String day;
    private String hour;
    private String minute;
    private String second;

    private String url;

    private String wikiPageUrl;

    public ReleaseLabelLinkAction(AbstractBuild<?, ?> abstractBuild, BuildListener listener, final long delay, final String wikiPageUrl) {

        this.wikiPageUrl = wikiPageUrl;

        // Get the BUILD and BUILD_LABEL parameter
        List<ParametersAction> actions = abstractBuild.getActions(ParametersAction.class);
        for (ParametersAction action : actions) {
            ParameterValue buildParam = action.getParameter(ReleaseLabelReportPublisher.BUILD);
            if (buildParam != null) {
                build = (String) buildParam.getValue();
            }
            ParameterValue buildLabelParam = action.getParameter(ReleaseLabelReportPublisher.BUILD_LABEL);
            if (buildLabelParam != null) {
                final String tempLabel = (String) buildLabelParam.getValue();
                if(tempLabel.startsWith("PATCH")){
                    label = "PATCH";
                }else if(tempLabel.startsWith("QA")){
                    label = "QA";
                }else{
                    label = "INVALID LABEL";
                }
            }
        }

        // Get the time when the build started along with the delay
        final long buildStartTimeIncludingTheDelay = abstractBuild.getStartTimeInMillis() - delay * 1000;
        CALENDAR.setTimeInMillis(buildStartTimeIncludingTheDelay);

        hour = String.format(Locale.US, "%02d", CALENDAR.get(Calendar.HOUR_OF_DAY));
        minute = String.format(Locale.US, "%02d", CALENDAR.get(Calendar.MINUTE));
        second = String.format(Locale.US, "%02d", CALENDAR.get(Calendar.SECOND));

        final int year = CALENDAR.get(Calendar.YEAR);
        final int month = CALENDAR.get(Calendar.MONTH) + 1;
        final int date = CALENDAR.get(Calendar.DATE);

        day = String.format(Locale.US, "%d-%02d-%02d", year, month, date);

        // Construct the URL
        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(wikiPageUrl).append(AMP)
                .append("BUILD=").append(build).append(AMP)
                .append("LABEL=").append(label).append(AMP)
                .append("DAY=").append(day).append(AMP)
                .append("HOUR=").append(hour).append(AMP)
                .append("MINUTE=").append(minute).append(AMP)
                .append("SECOND=").append(second).append(AMP)
                .append("SUBMIT=Submit");
        url = urlBuilder.toString();
        listener.getLogger().println("AMONIX: Wiki URL = "+url);

    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    public String getBuild() {
        return build;
    }

    public String getLabel() {
        return label;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public String getSecond() {
        return second;
    }

    public String getWikiPageUrl() {
        return wikiPageUrl;
    }

    public String getUrl() {
        return url;
    }
}
