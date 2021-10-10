package de.dertyp7214.rboardthememanager.core

import org.w3c.dom.Node
import org.w3c.dom.NodeList

operator fun NodeList.iterator(): MutableIterator<Node> {
    return toList().iterator()
}

fun NodeList.toList(): ArrayList<Node> {
    val list = ArrayList<Node>()
    for (i in 0 until length) list.add(item(i))
    return list
}