package com.ulyssesp.fitnesspomodoro.flrx;

import android.os.Parcelable;

import java.util.EnumSet;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 *
 * @param <T> The model of the Store
 */
public abstract class Store <T extends Parcelable, E extends Enum<E>> {
    Dispatcher<E> mDispatcher;
    private Observable<T> mDataObservable;
    private T mCurrentData;

    private final EnumSet<E> mAcceptedActions;

    public Store(Dispatcher<E> dispatcher, EnumSet<E> acceptedActions) {
        mDispatcher = dispatcher;
        mAcceptedActions = acceptedActions;
        Scheduler storeThread = Schedulers.newThread();
        mDataObservable = dispatcher.actionsObservable()
            .observeOn(storeThread)
            .filter((action) ->  acceptedActions.contains(action.getType()))
            .scan(initialState(), this::reducer)
            .cacheWithInitialCapacity(1)
            .doOnNext(t -> mCurrentData = t);
    }

    protected abstract T initialState();


    protected EnumSet<E> getActions() {
        return EnumSet.copyOf(mAcceptedActions);
    }

    protected void postAction(Action<E> action) {
        mDispatcher.postAction(action);
    }

    public Observable<T> dataObservable() {
        return mDataObservable.distinctUntilChanged();
    }

    public abstract T reducer(T state, Action<E> action);
}
