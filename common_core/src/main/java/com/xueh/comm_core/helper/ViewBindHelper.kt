package com.xueh.comm_core.helper

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayout
import java.lang.reflect.ParameterizedType
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 创 建 人: xueh
 * 创建日期: 2021/4/8 14:06
 * 备注：
 */

//使用反射
@JvmName("inflateWithGeneric")
fun <VB : ViewBinding> Any.inflateBindingWithGeneric(layoutInflater: LayoutInflater): VB =
    withGenericBindingClass(this) { clazz ->
        clazz.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
    }

@JvmName("inflateWithGeneric")
fun <VB : ViewBinding> Any.inflateBindingWithGeneric(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): VB =
    withGenericBindingClass(this) { clazz ->
        clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
            .invoke(null, layoutInflater, parent, attachToParent) as VB
    }

@JvmName("inflateWithGeneric")
fun <VB : ViewBinding> Any.inflateBindingWithGeneric(parent: ViewGroup): VB =
    inflateBindingWithGeneric(LayoutInflater.from(parent.context), parent, false)

fun <VB : ViewBinding> Any.bindViewWithGeneric(view: View): VB =
    withGenericBindingClass(this) { clazz ->
        clazz.getMethod("bind", LayoutInflater::class.java).invoke(null, view) as VB
    }

private fun <VB : ViewBinding> withGenericBindingClass(any: Any, block: (Class<VB>) -> VB): VB {
    any.allParameterizedType.forEach { parameterizedType ->
        parameterizedType.actualTypeArguments.forEach {
            try {
                return block.invoke(it as Class<VB>)
            } catch (e: NoSuchMethodException) {
            }
        }
    }
    throw IllegalArgumentException("There is no generic of ViewBinding.")
}

private val Any.allParameterizedType: List<ParameterizedType>
    get() {
        val genericParameterizedType = mutableListOf<ParameterizedType>()
        var genericSuperclass = javaClass.genericSuperclass
        var superclass = javaClass.superclass
        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                genericParameterizedType.add(genericSuperclass)
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }
        return genericParameterizedType
    }






//不用反射
fun <VB : ViewBinding> Activity.binding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).apply { setContentView(root) }
}

fun <VB : ViewBinding> Fragment.binding(bind: (View) -> VB) =
        FragmentBindingDelegate(bind)

fun <VB : ViewBinding> Dialog.binding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).apply { setContentView(root) }
}

fun <VB : ViewBinding> ViewGroup.binding(
        inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
        attachToParent: Boolean = true
) =
        inflate(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)


class FragmentBindingDelegate<VB : ViewBinding>(
        private val bind: (View) -> VB
) : ReadOnlyProperty<Fragment, VB> {

    private var binding: VB? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (binding == null) {
            binding = bind(thisRef.requireView())
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroyView() {
                    binding = null
                }
            })
        }
        return binding!!
    }
}