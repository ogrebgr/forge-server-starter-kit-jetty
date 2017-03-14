package com.bolyartech.server.skeleton.jetty;

import com.bolyartech.forge.server.SkeletonMainServlet;
import com.bolyartech.forge.server.config.FileForgeServerConfigurationLoader;
import com.bolyartech.forge.server.config.ForgeConfigurationException;
import com.bolyartech.forge.server.db.DbConfigurationLoader;
import com.bolyartech.forge.server.db.FileDbConfigurationLoader;
import com.bolyartech.forge.server.jetty.ForgeJetty;
import com.bolyartech.forge.server.jetty.ForgeJettyConfiguration;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoader;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoaderFile;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;


public class SkeletonJettyServer extends ForgeJetty {
    private static final org.slf4j.Logger mLogger = LoggerFactory.getLogger(SkeletonJettyServer.class);


    public SkeletonJettyServer(ForgeJettyConfiguration forgeJettyConfiguration, HttpServlet mainServlet) {
        super(forgeJettyConfiguration, mainServlet);
    }


    public static void main(String[] args) {
        String jettyConfigPath = ForgeJettyConfigurationLoaderFile.detectJettyConfigFilePath();

        if (jettyConfigPath != null) {
            ForgeJettyConfigurationLoader loader = new ForgeJettyConfigurationLoaderFile(jettyConfigPath);

            try {
                ForgeJettyConfiguration conf = loader.load();
                SkeletonJettyServer server = new SkeletonJettyServer(conf, createMainServlet(conf.getConfigDir()));
                server.start();
            } catch (ForgeConfigurationException e) {
                throw new IllegalStateException(e);
            }
        } else {
            mLogger.error("No configuration. Aborting.");
        }
    }


    private static HttpServlet createMainServlet(String configDir) {
        FileForgeServerConfigurationLoader forgeConfigLoader = new FileForgeServerConfigurationLoader(configDir);
        DbConfigurationLoader dbConfigurationLoader = new FileDbConfigurationLoader(configDir);
        try {
            return new SkeletonMainServlet(forgeConfigLoader.load().getStaticFilesDir(), dbConfigurationLoader.load());
        } catch (ForgeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }
}
