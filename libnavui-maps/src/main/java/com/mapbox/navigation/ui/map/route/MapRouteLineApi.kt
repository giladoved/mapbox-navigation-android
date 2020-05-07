package com.mapbox.navigation.ui.map.route

import android.content.Context
import androidx.core.content.ContextCompat
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.navigation.ui.base.map.route.model.RouteLineOptions
import com.mapbox.navigation.ui.base.map.route.model.RouteLineState
import com.mapbox.navigation.ui.map.route.MapRouteLineApi.MapRouteLineSupport.getBooleanStyledValue
import com.mapbox.navigation.ui.map.route.MapRouteLineApi.MapRouteLineSupport.getFloatStyledValue
import com.mapbox.navigation.ui.map.route.MapRouteLineApi.MapRouteLineSupport.getStyledColor
import com.mapbox.navigation.ui.maps.R

object MapRouteLineApi {

    private fun getInitialState(options: RouteLineOptions): RouteLineState {
        val context = options.context
        return RouteLineState(
            options = options,
            features = emptyList(),
            roundedLineCap = getBooleanStyledValue(
                R.styleable.MapRouteLine_roundedLineCap,
                true,
                context
            ),
            primaryRouteVisible = options.primaryRouteVisible,
            primaryRouteColor = getStyledColor(
                R.styleable.MapRouteLine_primaryRouteColor,
                R.color.mapbox_navigation_primary_route_layer_blue,
                context
            ),
            primaryShieldColor = getStyledColor(
                R.styleable.MapRouteLine_primaryRouteShieldColor,
                R.color.mapbox_navigation_primary_route_shield_layer_color,
                context
            ),
            alternativeRouteVisible = options.alternativeRouteVisible,
            alternativeRouteColor = getStyledColor(
                R.styleable.MapRouteLine_alternativeRouteColor,
                R.color.mapbox_navigation_alternative_route_color,
                context
            ),
            alternativeShieldColor = getStyledColor(
                R.styleable.MapRouteLine_alternativeRouteShieldColor,
                R.color.mapbox_navigation_alternative_route_shield_color,
                context
            ),
            primaryRouteScale = getFloatStyledValue(
                R.styleable.MapRouteLine_primaryRouteScale,
                1f,
                context
            ).toDouble(),
            alternativeRouteScale = getFloatStyledValue(
                R.styleable.MapRouteLine_alternativeRouteScale,
                1f,
                context
            ).toDouble()
        )
    }

    private fun RouteLineState?.rebuildWithOptions(options: RouteLineOptions): RouteLineState {
        val state = this ?: getInitialState(options)
        return if (state.options != options) {
            state.toBuilder()
                .options(options)
                .primaryRouteVisible(options.primaryRouteVisible)
                .alternativeRouteVisible(options.alternativeRouteVisible)
                .build()
        } else {
            state
        }
    }

    fun getState(options: RouteLineOptions, previousState: RouteLineState? = null): RouteLineState =
        previousState.rebuildWithOptions(options)

    fun getState(previousState: RouteLineState, directionsRoute: DirectionsRoute) =
        getState(previousState, listOf(directionsRoute))

    fun getState(
        previousState: RouteLineState,
        directionsRoutes: List<DirectionsRoute>
    ): RouteLineState {
        return previousState.toBuilder()
            .features(generateFeatures(directionsRoutes))
            .build()
    }

    private fun generateFeatures(routes: List<DirectionsRoute>): List<Feature> {
        return routes.map {
            Feature.fromGeometry(
                LineString.fromPolyline(
                    it.geometry() ?: "",
                    6 //todo hardcoded
                )
            )
        }
    }

    private object MapRouteLineSupport {
        /**
         * Returns a resource value from the style or a default value
         * @param index the index of the item in the styled attributes.
         * @param defaultValue the default value to use if no value is found
         * @param context the context to obtain the resource from
         * @param styleRes the style resource to look in
         *
         * @return the resource value
         */
        fun getStyledColor(index: Int, colorResourceId: Int, context: Context): Int {
            val typedArray = context.theme.obtainStyledAttributes(R.styleable.MapRouteLine)
            return typedArray.getColor(
                index,
                ContextCompat.getColor(
                    context,
                    colorResourceId
                )
            ).also {
                typedArray.recycle()
            }
        }

        /**
         * Returns a resource value from the style or a default value
         * @param index the index of the item in the styled attributes.
         * @param defaultValue the default value to use if no value is found
         * @param context the context to obtain the resource from
         * @param styleRes the style resource to look in
         *
         * @return the resource value
         */
        fun getFloatStyledValue(index: Int, defaultValue: Float, context: Context): Float {
            val typedArray = context.theme.obtainStyledAttributes(R.styleable.MapRouteLine)
            return typedArray.getFloat(index, defaultValue).also {
                typedArray.recycle()
            }
        }

        /**
         * Returns a resource value from the style or a default value
         * @param index the index of the item in the styled attributes.
         * @param defaultValue the default value to use if no value is found
         * @param context the context to obtain the resource from
         * @param styleRes the style resource to look in
         *
         * @return the resource value
         */
        fun getBooleanStyledValue(index: Int, defaultValue: Boolean, context: Context): Boolean {
            val typedArray = context.theme.obtainStyledAttributes(R.styleable.MapRouteLine)
            return typedArray.getBoolean(index, defaultValue).also {
                typedArray.recycle()
            }
        }

        /**
         * Returns a resource value from the style or a default value
         * @param index the index of the item in the styled attributes.
         * @param defaultValue the default value to use if no value is found
         * @param context the context to obtain the resource from
         * @param styleRes the style resource to look in
         *
         * @return the resource value
         */
        fun getResourceStyledValue(index: Int, defaultValue: Int, context: Context): Int {
            val typedArray = context.theme.obtainStyledAttributes(R.styleable.MapRouteLine)
            return typedArray.getResourceId(
                index,
                defaultValue
            ).also {
                typedArray.recycle()
            }
        }
    }
}

fun RouteLineState?.newState(options: RouteLineOptions): RouteLineState =
    MapRouteLineApi.getState(options, this)

fun RouteLineState.newState(directionsRoute: DirectionsRoute): RouteLineState =
    MapRouteLineApi.getState(this, directionsRoute)

fun RouteLineState.newState(directionsRoutes: List<DirectionsRoute>): RouteLineState =
    MapRouteLineApi.getState(this, directionsRoutes)