package com.bolyartech.server.skit.jetty;

import com.bolyartech.forge.server.BaseServletDefaultImpl;
import com.bolyartech.forge.server.config.FileForgeServerConfigurationLoader;
import com.bolyartech.forge.server.config.ForgeConfigurationException;
import com.bolyartech.forge.server.db.*;
import com.bolyartech.forge.server.jetty.ForgeJetty;
import com.bolyartech.forge.server.jetty.ForgeJettyConfiguration;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoader;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoaderFile;
import com.bolyartech.forge.server.module.HttpModule;
import com.bolyartech.forge.server.modules.main.MainModule;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServlet;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class StarterKitServer extends ForgeJetty {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StarterKitServer.class);


    public StarterKitServer(ForgeJettyConfiguration forgeJettyConfiguration, HttpServlet mainServlet) {
        super(forgeJettyConfiguration, mainServlet);
    }


    public static void main(String[] args) {
        String jettyConfigPath = ForgeJettyConfigurationLoaderFile.detectJettyConfigFilePath();

        if (jettyConfigPath != null) {
            ForgeJettyConfigurationLoader loader = new ForgeJettyConfigurationLoaderFile(jettyConfigPath);

            try {
                ForgeJettyConfiguration conf = loader.load();
                String confDir;
                if (Strings.isNullOrEmpty(conf.getConfigDir()) || conf.getConfigDir().equals(".")) {
                    File tmp = new File(jettyConfigPath);
                    confDir = tmp.getParent();
                } else {
                    confDir = conf.getConfigDir();
                }
                StarterKitServer server = new StarterKitServer(conf, createBaseServlet(confDir));
                server.start();
            } catch (ForgeConfigurationException e) {
                throw new IllegalStateException(e);
            }
        } else {
            logger.error("No configuration. Aborting.");
        }
    }


    private static HttpServlet createBaseServlet(String configDir) throws ForgeConfigurationException {
        FileForgeServerConfigurationLoader forgeConfigLoader = new FileForgeServerConfigurationLoader(configDir);
        DbConfigurationLoader dbConfigurationLoader = new FileDbConfigurationLoader(configDir);


        DbPool dbPool = createDbPool(dbConfigurationLoader.load());

        List<HttpModule> modules = new ArrayList<>();

        modules.add(new MainModule(forgeConfigLoader.load().getStaticFilesDir()));

        return new BaseServletDefaultImpl(modules, true, 5);
    }


    private static DbPool createDbPool(DbConfiguration dbConfiguration) {
        return DbUtils.createC3P0DbPool(dbConfiguration);
    }
}
