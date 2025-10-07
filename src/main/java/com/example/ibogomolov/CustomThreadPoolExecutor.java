package com.example.ibogomolov;

import java.util.concurrent.Executor;

public class CustomThreadPoolExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
