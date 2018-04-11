package com.alexstyl.specialdates.upcoming

import android.content.Context
import com.alexstyl.resources.Colors
import com.alexstyl.specialdates.CrashAndErrorTracker
import com.alexstyl.specialdates.Strings
import com.alexstyl.specialdates.UpcomingEventsJobCreator
import com.alexstyl.specialdates.analytics.Analytics
import com.alexstyl.specialdates.date.Date
import com.alexstyl.specialdates.events.bankholidays.BankHolidaysPreferences
import com.alexstyl.specialdates.events.bankholidays.BankHolidayProvider
import com.alexstyl.specialdates.events.bankholidays.BankHolidaysUserSettings
import com.alexstyl.specialdates.events.bankholidays.GreekBankHolidaysCalculator
import com.alexstyl.specialdates.events.namedays.NamedayUserSettings
import com.alexstyl.specialdates.events.namedays.calendar.resource.NamedayCalendarProvider
import com.alexstyl.specialdates.events.peopleevents.PeopleEventsProvider
import com.alexstyl.specialdates.events.peopleevents.PeopleEventsUpdater
import com.alexstyl.specialdates.facebook.FacebookUserSettings
import com.alexstyl.specialdates.home.HomeNavigator
import com.alexstyl.specialdates.permissions.MementoPermissions
import com.alexstyl.specialdates.theming.AttributeExtractor
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class UpcomingEventsModule {

    @Provides
    fun providesUpcomingEventsProviderWithAds(context: Context,
                                              strings: Strings,
                                              colors: Colors,
                                              namedayUserSettings: NamedayUserSettings,
                                              namedayCalendarProvider: NamedayCalendarProvider,
                                              eventsProvider: PeopleEventsProvider,
                                              bankHolidaysUserSettings: BankHolidaysUserSettings,
                                              greekBankHolidaysCalculator: GreekBankHolidaysCalculator): UpcomingEventsProvider {
        val date = Date.today()
        return CompositeUpcomingEventsProvider(
                eventsProvider,
                namedayUserSettings,
                namedayCalendarProvider,
                bankHolidaysUserSettings,
                BankHolidayProvider(greekBankHolidaysCalculator),
                UpcomingEventRowViewModelFactory(
                        date,
                        colors,
                        AndroidUpcomingDateStringCreator(strings, date, context),
                        ContactViewModelFactory(colors, strings)
                )
        )
    }

    @Provides
    fun navigator(analytics: Analytics,
                  strings: Strings,
                  tracker: CrashAndErrorTracker,
                  facebookUserSettings: FacebookUserSettings,
                  extractor: AttributeExtractor): HomeNavigator {
        return HomeNavigator(analytics, strings, facebookUserSettings, tracker, extractor)
    }

    @Provides
    fun jobCreator(updater: PeopleEventsUpdater): UpcomingEventsJobCreator {
        return UpcomingEventsJobCreator(updater)
    }

    @Provides
    fun presenter(permissionsChecker: MementoPermissions, provider: UpcomingEventsProvider): UpcomingEventsPresenter {
        val today = Date.today()
        return UpcomingEventsPresenter(
                today,
                permissionsChecker,
                provider,
                Schedulers.io(),
                AndroidSchedulers.mainThread()
        )
    }
}