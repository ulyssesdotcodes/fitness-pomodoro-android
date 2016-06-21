package com.ulyssesp.fitnesspomodoro.flrx;

import android.os.Parcelable;
import android.support.v4.util.Pair;

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

    private final EnumSet<E> mAcceptedActions;

    public Store(Dispatcher<E> dispatcher, EnumSet<E> acceptedActions) {
        mDispatcher = dispatcher;
        mAcceptedActions = acceptedActions;
        Scheduler storeThread = Schedulers.newThread();
        mDataObservable = dispatcher.actionsObservable()
            .observeOn(storeThread)
            .filter((action) ->  acceptedActions.contains(action.getType()))
            .scan(Pair.create(initialState(), Observable.<Action<E>>empty()),
                (p, a) -> this.reducer(p.first, a))
            .doOnNext(p -> p.second.subscribe(this::postAction))
            .map(p -> p.first)
            .cacheWithInitialCapacity(1);
    }

    protected abstract T initialState();


    protected EnumSet<E> getActions() {
        return EnumSet.copyOf(mAcceptedActions);
    }

    private void postAction(Action<E> action) {
        mDispatcher.postAction(action);
    }

    public Observable<T> dataObservable() {
        return mDataObservable.distinctUntilChanged();
    }

    public abstract Pair<T, Observable<Action<E>>> reducer(T state, Action<E> action);
}
