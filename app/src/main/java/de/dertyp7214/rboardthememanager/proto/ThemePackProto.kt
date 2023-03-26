package de.dertyp7214.rboardthememanager.proto

import com.github.os72.protobuf.dynamic.DynamicSchema
import com.github.os72.protobuf.dynamic.MessageDefinition
import com.google.protobuf.DynamicMessage
import de.dertyp7214.rboardthememanager.data.ThemePack

object ThemePackProto {
    private val objectDef = MessageDefinition.newBuilder("Object")
        .addField("required", "string", "url", 1)
        .addField("required", "string", "author", 2)
        .addField("repeated", "string", "tags", 3)
        .addField("repeated", "string", "themes", 4)
        .addField("required", "int64", "size", 5)
        .addField("optional", "int64", "date", 6)
        .addField("optional", "string", "name", 7)
        .addField("optional", "string", "description", 8)
        .addField("optional", "string", "hash", 9)

    private val objectListDef = MessageDefinition.newBuilder("ObjectList")
        .addField("repeated", "Object", "objects", 1)

    private val schemaBuilder = DynamicSchema.newBuilder().apply {
        setName("ThemePack")
        addMessageDefinition(objectDef.build())
        addMessageDefinition(objectListDef.build())
    }

    private val schema = schemaBuilder.build()

    @Suppress("UNCHECKED_CAST")
    fun parsePacks(data: ByteArray): List<ThemePack> {
        val objectList = schema.newMessageBuilder("ObjectList").mergeFrom(data).build()
        val objectListDef = objectList.descriptorForType

        val list = ArrayList<ThemePack>()

        var index = 0
        val count = objectList.getRepeatedFieldCount(objectListDef.findFieldByName("objects"))

        while (index < count) {
            val obj = schema.newMessageBuilder("Object").mergeFrom(
                objectList.getRepeatedField(
                    objectListDef.findFieldByName("objects"),
                    index
                ) as DynamicMessage
            ).build()
            val objectDef = obj.descriptorForType

            list.add(
                ThemePack(
                    obj.getField(objectDef.findFieldByName("author")) as String,
                    obj.getField(objectDef.findFieldByName("url")) as String,
                    obj.getField(objectDef.findFieldByName("hash")) as String?,
                    obj.getField(objectDef.findFieldByName("name")) as String,
                    obj.getField(objectDef.findFieldByName("tags")) as List<String>,
                    obj.getField(objectDef.findFieldByName("themes")) as List<String>,
                    obj.getField(objectDef.findFieldByName("size")) as Long,
                    obj.getField(objectDef.findFieldByName("description")) as String?,
                    false,
                    obj.getField(objectDef.findFieldByName("date")) as Long
                )
            )
            index++
        }

        return list
    }
}