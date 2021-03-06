package com.alexstyl.specialdates.people;

import com.alexstyl.specialdates.CrashAndErrorTracker;
import com.alexstyl.specialdates.Strings;
import com.alexstyl.specialdates.events.peopleevents.PeopleEventsProvider;
import com.alexstyl.specialdates.facebook.FacebookUserSettings;
import com.alexstyl.specialdates.permissions.MementoPermissions;

import dagger.Module;
import dagger.Provides;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class PeopleModule {

    @Provides
    PeopleViewModelFactory viewModelFactory(Strings strings, FacebookUserSettings facebookPrefs) {
        return new PeopleViewModelFactory(strings, facebookPrefs);
    }

    @Provides
    PeoplePresenter presenter(MementoPermissions permissions,
                              PeopleEventsProvider peopleEventsProvider,
                              PeopleViewModelFactory viewModelFactory,
                              CrashAndErrorTracker errorTracker) {
        return new PeoplePresenter(
                permissions,
                peopleEventsProvider,
                viewModelFactory,
                errorTracker,
                Schedulers.io(),
                AndroidSchedulers.mainThread()
        );
    }
}
