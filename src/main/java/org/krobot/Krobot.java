package org.krobot;

import org.krobot.runtime.KrobotRuntime;

public final class Krobot
{
    public static final String VERSION = "3.0.0";

    public static final String PROPERTY_KEY = "krobot.key";
    public static final String PROPERTY_KEY_FILE = "krobot.keyFile";
    public static final String PROPERTY_DISABLE_KEY_SAVING = "krobot.disableKeySaving";
    public static final String PROPERTY_DISABLE_ASKING_KEY = "krobot.disableAskingKey";
    public static final String PROPERTY_DISABLE_START_MESSAGE = "krobot.krobot.disableStartMessage";

    public static KrobotRunner create()
    {
        return new KrobotRunner();
    }

    public static KrobotRuntime getRuntime()
    {
        return KrobotRuntime.get();
    }
}
