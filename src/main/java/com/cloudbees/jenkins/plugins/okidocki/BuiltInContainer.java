package com.cloudbees.jenkins.plugins.okidocki;

import hudson.Extension;
import hudson.model.BuildBadgeAction;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.SCMListener;
import hudson.scm.ChangeLogSet;
import hudson.scm.SCM;

import java.io.IOException;

/**
 * Used to determine if launcher has to be decorated to execute in container, after SCM checkout completed.
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class BuiltInContainer implements BuildBadgeAction {

    /* package */ String image;
    /* package */ transient String container;
    private transient boolean enable;
    private final transient Docker docker;

    public BuiltInContainer(Docker docker) {
        this.docker = docker;
    }

    public void afterSCM() {
        this.enable = true;
    }

    public boolean enabled() {
        return enable;
    }

    public String getIconFileName() {
        return "/plugin/oki-docki/docker-badge.png";
    }

    public String getImage() {
        return image;
    }

    public String getDisplayName() {
        return "build inside docker container";
    }

    public String getUrlName() {
        return "/docker";
    }

    public boolean tearDown() throws IOException, InterruptedException {
        if (container != null) {
            enable = false;
            docker.kill(container);
        }
        return true;

    }


    @Extension
    public static class Listener extends SCMListener {
        @Override
        public void onChangeLogParsed(Run<?, ?> build, SCM scm, TaskListener listener, ChangeLogSet<?> changelog) throws Exception {
            BuiltInContainer runInContainer = build.getAction(BuiltInContainer.class);
            if (runInContainer != null) runInContainer.afterSCM();
        }
    }

}
