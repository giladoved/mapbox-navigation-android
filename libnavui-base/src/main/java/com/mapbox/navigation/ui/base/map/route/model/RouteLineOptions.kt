package com.mapbox.navigation.ui.base.map.route.model

import android.content.Context

data class RouteLineOptions(
    val context: Context,
    val primaryRouteVisible: Boolean,
    val alternativeRouteVisible: Boolean = true
) {
    fun toBuilder() = Builder(
        context,
        primaryRouteVisible,
        alternativeRouteVisible
    )

    data class Builder(
        private var context: Context,
        private var primaryRouteVisible: Boolean = true,
        private var alternativeRouteVisible: Boolean = true
    ) {

        fun primaryRouteVisible(primaryRouteVisible: Boolean): Builder =
            apply { this.primaryRouteVisible = primaryRouteVisible }

        fun alternativeRouteVisible(alternativeRouteVisible: Boolean): Builder =
            apply { this.alternativeRouteVisible = alternativeRouteVisible }

        fun build() = RouteLineOptions(
            context,
            primaryRouteVisible,
            alternativeRouteVisible
        )
    }
}