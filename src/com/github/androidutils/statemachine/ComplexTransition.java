package com.github.androidutils.statemachine;

import android.os.Message;

import com.github.androidutils.logger.Logger;

public abstract class ComplexTransition extends State {
    private final StateMachine sm;
    private final Logger log;

    abstract public void performComplexTransition();

    public ComplexTransition(StateMachine sm, Logger logger) {
        this.sm = sm;
        log = logger;
    }

    @Override
    public final void enter() {
        performComplexTransition();
    }

    @Override
    public final boolean processMessage(Message msg) {
        log.e("performComplexTransition() must transit immediately");
        sm.deferMessage(msg);
        return true;
    }

    @Override
    public final void exit() {
        // nothing to do
    }
}
