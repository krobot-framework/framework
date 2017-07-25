package org.krobot.module;

import org.krobot.KrobotModule;

import java.util.ArrayList;
import java.util.List;

import static org.krobot.module.ImportRules.Includes.*;

public class ImportRules
{
    private Class<? extends KrobotModule> module;

    private Includes[] includes = new Includes[] {COMMANDS, CONFIGS, EVENTS};
    private List<ConfigBridge> bridges = new ArrayList<>();

    public ImportRules(Class<? extends KrobotModule> module)
    {
        this.module = module;
    }

    public Includes[] getIncludes()
    {
        return includes;
    }

    public void setIncludes(Includes[] includes)
    {
        this.includes = includes;
    }

    public List<ConfigBridge> getBridges()
    {
        return bridges;
    }

    public Class<? extends KrobotModule> getModule()
    {
        return module;
    }

    public static class ConfigBridge
    {
        private String config;
        private String dest;
        private BridgeTarget target;

        ConfigBridge(String config, String dest, BridgeTarget target)
        {
            this.config = config;
            this.dest = dest;
            this.target = target;
        }

        public String getConfig()
        {
            return config;
        }

        public String getDest()
        {
            return dest;
        }

        public BridgeTarget getTarget()
        {
            return target;
        }
    }

    public enum Includes
    {
        COMMANDS,
        CONFIGS,
        EVENTS
    }

    public enum BridgeTarget
    {
        CONFIG,
        PATH
    }
}
