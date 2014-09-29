package com.github.androidutils.statemachine;

import com.github.androidutils.handler.IMessage;

public abstract class ComplexTransition extends State {
    abstract public void performComplexTransition();

    @Override
    public final void enter() {
        performComplexTransition();
    }

    @Override
    public final void resume() {
        performComplexTransition();
    }

    @Override
    public final boolean processMessage(IMessage msg) {
        throw new RuntimeException("performComplexTransition() must transit immediately");
    }

    @Override
    public final void exit() {
        // nothing to do
    }
}
