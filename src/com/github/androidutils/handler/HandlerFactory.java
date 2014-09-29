package com.github.androidutils.handler;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import com.google.common.base.Preconditions;

public class HandlerFactory implements IHandlerFactory {
    Looper looper;

    public HandlerFactory(Looper looper) {
        this.looper = Preconditions.checkNotNull(looper);
    }

    @Override
    public IHandler createHandler(final IHandlingStrategy handlingStrategy) {
        return new HandlerImpl(handlingStrategy, looper);
    }

    private static Message from(IMessage message) {
        Message obtained = Message.obtain();
        obtained.obj = message;
        obtained.what = message.what();
        obtained.arg1 = message.arg1();
        obtained.arg2 = message.arg2();
        return obtained;
    }

    private static final class HandlerImpl implements IHandler {
        private final IHandlingStrategy handlingStrategy;
        Handler handler;

        private HandlerImpl(final IHandlingStrategy handlingStrategy, Looper looper) {
            this.handlingStrategy = handlingStrategy;
            Callback callback = new Callback() {
                @Override
                public boolean handleMessage(Message arg0) {
                    return handlingStrategy.handleMessage((IMessage) arg0.obj);
                }
            };
            handler = new Handler(looper, callback);
        }

        @Override
        public void sendMessageAtFrontOfQueue(IMessage message) {
            handler.sendMessageAtFrontOfQueue(from(message));
        }

        @Override
        public void sendMessage(IMessage message) {
            handler.sendMessage(from(message));
        }

        @Override
        public void removeMessages(int what) {
            handler.removeMessages(what);
        }

        @Override
        public IMessage obtainMessage(int what) {
            return new HandlerMessage(what, handler);
        }

        @Override
        public IMessage obtainMessage() {
            return new HandlerMessage(handler);
        }

        @Override
        public IMessage obtainMessage(int what, Object object) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    private static class HandlerMessage implements IMessage {

        public HandlerMessage(int what, Handler handler) {
            // TODO Auto-generated constructor stub
        }

        public HandlerMessage(Handler handler) {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void setArg1(int i) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setObj(Object obj) {
            // TODO Auto-generated method stub

        }

        @Override
        public Object obj() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void sendToTarget() {
            // TODO Auto-generated method stub

        }

        @Override
        public int what() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void setArg2(int minute) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setWhat(int change) {
            // TODO Auto-generated method stub

        }

        @Override
        public int arg1() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int arg2() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void copyFrom(IMessage msg) {
            // TODO Auto-generated method stub

        }

    }
}
