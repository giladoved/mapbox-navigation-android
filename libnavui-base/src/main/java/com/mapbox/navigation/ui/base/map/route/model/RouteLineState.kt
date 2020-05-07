package com.mapbox.navigation.ui.base.map.route.model

import com.mapbox.geojson.Feature
import com.mapbox.navigation.ui.base.State

data class RouteLineState(
    val options: RouteLineOptions,
    val features: List<Feature>,
    val roundedLineCap: Boolean,
    val primaryRouteVisible: Boolean,
    val primaryRouteColor: Int,
    val primaryRouteScale: Double,
    val primaryShieldColor: Int,
    val alternativeRouteVisible: Boolean,
    val alternativeRouteColor: Int,
    val alternativeRouteScale: Double,
    val alternativeShieldColor: Int
) : State {

    fun toBuilder() = Builder(
        options,
        features,
        roundedLineCap,
        primaryRouteVisible,
        primaryRouteColor,
        primaryRouteScale,
        primaryShieldColor,
        alternativeRouteVisible,
        alternativeRouteColor,
        alternativeRouteScale,
        alternativeShieldColor
    )

    data class Builder(
        var options: RouteLineOptions,
        var features: List<Feature>,
        var roundedLineCap: Boolean,
        var primaryRouteVisible: Boolean,
        var primaryRouteColor: Int,
        var primaryRouteScale: Double,
        var primaryShieldColor: Int,
        var alternativeRouteVisible: Boolean,
        var alternativeRouteColor: Int,
        var alternativeRouteScale: Double,
        var alternativeShieldColor: Int
    ) {
        fun options(options: RouteLineOptions): Builder =
            apply { this.options = options }

        fun features(features: List<Feature>): Builder =
            apply { this.features = features }

        fun roundedLineCap(roundedLineCap: Boolean): Builder =
            apply { this.roundedLineCap = roundedLineCap }

        fun primaryRouteVisible(primaryRouteVisible: Boolean): Builder =
            apply { this.primaryRouteVisible = primaryRouteVisible }

        fun primaryRouteColor(primaryRouteColor: Int): Builder =
            apply { this.primaryRouteColor = primaryRouteColor }

        fun primaryRouteScale(primaryRouteScale: Double): Builder =
            apply { this.primaryRouteScale = primaryRouteScale }

        fun primaryShieldColor(primaryShieldColor: Int): Builder =
            apply { this.primaryShieldColor = primaryShieldColor }

        fun alternativeRouteVisible(alternativeRouteVisible: Boolean): Builder =
            apply { this.alternativeRouteVisible = alternativeRouteVisible }

        fun alternativeRouteColor(alternativeRouteColor: Int): Builder =
            apply { this.alternativeRouteColor = alternativeRouteColor }

        fun alternativeRouteScale(alternativeRouteScale: Double): Builder =
            apply { this.alternativeRouteScale = alternativeRouteScale }

        fun alternativeShieldColor(alternativeShieldColor: Int): Builder =
            apply { this.alternativeShieldColor = alternativeShieldColor }

        fun build() = RouteLineState(
            options,
            features,
            roundedLineCap,
            primaryRouteVisible,
            primaryRouteColor,
            primaryRouteScale,
            primaryShieldColor,
            alternativeRouteVisible,
            alternativeRouteColor,
            alternativeRouteScale,
            alternativeShieldColor
        )
    }
}