package eating.well.recipe.keeper.app

import android.app.Application
import eating.well.recipe.keeper.app.di.appModule
import eating.well.recipe.keeper.app.di.dataModule
import eating.well.recipe.keeper.app.di.roomModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(listOf(roomModule, appModule, dataModule))
        }
    }
}