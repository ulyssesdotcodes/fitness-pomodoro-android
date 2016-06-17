package com.ulyssesp.fitnesspomodoro.flrx;

import android.os.Parcelable;

import java.util.EnumSet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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

    public void postAction(E type) {
        postAction(Action.create(type));
    }

    public <R extends Parcelable> void postAction(E type, R payload) {
        postAction(Action.create(type, payload));
    }

    public Observable<Action<E>> actionsObservable() {
        return mActionObservable
            .subscribeOn(AndroidSchedulers.mainThread())
            .asObservable();
    }
}
