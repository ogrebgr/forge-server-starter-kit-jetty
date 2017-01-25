package com.bolyartech.server.skeleton.jetty;

import com.bolyartech.forge.server.SkeletonMainServlet;
import com.bolyartech.forge.server.jetty.ForgeJetty;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoader;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoaderImpl;

import javax.servlet.http.HttpServlet;


public class SkeletonJettyServer extends ForgeJetty {

    public SkeletonJettyServer(ForgeJettyConfigurationLoader forgeJettyConfigurationLoader) {
        super(forgeJettyConfigurationLoader);
    }


    public static void main(String[] args) {
        SkeletonJettyServer server = new SkeletonJettyServer(new ForgeJettyConfigurationLoaderImpl());
        server.start();
    }

    public HttpServlet createMainServlet() {
        return new SkeletonMainServlet();
    }
}
