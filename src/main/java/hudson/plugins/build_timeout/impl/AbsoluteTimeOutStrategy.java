package hudson.plugins.build_timeout.impl;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.plugins.build_timeout.BuildTimeOutStrategy;
import hudson.plugins.build_timeout.BuildTimeOutStrategyDescriptor;
import hudson.plugins.build_timeout.BuildTimeoutWrapper;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * If the build took longer than <tt>timeoutSeconds</tt> amount of minutes, it will be terminated.
 */
public class AbsoluteTimeOutStrategy extends BuildTimeOutStrategy {

    private final String timeoutSeconds;

    /**
     * @return seconds to timeout.
     */
    public String getTimeoutSeconds() {
        return timeoutSeconds;
    }

    @Deprecated
    public AbsoluteTimeOutStrategy(int timeoutSeconds) {
        this.timeoutSeconds = Integer.toString(Math.max((int) (BuildTimeoutWrapper.MINIMUM_TIMEOUT_MILLISECONDS / SECONDS), timeoutSeconds));
    }

    @DataBoundConstructor
    public AbsoluteTimeOutStrategy(String timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public long getTimeOut(@Nonnull AbstractBuild<?,?> build, @Nonnull BuildListener listener)
            throws InterruptedException, MacroEvaluationException, IOException {
        return SECONDS * Math.max((int) (BuildTimeoutWrapper.MINIMUM_TIMEOUT_MILLISECONDS / SECONDS), Integer.parseInt(
                expandAll(build, listener, getTimeoutSeconds())));
    }

    @Override
    public Descriptor<BuildTimeOutStrategy> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension(ordinal=100) // This is displayed at the top as the default
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static class DescriptorImpl extends BuildTimeOutStrategyDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.AbsoluteTimeOutStrategy_DisplayName();
        }

        @Override
        public boolean isApplicableAsBuildStep() {
            return true;
        }
    }
}
