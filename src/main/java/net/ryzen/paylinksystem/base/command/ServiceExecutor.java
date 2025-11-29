package net.ryzen.paylinksystem.base.command;


import net.ryzen.paylinksystem.base.request.ServiceRequest;

public interface ServiceExecutor {
    <REQUEST extends ServiceRequest, RESPONSE> RESPONSE execute(Class<? extends Command<REQUEST,RESPONSE>> commandClass, REQUEST request);
}
