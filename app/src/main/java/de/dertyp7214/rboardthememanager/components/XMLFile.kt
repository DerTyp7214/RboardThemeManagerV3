package de.dertyp7214.rboardthememanager.components

import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.core.joinToString
import de.dertyp7214.rboardthememanager.core.readXML
import de.dertyp7214.rboardthememanager.core.times
import de.dertyp7214.rboardthememanager.core.writeFile
import java.util.*
import kotlin.collections.HashMap

@Suppress("unused")
class XMLFile(val path: String? = null, private val initMap: Map<String, Any>? = null) {
    private val values: HashMap<String, XMLEntry> = hashMapOf()

    constructor(initMap: Map<String, Any>?) : this(null, initMap)
    constructor(initString: String?) : this(initString?.readXML())
    constructor(path: String, initString: String?) : this(path, initString?.readXML())

    init {
        path.let { if (it != null) SuFile(it).readXML() else initMap ?: mapOf() }
            .forEach { (key, value) ->
                values[key] = XMLEntry.parse(key, value)
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

    operator fun get(name: String): Any? = values[name]?.value

    fun toString(comment: String = ""): String {
        return "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n${comment.let { if (it.isNotEmpty()) "<!--RBOARD:$comment-->" else "" }}\n<map>\n${
            values.joinToString("\n") { "${" " * 4}${it.value}" }
        }\n</map>"
    }

    override fun toString() = toString("")

    override fun equals(other: Any?): Boolean {
        if (other !is XMLFile) return false
        return other.values.size == values.size && other.values.filter { it1 -> values.any { it2 -> it2.key == it1.key } }.size == values.size
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

    override fun toString(): String {
        return try {
            when (type) {
                XMLType.LONG, XMLType.BOOLEAN, XMLType.INT, XMLType.DOUBLE, XMLType.FLOAT -> "<${type.identifier} name=\"$name\" value=\"$value\"/>"
                XMLType.STRING -> "<string name=\"$name\">$value</string>"
                XMLType.SET -> "<set name=\"$name\">\n${
                    (value as Set<*>).joinToString("\n") { "${" " * 4}$it" }
                }\n</set>"
            }
        } catch (e: Exception) {
            "<${type.identifier} name=\"$name\" />"
        }
    }

    companion object {
        fun <T> parse(name: String, value: T): XMLEntry {
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