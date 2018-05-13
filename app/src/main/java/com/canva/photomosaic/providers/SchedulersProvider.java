package com.canva.photomosaic.providers;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SchedulersProvider{
    @NonNull
    public Scheduler mainUiThread() {
        return AndroidSchedulers.mainThread();
    }

    @NonNull
    public Scheduler io() {
        return Schedulers.io();
    }

    @NonNull
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @NonNull
    public Scheduler immediate() {
        return Schedulers.trampoline();
    }

}