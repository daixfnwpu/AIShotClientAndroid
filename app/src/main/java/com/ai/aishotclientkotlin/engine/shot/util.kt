package com.ai.aishotclientkotlin.engine.shot


data class InputRubberKey(val input1: Any, val input2: Any, val input3: Any, val input4: Any)

// 定义对应表
val lookupRubberTable = mapOf(
    InputRubberKey("A", "B", "C", "D") to 0.1,
    InputRubberKey(1, 2, 3, 4) to 0.2,
    InputRubberKey(true, false, "X", "Y") to 0.3
)

// 查找函数
fun getOutputRubber(input1: Any, input2: Any, input3: Any, input4: Any): Double {
    val key = InputRubberKey(input1, input2, input3, input4)
    return lookupRubberTable[key] ?: 1.0 // 如果找不到对应的值，返回 null
}
