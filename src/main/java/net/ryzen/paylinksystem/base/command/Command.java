package net.ryzen.paylinksystem.base.command;


import net.ryzen.paylinksystem.base.request.ServiceRequest;

public interface Command<REQUEST extends ServiceRequest, RESPONSE> {
    RESPONSE execute(REQUEST request);
}
