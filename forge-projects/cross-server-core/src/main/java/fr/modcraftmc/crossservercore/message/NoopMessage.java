package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;

@AutoRegister("noop_message") // register a no operation message to fire error when registering a message that does not use auto register nor override getMessageName method
public class NoopMessage extends BaseMessage {
    public static final String MESSAGE_NAME = "noop_message";

    @Override
    public void handle() {
        
    }
}
