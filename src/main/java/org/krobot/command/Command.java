package org.krobot.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.krobot.command.runtime.CommandHandler;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command
{
    String value();
    String desc() default "";
    String[] aliases() default {};
    Class<? extends CommandHandler>[] subs() default {};
}
