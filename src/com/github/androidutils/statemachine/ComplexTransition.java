package com.github.androidutils.statemachine;

import android.os.Message;

public abstract class ComplexTransition extends State {
    abstract public void performComplexTransition();

    @Override
    public final void enter(Message reason) {
        performComplexTransition();
    }

    @Override
    public final void resume() {
        performComplexTransition();
    }

    @Override
    public final boolean processMessage(Message msg) {
        throw new RuntimeException("performComplexTransition() must transit immediately");
    }

    @Override
    public final void exit(Message reason) {
        // nothing to do
    }
}
