package com.xueh.comm_core.helper

import org.greenrobot.eventbus.EventBus

/**
 * create time: 2017/7/4 16:52
 * description: class-EventBus工具类
 */
object EventBusHelper {
    /**
     * 注册EventBus
     *
     * @param subscriber 订阅者对象
     */
    fun register(subscriber: Any?) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber)
        }
    }

    /**
     * 取消注册EventBus
     *
     * @param subscriber 订阅者对象
     */
    fun unregister(subscriber: Any?) {
        EventBus.getDefault().unregister(subscriber)
    }

    /**
     * 发布订阅事件
     *
     * @param event 事件对象
     */
    fun post(event: Any?) {
        EventBus.getDefault().post(event)
    }

    /**
     * 发布粘性订阅事件
     *
     * @param event 事件对象
     */
    fun postSticky(event: Any?) {
        EventBus.getDefault().postSticky(event)
    }

    /**
     * 移除指定的粘性订阅事件
     *
     * @param eventType class的字节码，例如：String.class
     */
    fun <T> removeStickyEvent(eventType: Class<T>?) {
        val stickyEvent = EventBus.getDefault().getStickyEvent(eventType)
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent as T)
        }
    }

    /**
     * 移除所有的粘性订阅事件
     */
    fun removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents()
    }

    /**
     * 取消事件传送
     *
     * @param event 事件对象
     */
    fun cancelEventDelivery(event: Any?) {
        EventBus.getDefault().cancelEventDelivery(event)
    }
}