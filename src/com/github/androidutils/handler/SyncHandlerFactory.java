package com.github.androidutils.handler;

import com.google.common.base.Preconditions;

public class SyncHandlerFactory implements IHandlerFactory {

    public SyncHandlerFactory() {
    }

    @Override
    public IHandler createHandler(IHandlingStrategy handlingStrategy) {
        return new SyncHandler(handlingStrategy);
    }

    private static class SyncHandler implements IHandler {
        private final IHandlingStrategy handlingStrategy;

        public SyncHandler(IHandlingStrategy handlingStrategy) {
            this.handlingStrategy = Preconditions.checkNotNull(handlingStrategy);
        }

        @Override
        public IMessage obtainMessage(int what, Object object) {
            return new SyncMessage(handlingStrategy).setWhat(what).setObj(object);
        }

        @Override
        public void sendMessageAtFrontOfQueue(IMessage message) {
            handlingStrategy.handleMessage(message);
        }

        @Override
        public void sendMessage(IMessage message) {
            handlingStrategy.handleMessage(message);
        }

        @Override
        public IMessage obtainMessage() {
            return new SyncMessage(handlingStrategy);
        }

        @Override
        public IMessage obtainMessage(int what) {
            return new SyncMessage(handlingStrategy).setWhat(what);
        }

        @Override
        public void removeMessages(int what) {

        }
    }

    private static class SyncMessage implements IMessage {
        private final IHandlingStrategy handlingStrategy;
        private Object obj;
        private int what;
        private int arg1;
        private int arg2;

        public SyncMessage(IHandlingStrategy handlingStrategy) {
            this.handlingStrategy = Preconditions.checkNotNull(handlingStrategy);
        }

        @Override
        public IMessage setWhat(int what) {
            this.what = what;
            return this;
        }

        @Override
        public IMessage setArg1(int arg1) {
            this.arg1 = arg1;
            return this;
        }

        @Override
        public IMessage setArg2(int arg2) {
            this.arg2 = arg2;
            return this;
        }

        @Override
        public IMessage setObj(Object obj) {
            this.obj = obj;
            return this;
        }

        @Override
        public IMessage copyFrom(IMessage msg) {
            return setWhat(msg.what()).setArg1(msg.arg1()).setArg2(msg.arg2()).setObj(msg);
        }

        @Override
        public void sendToTarget() {
            handlingStrategy.handleMessage(this);
        }

        @Override
        public Object obj() {
            return obj;
        }

        @Override
        public int what() {
            return what;
        }

        @Override
        public int arg1() {
            return arg1;
        }

        @Override
        public int arg2() {
            return arg2;
        }
    }
}
