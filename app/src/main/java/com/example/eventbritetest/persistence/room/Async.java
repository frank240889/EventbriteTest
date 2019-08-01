package com.example.eventbritetest.persistence.room;

import com.example.eventbritetest.interfaces.AsyncCallback;
import com.example.eventbritetest.model.persistence.Event;
import com.example.eventbritetest.utils.AsyncTaskEventbrite;

import java.lang.ref.WeakReference;
import java.util.List;

public class Async {
    public static class Read extends AsyncTaskEventbrite<Void, Void, List<Event>>{
        private WeakReference<AsyncCallback<Void, Void, List<Event>, Void, Void>> mAsyncCallback;
        private EventDao mEventDao;

        public Read() {}
        public Read(EventDao eventDao) {
            mEventDao = eventDao;
        }

        public AsyncTaskEventbrite<Void, Void, List<Event>> setCallback(AsyncCallback<Void, Void, List<Event>, Void, Void> callback) {
            mAsyncCallback = new WeakReference<>(callback);
            return this;
        }

        @Override
        protected void onPreExecute() {
            if(getCallback() != null)
                getCallback().onStart(null);
        }

        @Override
        protected List<Event> doInBackground(Void... voids) {
            return mEventDao.getAllEvents();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            if(getCallback() != null)
                getCallback().onResult(events);
        }

        @Override
        protected void onCancelled(List<Event> events) {
            super.onCancelled(events);
            if(getCallback() != null)
                getCallback().onCancelled(null);

        }

        @Override
        protected AsyncCallback<Void, Void, List<Event>, Void, Void> getCallback() {
            if(mAsyncCallback != null && mAsyncCallback.get() != null)
                return mAsyncCallback.get();
            return null;
        }

        @Override
        protected void clear() {
            if(getCallback() != null)
                mAsyncCallback.clear();
            mAsyncCallback = null;
        }
    }

    public static class Recreate extends AsyncTaskEventbrite<List<Event>, Void, Void>{
        private WeakReference<AsyncCallback<Void, Void, Void, Void, Void>> mAsyncCallback;
        private EventDao mEventDao;

        public Recreate() {}
        public Recreate(EventDao eventDao) {
            mEventDao = eventDao;
        }

        public AsyncTaskEventbrite<List<Event>,Void, Void> setCallback(AsyncCallback<Void, Void, Void, Void, Void> callback) {
            mAsyncCallback = new WeakReference<>(callback);
            return this;
        }

        @Override
        protected void onPreExecute() {
            if(getCallback() != null)
                getCallback().onStart(null);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Event>... events) {
            List<Event> eventList = events[0];
            mEventDao.deleteAndInsert(eventList);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            if(getCallback() != null)
                getCallback().onResult(null);
        }

        @Override
        protected void onCancelled(Void voids) {
            super.onCancelled(voids);
            if(getCallback() != null)
                getCallback().onCancelled(null);

        }

        @Override
        protected AsyncCallback<Void, Void, Void, Void, Void> getCallback() {
            if(mAsyncCallback != null && mAsyncCallback.get() != null)
                return mAsyncCallback.get();
            return null;
        }

        @Override
        protected void clear() {
            if(getCallback() != null)
                mAsyncCallback.clear();
            mAsyncCallback = null;
        }
    }

    public static class Create extends AsyncTaskEventbrite<List<Event>, Void, Void>{
        private WeakReference<AsyncCallback<Void, Void, Void, Void, Void>> mAsyncCallback;
        private EventDao mEventDao;

        public Create() {}
        public Create(EventDao eventDao) {
            mEventDao = eventDao;
        }

        public AsyncTaskEventbrite<List<Event>,Void, Void> setCallback(AsyncCallback<Void, Void, Void, Void, Void> callback) {
            mAsyncCallback = new WeakReference<>(callback);
            return this;
        }

        @Override
        protected void onPreExecute() {
            if(getCallback() != null)
                getCallback().onStart(null);
        }

        @Override
        protected final Void doInBackground(List<Event>... events) {
            List<Event> eventList = events[0];
            mEventDao.insertEvents(eventList);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            if(getCallback() != null)
                getCallback().onResult(null);
        }

        @Override
        protected void onCancelled(Void voids) {
            super.onCancelled(voids);
            if(getCallback() != null)
                getCallback().onCancelled(null);

        }

        @Override
        protected AsyncCallback<Void, Void, Void, Void, Void> getCallback() {
            if(mAsyncCallback != null && mAsyncCallback.get() != null)
                return mAsyncCallback.get();
            return null;
        }

        @Override
        protected void clear() {
            if(getCallback() != null)
                mAsyncCallback.clear();
            mAsyncCallback = null;
        }
    }

    public static class Count extends AsyncTaskEventbrite<Void, Void, Integer>{
        private WeakReference<AsyncCallback<Void, Void, Integer, Void, Void>> mAsyncCallback;
        private AsyncCallback<Void, Void, Integer, Void, Void> mCallback;
        private boolean mUseWeakReference;
        private EventDao mEventDao;

        public Count() {}
        public Count(EventDao eventDao) {
            mEventDao = eventDao;
        }

        public AsyncTaskEventbrite<Void,Void, Integer> setCallback(AsyncCallback<Void, Void, Integer, Void, Void> callback, boolean useWeakReference) {
            mUseWeakReference = useWeakReference;
            if(useWeakReference)
                mAsyncCallback = new WeakReference<>(callback);
            else
                mCallback = callback;
            return this;
        }

        @Override
        protected void onPreExecute() {
            if(getCallback() != null)
                getCallback().onStart(null);
        }

        @Override
        protected final Integer doInBackground(Void... voids) {
            return mEventDao.getCount();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(getCallback() != null)
                getCallback().onResult(integer);
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
            if(getCallback() != null)
                getCallback().onCancelled(null);

        }

        @Override
        protected AsyncCallback<Void, Void, Integer, Void, Void> getCallback() {
            if(mUseWeakReference) {
                if(mAsyncCallback != null && mAsyncCallback.get() != null)
                    return mAsyncCallback.get();
            }
            else {
                if(mCallback != null)
                    return mCallback;
            }
            return null;
        }

        @Override
        protected void clear() {
            if(mUseWeakReference) {
                if(getCallback() != null)
                    mAsyncCallback.clear();
                mAsyncCallback = null;
            }
            else {
                mCallback = null;
            }
        }
    }
}
