package com.github.androidutils.eventbus;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ReflectionParcelableEvent implements Parcelable {
    public static final Creator<ReflectionParcelableEvent> CREATOR = new Creator<ReflectionParcelableEvent>() {
        @Override
        public ReflectionParcelableEvent createFromParcel(final Parcel source) {
            try {
                final ReflectionParcelableEvent event = (ReflectionParcelableEvent) Class.forName(source.readString())
                        .newInstance();
                List<Field> fields = Arrays.asList(event.getClass().getDeclaredFields());
                for (Field field : fields) {
                    try {
                        if (Parcelable.class.isAssignableFrom(field.getType())) {
                            field.set(event, source.readParcelable(ClassLoader.getSystemClassLoader()));
                        } else if (String.class.equals(field.getType())) {
                            field.set(event, source.readString());
                        } else if (int.class.equals(field.getType())) {
                            field.set(event, source.readInt());
                        } else if (boolean.class.equals(field.getType())) {
                            field.set(event, source.readInt() == 1);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                }
                return event;
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        @Override
        public ReflectionParcelableEvent[] newArray(int size) {
            return new ReflectionParcelableEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(this.getClass().getName());
        List<Field> fields = Arrays.asList(this.getClass().getDeclaredFields());
        for (Field field : fields) {
            try {
                if (Parcelable.class.isAssignableFrom(field.getType())) {
                    dest.writeParcelable((Parcelable) field.get(this), flags);
                } else if (String.class.equals(field.getType())) {
                    dest.writeString((String) field.get(this));
                } else if (int.class.equals(field.getType())) {
                    dest.writeInt(field.getInt(this));
                } else if (boolean.class.equals(field.getType())) {
                    dest.writeInt(field.getBoolean(this) ? 1 : 0);
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append('[');
        List<Field> fields = Arrays.asList(this.getClass().getDeclaredFields());
        sb.append(fields);
        return sb.toString();
    }
}
