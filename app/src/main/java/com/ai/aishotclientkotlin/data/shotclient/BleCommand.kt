package com.ai.aishotclientkotlin.data.shotclient
import kotlinx.serialization.*
import kotlinx.serialization.json.*


interface Command {
    val command: String
    val data: Any
}

//在esp32s上开启wifi服务的地址和密码；
@Serializable
data class SetWifiCommand(
    override val command: String = "wp",
    override val data: ShotWifiData
) : Command

//用于设置Shot的固有参数的命令；
@Serializable
data class ShotPropertyCommand(
    override val command: String = "spc",
    override val data: ShotPropertyData
) : Command



/// 连接手机wifi热点的wifi地址和密码；
@Serializable
data class ConnectWifiCommand(
    override val command: String = "wc",
    override val data: ConnectWifiData
) : Command

@Serializable
data class SendMessageCommand(
    override val command: String = "s_m",
    override val data: MessageData
) : Command


@Serializable
data class ShotWifiData(val ws: String, val p: String)
@Serializable
data class ConnectWifiData(val ws: String, val p: String)
@Serializable
data class ShotPropertyData(val recipientId: String, val content: String)
@Serializable
data class MessageData(val recipientId: String, val content: String)

