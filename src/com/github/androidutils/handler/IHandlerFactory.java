package com.github.androidutils.handler;


public interface IHandlerFactory {

    IHandler createHandler(IHandlingStrategy handlingStrategy);

}
