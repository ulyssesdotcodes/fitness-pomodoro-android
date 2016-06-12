package com.ulyssesp.fitnesspomodoro.flrx;


import android.os.Parcel;
import android.os.Parcelable;

import com.ulyssesp.fitnesspomodoro.utils.Optional;

import rx.Single;

public class Action<R extends Enum<R>> {
    private R mType;
    private Single<Optional<Parcel>> mPayloadFunc;

    Action(R type, Single<Optional<Parcelable>> payloadFunc) {
        this.mType = type;
        mPayloadFunc =
            payloadFunc
                .map((opt) ->
                    opt.transform((parcelable) -> {
                        Parcel p = Parcel.obtain();
                        parcelable.writeToParcel(p, 0);
                        Parcel p2 = Parcel.obtain();
                        final byte[] bytes = p.marshall();
                        p2.unmarshall(bytes, 0, bytes.length);
                        p2.setDataPosition(0);
                        return p2;
                    })
                );
    }

    public static <R extends Enum<R>> Action<R> create(R type, Single<Parcelable> dataModel) {
        return new Action<>(type, dataModel.map(Optional::of));
    }

    public static <R extends Enum<R>> Action<R> create(R type) {
        return new Action<>(type, Single.just(Optional.absent()));
    }

    public R getType() {
        return mType;
    }

    public Optional<Parcel> getPayload() {
        return mPayloadFunc.toBlocking().value();
    }
}
