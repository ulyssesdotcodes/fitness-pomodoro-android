package com.ulyssesp.fitnesspomodoro.flrx;

import java.util.EnumSet;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class Dispatcher<E extends Enum<E>> {
    private PublishSubject<Action<E>> mActionObservable;

    private final EnumSet<E> mActions;

    public Dispatcher(EnumSet<E> actions) {
        mActions = actions;
        mActionObservable = PublishSubject.create();
    }

    public void postAction(Action<E> action) {
        mActionObservable.onNext(action);
    }

    public Observable<Action<E>> actionsObservable() {
        return mActionObservable
            .subscribeOn(Schedulers.computation())
            .asObservable()
            .onBackpressureDrop();
    }
}
