@file:Suppress("MemberVisibilityCanBePrivate")

package de.dertyp7214.rboardthememanager.components

import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.core.joinToString
import de.dertyp7214.rboardthememanager.core.readXML
import de.dertyp7214.rboardthememanager.core.times
import de.dertyp7214.rboardthememanager.core.writeFile
import java.util.*
import kotlin.collections.HashMap

@Suppress("unused")
class XMLFile(
    val path: String? = null,
    private val initMap: Map<String, Any>? = null,
    initValues: Map<String, XMLEntry> = hashMapOf(),
    empty: Boolean = false
) {
    private val values: HashMap<String, XMLEntry> = HashMap(initValues)

    constructor(initMap: Map<String, Any>?) : this(null, initMap)
    constructor(initString: String?) : this(initString?.readXML())
    constructor(path: String, initString: String?) : this(path, initString?.readXML())

    init {
        if (!empty) path.let { if (it != null) SuFile(it).readXML() else initMap ?: mapOf() }
            .forEach { (key, value) ->
                values[key] = XMLEntry[key, value]
            }
    }

    fun simpleMap(): Map<String, Any> {
        return values.map {
            if (it.value.type == XMLType.SET) Pair(it.key, it.value.getValue())
            else Pair(it.key, it.value.value)
        }.toMap()
    }

    fun getValue(name: String): XMLEntry? {
        return values[name]
    }

    fun setValue(value: XMLEntry) {
        values[value.name] = value
    }

    fun writeFile() {
        if (path != null) SuFile(path).writeFile(toString())
    }

    fun forEach(block: (XMLEntry) -> Unit) = values.forEach { (_, entry) -> block(entry) }
    fun filter(predicate: (XMLEntry) -> Boolean) =
        XMLFile(initValues = values.filter { (_, entry) -> predicate(entry) })

    fun has(name: String) = values.containsKey(name)
    fun has(entry: XMLEntry) = has(entry.name)
    fun hasNot(entry: XMLEntry) = !has(entry)
    fun entryEquals(entry: XMLEntry) = values[entry.name]?.value == entry.value
    fun entryNotEquals(entry: XMLEntry) = !entryEquals(entry)
    operator fun get(name: String): Any? = values[name]?.value

    fun toString(comment: String = ""): String {
        return "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n${comment.let { if (it.isNotEmpty()) "<!--RBOARD:$comment-->\n" else "" }}<map>\n${
            values.joinToString("\n") { it.value[4] }
        }\n</map>"
    }

    override fun toString() = toString("")

    override fun equals(other: Any?): Boolean {
        if (other !is XMLFile) return false
        return other.values.size == values.size && other.values.filter { (_, entry) -> values[entry.name] == entry }.size == values.size
    }

    override fun hashCode(): Int {
        var result = path?.hashCode() ?: 0
        result = 31 * result + (initMap?.hashCode() ?: 0)
        result = 31 * result + values.hashCode()
        return result
    }
}

enum class XMLType(val identifier: String) {
    STRING("string"),
    LONG("long"),
    BOOLEAN("boolean"),
    INT("int"),
    DOUBLE("double"),
    FLOAT("float"),
    SET("set");

    companion object {
        fun parseType(type: String?): XMLType? = when (type?.lowercase(Locale.getDefault())) {
            "string" -> STRING
            "long" -> LONG
            "boolean" -> BOOLEAN
            "integer", "int" -> INT
            "double" -> DOUBLE
            "float" -> FLOAT
            "set" -> SET
            else -> null
        }
    }
}

class XMLEntry(val name: String, _value: Any, val type: XMLType) {
    var value: Any = _value
        private set

    fun getValue(): String {
        return when (type) {
            XMLType.SET -> (value as Set<*>).joinToString(",")
            else -> value.toString()
        }
    }

    fun setValue(newValue: Any): XMLEntry {
        value = if (type == XMLType.SET && newValue is String) newValue.split(",").toSet()
        else when (type) {
            XMLType.BOOLEAN -> newValue.toString().lowercase().toBooleanStrictOrNull() ?: false
            XMLType.DOUBLE -> newValue.toString().toDoubleOrNull() ?: .0
            XMLType.FLOAT -> newValue.toString().toFloatOrNull() ?: .0f
            XMLType.LONG -> newValue.toString().toLongOrNull() ?: 0L
            XMLType.INT -> newValue.toString().toIntOrNull() ?: 0
            else -> newValue
        }
        return this
    }

    operator fun get(rightType: Boolean): Any? =
        if (!rightType) getValue()
        else when (type) {
            XMLType.LONG -> getValue().toLongOrNull()
            XMLType.BOOLEAN -> getValue().toBooleanStrictOrNull()
            XMLType.INT -> getValue().toIntOrNull()
            XMLType.DOUBLE -> getValue().toDoubleOrNull()
            XMLType.FLOAT -> getValue().toFloatOrNull()
            else -> getValue()
        }

    operator fun get(indent: Int) = try {
        when (type) {
            XMLType.LONG, XMLType.BOOLEAN, XMLType.INT, XMLType.DOUBLE, XMLType.FLOAT -> "${" " * indent}<${type.identifier} name=\"$name\" value=\"$value\"/>"
            XMLType.STRING -> "${" " * indent}<string name=\"$name\">$value</string>"
            XMLType.SET -> "${" " * indent}<set name=\"$name\">\n${
                (value as Set<*>).joinToString("\n") { "${" " * (4 + indent)}$it" }
            }\n${" " * indent}</set>"
        }
    } catch (e: Exception) {
        "${" " * indent}<${type.identifier} name=\"$name\" />"
    }

    override fun toString() = this[0]

    override fun equals(other: Any?): Boolean {
        if (other !is XMLEntry) return false
        return other.value == value && other.name == name && other.type == type
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    companion object {
        operator fun <T> get(name: String, value: T): XMLEntry {
            val type = when (value) {
                is Long -> XMLType.LONG
                is Boolean -> XMLType.BOOLEAN
                is Int -> XMLType.INT
                is Float -> XMLType.FLOAT
                is Double -> XMLType.DOUBLE
                is Set<*> -> XMLType.SET
                else -> XMLType.STRING
            }
            return XMLEntry(
                name,
                value as Any,
                type
            ).setValue(value)
        }
    }
}