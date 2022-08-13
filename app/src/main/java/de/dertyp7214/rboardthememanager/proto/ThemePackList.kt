package de.dertyp7214.rboardthememanager.proto

import com.google.protobuf.*
import java.io.IOException
import java.util.*

object ThemePackList {
    private val descriptorData = arrayOf(
        "\n\u0008my.proto\u0012\u0006rboard\"-\n\nObjectList\u0012\u001f\n\u0007obje" +
                "cts\u0018\u0001 \u0003(\u000b2\u000e.rboard.Object\"\u0097\u0001\n\u0006Object\u0012\u000b\n\u0003" +
                "url\u0018\u0001 \u0001(\t\u0012\u000e\n\u0006author\u0018\u0002 \u0001(\t\u0012\u000c\n\u0004tags\u0018\u0003 \u0003(\t\u0012" +
                "\u000e\n\u0006themes\u0018\u0004 \u0003(\t\u0012\u000c\n\u0004size\u0018\u0005 \u0001(\u0003\u0012\u000c\n\u0004date\u0018\u0006 " +
                "\u0001(\u0003\u0012\u000c\n\u0004name\u0018\u0007 \u0001(\t\u0012\u0018\n\u000bdescription\u0018\u0008 \u0001(\tH\u0000" +
                "\u0088\u0001\u0001B\u000e\n\u000c_descriptionb\u0006proto3"
    )
    private val descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
        descriptorData,
        arrayOf()
    )
    private val internal_static_rboard_ObjectList_descriptor = getDescriptor()!!
        .messageTypes[0]
    private val internal_static_rboard_ObjectList_fieldAccessorTable =
        GeneratedMessageV3.FieldAccessorTable(
            internal_static_rboard_ObjectList_descriptor, arrayOf("Objects")
        )
    private val internal_static_rboard_Object_descriptor = getDescriptor()!!
        .messageTypes[1]
    private val internal_static_rboard_Object_fieldAccessorTable =
        GeneratedMessageV3.FieldAccessorTable(
            internal_static_rboard_Object_descriptor,
            arrayOf(
                "Url",
                "Author",
                "Tags",
                "Themes",
                "Size",
                "Date",
                "Name",
                "Description",
                "Description"
            )
        )

    private fun getDescriptor(): Descriptors.FileDescriptor? {
        return descriptor
    }


    interface ObjectListOrBuilder : MessageOrBuilder {
        /**
         * `repeated .rboard.Object objects = 1;`
         */
        val objectsList: List<Object?>?

        /**
         * `repeated .rboard.Object objects = 1;`
         */
        fun getObjects(index: Int): Object?

        /**
         * `repeated .rboard.Object objects = 1;`
         */
        val objectsCount: Int

        /**
         * `repeated .rboard.Object objects = 1;`
         */
        val objectsOrBuilderList: List<ObjectOrBuilder?>?

        /**
         * `repeated .rboard.Object objects = 1;`
         */
        fun getObjectsOrBuilder(
            index: Int
        ): ObjectOrBuilder?
    }

    /**
     * Protobuf type `rboard.ObjectList`
     */
    class ObjectList : GeneratedMessageV3, ObjectListOrBuilder {
        // Use ObjectList.newBuilder() to construct.
        private constructor(builder: GeneratedMessageV3.Builder<*>) : super(builder) {}
        private constructor() {
            objectsList = arrayListOf()
        }

        override fun newInstance(
            unused: UnusedPrivateParameter
        ): Any {
            return ObjectList()
        }

        override fun getUnknownFields(): UnknownFieldSet {
            return unknownFields
        }

        private constructor(
            input: CodedInputStream,
            extensionRegistry: ExtensionRegistryLite?
        ) : this() {
            if (extensionRegistry == null) {
                throw NullPointerException()
            }
            var mutable_bitField0_ = 0
            val unknownFields = UnknownFieldSet.newBuilder()
            try {
                var done = false
                while (!done) {
                    when (val tag = input.readTag()) {
                        0 -> done = true
                        10 -> {
                            if ((mutable_bitField0_ and 0x00000001) == 0) {
                                objectsList = ArrayList()
                                mutable_bitField0_ = mutable_bitField0_ or 0x00000001
                            }
                            objectsList?.add(
                                input.readMessage(Object.parser(), extensionRegistry)
                            )
                        }
                        else -> {
                            if (!parseUnknownField(
                                    input, unknownFields, extensionRegistry, tag
                                )
                            ) {
                                done = true
                            }
                        }
                    }
                }
            } catch (e: InvalidProtocolBufferException) {
                throw e.setUnfinishedMessage(this)
            } catch (e: IOException) {
                throw InvalidProtocolBufferException(
                    e
                ).setUnfinishedMessage(this)
            } finally {
                if (((mutable_bitField0_ and 0x00000001) != 0)) {
                    objectsList = objectsList
                }
                this.unknownFields = unknownFields.build()
                makeExtensionsImmutable()
            }
        }

        override fun internalGetFieldAccessorTable(): FieldAccessorTable {
            return internal_static_rboard_ObjectList_fieldAccessorTable
                .ensureFieldAccessorsInitialized(
                    ObjectList::class.java, Builder::class.java
                )
        }

        /**
         * `repeated .rboard.Object objects = 1;`
         */
        override var objectsList: ArrayList<Object>? = null
            private set

        /**
         * `repeated .rboard.Object objects = 1;`
         */
        override val objectsOrBuilderList: List<ObjectOrBuilder?>?
            get() = objectsList

        /**
         * `repeated .rboard.Object objects = 1;`
         */
        override fun getObjects(index: Int): Object {
            return objectsList!![index]
        }

        override val objectsCount: Int = objectsList?.size ?: 0

        /**
         * `repeated .rboard.Object objects = 1;`
         */
        override fun getObjectsOrBuilder(
            index: Int
        ): ObjectOrBuilder {
            return objectsList!![index]
        }

        private var memoizedIsInitialized: Byte = -1
        override fun isInitialized(): Boolean {
            val isInitialized = memoizedIsInitialized
            if (isInitialized.toInt() == 1) return true
            if (isInitialized.toInt() == 0) return false
            memoizedIsInitialized = 1
            return true
        }

        @Throws(IOException::class)
        override fun writeTo(output: CodedOutputStream) {
            for (i in objectsList!!.indices) {
                output.writeMessage(1, objectsList!![i])
            }
            unknownFields.writeTo(output)
        }

        override fun getSerializedSize(): Int {
            var size = memoizedSize
            if (size != -1) return size
            size = 0
            for (i in objectsList!!.indices) {
                size += CodedOutputStream
                    .computeMessageSize(1, objectsList!![i])
            }
            size += unknownFields.serializedSize
            memoizedSize = size
            return size
        }

        override fun equals(other: Any?): Boolean {
            if (other === this) {
                return true
            }
            if (other !is ObjectList) {
                return super.equals(other)
            }
            if (objectsList != other.objectsList) return false
            return unknownFields == other.unknownFields
        }

        override fun hashCode(): Int {
            if (memoizedHashCode != 0) {
                return memoizedHashCode
            }
            var hash = 41
            hash = (19 * hash) + getDescriptor().hashCode()
            if (objectsCount > 0) {
                hash = (37 * hash) + OBJECTS_FIELD_NUMBER
                hash = (53 * hash) + objectsList.hashCode()
            }
            hash = (29 * hash) + unknownFields.hashCode()
            memoizedHashCode = hash
            return hash
        }

        override fun newBuilderForType(): Builder {
            return newBuilder()
        }

        override fun toBuilder(): Builder {
            return if (this === defaultInstance) Builder() else Builder().mergeFrom(this)
        }

        /**
         * Protobuf type `rboard.ObjectList`
         */
        class Builder : GeneratedMessageV3.Builder<Builder?>(),
            ObjectListOrBuilder {
            override fun internalGetFieldAccessorTable(): FieldAccessorTable {
                return internal_static_rboard_ObjectList_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                        ObjectList::class.java, Builder::class.java
                    )
            }

            private fun maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    objectsFieldBuilder
                }
            }

            override fun clear(): Builder {
                super.clear()
                if (objectsBuilder_ == null) {
                    objects_ = arrayListOf()
                    bitField0_ = (bitField0_ and 0x00000001.inv())
                } else {
                    objectsBuilder_!!.clear()
                }
                return this
            }

            override fun getDescriptorForType(): Descriptors.Descriptor {
                return (internal_static_rboard_ObjectList_descriptor)!!
            }

            override fun getDefaultInstanceForType(): ObjectList {
                return defaultInstance
            }

            override fun build(): ObjectList {
                val result = buildPartial()
                if (!result.isInitialized) {
                    throw newUninitializedMessageException(result)
                }
                return result
            }

            override fun buildPartial(): ObjectList {
                val result = ObjectList(this)
                if (objectsBuilder_ == null) {
                    if (((bitField0_ and 0x00000001) != 0)) {
                        bitField0_ = (bitField0_ and 0x00000001.inv())
                    }
                    result.objectsList = objects_
                } else {
                    result.objectsList = objectsBuilder_!!.build() as ArrayList<Object>?
                }
                onBuilt()
                return result
            }

            override fun clone(): Builder {
                return (super.clone())!!
            }

            override fun setField(
                field: Descriptors.FieldDescriptor,
                value: Any
            ): Builder {
                return (super.setField(field, value))!!
            }

            override fun clearField(
                field: Descriptors.FieldDescriptor
            ): Builder {
                return (super.clearField(field))!!
            }

            override fun clearOneof(
                oneof: Descriptors.OneofDescriptor
            ): Builder {
                return (super.clearOneof(oneof))!!
            }

            override fun setRepeatedField(
                field: Descriptors.FieldDescriptor,
                index: Int, value: Any
            ): Builder {
                return (super.setRepeatedField(field, index, value))!!
            }

            override fun addRepeatedField(
                field: Descriptors.FieldDescriptor,
                value: Any
            ): Builder {
                return (super.addRepeatedField(field, value))!!
            }

            override fun mergeFrom(other: Message): Builder {
                return if (other is ObjectList) {
                    mergeFrom(other)
                } else {
                    super.mergeFrom(other)
                    this
                }
            }

            fun mergeFrom(other: ObjectList): Builder {
                if (other === defaultInstance) return this
                if (objectsBuilder_ == null) {
                    if (other.objectsList!!.isNotEmpty()) {
                        if (objects_!!.isEmpty()) {
                            objects_ = other.objectsList
                            bitField0_ = (bitField0_ and 0x00000001.inv())
                        } else {
                            ensureObjectsIsMutable()
                            objects_!!.addAll(other.objectsList!!)
                        }
                        onChanged()
                    }
                } else {
                    if (other.objectsList!!.isNotEmpty()) {
                        if (objectsBuilder_!!.isEmpty) {
                            objectsBuilder_!!.dispose()
                            objectsBuilder_ = null
                            objects_ = other.objectsList
                            bitField0_ = (bitField0_ and 0x00000001.inv())
                            objectsBuilder_ =
                                if (alwaysUseFieldBuilders) objectsFieldBuilder else null
                        } else {
                            objectsBuilder_!!.addAllMessages(other.objectsList)
                        }
                    }
                }
                mergeUnknownFields(other.unknownFields)
                onChanged()
                return this
            }

            override fun isInitialized(): Boolean {
                return true
            }

            @Throws(IOException::class)
            override fun mergeFrom(
                input: CodedInputStream,
                extensionRegistry: ExtensionRegistryLite
            ): Builder {
                var parsedMessage: ObjectList? = null
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
                } catch (e: InvalidProtocolBufferException) {
                    parsedMessage = e.unfinishedMessage as ObjectList
                    throw e.unwrapIOException()
                } finally {
                    parsedMessage?.let { mergeFrom(it) }
                }
                return this
            }

            private var bitField0_ = 0
            private var objects_: ArrayList<Object>? = arrayListOf()
            private fun ensureObjectsIsMutable() {
                if ((bitField0_ and 0x00000001) == 0) {
                    objects_ = objects_?.let { ArrayList(it) }
                    bitField0_ = bitField0_ or 0x00000001
                }
            }

            private var objectsBuilder_: RepeatedFieldBuilderV3<Object, Object.Builder, ObjectOrBuilder>? =
                null

            override val objectsList: List<Object?>?
                get() = if (objectsBuilder_ == null) {
                    objects_?.let { Collections.unmodifiableList(it) }
                } else {
                    objectsBuilder_!!.messageList
                }

            /**
             * `repeated .rboard.Object objects = 1;`
             */
            override fun getObjects(index: Int): Object {
                return if (objectsBuilder_ == null) {
                    objects_!![index]
                } else {
                    objectsBuilder_!!.getMessage(index)
                }
            }

            override val objectsCount: Int
                get() = if (objectsBuilder_ == null) {
                    objects_!!.size
                } else {
                    objectsBuilder_!!.count
                }

            override val objectsOrBuilderList: List<ObjectOrBuilder?>?
                get() = if (objectsBuilder_ != null) {
                    objectsBuilder_!!.messageOrBuilderList
                } else {
                    objects_?.let { Collections.unmodifiableList(it) }
                }

            /**
             * `repeated .rboard.Object objects = 1;`
             */
            override fun getObjectsOrBuilder(
                index: Int
            ): ObjectOrBuilder {
                return if (objectsBuilder_ == null) {
                    objects_!![index]
                } else {
                    objectsBuilder_!!.getMessageOrBuilder(index)
                }
            }

            private val objectsFieldBuilder: RepeatedFieldBuilderV3<Object, Object.Builder, ObjectOrBuilder>
                get() {
                    if (objectsBuilder_ == null) {
                        objectsBuilder_ = RepeatedFieldBuilderV3(
                            objects_,
                            ((bitField0_ and 0x00000001) != 0),
                            parentForChildren,
                            isClean
                        )
                        objects_ = null
                    }
                    return objectsBuilder_!!
                }

            override fun setUnknownFields(
                unknownFields: UnknownFieldSet
            ): Builder {
                return (super.setUnknownFields(unknownFields))!!
            }

            override fun mergeUnknownFields(
                unknownFields: UnknownFieldSet
            ): Builder {
                return (super.mergeUnknownFields(unknownFields))!!
            } // @@protoc_insertion_point(builder_scope:rboard.ObjectList)

            init {
                maybeForceBuilderInitialization()
            }
        }

        override fun getParserForType(): Parser<ObjectList> {
            return PARSER
        }

        override fun newBuilderForType(parent: BuilderParent?) = newBuilderForType()

        override fun getDefaultInstanceForType(): ObjectList {
            return defaultInstance
        }

        companion object {
            private const val serialVersionUID = 0L
            fun getDescriptor(): Descriptors.Descriptor? {
                return internal_static_rboard_ObjectList_descriptor
            }

            const val OBJECTS_FIELD_NUMBER = 1

            @Throws(InvalidProtocolBufferException::class)
            fun parseFrom(data: ByteArray?): ObjectList {
                return PARSER.parseFrom(data)
            }

            fun newBuilder(): Builder {
                return defaultInstance.toBuilder()
            }

            // @@protoc_insertion_point(class_scope:rboard.ObjectList)
            val defaultInstance: ObjectList = ObjectList()

            private val PARSER: Parser<ObjectList> = object : AbstractParser<ObjectList>() {
                @Throws(InvalidProtocolBufferException::class)
                override fun parsePartialFrom(
                    input: CodedInputStream,
                    extensionRegistry: ExtensionRegistryLite
                ): ObjectList {
                    return ObjectList(input, extensionRegistry)
                }
            }
        }
    }

    interface ObjectOrBuilder : MessageOrBuilder {
        /**
         * `string url = 1;`
         *
         * @return The url.
         */
        fun getUrl(): String?

        /**
         * `string url = 1;`
         *
         * @return The bytes for url.
         */
        fun getUrlBytes(): ByteString?

        /**
         * `string author = 2;`
         *
         * @return The author.
         */
        fun getAuthor(): String?

        /**
         * `string author = 2;`
         *
         * @return The bytes for author.
         */
        fun getAuthorBytes(): ByteString?

        /**
         * `repeated string tags = 3;`
         *
         * @return A list containing the tags.
         */
        fun getTagsList(): List<String?>?

        /**
         * `repeated string tags = 3;`
         *
         * @return The count of tags.
         */
        fun getTagsCount(): Int

        /**
         * `repeated string tags = 3;`
         *
         * @param index The index of the element to return.
         * @return The tags at the given index.
         */
        fun getTags(index: Int): String?

        /**
         * `repeated string tags = 3;`
         *
         * @param index The index of the value to return.
         * @return The bytes of the tags at the given index.
         */
        fun getTagsBytes(index: Int): ByteString?

        /**
         * `repeated string themes = 4;`
         *
         * @return A list containing the themes.
         */
        fun getThemesList(): List<String?>?

        /**
         * `repeated string themes = 4;`
         *
         * @return The count of themes.
         */
        fun getThemesCount(): Int

        /**
         * `repeated string themes = 4;`
         *
         * @param index The index of the element to return.
         * @return The themes at the given index.
         */
        fun getThemes(index: Int): String?

        /**
         * `repeated string themes = 4;`
         *
         * @param index The index of the value to return.
         * @return The bytes of the themes at the given index.
         */
        fun getThemesBytes(index: Int): ByteString?

        /**
         * `int64 size = 5;`
         *
         * @return The size.
         */
        fun getSize(): Long

        /**
         * `int64 date = 6;`
         *
         * @return The date.
         */
        fun getDate(): Long

        /**
         * `string name = 7;`
         *
         * @return The name.
         */
        fun getName(): String?

        /**
         * `string name = 7;`
         *
         * @return The bytes for name.
         */
        fun getNameBytes(): ByteString?

        /**
         * `string description = 8;`
         *
         * @return Whether the description field is set.
         */
        fun hasDescription(): Boolean

        /**
         * `string description = 8;`
         *
         * @return The description.
         */
        fun getDescription(): String?

        /**
         * `string description = 8;`
         *
         * @return The bytes for description.
         */
        fun getDescriptionBytes(): ByteString?
    }

    /**
     * Protobuf type `rboard.Object`
     */
    class Object : GeneratedMessageV3, ObjectOrBuilder {
        // Use Object.newBuilder() to construct.
        private constructor(builder: GeneratedMessageV3.Builder<*>) : super(builder) {}
        private constructor() {
            url_ = ""
            author_ = ""
            tags_ = LazyStringArrayList.EMPTY
            themes_ = LazyStringArrayList.EMPTY
            name_ = ""
            description_ = ""
        }

        override fun newInstance(
            unused: UnusedPrivateParameter
        ): Any {
            return Object()
        }

        override fun getUnknownFields(): UnknownFieldSet {
            return unknownFields
        }

        private constructor(
            input: CodedInputStream,
            extensionRegistry: ExtensionRegistryLite?
        ) : this() {
            if (extensionRegistry == null) {
                throw NullPointerException()
            }
            var mutable_bitField0_ = 0
            val unknownFields = UnknownFieldSet.newBuilder()
            try {
                var done = false
                while (!done) {
                    val tag = input.readTag()
                    when (tag) {
                        0 -> done = true
                        10 -> {
                            val s = input.readStringRequireUtf8()
                            url_ = s
                        }
                        18 -> {
                            val s = input.readStringRequireUtf8()
                            author_ = s
                        }
                        26 -> {
                            val s = input.readStringRequireUtf8()
                            if ((mutable_bitField0_ and 0x00000001) == 0) {
                                tags_ = LazyStringArrayList()
                                mutable_bitField0_ = mutable_bitField0_ or 0x00000001
                            }
                            tags_!!.add(s)
                        }
                        34 -> {
                            val s = input.readStringRequireUtf8()
                            if ((mutable_bitField0_ and 0x00000002) == 0) {
                                themes_ = LazyStringArrayList()
                                mutable_bitField0_ = mutable_bitField0_ or 0x00000002
                            }
                            themes_!!.add(s)
                        }
                        40 -> {
                            size_ = input.readInt64()
                        }
                        48 -> {
                            date_ = input.readInt64()
                        }
                        58 -> {
                            val s = input.readStringRequireUtf8()
                            name_ = s
                        }
                        66 -> {
                            val s = input.readStringRequireUtf8()
                            bitField0_ = bitField0_ or 0x00000001
                            description_ = s
                        }
                        else -> {
                            if (!parseUnknownField(
                                    input, unknownFields, extensionRegistry, tag
                                )
                            ) {
                                done = true
                            }
                        }
                    }
                }
            } catch (e: InvalidProtocolBufferException) {
                throw e.setUnfinishedMessage(this)
            } catch (e: IOException) {
                throw InvalidProtocolBufferException(
                    e
                ).setUnfinishedMessage(this)
            } finally {
                if (((mutable_bitField0_ and 0x00000001) != 0)) {
                    tags_ = tags_!!.unmodifiableView
                }
                if (((mutable_bitField0_ and 0x00000002) != 0)) {
                    themes_ = themes_!!.unmodifiableView
                }
                this.unknownFields = unknownFields.build()
                makeExtensionsImmutable()
            }
        }

        override fun internalGetFieldAccessorTable(): FieldAccessorTable {
            return internal_static_rboard_Object_fieldAccessorTable
                .ensureFieldAccessorsInitialized(
                    Object::class.java, Builder::class.java
                )
        }

        private var bitField0_ = 0

        @Volatile
        private var url_: Any? = null

        /**
         * `string url = 1;`
         *
         * @return The url.
         */
        override fun getUrl(): String {
            val ref = url_
            return if (ref is String) {
                ref
            } else {
                val bs = ref as ByteString
                val s = bs.toStringUtf8()
                url_ = s
                s
            }
        }

        /**
         * `string url = 1;`
         *
         * @return The bytes for url.
         */
        override fun getUrlBytes(): ByteString? {
            val ref = url_
            return if (ref is String) {
                val b = ByteString.copyFromUtf8(
                    ref as String?
                )
                url_ = b
                b
            } else {
                ref as ByteString?
            }
        }

        @Volatile
        private var author_: Any? = null

        /**
         * `string author = 2;`
         *
         * @return The author.
         */
        override fun getAuthor(): String {
            val ref = author_
            return if (ref is String) {
                ref
            } else {
                val bs = ref as ByteString
                val s = bs.toStringUtf8()
                author_ = s
                s
            }
        }

        /**
         * `string author = 2;`
         *
         * @return The bytes for author.
         */
        override fun getAuthorBytes(): ByteString? {
            val ref = author_
            return if (ref is String) {
                val b = ByteString.copyFromUtf8(
                    ref as String?
                )
                author_ = b
                b
            } else {
                ref as ByteString?
            }
        }

        private var tags_: LazyStringList? = null

        /**
         * `repeated string tags = 3;`
         *
         * @return A list containing the tags.
         */
        override fun getTagsList(): ProtocolStringList? {
            return tags_
        }

        /**
         * `repeated string tags = 3;`
         *
         * @return The count of tags.
         */
        override fun getTagsCount(): Int {
            return tags_!!.size
        }

        /**
         * `repeated string tags = 3;`
         *
         * @param index The index of the element to return.
         * @return The tags at the given index.
         */
        override fun getTags(index: Int): String {
            return tags_!![index]
        }

        /**
         * `repeated string tags = 3;`
         *
         * @param index The index of the value to return.
         * @return The bytes of the tags at the given index.
         */
        override fun getTagsBytes(index: Int): ByteString {
            return tags_!!.getByteString(index)
        }

        private var themes_: LazyStringList? = null

        /**
         * `repeated string themes = 4;`
         *
         * @return A list containing the themes.
         */
        override fun getThemesList(): ProtocolStringList? {
            return themes_
        }

        /**
         * `repeated string themes = 4;`
         *
         * @return The count of themes.
         */
        override fun getThemesCount(): Int {
            return themes_!!.size
        }

        /**
         * `repeated string themes = 4;`
         *
         * @param index The index of the element to return.
         * @return The themes at the given index.
         */
        override fun getThemes(index: Int): String {
            return themes_!![index]
        }

        /**
         * `repeated string themes = 4;`
         *
         * @param index The index of the value to return.
         * @return The bytes of the themes at the given index.
         */
        override fun getThemesBytes(index: Int): ByteString {
            return themes_!!.getByteString(index)
        }

        private var size_: Long = 0

        /**
         * `int64 size = 5;`
         *
         * @return The size.
         */
        override fun getSize(): Long {
            return size_
        }

        private var date_: Long = 0

        /**
         * `int64 date = 6;`
         *
         * @return The date.
         */
        override fun getDate(): Long {
            return date_
        }

        @Volatile
        private var name_: Any? = null

        /**
         * `string name = 7;`
         *
         * @return The name.
         */
        override fun getName(): String {
            val ref = name_
            return if (ref is String) {
                ref
            } else {
                val bs = ref as ByteString
                val s = bs.toStringUtf8()
                name_ = s
                s
            }
        }

        /**
         * `string name = 7;`
         *
         * @return The bytes for name.
         */
        override fun getNameBytes(): ByteString? {
            val ref = name_
            return if (ref is String) {
                val b = ByteString.copyFromUtf8(
                    ref as String?
                )
                name_ = b
                b
            } else {
                ref as ByteString?
            }
        }

        @Volatile
        private var description_: Any? = null

        /**
         * `string description = 8;`
         *
         * @return Whether the description field is set.
         */
        override fun hasDescription(): Boolean {
            return ((bitField0_ and 0x00000001) != 0)
        }

        /**
         * `string description = 8;`
         *
         * @return The description.
         */
        override fun getDescription(): String {
            val ref = description_
            return if (ref is String) {
                ref
            } else {
                val bs = ref as ByteString
                val s = bs.toStringUtf8()
                description_ = s
                s
            }
        }

        /**
         * `string description = 8;`
         *
         * @return The bytes for description.
         */
        override fun getDescriptionBytes(): ByteString? {
            val ref = description_
            return if (ref is String) {
                val b = ByteString.copyFromUtf8(
                    ref as String?
                )
                description_ = b
                b
            } else {
                ref as ByteString?
            }
        }

        private var memoizedIsInitialized: Byte = -1
        override fun isInitialized(): Boolean {
            val isInitialized = memoizedIsInitialized
            if (isInitialized.toInt() == 1) return true
            if (isInitialized.toInt() == 0) return false
            memoizedIsInitialized = 1
            return true
        }

        @Throws(IOException::class)
        override fun writeTo(output: CodedOutputStream) {
            if (!getUrlBytes()!!.isEmpty) {
                writeString(output, 1, url_)
            }
            if (!getAuthorBytes()!!.isEmpty) {
                writeString(output, 2, author_)
            }
            for (i in tags_!!.indices) {
                writeString(output, 3, tags_!!.getRaw(i))
            }
            for (i in themes_!!.indices) {
                writeString(output, 4, themes_!!.getRaw(i))
            }
            if (size_ != 0L) {
                output.writeInt64(5, size_)
            }
            if (date_ != 0L) {
                output.writeInt64(6, date_)
            }
            if (!getNameBytes()!!.isEmpty) {
                writeString(output, 7, name_)
            }
            if (((bitField0_ and 0x00000001) != 0)) {
                writeString(output, 8, description_)
            }
            unknownFields.writeTo(output)
        }

        override fun getSerializedSize(): Int {
            var size = memoizedSize
            if (size != -1) return size
            size = 0
            if (!getUrlBytes()!!.isEmpty) {
                size += computeStringSize(1, url_)
            }
            if (!getAuthorBytes()!!.isEmpty) {
                size += computeStringSize(2, author_)
            }
            run {
                var dataSize = 0
                for (i in tags_!!.indices) {
                    dataSize += computeStringSizeNoTag(
                        tags_!!.getRaw(
                            i
                        )
                    )
                }
                size += dataSize
                size += 1 * getTagsList()!!.size
            }
            run {
                var dataSize = 0
                for (i in themes_!!.indices) {
                    dataSize += computeStringSizeNoTag(
                        themes_!!.getRaw(i)
                    )
                }
                size += dataSize
                size += 1 * getThemesList()!!.size
            }
            if (size_ != 0L) {
                size += CodedOutputStream
                    .computeInt64Size(5, size_)
            }
            if (date_ != 0L) {
                size += CodedOutputStream
                    .computeInt64Size(6, date_)
            }
            if (!getNameBytes()!!.isEmpty) {
                size += computeStringSize(7, name_)
            }
            if (((bitField0_ and 0x00000001) != 0)) {
                size += computeStringSize(8, description_)
            }
            size += unknownFields.serializedSize
            memoizedSize = size
            return size
        }

        override fun equals(other: Any?): Boolean {
            if (other === this) {
                return true
            }
            if (other !is Object) {
                return super.equals(other)
            }
            if (getUrl() != other.getUrl()) return false
            if (getAuthor() != other.getAuthor()) return false
            if (getTagsList() != other.getTagsList()) return false
            if (getThemesList() != other.getThemesList()) return false
            if ((getSize()
                        != other.getSize())
            ) return false
            if ((getDate()
                        != other.getDate())
            ) return false
            if (getName() != other.getName()) return false
            if (hasDescription() != other.hasDescription()) return false
            if (hasDescription()) {
                if (getDescription() != other.getDescription()) return false
            }
            return unknownFields == other.unknownFields
        }

        override fun hashCode(): Int {
            if (memoizedHashCode != 0) {
                return memoizedHashCode
            }
            var hash = 41
            hash = (19 * hash) + getDescriptor().hashCode()
            hash = (37 * hash) + URL_FIELD_NUMBER
            hash = (53 * hash) + getUrl().hashCode()
            hash = (37 * hash) + AUTHOR_FIELD_NUMBER
            hash = (53 * hash) + getAuthor().hashCode()
            if (getTagsCount() > 0) {
                hash = (37 * hash) + TAGS_FIELD_NUMBER
                hash = (53 * hash) + getTagsList().hashCode()
            }
            if (getThemesCount() > 0) {
                hash = (37 * hash) + THEMES_FIELD_NUMBER
                hash = (53 * hash) + getThemesList().hashCode()
            }
            hash = (37 * hash) + SIZE_FIELD_NUMBER
            hash = (53 * hash) + Internal.hashLong(
                getSize()
            )
            hash = (37 * hash) + DATE_FIELD_NUMBER
            hash = (53 * hash) + Internal.hashLong(
                getDate()
            )
            hash = (37 * hash) + NAME_FIELD_NUMBER
            hash = (53 * hash) + getName().hashCode()
            if (hasDescription()) {
                hash = (37 * hash) + DESCRIPTION_FIELD_NUMBER
                hash = (53 * hash) + getDescription().hashCode()
            }
            hash = (29 * hash) + unknownFields.hashCode()
            memoizedHashCode = hash
            return hash
        }

        override fun newBuilderForType(): Builder {
            return newBuilder()
        }

        override fun toBuilder(): Builder {
            return if (this === DEFAULT_INSTANCE) Builder() else Builder().mergeFrom(this)
        }

        override fun newBuilderForType(parent: BuilderParent) = newBuilderForType()

        /**
         * Protobuf type `rboard.Object`
         */
        class Builder : GeneratedMessageV3.Builder<Builder>(),
            ObjectOrBuilder {
            override fun internalGetFieldAccessorTable(): FieldAccessorTable {
                return internal_static_rboard_Object_fieldAccessorTable.ensureFieldAccessorsInitialized(
                    Object::class.java, Builder::class.java
                )
            }

            override fun clear(): Builder {
                super.clear()
                url_ = ""
                author_ = ""
                tags_ = LazyStringArrayList.EMPTY
                bitField0_ = (bitField0_ and 0x00000001.inv())
                themes_ = LazyStringArrayList.EMPTY
                bitField0_ = (bitField0_ and 0x00000002.inv())
                size_ = 0L
                date_ = 0L
                name_ = ""
                description_ = ""
                bitField0_ = (bitField0_ and 0x00000004.inv())
                return this
            }

            override fun getDescriptorForType(): Descriptors.Descriptor {
                return (internal_static_rboard_Object_descriptor)!!
            }

            override fun getDefaultInstanceForType(): Object {
                return getDefaultInstance()
            }

            override fun build(): Object {
                val result = buildPartial()
                if (!result.isInitialized) {
                    throw newUninitializedMessageException(result)
                }
                return result
            }

            override fun buildPartial(): Object {
                val result = Object(this)
                val from_bitField0_ = bitField0_
                var to_bitField0_ = 0
                result.url_ = url_
                result.author_ = author_
                if (((bitField0_ and 0x00000001) != 0)) {
                    tags_ = tags_!!.unmodifiableView
                    bitField0_ = (bitField0_ and 0x00000001.inv())
                }
                result.tags_ = tags_
                if (((bitField0_ and 0x00000002) != 0)) {
                    themes_ = themes_!!.unmodifiableView
                    bitField0_ = (bitField0_ and 0x00000002.inv())
                }
                result.themes_ = themes_
                result.size_ = size_
                result.date_ = date_
                result.name_ = name_
                if (((from_bitField0_ and 0x00000004) != 0)) {
                    to_bitField0_ = to_bitField0_ or 0x00000001
                }
                result.description_ = description_
                result.bitField0_ = to_bitField0_
                onBuilt()
                return result
            }

            override fun clone(): Builder {
                return (super.clone())!!
            }

            override fun setField(
                field: Descriptors.FieldDescriptor,
                value: Any
            ): Builder {
                return (super.setField(field, value))!!
            }

            override fun clearField(
                field: Descriptors.FieldDescriptor
            ): Builder {
                return (super.clearField(field))!!
            }

            override fun clearOneof(
                oneof: Descriptors.OneofDescriptor
            ): Builder {
                return (super.clearOneof(oneof))!!
            }

            override fun setRepeatedField(
                field: Descriptors.FieldDescriptor,
                index: Int, value: Any
            ): Builder {
                return (super.setRepeatedField(field, index, value))!!
            }

            override fun addRepeatedField(
                field: Descriptors.FieldDescriptor,
                value: Any
            ): Builder {
                return (super.addRepeatedField(field, value))!!
            }

            override fun mergeFrom(other: Message): Builder {
                return if (other is Object) {
                    mergeFrom(other)
                } else {
                    super.mergeFrom(other)
                    this
                }
            }

            fun mergeFrom(other: Object): Builder {
                if (other === getDefaultInstance()) return this
                if (other.getUrl().isNotEmpty()) {
                    url_ = other.url_
                    onChanged()
                }
                if (other.getAuthor().isNotEmpty()) {
                    author_ = other.author_
                    onChanged()
                }
                if (other.tags_!!.isNotEmpty()) {
                    if (tags_!!.isEmpty()) {
                        tags_ = other.tags_
                        bitField0_ = (bitField0_ and 0x00000001.inv())
                    } else {
                        ensureTagsIsMutable()
                        tags_!!.addAll((other.tags_)!!)
                    }
                    onChanged()
                }
                if (other.themes_!!.isNotEmpty()) {
                    if (themes_!!.isEmpty()) {
                        themes_ = other.themes_
                        bitField0_ = (bitField0_ and 0x00000002.inv())
                    } else {
                        ensureThemesIsMutable()
                        themes_!!.addAll((other.themes_)!!)
                    }
                    onChanged()
                }
                if (other.getSize() != 0L) {
                    setSize(other.getSize())
                }
                if (other.getDate() != 0L) {
                    setDate(other.getDate())
                }
                if (other.getName().isNotEmpty()) {
                    name_ = other.name_
                    onChanged()
                }
                if (other.hasDescription()) {
                    bitField0_ = bitField0_ or 0x00000004
                    description_ = other.description_
                    onChanged()
                }
                mergeUnknownFields(other.unknownFields)
                onChanged()
                return this
            }

            override fun isInitialized(): Boolean {
                return true
            }

            @Throws(IOException::class)
            override fun mergeFrom(
                input: CodedInputStream,
                extensionRegistry: ExtensionRegistryLite
            ): Builder {
                var parsedMessage: Object? = null
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
                } catch (e: InvalidProtocolBufferException) {
                    parsedMessage = e.unfinishedMessage as Object
                    throw e.unwrapIOException()
                } finally {
                    parsedMessage?.let { mergeFrom(it) }
                }
                return this
            }

            private var bitField0_ = 0
            private var url_: Any? = ""

            /**
             * `string url = 1;`
             *
             * @return The url.
             */
            override fun getUrl(): String {
                val ref = url_
                return if (ref !is String) {
                    val bs = ref as ByteString
                    val s = bs.toStringUtf8()
                    url_ = s
                    s
                } else {
                    ref
                }
            }

            /**
             * `string url = 1;`
             *
             * @return The bytes for url.
             */
            override fun getUrlBytes(): ByteString {
                val ref = (url_)!!
                return if (ref is String) {
                    val b = ByteString.copyFromUtf8(
                        ref
                    )
                    url_ = b
                    b
                } else {
                    ref as ByteString
                }
            }

            private var author_: Any? = ""

            /**
             * `string author = 2;`
             *
             * @return The author.
             */
            override fun getAuthor(): String {
                val ref = author_
                return if (ref !is String) {
                    val bs = ref as ByteString
                    val s = bs.toStringUtf8()
                    author_ = s
                    s
                } else {
                    ref
                }
            }

            /**
             * `string author = 2;`
             *
             * @return The bytes for author.
             */
            override fun getAuthorBytes(): ByteString {
                val ref = (author_)!!
                return if (ref is String) {
                    val b = ByteString.copyFromUtf8(
                        ref
                    )
                    author_ = b
                    b
                } else {
                    ref as ByteString
                }
            }

            private var tags_ = LazyStringArrayList.EMPTY
            private fun ensureTagsIsMutable() {
                if ((bitField0_ and 0x00000001) == 0) {
                    tags_ = LazyStringArrayList(tags_)
                    bitField0_ = bitField0_ or 0x00000001
                }
            }

            /**
             * `repeated string tags = 3;`
             *
             * @return A list containing the tags.
             */
            override fun getTagsList(): ProtocolStringList {
                return tags_!!.unmodifiableView
            }

            /**
             * `repeated string tags = 3;`
             *
             * @return The count of tags.
             */
            override fun getTagsCount(): Int {
                return tags_!!.size
            }

            /**
             * `repeated string tags = 3;`
             *
             * @param index The index of the element to return.
             * @return The tags at the given index.
             */
            override fun getTags(index: Int): String {
                return tags_!![index]
            }

            /**
             * `repeated string tags = 3;`
             *
             * @param index The index of the value to return.
             * @return The bytes of the tags at the given index.
             */
            override fun getTagsBytes(index: Int): ByteString {
                return tags_!!.getByteString(index)
            }

            private var themes_ = LazyStringArrayList.EMPTY
            private fun ensureThemesIsMutable() {
                if ((bitField0_ and 0x00000002) == 0) {
                    themes_ = LazyStringArrayList(themes_)
                    bitField0_ = bitField0_ or 0x00000002
                }
            }

            /**
             * `repeated string themes = 4;`
             *
             * @return A list containing the themes.
             */
            override fun getThemesList(): ProtocolStringList {
                return themes_!!.unmodifiableView
            }

            /**
             * `repeated string themes = 4;`
             *
             * @return The count of themes.
             */
            override fun getThemesCount(): Int {
                return themes_!!.size
            }

            /**
             * `repeated string themes = 4;`
             *
             * @param index The index of the element to return.
             * @return The themes at the given index.
             */
            override fun getThemes(index: Int): String {
                return themes_!![index]
            }

            /**
             * `repeated string themes = 4;`
             *
             * @param index The index of the value to return.
             * @return The bytes of the themes at the given index.
             */
            override fun getThemesBytes(index: Int): ByteString {
                return themes_!!.getByteString(index)
            }

            private var size_: Long = 0

            /**
             * `int64 size = 5;`
             *
             * @return The size.
             */
            override fun getSize(): Long {
                return size_
            }

            /**
             * `int64 size = 5;`
             *
             * @param value The size to set.
             * @return This builder for chaining.
             */
            private fun setSize(value: Long): Builder {
                size_ = value
                onChanged()
                return this
            }

            private var date_: Long = 0

            /**
             * `int64 date = 6;`
             *
             * @return The date.
             */
            override fun getDate(): Long {
                return date_
            }

            /**
             * `int64 date = 6;`
             *
             * @param value The date to set.
             * @return This builder for chaining.
             */
            private fun setDate(value: Long): Builder {
                date_ = value
                onChanged()
                return this
            }

            private var name_: Any? = ""

            /**
             * `string name = 7;`
             *
             * @return The name.
             */
            override fun getName(): String {
                val ref = name_
                return if (ref !is String) {
                    val bs = ref as ByteString
                    val s = bs.toStringUtf8()
                    name_ = s
                    s
                } else {
                    ref
                }
            }

            /**
             * `string name = 7;`
             *
             * @return The bytes for name.
             */
            override fun getNameBytes(): ByteString {
                val ref = (name_)!!
                return if (ref is String) {
                    val b = ByteString.copyFromUtf8(
                        ref
                    )
                    name_ = b
                    b
                } else {
                    ref as ByteString
                }
            }

            private var description_: Any? = ""

            /**
             * `string description = 8;`
             *
             * @return Whether the description field is set.
             */
            override fun hasDescription(): Boolean {
                return ((bitField0_ and 0x00000004) != 0)
            }

            /**
             * `string description = 8;`
             *
             * @return The description.
             */
            override fun getDescription(): String {
                val ref = description_
                return if (ref !is String) {
                    val bs = ref as ByteString
                    val s = bs.toStringUtf8()
                    description_ = s
                    s
                } else {
                    ref
                }
            }

            /**
             * `string description = 8;`
             *
             * @return The bytes for description.
             */
            override fun getDescriptionBytes(): ByteString {
                val ref = (description_)!!
                return if (ref is String) {
                    val b = ByteString.copyFromUtf8(
                        ref
                    )
                    description_ = b
                    b
                } else {
                    ref as ByteString
                }
            }

            override fun setUnknownFields(
                unknownFields: UnknownFieldSet
            ): Builder {
                return (super.setUnknownFields(unknownFields))!!
            }

            override fun mergeUnknownFields(
                unknownFields: UnknownFieldSet
            ): Builder {
                return (super.mergeUnknownFields(unknownFields))!!
            } // @@protoc_insertion_point(builder_scope:rboard.Object)
        }

        override fun getParserForType(): Parser<Object> {
            return PARSER
        }

        override fun getDefaultInstanceForType(): Object {
            return DEFAULT_INSTANCE
        }

        companion object {
            private val serialVersionUID = 0L
            fun getDescriptor(): Descriptors.Descriptor? {
                return internal_static_rboard_Object_descriptor
            }

            val URL_FIELD_NUMBER = 1
            val AUTHOR_FIELD_NUMBER = 2
            val TAGS_FIELD_NUMBER = 3
            val THEMES_FIELD_NUMBER = 4
            val SIZE_FIELD_NUMBER = 5
            val DATE_FIELD_NUMBER = 6
            val NAME_FIELD_NUMBER = 7
            val DESCRIPTION_FIELD_NUMBER = 8

            fun newBuilder(): Builder {
                return DEFAULT_INSTANCE.toBuilder()
            }

            // @@protoc_insertion_point(class_scope:rboard.Object)
            private val DEFAULT_INSTANCE: Object = Object()

            fun getDefaultInstance(): Object {
                return DEFAULT_INSTANCE
            }

            private val PARSER: Parser<Object> = object : AbstractParser<Object>() {
                @Throws(InvalidProtocolBufferException::class)
                override fun parsePartialFrom(
                    input: CodedInputStream,
                    extensionRegistry: ExtensionRegistryLite
                ): Object {
                    return Object(input, extensionRegistry)
                }
            }

            fun parser(): Parser<Object> {
                return PARSER
            }
        }
    }
}