package com.mapbox.navigation.ui.map.route

import com.mapbox.geojson.FeatureCollection
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.style.expressions.Expression
import com.mapbox.maps.plugin.style.expressions.dsl.interpolate
import com.mapbox.maps.plugin.style.layers.LineLayer
import com.mapbox.maps.plugin.style.layers.lineLayer
import com.mapbox.maps.plugin.style.layers.properties.LineCap
import com.mapbox.maps.plugin.style.layers.properties.LineJoin
import com.mapbox.maps.plugin.style.layers.properties.Visibility
import com.mapbox.maps.plugin.style.sources.geojsonSource
import com.mapbox.maps.plugin.style.sources.updateGeoJSON
import com.mapbox.navigation.ui.base.View
import com.mapbox.navigation.ui.base.map.route.model.RouteLineState

class MapRouteLine(style: Style) : View<RouteLineState> {

    // todo port waypoint layer

    private val primaryRouteRenderer = RouteRenderer<LineLayerRenderData.PrimaryRoute>(
        style,
        "mapbox_navigation_primary_route_source",
        "mapbox_navigation_primary_route_layer",
        "mapbox_navigation_primary_shield_layer"
    )
    private val alternativeRouteRenderer = RouteRenderer<LineLayerRenderData.AlternativeRoute>(
        style,
        "mapbox_navigation_alternative_route_layer",
        "mapbox_navigation_alternative_route_source",
        "mapbox_navigation_alternative_shield_layer"
    )

    override fun render(state: RouteLineState) {
        state.getLineLayerRenderData().forEach { data ->
            when (data) {
                is LineLayerRenderData.PrimaryRoute -> primaryRouteRenderer.update(data)
                is LineLayerRenderData.AlternativeRoute -> alternativeRouteRenderer.update(data)
            }
        }
    }

    private fun RouteLineState.getLineLayerRenderData(): Set<LineLayerRenderData> {
        return setOf(
            LineLayerRenderData.PrimaryRoute(
                featureCollection = getPrimaryRouteFeatureCollection(),
                visibility = getVisibility(primaryRouteVisible),
                lineCap = getLineCap(),
                lineJoin = getLineJoin(),
                routeWidthExpression = getRouteWidth(primaryRouteScale),
                shieldWidthExpression = getShieldWidth(primaryRouteScale),
                routeColor = primaryRouteColor,
                shieldColor = primaryShieldColor
            ),
            LineLayerRenderData.AlternativeRoute(
                featureCollection = getAlternativeRoutesCollection(),
                visibility = getVisibility(alternativeRouteVisible),
                lineCap = getLineCap(),
                lineJoin = getLineJoin(),
                routeWidthExpression = getRouteWidth(alternativeRouteScale),
                shieldWidthExpression = getShieldWidth(alternativeRouteScale),
                routeColor = alternativeRouteColor,
                shieldColor = alternativeShieldColor
            )
        )
    }

    private fun RouteLineState.getPrimaryRouteFeatureCollection() =
        FeatureCollection.fromFeatures(this.features.getOrNull(0)?.let { listOf(it) }
            ?: emptyList())

    private fun RouteLineState.getAlternativeRoutesCollection() =
        FeatureCollection.fromFeatures(this.features.drop(1) ?: emptyList())

    private fun RouteLineState.getLineCap() =
        if (this.roundedLineCap) LineCap.ROUND else LineCap.BUTT

    private fun RouteLineState.getLineJoin() =
        if (this.roundedLineCap) LineJoin.ROUND else LineJoin.BEVEL

    private fun getRouteWidth(scale: Double) = interpolate {
        exponential {
            literal(1.5)
        }
        zoom()
        stop { literal(4); product { literal(3); literal(scale) } }
        stop { literal(10); product { literal(4); literal(scale) } }
        stop { literal(13); product { literal(6); literal(scale) } }
        stop { literal(16); product { literal(10); literal(scale) } }
        stop { literal(19); product { literal(14); literal(scale) } }
        stop { literal(22); product { literal(18); literal(scale) } }
    }

    private fun getShieldWidth(scale: Double) = interpolate {
        exponential {
            literal(1.5)
        }
        zoom()
        stop { literal(10); literal(7) }
        stop { literal(14); product { literal(10.5); literal(scale) } }
        stop { literal(16.5); product { literal(15.5); literal(scale) } }
        stop { literal(19); product { literal(24); literal(scale) } }
        stop { literal(22); product { literal(29); literal(scale) } }
    }

    private fun getVisibility(isVisible: Boolean) =
        if (isVisible) Visibility.VISIBLE else Visibility.NONE
}

private sealed class LineLayerRenderData(
    val featureCollection: FeatureCollection,
    val visibility: Visibility,
    val lineCap: LineCap,
    val lineJoin: LineJoin,
    val routeWidthExpression: Expression,
    val shieldWidthExpression: Expression,
    val routeColor: Int,
    val shieldColor: Int
) {
    class PrimaryRoute(
        featureCollection: FeatureCollection,
        visibility: Visibility,
        lineCap: LineCap,
        lineJoin: LineJoin,
        routeWidthExpression: Expression,
        shieldWidthExpression: Expression,
        routeColor: Int,
        shieldColor: Int
    ) : LineLayerRenderData(
        featureCollection,
        visibility,
        lineCap,
        lineJoin,
        routeWidthExpression,
        shieldWidthExpression,
        routeColor,
        shieldColor
    )

    class AlternativeRoute(
        featureCollection: FeatureCollection,
        visibility: Visibility,
        lineCap: LineCap,
        lineJoin: LineJoin,
        routeWidthExpression: Expression,
        shieldWidthExpression: Expression,
        routeColor: Int,
        shieldColor: Int
    ) : LineLayerRenderData(
        featureCollection,
        visibility,
        lineCap,
        lineJoin,
        routeWidthExpression,
        shieldWidthExpression,
        routeColor,
        shieldColor
    )
}

private class RouteRenderer<D : LineLayerRenderData>(
    style: Style,
    sourceId: String,
    routeLayerId: String,
    shieldLayerId: String
) {
    private val source = geojsonSource(sourceId) {
        featureCollection(FeatureCollection.fromFeatures(emptyArray()))
        lineMetrics(true)
    }
    private val routeLayer = lineLayer(routeLayerId, sourceId) {}
    private val shieldLayer = lineLayer(shieldLayerId, sourceId) {}

    init {
        source.bindTo(style)
        shieldLayer.bindTo(style)
        routeLayer.bindTo(style)
    }

    fun update(data: D) {
        source.updateGeoJSON(data.featureCollection)
        fun LineLayer.update(color: Int, width: Expression) {
            visibility(data.visibility)
            lineColor(color)
            lineWidth(width)
            lineCap(data.lineCap)
            lineJoin(data.lineJoin)
        }
        routeLayer.update(data.routeColor, data.routeWidthExpression)
        shieldLayer.update(data.shieldColor, data.shieldWidthExpression)
    }
}