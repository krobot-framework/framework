/*
 * Copyright 2017 The Krobot Contributors
 *
 * This file is part of Krobot.
 *
 * Krobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Krobot.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.krobot.command;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.inject.Singleton;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.krobot.Krobot;
import org.krobot.MessageContext;
import org.krobot.permission.BotNotAllowedException;
import org.krobot.permission.UserNotAllowedException;
import org.krobot.util.ColoredLogger;
import org.krobot.util.Dialog;
import org.krobot.util.Markdown;
import org.krobot.util.MessageUtils;

@Singleton
public class ExceptionHandler
{
    private static final ColoredLogger log = ColoredLogger.getLogger("ExceptionHandler");

    private Map<Class<? extends Throwable>, IExceptionHandler> handlers = new HashMap<>();

    public void handle(MessageContext context, KrobotCommand command, String[] args, Throwable t)
    {
        Optional<IExceptionHandler> handler = handlers.entrySet().stream().filter(e -> e.getKey().isInstance(t)).map(Entry::getValue).findFirst();

        if (handler.isPresent())
        {
        	if (command.getErrorMP())
        		handler.get().handle(new MessageContext(context.getJDA(), context.getUser(), context.getMessage(), context.getUser().openPrivateChannel().complete()), t);
        	else
        		handler.get().handle(context, t);
            return;
        }

        log.errorAuto("@|red Unhandled |@ @|red,bold '" + t.getClass().getName() + "'|@ @|red while executing command '|@@|red,bold " + command.getLabel() + "|@@|red '|@", t);

        String report = makeCrashReport(t, command, args, context);

        try
        {
            MessageUtils.deleteAfter(context.send(Dialog.error("Command crashed !", "A crash report has been sent to you " + context.getUser().getAsMention() + " . Please send it to the developer as soon as possible !")).get(), 5000);
        }
        catch (InterruptedException | ExecutionException ignored)
        {
        }

        PrivateChannel channel = context.getUser().openPrivateChannel().complete();
        for (String message : MessageUtils.splitMessageKeepLines(report, MessageUtils.MAX_MESSAGE_CHARS - Markdown.code("").length()))
        {
            channel.sendMessage(Markdown.code(message)).queue();
        }
    }

    /**
     * Create a crash report based on an Exception
     *
     * @param throwable The command of the report
     * @param command The command that was being handled when the exception was thrown
     * @param args The arguments of the command
     * @param context The context of the command
     *
     * @return The generated crash report
     */
    protected String makeCrashReport(Throwable throwable, KrobotCommand command, String[] args, MessageContext context)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("####################################\n\n");

        builder.append("Krobot v").append(Krobot.VERSION).append(" crash report\n\n");

        builder.append("Command       : ").append(command.toString()).append("\n");
        builder.append("Args          : ").append(Arrays.toString(args)).append("\n");
        builder.append("Caller        : ").append(context.getUser().getName()).append("#").append(context.getUser().getDiscriminator()).append("\n");
        builder.append("Conversation  : #").append(context.getChannel().getName()).append("\n");
        builder.append("Time          : ").append(new Date()).append("\n\n");

        builder.append(ExceptionUtils.getStackTrace(throwable)).append("\n\n");

        builder.append("####################################");

        return builder.toString();
    }

    public <T extends Throwable> void on(Class<T> cl, IExceptionHandler<T> handler)
    {
        handlers.put(cl, handler);
    }

    // Default handlers
    {
        on(WrongArgumentNumberException.class, (context, t) -> context.send(Dialog.warn("Wrong number of argument", t.getMessage())));
        on(BadArgumentTypeException.class, (context, t) -> {
            String message = t.getMessage() != null ? t.getMessage() : "Can't convert '" + t.getValue() + "' to a '" + t.getType() + "'";
            context.send(Dialog.warn("Wrong argument types", message));
        });

        on(BotNotAllowedException.class, (context, t) -> context.send(Dialog.error("Missing required permission", t.getMessage())));
        on(UserNotAllowedException.class, (context, t) -> context.send(Dialog.error("You're missing a required permission", t.getMessage())));
        on(PrivateChannelNotSupportedException.class, (context, t) -> context.send(Dialog.error("Unsupported channel", t.getMessage())));
    }
}
