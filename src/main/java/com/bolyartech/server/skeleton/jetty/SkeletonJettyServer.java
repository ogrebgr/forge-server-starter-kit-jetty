package com.bolyartech.server.skeleton.jetty;

import com.bolyartech.forge.server.BaseServletDefaultImpl;
import com.bolyartech.forge.server.config.FileForgeServerConfigurationLoader;
import com.bolyartech.forge.server.config.ForgeConfigurationException;
import com.bolyartech.forge.server.db.*;
import com.bolyartech.forge.server.jetty.ForgeJetty;
import com.bolyartech.forge.server.jetty.ForgeJettyConfiguration;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoader;
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoaderFile;
import com.bolyartech.forge.server.module.HttpModule;
import com.bolyartech.forge.server.module.admin.AdminModule;
import com.bolyartech.forge.server.module.user.UserModule;
import com.bolyartech.forge.server.module.user_blowfish.BlowfishUserModule;
import com.bolyartech.forge.server.module.user_facebook.FacebookUserModule;
import com.bolyartech.forge.server.module.user_google.GoogleUserModule;
import com.bolyartech.forge.server.module.user_scram.UserScramModule;
import com.bolyartech.forge.server.modules.main.MainModule;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;


public class SkeletonJettyServer extends ForgeJetty {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SkeletonJettyServer.class);


    public SkeletonJettyServer(ForgeJettyConfiguration forgeJettyConfiguration, HttpServlet mainServlet) {
        super(forgeJettyConfiguration, mainServlet);
    }


    public static void main(String[] args) {
        String jettyConfigPath = ForgeJettyConfigurationLoaderFile.detectJettyConfigFilePath();

        if (jettyConfigPath != null) {
            ForgeJettyConfigurationLoader loader = new ForgeJettyConfigurationLoaderFile(jettyConfigPath);

            try {
                ForgeJettyConfiguration conf = loader.load();
                SkeletonJettyServer server = new SkeletonJettyServer(conf, createBaseServlet(conf.getConfigDir()));
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
        modules.add(UserModule.createDefault(dbPool));
        modules.add(AdminModule.createDefault(dbPool));
        modules.add(UserScramModule.createDefault(dbPool));
        modules.add(FacebookUserModule.createDefault(dbPool));
        modules.add(GoogleUserModule.createDefault(dbPool));
        modules.add(BlowfishUserModule.createDefault(dbPool));

        return new BaseServletDefaultImpl(modules);
    }


    private static DbPool createDbPool(DbConfiguration dbConfiguration) {
        return DbUtils.createC3P0DbPool(dbConfiguration);
    }
}
