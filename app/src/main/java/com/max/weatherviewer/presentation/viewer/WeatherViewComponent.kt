package com.max.weatherviewer.presentation.viewer

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.max.weatherviewer.R
import com.max.weatherviewer.api.location.LocationModel
import com.max.weatherviewer.api.weather.Location
import com.max.weatherviewer.api.weather.WeatherProvider
import com.max.weatherviewer.navigateDefaultAnimated
import com.max.weatherviewer.presentation.map.MapFragmentArgs
import com.oliynick.max.elm.core.component.*
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

typealias WeatherComponent = Component<Message, State>

fun <S> S.weatherModule(startLocation: Location): Kodein.Module where S : CoroutineScope,
                                                                      S : Fragment {

    return Kodein.Module("weatherModule") {

        bind<Fragment>() with scoped(scope).singleton { this@weatherModule as Fragment }

        bind<Dependencies>() with singleton { Dependencies(instance(), instance(), instance()) }

        bind<WeatherComponent>() with scoped(scope).singleton {

            suspend fun resolver(command: Command) = instance<Dependencies>().resolveEffect(command)

            component(State.Loading(startLocation), ::resolver, ::update,
                      androidLogger("WeatherViewer"), Command.LoadWeather(startLocation))
        }
    }
}

private data class Dependencies(
        val locationModel: LocationModel,
        val fragment: Fragment,
        val weatherProvider: WeatherProvider
)

private suspend fun Dependencies.resolveEffect(command: Command): Set<Message> {

    suspend fun resolve() = when (command) {
        is Command.LoadWeather -> command.effect { Message.WeatherLoaded(weatherProvider.fetchWeather(l)) }
        is Command.FeedLoaded -> command.effect { Message.WeatherLoaded(data) }
        is Command.SelectLocation -> command.sideEffect { fragment.navigateToMap(withSelectedLocation) }
    }

    return runCatching { resolve() }.getOrElse { th -> effect { Message.OpFuckup(th) } }
}

private fun Fragment.navigateToMap(withStartLocation: Location?) {
    findNavController()
        .navigateDefaultAnimated(R.id.mapFragment,
                                 MapFragmentArgs(withStartLocation).toBundle())
}