package com.pet.shorts.ui.components.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

fun Modifier.holdable(
    onClick: (() -> Unit)? = null,
    onHoldReleased: (() -> Unit)? = null,
    onHold: suspend CoroutineScope.() -> Unit
): Modifier {
    return this then HoldableElement(
        onClick = onClick,
        onHoldReleased = onHoldReleased,
        onHold = onHold
    )
}

private data class HoldableElement(
    private val onClick: (() -> Unit)? = null,
    private val onHoldReleased: (() -> Unit)? = null,
    private val onHold: suspend CoroutineScope.() -> Unit
) : ModifierNodeElement<HoldableNode>() {

    override fun InspectorInfo.inspectableProperties() {
        name = "holdable"
        properties["onClick"] = onClick
        properties["onHoldReleased"] = onHoldReleased
        properties["onHold"] = onHold
    }

    override fun create() = HoldableNode(onClick, onHoldReleased, onHold)

    override fun update(node: HoldableNode) {
    }

}

private class HoldableNode(
    private var onClick: (() -> Unit)? = null,
    private var onHoldReleased: (() -> Unit)? = null,
    private var onHold: suspend CoroutineScope.() -> Unit
) : PointerInputModifierNode, Modifier.Node() {

    private var isLongPress = false
    private var longPressJob: Job? = null
    private var initialPointerPosition: Offset? = null

    override fun onCancelPointerInput() {
        refreshState()
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        if (pass == PointerEventPass.Initial) {
            when (pointerEvent.type) {
                PointerEventType.Press -> {
                    if (pointerEvent.changes.none { it.previousPressed }) {
                        initialPointerPosition = pointerEvent.changes.last { it.pressed }.position

                        longPressJob = coroutineScope.launch(Dispatchers.IO) {
                            delay(500)
                            isLongPress = true

                            onHold()
                        }
                    }
                }

                PointerEventType.Release -> {
                    onClick?.let { block ->
                        if (
                            !isLongPress && pointerEvent.changes.none {
                                initialPointerPosition?.let { offset ->
                                    abs(it.position.y - offset.y) > 5
                                            && abs(it.position.x - offset.x) > 5
                                } != false
                            }
                        ) {
                            block()
                        }
                    }
                    onHoldReleased?.let { block ->
                        if (isLongPress && pointerEvent.changes.none { it.pressed }) {
                            block()
                        }
                    }

                    if (pointerEvent.changes.none { it.pressed })
                        refreshState()
                }
            }
        }
    }

    private fun refreshState() {
        longPressJob?.cancel()
        longPressJob = null
        initialPointerPosition = null
        isLongPress = false
    }
}