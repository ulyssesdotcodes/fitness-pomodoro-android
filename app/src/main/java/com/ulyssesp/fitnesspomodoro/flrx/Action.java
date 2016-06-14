package com.ulyssesp.fitnesspomodoro.flrx;


import android.os.Parcel;
import android.os.Parcelable;

import com.ulyssesp.fitnesspomodoro.utils.Optional;

public class Action<R extends Enum<R>> {
    private R mType;
    private Optional<Parcel> mPayload;

    Action(R type, Optional<Parcelable> payload) {
        this.mType = type;
        mPayload = payload.transform((parcelable) -> {
            Parcel p = Parcel.obtain();
            parcelable.writeToParcel(p, 0);
            Parcel p2 = Parcel.obtain();
            final byte[] bytes = p.marshall();
            p2.unmarshall(bytes, 0, bytes.length);
            p2.setDataPosition(0);
            return p2;
        });
    }

    public static <R extends Enum<R>> Action<R> create(R type, Parcelable dataModel){
        return new Action<>(type, Optional.of(dataModel));
    }

    public static <R extends Enum<R>> Action<R> create(R type) {
        return new Action<>(type, Optional.absent());
    }

    public R getType() {
        return mType;
    }

    public Optional<Parcel> getPayload() {
        return mPayload;
    }
}
