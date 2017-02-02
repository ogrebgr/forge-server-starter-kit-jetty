package com.bolyartech.server.skeleton.jetty;

import com.bolyartech.forge.server.SkeletonMainServlet;
import com.bolyartech.forge.server.jetty.ForgeJetty;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoader;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoaderClasspath;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoaderFile;

import javax.servlet.http.HttpServlet;


public class SkeletonJettyServer extends ForgeJetty {

    public SkeletonJettyServer(ForgeJettyConfigurationLoader forgeJettyConfigurationLoader) {
        super(forgeJettyConfigurationLoader);
    }


    public static void main(String[] args) {
        SkeletonJettyServer server = new SkeletonJettyServer(
                new ForgeJettyConfigurationLoaderFile(ForgeJettyConfigurationLoaderFile.detectJettyConfigFilePath()));

        server.start();
    }

    public HttpServlet createMainServlet() {
        return new SkeletonMainServlet();
    }
}
