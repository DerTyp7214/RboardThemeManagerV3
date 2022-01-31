package de.dertyp7214.rboardthememanager.components

import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.core.joinToString
import de.dertyp7214.rboardthememanager.core.readXML
import de.dertyp7214.rboardthememanager.core.writeFile
import de.dertyp7214.rboardthememanager.core.times

class XMLFile(val path: String) {
    private val values: HashMap<String, XMLEntry> = hashMapOf()

    init {
        SuFile(path).readXML().forEach { (key, value) ->
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
        SuFile(path).writeFile(toString())
    }

    override fun toString(): String {
        return "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n<map>\n${
            values.joinToString("\n") { "${" " * 4}${it.value}" }
        }\n</map>"
    }
}

enum class XMLType(val identifier: String) {
    STRING("string"),
    LONG("long"),
    BOOLEAN("boolean"),
    INT("int"),
    DOUBLE("double"),
    FLOAT("float"),
    SET("set")
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
        else newValue
        return this
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