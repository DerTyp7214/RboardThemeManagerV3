package de.dertyp7214.rboardthememanager.proto;

public final class My {
    private My() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public interface ObjectListOrBuilder extends
            // @@protoc_insertion_point(interface_extends:rboard.ObjectList)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        java.util.List<de.dertyp7214.rboardthememanager.proto.My.Object>
        getObjectsList();

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        de.dertyp7214.rboardthememanager.proto.My.Object getObjects(int index);

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        int getObjectsCount();

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        java.util.List<? extends de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder>
        getObjectsOrBuilderList();

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder getObjectsOrBuilder(
                int index);
    }

    /**
     * Protobuf type {@code rboard.ObjectList}
     */
    public static final class ObjectList extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:rboard.ObjectList)
            ObjectListOrBuilder {
        private static final long serialVersionUID = 0L;

        // Use ObjectList.newBuilder() to construct.
        private ObjectList(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private ObjectList() {
            objects_ = java.util.Collections.emptyList();
        }

        @java.lang.Override
        @SuppressWarnings({"unused"})
        protected java.lang.Object newInstance(
                UnusedPrivateParameter unused) {
            return new ObjectList();
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return this.unknownFields;
        }

        private ObjectList(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new java.lang.NullPointerException();
            }
            int mutable_bitField0_ = 0;
            com.google.protobuf.UnknownFieldSet.Builder unknownFields =
                    com.google.protobuf.UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        case 10: {
                            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                                objects_ = new java.util.ArrayList<de.dertyp7214.rboardthememanager.proto.My.Object>();
                                mutable_bitField0_ |= 0x00000001;
                            }
                            objects_.add(
                                    input.readMessage(de.dertyp7214.rboardthememanager.proto.My.Object.parser(), extensionRegistry));
                            break;
                        }
                        default: {
                            if (!parseUnknownField(
                                    input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000001) != 0)) {
                    objects_ = java.util.Collections.unmodifiableList(objects_);
                }
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_ObjectList_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_ObjectList_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            de.dertyp7214.rboardthememanager.proto.My.ObjectList.class, de.dertyp7214.rboardthememanager.proto.My.ObjectList.Builder.class);
        }

        public static final int OBJECTS_FIELD_NUMBER = 1;
        private java.util.List<de.dertyp7214.rboardthememanager.proto.My.Object> objects_;

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        @java.lang.Override
        public java.util.List<de.dertyp7214.rboardthememanager.proto.My.Object> getObjectsList() {
            return objects_;
        }

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        @java.lang.Override
        public java.util.List<? extends de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder>
        getObjectsOrBuilderList() {
            return objects_;
        }

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        @java.lang.Override
        public int getObjectsCount() {
            return objects_.size();
        }

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        @java.lang.Override
        public de.dertyp7214.rboardthememanager.proto.My.Object getObjects(int index) {
            return objects_.get(index);
        }

        /**
         * <code>repeated .rboard.Object objects = 1;</code>
         */
        @java.lang.Override
        public de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder getObjectsOrBuilder(
                int index) {
            return objects_.get(index);
        }

        private byte memoizedIsInitialized = -1;

        @java.lang.Override
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            for (int i = 0; i < objects_.size(); i++) {
                output.writeMessage(1, objects_.get(i));
            }
            unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            for (int i = 0; i < objects_.size(); i++) {
                size += com.google.protobuf.CodedOutputStream
                        .computeMessageSize(1, objects_.get(i));
            }
            size += unknownFields.getSerializedSize();
            memoizedSize = size;
            return size;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof de.dertyp7214.rboardthememanager.proto.My.ObjectList)) {
                return super.equals(obj);
            }
            de.dertyp7214.rboardthememanager.proto.My.ObjectList other = (de.dertyp7214.rboardthememanager.proto.My.ObjectList) obj;

            if (!getObjectsList()
                    .equals(other.getObjectsList())) return false;
            if (!unknownFields.equals(other.unknownFields)) return false;
            return true;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            if (getObjectsCount() > 0) {
                hash = (37 * hash) + OBJECTS_FIELD_NUMBER;
                hash = (53 * hash) + getObjectsList().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(de.dertyp7214.rboardthememanager.proto.My.ObjectList prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @java.lang.Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code rboard.ObjectList}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:rboard.ObjectList)
                de.dertyp7214.rboardthememanager.proto.My.ObjectListOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_ObjectList_descriptor;
            }

            @java.lang.Override
            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_ObjectList_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                de.dertyp7214.rboardthememanager.proto.My.ObjectList.class, de.dertyp7214.rboardthememanager.proto.My.ObjectList.Builder.class);
            }

            // Construct using de.dertyp7214.rboardthememanager.proto.My.ObjectList.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                    getObjectsFieldBuilder();
                }
            }

            @java.lang.Override
            public Builder clear() {
                super.clear();
                if (objectsBuilder_ == null) {
                    objects_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000001);
                } else {
                    objectsBuilder_.clear();
                }
                return this;
            }

            @java.lang.Override
            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_ObjectList_descriptor;
            }

            @java.lang.Override
            public de.dertyp7214.rboardthememanager.proto.My.ObjectList getDefaultInstanceForType() {
                return de.dertyp7214.rboardthememanager.proto.My.ObjectList.getDefaultInstance();
            }

            @java.lang.Override
            public de.dertyp7214.rboardthememanager.proto.My.ObjectList build() {
                de.dertyp7214.rboardthememanager.proto.My.ObjectList result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            @java.lang.Override
            public de.dertyp7214.rboardthememanager.proto.My.ObjectList buildPartial() {
                de.dertyp7214.rboardthememanager.proto.My.ObjectList result = new de.dertyp7214.rboardthememanager.proto.My.ObjectList(this);
                int from_bitField0_ = bitField0_;
                if (objectsBuilder_ == null) {
                    if (((bitField0_ & 0x00000001) != 0)) {
                        objects_ = java.util.Collections.unmodifiableList(objects_);
                        bitField0_ = (bitField0_ & ~0x00000001);
                    }
                    result.objects_ = objects_;
                } else {
                    result.objects_ = objectsBuilder_.build();
                }
                onBuilt();
                return result;
            }

            @java.lang.Override
            public Builder clone() {
                return super.clone();
            }

            @java.lang.Override
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return super.setField(field, value);
            }

            @java.lang.Override
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return super.clearField(field);
            }

            @java.lang.Override
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return super.clearOneof(oneof);
            }

            @java.lang.Override
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, java.lang.Object value) {
                return super.setRepeatedField(field, index, value);
            }

            @java.lang.Override
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return super.addRepeatedField(field, value);
            }

            @java.lang.Override
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof de.dertyp7214.rboardthememanager.proto.My.ObjectList) {
                    return mergeFrom((de.dertyp7214.rboardthememanager.proto.My.ObjectList) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(de.dertyp7214.rboardthememanager.proto.My.ObjectList other) {
                if (other == de.dertyp7214.rboardthememanager.proto.My.ObjectList.getDefaultInstance()) return this;
                if (objectsBuilder_ == null) {
                    if (!other.objects_.isEmpty()) {
                        if (objects_.isEmpty()) {
                            objects_ = other.objects_;
                            bitField0_ = (bitField0_ & ~0x00000001);
                        } else {
                            ensureObjectsIsMutable();
                            objects_.addAll(other.objects_);
                        }
                        onChanged();
                    }
                } else {
                    if (!other.objects_.isEmpty()) {
                        if (objectsBuilder_.isEmpty()) {
                            objectsBuilder_.dispose();
                            objectsBuilder_ = null;
                            objects_ = other.objects_;
                            bitField0_ = (bitField0_ & ~0x00000001);
                            objectsBuilder_ =
                                    com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                                            getObjectsFieldBuilder() : null;
                        } else {
                            objectsBuilder_.addAllMessages(other.objects_);
                        }
                    }
                }
                this.mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @java.lang.Override
            public final boolean isInitialized() {
                return true;
            }

            @java.lang.Override
            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                de.dertyp7214.rboardthememanager.proto.My.ObjectList parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (de.dertyp7214.rboardthememanager.proto.My.ObjectList) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            private java.util.List<de.dertyp7214.rboardthememanager.proto.My.Object> objects_ =
                    java.util.Collections.emptyList();

            private void ensureObjectsIsMutable() {
                if (!((bitField0_ & 0x00000001) != 0)) {
                    objects_ = new java.util.ArrayList<de.dertyp7214.rboardthememanager.proto.My.Object>(objects_);
                    bitField0_ |= 0x00000001;
                }
            }

            private com.google.protobuf.RepeatedFieldBuilderV3<
                    de.dertyp7214.rboardthememanager.proto.My.Object, de.dertyp7214.rboardthememanager.proto.My.Object.Builder, de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder> objectsBuilder_;

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public java.util.List<de.dertyp7214.rboardthememanager.proto.My.Object> getObjectsList() {
                if (objectsBuilder_ == null) {
                    return java.util.Collections.unmodifiableList(objects_);
                } else {
                    return objectsBuilder_.getMessageList();
                }
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public int getObjectsCount() {
                if (objectsBuilder_ == null) {
                    return objects_.size();
                } else {
                    return objectsBuilder_.getCount();
                }
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public de.dertyp7214.rboardthememanager.proto.My.Object getObjects(int index) {
                if (objectsBuilder_ == null) {
                    return objects_.get(index);
                } else {
                    return objectsBuilder_.getMessage(index);
                }
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder setObjects(
                    int index, de.dertyp7214.rboardthememanager.proto.My.Object value) {
                if (objectsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureObjectsIsMutable();
                    objects_.set(index, value);
                    onChanged();
                } else {
                    objectsBuilder_.setMessage(index, value);
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder setObjects(
                    int index, de.dertyp7214.rboardthememanager.proto.My.Object.Builder builderForValue) {
                if (objectsBuilder_ == null) {
                    ensureObjectsIsMutable();
                    objects_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    objectsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder addObjects(de.dertyp7214.rboardthememanager.proto.My.Object value) {
                if (objectsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureObjectsIsMutable();
                    objects_.add(value);
                    onChanged();
                } else {
                    objectsBuilder_.addMessage(value);
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder addObjects(
                    int index, de.dertyp7214.rboardthememanager.proto.My.Object value) {
                if (objectsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureObjectsIsMutable();
                    objects_.add(index, value);
                    onChanged();
                } else {
                    objectsBuilder_.addMessage(index, value);
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder addObjects(
                    de.dertyp7214.rboardthememanager.proto.My.Object.Builder builderForValue) {
                if (objectsBuilder_ == null) {
                    ensureObjectsIsMutable();
                    objects_.add(builderForValue.build());
                    onChanged();
                } else {
                    objectsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder addObjects(
                    int index, de.dertyp7214.rboardthememanager.proto.My.Object.Builder builderForValue) {
                if (objectsBuilder_ == null) {
                    ensureObjectsIsMutable();
                    objects_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    objectsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder addAllObjects(
                    java.lang.Iterable<? extends de.dertyp7214.rboardthememanager.proto.My.Object> values) {
                if (objectsBuilder_ == null) {
                    ensureObjectsIsMutable();
                    com.google.protobuf.AbstractMessageLite.Builder.addAll(
                            values, objects_);
                    onChanged();
                } else {
                    objectsBuilder_.addAllMessages(values);
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder clearObjects() {
                if (objectsBuilder_ == null) {
                    objects_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000001);
                    onChanged();
                } else {
                    objectsBuilder_.clear();
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public Builder removeObjects(int index) {
                if (objectsBuilder_ == null) {
                    ensureObjectsIsMutable();
                    objects_.remove(index);
                    onChanged();
                } else {
                    objectsBuilder_.remove(index);
                }
                return this;
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public de.dertyp7214.rboardthememanager.proto.My.Object.Builder getObjectsBuilder(
                    int index) {
                return getObjectsFieldBuilder().getBuilder(index);
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder getObjectsOrBuilder(
                    int index) {
                if (objectsBuilder_ == null) {
                    return objects_.get(index);
                } else {
                    return objectsBuilder_.getMessageOrBuilder(index);
                }
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public java.util.List<? extends de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder>
            getObjectsOrBuilderList() {
                if (objectsBuilder_ != null) {
                    return objectsBuilder_.getMessageOrBuilderList();
                } else {
                    return java.util.Collections.unmodifiableList(objects_);
                }
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public de.dertyp7214.rboardthememanager.proto.My.Object.Builder addObjectsBuilder() {
                return getObjectsFieldBuilder().addBuilder(
                        de.dertyp7214.rboardthememanager.proto.My.Object.getDefaultInstance());
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public de.dertyp7214.rboardthememanager.proto.My.Object.Builder addObjectsBuilder(
                    int index) {
                return getObjectsFieldBuilder().addBuilder(
                        index, de.dertyp7214.rboardthememanager.proto.My.Object.getDefaultInstance());
            }

            /**
             * <code>repeated .rboard.Object objects = 1;</code>
             */
            public java.util.List<de.dertyp7214.rboardthememanager.proto.My.Object.Builder>
            getObjectsBuilderList() {
                return getObjectsFieldBuilder().getBuilderList();
            }

            private com.google.protobuf.RepeatedFieldBuilderV3<
                    de.dertyp7214.rboardthememanager.proto.My.Object, de.dertyp7214.rboardthememanager.proto.My.Object.Builder, de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder>
            getObjectsFieldBuilder() {
                if (objectsBuilder_ == null) {
                    objectsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
                            de.dertyp7214.rboardthememanager.proto.My.Object, de.dertyp7214.rboardthememanager.proto.My.Object.Builder, de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder>(
                            objects_,
                            ((bitField0_ & 0x00000001) != 0),
                            getParentForChildren(),
                            isClean());
                    objects_ = null;
                }
                return objectsBuilder_;
            }

            @java.lang.Override
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.setUnknownFields(unknownFields);
            }

            @java.lang.Override
            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.mergeUnknownFields(unknownFields);
            }


            // @@protoc_insertion_point(builder_scope:rboard.ObjectList)
        }

        // @@protoc_insertion_point(class_scope:rboard.ObjectList)
        private static final de.dertyp7214.rboardthememanager.proto.My.ObjectList DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new de.dertyp7214.rboardthememanager.proto.My.ObjectList();
        }

        public static de.dertyp7214.rboardthememanager.proto.My.ObjectList getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ObjectList>
                PARSER = new com.google.protobuf.AbstractParser<ObjectList>() {
            @java.lang.Override
            public ObjectList parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ObjectList(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ObjectList> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ObjectList> getParserForType() {
            return PARSER;
        }

        @java.lang.Override
        public de.dertyp7214.rboardthememanager.proto.My.ObjectList getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface ObjectOrBuilder extends
            // @@protoc_insertion_point(interface_extends:rboard.Object)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>string url = 1;</code>
         *
         * @return The url.
         */
        java.lang.String getUrl();

        /**
         * <code>string url = 1;</code>
         *
         * @return The bytes for url.
         */
        com.google.protobuf.ByteString
        getUrlBytes();

        /**
         * <code>string author = 2;</code>
         *
         * @return The author.
         */
        java.lang.String getAuthor();

        /**
         * <code>string author = 2;</code>
         *
         * @return The bytes for author.
         */
        com.google.protobuf.ByteString
        getAuthorBytes();

        /**
         * <code>repeated string tags = 3;</code>
         *
         * @return A list containing the tags.
         */
        java.util.List<java.lang.String>
        getTagsList();

        /**
         * <code>repeated string tags = 3;</code>
         *
         * @return The count of tags.
         */
        int getTagsCount();

        /**
         * <code>repeated string tags = 3;</code>
         *
         * @param index The index of the element to return.
         * @return The tags at the given index.
         */
        java.lang.String getTags(int index);

        /**
         * <code>repeated string tags = 3;</code>
         *
         * @param index The index of the value to return.
         * @return The bytes of the tags at the given index.
         */
        com.google.protobuf.ByteString
        getTagsBytes(int index);

        /**
         * <code>repeated string themes = 4;</code>
         *
         * @return A list containing the themes.
         */
        java.util.List<java.lang.String>
        getThemesList();

        /**
         * <code>repeated string themes = 4;</code>
         *
         * @return The count of themes.
         */
        int getThemesCount();

        /**
         * <code>repeated string themes = 4;</code>
         *
         * @param index The index of the element to return.
         * @return The themes at the given index.
         */
        java.lang.String getThemes(int index);

        /**
         * <code>repeated string themes = 4;</code>
         *
         * @param index The index of the value to return.
         * @return The bytes of the themes at the given index.
         */
        com.google.protobuf.ByteString
        getThemesBytes(int index);

        /**
         * <code>int64 size = 5;</code>
         *
         * @return The size.
         */
        long getSize();

        /**
         * <code>int64 date = 6;</code>
         *
         * @return The date.
         */
        long getDate();

        /**
         * <code>string name = 7;</code>
         *
         * @return The name.
         */
        java.lang.String getName();

        /**
         * <code>string name = 7;</code>
         *
         * @return The bytes for name.
         */
        com.google.protobuf.ByteString
        getNameBytes();

        /**
         * <code>string description = 8;</code>
         *
         * @return Whether the description field is set.
         */
        boolean hasDescription();

        /**
         * <code>string description = 8;</code>
         *
         * @return The description.
         */
        java.lang.String getDescription();

        /**
         * <code>string description = 8;</code>
         *
         * @return The bytes for description.
         */
        com.google.protobuf.ByteString
        getDescriptionBytes();
    }

    /**
     * Protobuf type {@code rboard.Object}
     */
    public static final class Object extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:rboard.Object)
            ObjectOrBuilder {
        private static final long serialVersionUID = 0L;

        // Use Object.newBuilder() to construct.
        private Object(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private Object() {
            url_ = "";
            author_ = "";
            tags_ = com.google.protobuf.LazyStringArrayList.EMPTY;
            themes_ = com.google.protobuf.LazyStringArrayList.EMPTY;
            name_ = "";
            description_ = "";
        }

        @java.lang.Override
        @SuppressWarnings({"unused"})
        protected java.lang.Object newInstance(
                UnusedPrivateParameter unused) {
            return new Object();
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return this.unknownFields;
        }

        private Object(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new java.lang.NullPointerException();
            }
            int mutable_bitField0_ = 0;
            com.google.protobuf.UnknownFieldSet.Builder unknownFields =
                    com.google.protobuf.UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            url_ = s;
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            author_ = s;
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();
                            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                                tags_ = new com.google.protobuf.LazyStringArrayList();
                                mutable_bitField0_ |= 0x00000001;
                            }
                            tags_.add(s);
                            break;
                        }
                        case 34: {
                            java.lang.String s = input.readStringRequireUtf8();
                            if (!((mutable_bitField0_ & 0x00000002) != 0)) {
                                themes_ = new com.google.protobuf.LazyStringArrayList();
                                mutable_bitField0_ |= 0x00000002;
                            }
                            themes_.add(s);
                            break;
                        }
                        case 40: {

                            size_ = input.readInt64();
                            break;
                        }
                        case 48: {

                            date_ = input.readInt64();
                            break;
                        }
                        case 58: {
                            java.lang.String s = input.readStringRequireUtf8();

                            name_ = s;
                            break;
                        }
                        case 66: {
                            java.lang.String s = input.readStringRequireUtf8();
                            bitField0_ |= 0x00000001;
                            description_ = s;
                            break;
                        }
                        default: {
                            if (!parseUnknownField(
                                    input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000001) != 0)) {
                    tags_ = tags_.getUnmodifiableView();
                }
                if (((mutable_bitField0_ & 0x00000002) != 0)) {
                    themes_ = themes_.getUnmodifiableView();
                }
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_Object_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_Object_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            de.dertyp7214.rboardthememanager.proto.My.Object.class, de.dertyp7214.rboardthememanager.proto.My.Object.Builder.class);
        }

        private int bitField0_;
        public static final int URL_FIELD_NUMBER = 1;
        private volatile java.lang.Object url_;

        /**
         * <code>string url = 1;</code>
         *
         * @return The url.
         */
        @java.lang.Override
        public java.lang.String getUrl() {
            java.lang.Object ref = url_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                url_ = s;
                return s;
            }
        }

        /**
         * <code>string url = 1;</code>
         *
         * @return The bytes for url.
         */
        @java.lang.Override
        public com.google.protobuf.ByteString
        getUrlBytes() {
            java.lang.Object ref = url_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                url_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int AUTHOR_FIELD_NUMBER = 2;
        private volatile java.lang.Object author_;

        /**
         * <code>string author = 2;</code>
         *
         * @return The author.
         */
        @java.lang.Override
        public java.lang.String getAuthor() {
            java.lang.Object ref = author_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                author_ = s;
                return s;
            }
        }

        /**
         * <code>string author = 2;</code>
         *
         * @return The bytes for author.
         */
        @java.lang.Override
        public com.google.protobuf.ByteString
        getAuthorBytes() {
            java.lang.Object ref = author_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                author_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int TAGS_FIELD_NUMBER = 3;
        private com.google.protobuf.LazyStringList tags_;

        /**
         * <code>repeated string tags = 3;</code>
         *
         * @return A list containing the tags.
         */
        public com.google.protobuf.ProtocolStringList
        getTagsList() {
            return tags_;
        }

        /**
         * <code>repeated string tags = 3;</code>
         *
         * @return The count of tags.
         */
        public int getTagsCount() {
            return tags_.size();
        }

        /**
         * <code>repeated string tags = 3;</code>
         *
         * @param index The index of the element to return.
         * @return The tags at the given index.
         */
        public java.lang.String getTags(int index) {
            return tags_.get(index);
        }

        /**
         * <code>repeated string tags = 3;</code>
         *
         * @param index The index of the value to return.
         * @return The bytes of the tags at the given index.
         */
        public com.google.protobuf.ByteString
        getTagsBytes(int index) {
            return tags_.getByteString(index);
        }

        public static final int THEMES_FIELD_NUMBER = 4;
        private com.google.protobuf.LazyStringList themes_;

        /**
         * <code>repeated string themes = 4;</code>
         *
         * @return A list containing the themes.
         */
        public com.google.protobuf.ProtocolStringList
        getThemesList() {
            return themes_;
        }

        /**
         * <code>repeated string themes = 4;</code>
         *
         * @return The count of themes.
         */
        public int getThemesCount() {
            return themes_.size();
        }

        /**
         * <code>repeated string themes = 4;</code>
         *
         * @param index The index of the element to return.
         * @return The themes at the given index.
         */
        public java.lang.String getThemes(int index) {
            return themes_.get(index);
        }

        /**
         * <code>repeated string themes = 4;</code>
         *
         * @param index The index of the value to return.
         * @return The bytes of the themes at the given index.
         */
        public com.google.protobuf.ByteString
        getThemesBytes(int index) {
            return themes_.getByteString(index);
        }

        public static final int SIZE_FIELD_NUMBER = 5;
        private long size_;

        /**
         * <code>int64 size = 5;</code>
         *
         * @return The size.
         */
        @java.lang.Override
        public long getSize() {
            return size_;
        }

        public static final int DATE_FIELD_NUMBER = 6;
        private long date_;

        /**
         * <code>int64 date = 6;</code>
         *
         * @return The date.
         */
        @java.lang.Override
        public long getDate() {
            return date_;
        }

        public static final int NAME_FIELD_NUMBER = 7;
        private volatile java.lang.Object name_;

        /**
         * <code>string name = 7;</code>
         *
         * @return The name.
         */
        @java.lang.Override
        public java.lang.String getName() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                name_ = s;
                return s;
            }
        }

        /**
         * <code>string name = 7;</code>
         *
         * @return The bytes for name.
         */
        @java.lang.Override
        public com.google.protobuf.ByteString
        getNameBytes() {
            java.lang.Object ref = name_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                name_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int DESCRIPTION_FIELD_NUMBER = 8;
        private volatile java.lang.Object description_;

        /**
         * <code>string description = 8;</code>
         *
         * @return Whether the description field is set.
         */
        @java.lang.Override
        public boolean hasDescription() {
            return ((bitField0_ & 0x00000001) != 0);
        }

        /**
         * <code>string description = 8;</code>
         *
         * @return The description.
         */
        @java.lang.Override
        public java.lang.String getDescription() {
            java.lang.Object ref = description_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                description_ = s;
                return s;
            }
        }

        /**
         * <code>string description = 8;</code>
         *
         * @return The bytes for description.
         */
        @java.lang.Override
        public com.google.protobuf.ByteString
        getDescriptionBytes() {
            java.lang.Object ref = description_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                description_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private byte memoizedIsInitialized = -1;

        @java.lang.Override
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getUrlBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, url_);
            }
            if (!getAuthorBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, author_);
            }
            for (int i = 0; i < tags_.size(); i++) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, tags_.getRaw(i));
            }
            for (int i = 0; i < themes_.size(); i++) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 4, themes_.getRaw(i));
            }
            if (size_ != 0L) {
                output.writeInt64(5, size_);
            }
            if (date_ != 0L) {
                output.writeInt64(6, date_);
            }
            if (!getNameBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 7, name_);
            }
            if (((bitField0_ & 0x00000001) != 0)) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 8, description_);
            }
            unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getUrlBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, url_);
            }
            if (!getAuthorBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, author_);
            }
            {
                int dataSize = 0;
                for (int i = 0; i < tags_.size(); i++) {
                    dataSize += computeStringSizeNoTag(tags_.getRaw(i));
                }
                size += dataSize;
                size += 1 * getTagsList().size();
            }
            {
                int dataSize = 0;
                for (int i = 0; i < themes_.size(); i++) {
                    dataSize += computeStringSizeNoTag(themes_.getRaw(i));
                }
                size += dataSize;
                size += 1 * getThemesList().size();
            }
            if (size_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeInt64Size(5, size_);
            }
            if (date_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeInt64Size(6, date_);
            }
            if (!getNameBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, name_);
            }
            if (((bitField0_ & 0x00000001) != 0)) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(8, description_);
            }
            size += unknownFields.getSerializedSize();
            memoizedSize = size;
            return size;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof de.dertyp7214.rboardthememanager.proto.My.Object)) {
                return super.equals(obj);
            }
            de.dertyp7214.rboardthememanager.proto.My.Object other = (de.dertyp7214.rboardthememanager.proto.My.Object) obj;

            if (!getUrl()
                    .equals(other.getUrl())) return false;
            if (!getAuthor()
                    .equals(other.getAuthor())) return false;
            if (!getTagsList()
                    .equals(other.getTagsList())) return false;
            if (!getThemesList()
                    .equals(other.getThemesList())) return false;
            if (getSize()
                    != other.getSize()) return false;
            if (getDate()
                    != other.getDate()) return false;
            if (!getName()
                    .equals(other.getName())) return false;
            if (hasDescription() != other.hasDescription()) return false;
            if (hasDescription()) {
                if (!getDescription()
                        .equals(other.getDescription())) return false;
            }
            if (!unknownFields.equals(other.unknownFields)) return false;
            return true;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + URL_FIELD_NUMBER;
            hash = (53 * hash) + getUrl().hashCode();
            hash = (37 * hash) + AUTHOR_FIELD_NUMBER;
            hash = (53 * hash) + getAuthor().hashCode();
            if (getTagsCount() > 0) {
                hash = (37 * hash) + TAGS_FIELD_NUMBER;
                hash = (53 * hash) + getTagsList().hashCode();
            }
            if (getThemesCount() > 0) {
                hash = (37 * hash) + THEMES_FIELD_NUMBER;
                hash = (53 * hash) + getThemesList().hashCode();
            }
            hash = (37 * hash) + SIZE_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getSize());
            hash = (37 * hash) + DATE_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getDate());
            hash = (37 * hash) + NAME_FIELD_NUMBER;
            hash = (53 * hash) + getName().hashCode();
            if (hasDescription()) {
                hash = (37 * hash) + DESCRIPTION_FIELD_NUMBER;
                hash = (53 * hash) + getDescription().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(de.dertyp7214.rboardthememanager.proto.My.Object prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @java.lang.Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code rboard.Object}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:rboard.Object)
                de.dertyp7214.rboardthememanager.proto.My.ObjectOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_Object_descriptor;
            }

            @java.lang.Override
            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_Object_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                de.dertyp7214.rboardthememanager.proto.My.Object.class, de.dertyp7214.rboardthememanager.proto.My.Object.Builder.class);
            }

            // Construct using de.dertyp7214.rboardthememanager.proto.My.Object.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }

            @java.lang.Override
            public Builder clear() {
                super.clear();
                url_ = "";

                author_ = "";

                tags_ = com.google.protobuf.LazyStringArrayList.EMPTY;
                bitField0_ = (bitField0_ & ~0x00000001);
                themes_ = com.google.protobuf.LazyStringArrayList.EMPTY;
                bitField0_ = (bitField0_ & ~0x00000002);
                size_ = 0L;

                date_ = 0L;

                name_ = "";

                description_ = "";
                bitField0_ = (bitField0_ & ~0x00000004);
                return this;
            }

            @java.lang.Override
            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return de.dertyp7214.rboardthememanager.proto.My.internal_static_rboard_Object_descriptor;
            }

            @java.lang.Override
            public de.dertyp7214.rboardthememanager.proto.My.Object getDefaultInstanceForType() {
                return de.dertyp7214.rboardthememanager.proto.My.Object.getDefaultInstance();
            }

            @java.lang.Override
            public de.dertyp7214.rboardthememanager.proto.My.Object build() {
                de.dertyp7214.rboardthememanager.proto.My.Object result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            @java.lang.Override
            public de.dertyp7214.rboardthememanager.proto.My.Object buildPartial() {
                de.dertyp7214.rboardthememanager.proto.My.Object result = new de.dertyp7214.rboardthememanager.proto.My.Object(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                result.url_ = url_;
                result.author_ = author_;
                if (((bitField0_ & 0x00000001) != 0)) {
                    tags_ = tags_.getUnmodifiableView();
                    bitField0_ = (bitField0_ & ~0x00000001);
                }
                result.tags_ = tags_;
                if (((bitField0_ & 0x00000002) != 0)) {
                    themes_ = themes_.getUnmodifiableView();
                    bitField0_ = (bitField0_ & ~0x00000002);
                }
                result.themes_ = themes_;
                result.size_ = size_;
                result.date_ = date_;
                result.name_ = name_;
                if (((from_bitField0_ & 0x00000004) != 0)) {
                    to_bitField0_ |= 0x00000001;
                }
                result.description_ = description_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @java.lang.Override
            public Builder clone() {
                return super.clone();
            }

            @java.lang.Override
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return super.setField(field, value);
            }

            @java.lang.Override
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return super.clearField(field);
            }

            @java.lang.Override
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return super.clearOneof(oneof);
            }

            @java.lang.Override
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, java.lang.Object value) {
                return super.setRepeatedField(field, index, value);
            }

            @java.lang.Override
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return super.addRepeatedField(field, value);
            }

            @java.lang.Override
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof de.dertyp7214.rboardthememanager.proto.My.Object) {
                    return mergeFrom((de.dertyp7214.rboardthememanager.proto.My.Object) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(de.dertyp7214.rboardthememanager.proto.My.Object other) {
                if (other == de.dertyp7214.rboardthememanager.proto.My.Object.getDefaultInstance()) return this;
                if (!other.getUrl().isEmpty()) {
                    url_ = other.url_;
                    onChanged();
                }
                if (!other.getAuthor().isEmpty()) {
                    author_ = other.author_;
                    onChanged();
                }
                if (!other.tags_.isEmpty()) {
                    if (tags_.isEmpty()) {
                        tags_ = other.tags_;
                        bitField0_ = (bitField0_ & ~0x00000001);
                    } else {
                        ensureTagsIsMutable();
                        tags_.addAll(other.tags_);
                    }
                    onChanged();
                }
                if (!other.themes_.isEmpty()) {
                    if (themes_.isEmpty()) {
                        themes_ = other.themes_;
                        bitField0_ = (bitField0_ & ~0x00000002);
                    } else {
                        ensureThemesIsMutable();
                        themes_.addAll(other.themes_);
                    }
                    onChanged();
                }
                if (other.getSize() != 0L) {
                    setSize(other.getSize());
                }
                if (other.getDate() != 0L) {
                    setDate(other.getDate());
                }
                if (!other.getName().isEmpty()) {
                    name_ = other.name_;
                    onChanged();
                }
                if (other.hasDescription()) {
                    bitField0_ |= 0x00000004;
                    description_ = other.description_;
                    onChanged();
                }
                this.mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @java.lang.Override
            public final boolean isInitialized() {
                return true;
            }

            @java.lang.Override
            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                de.dertyp7214.rboardthememanager.proto.My.Object parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (de.dertyp7214.rboardthememanager.proto.My.Object) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            private java.lang.Object url_ = "";

            /**
             * <code>string url = 1;</code>
             *
             * @return The url.
             */
            public java.lang.String getUrl() {
                java.lang.Object ref = url_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    url_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string url = 1;</code>
             *
             * @return The bytes for url.
             */
            public com.google.protobuf.ByteString
            getUrlBytes() {
                java.lang.Object ref = url_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    url_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string url = 1;</code>
             *
             * @param value The url to set.
             * @return This builder for chaining.
             */
            public Builder setUrl(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                url_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string url = 1;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearUrl() {

                url_ = getDefaultInstance().getUrl();
                onChanged();
                return this;
            }

            /**
             * <code>string url = 1;</code>
             *
             * @param value The bytes for url to set.
             * @return This builder for chaining.
             */
            public Builder setUrlBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                url_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object author_ = "";

            /**
             * <code>string author = 2;</code>
             *
             * @return The author.
             */
            public java.lang.String getAuthor() {
                java.lang.Object ref = author_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    author_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string author = 2;</code>
             *
             * @return The bytes for author.
             */
            public com.google.protobuf.ByteString
            getAuthorBytes() {
                java.lang.Object ref = author_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    author_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string author = 2;</code>
             *
             * @param value The author to set.
             * @return This builder for chaining.
             */
            public Builder setAuthor(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                author_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string author = 2;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearAuthor() {

                author_ = getDefaultInstance().getAuthor();
                onChanged();
                return this;
            }

            /**
             * <code>string author = 2;</code>
             *
             * @param value The bytes for author to set.
             * @return This builder for chaining.
             */
            public Builder setAuthorBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                author_ = value;
                onChanged();
                return this;
            }

            private com.google.protobuf.LazyStringList tags_ = com.google.protobuf.LazyStringArrayList.EMPTY;

            private void ensureTagsIsMutable() {
                if (!((bitField0_ & 0x00000001) != 0)) {
                    tags_ = new com.google.protobuf.LazyStringArrayList(tags_);
                    bitField0_ |= 0x00000001;
                }
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @return A list containing the tags.
             */
            public com.google.protobuf.ProtocolStringList
            getTagsList() {
                return tags_.getUnmodifiableView();
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @return The count of tags.
             */
            public int getTagsCount() {
                return tags_.size();
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @param index The index of the element to return.
             * @return The tags at the given index.
             */
            public java.lang.String getTags(int index) {
                return tags_.get(index);
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @param index The index of the value to return.
             * @return The bytes of the tags at the given index.
             */
            public com.google.protobuf.ByteString
            getTagsBytes(int index) {
                return tags_.getByteString(index);
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @param index The index to set the value at.
             * @param value The tags to set.
             * @return This builder for chaining.
             */
            public Builder setTags(
                    int index, java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureTagsIsMutable();
                tags_.set(index, value);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @param value The tags to add.
             * @return This builder for chaining.
             */
            public Builder addTags(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureTagsIsMutable();
                tags_.add(value);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @param values The tags to add.
             * @return This builder for chaining.
             */
            public Builder addAllTags(
                    java.lang.Iterable<java.lang.String> values) {
                ensureTagsIsMutable();
                com.google.protobuf.AbstractMessageLite.Builder.addAll(
                        values, tags_);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearTags() {
                tags_ = com.google.protobuf.LazyStringArrayList.EMPTY;
                bitField0_ = (bitField0_ & ~0x00000001);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string tags = 3;</code>
             *
             * @param value The bytes of the tags to add.
             * @return This builder for chaining.
             */
            public Builder addTagsBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);
                ensureTagsIsMutable();
                tags_.add(value);
                onChanged();
                return this;
            }

            private com.google.protobuf.LazyStringList themes_ = com.google.protobuf.LazyStringArrayList.EMPTY;

            private void ensureThemesIsMutable() {
                if (!((bitField0_ & 0x00000002) != 0)) {
                    themes_ = new com.google.protobuf.LazyStringArrayList(themes_);
                    bitField0_ |= 0x00000002;
                }
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @return A list containing the themes.
             */
            public com.google.protobuf.ProtocolStringList
            getThemesList() {
                return themes_.getUnmodifiableView();
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @return The count of themes.
             */
            public int getThemesCount() {
                return themes_.size();
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @param index The index of the element to return.
             * @return The themes at the given index.
             */
            public java.lang.String getThemes(int index) {
                return themes_.get(index);
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @param index The index of the value to return.
             * @return The bytes of the themes at the given index.
             */
            public com.google.protobuf.ByteString
            getThemesBytes(int index) {
                return themes_.getByteString(index);
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @param index The index to set the value at.
             * @param value The themes to set.
             * @return This builder for chaining.
             */
            public Builder setThemes(
                    int index, java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureThemesIsMutable();
                themes_.set(index, value);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @param value The themes to add.
             * @return This builder for chaining.
             */
            public Builder addThemes(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureThemesIsMutable();
                themes_.add(value);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @param values The themes to add.
             * @return This builder for chaining.
             */
            public Builder addAllThemes(
                    java.lang.Iterable<java.lang.String> values) {
                ensureThemesIsMutable();
                com.google.protobuf.AbstractMessageLite.Builder.addAll(
                        values, themes_);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearThemes() {
                themes_ = com.google.protobuf.LazyStringArrayList.EMPTY;
                bitField0_ = (bitField0_ & ~0x00000002);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string themes = 4;</code>
             *
             * @param value The bytes of the themes to add.
             * @return This builder for chaining.
             */
            public Builder addThemesBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);
                ensureThemesIsMutable();
                themes_.add(value);
                onChanged();
                return this;
            }

            private long size_;

            /**
             * <code>int64 size = 5;</code>
             *
             * @return The size.
             */
            @java.lang.Override
            public long getSize() {
                return size_;
            }

            /**
             * <code>int64 size = 5;</code>
             *
             * @param value The size to set.
             * @return This builder for chaining.
             */
            public Builder setSize(long value) {

                size_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>int64 size = 5;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearSize() {

                size_ = 0L;
                onChanged();
                return this;
            }

            private long date_;

            /**
             * <code>int64 date = 6;</code>
             *
             * @return The date.
             */
            @java.lang.Override
            public long getDate() {
                return date_;
            }

            /**
             * <code>int64 date = 6;</code>
             *
             * @param value The date to set.
             * @return This builder for chaining.
             */
            public Builder setDate(long value) {

                date_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>int64 date = 6;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearDate() {

                date_ = 0L;
                onChanged();
                return this;
            }

            private java.lang.Object name_ = "";

            /**
             * <code>string name = 7;</code>
             *
             * @return The name.
             */
            public java.lang.String getName() {
                java.lang.Object ref = name_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    name_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string name = 7;</code>
             *
             * @return The bytes for name.
             */
            public com.google.protobuf.ByteString
            getNameBytes() {
                java.lang.Object ref = name_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    name_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string name = 7;</code>
             *
             * @param value The name to set.
             * @return This builder for chaining.
             */
            public Builder setName(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                name_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string name = 7;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearName() {

                name_ = getDefaultInstance().getName();
                onChanged();
                return this;
            }

            /**
             * <code>string name = 7;</code>
             *
             * @param value The bytes for name to set.
             * @return This builder for chaining.
             */
            public Builder setNameBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                name_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object description_ = "";

            /**
             * <code>string description = 8;</code>
             *
             * @return Whether the description field is set.
             */
            public boolean hasDescription() {
                return ((bitField0_ & 0x00000004) != 0);
            }

            /**
             * <code>string description = 8;</code>
             *
             * @return The description.
             */
            public java.lang.String getDescription() {
                java.lang.Object ref = description_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    description_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string description = 8;</code>
             *
             * @return The bytes for description.
             */
            public com.google.protobuf.ByteString
            getDescriptionBytes() {
                java.lang.Object ref = description_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    description_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string description = 8;</code>
             *
             * @param value The description to set.
             * @return This builder for chaining.
             */
            public Builder setDescription(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000004;
                description_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string description = 8;</code>
             *
             * @return This builder for chaining.
             */
            public Builder clearDescription() {
                bitField0_ = (bitField0_ & ~0x00000004);
                description_ = getDefaultInstance().getDescription();
                onChanged();
                return this;
            }

            /**
             * <code>string description = 8;</code>
             *
             * @param value The bytes for description to set.
             * @return This builder for chaining.
             */
            public Builder setDescriptionBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);
                bitField0_ |= 0x00000004;
                description_ = value;
                onChanged();
                return this;
            }

            @java.lang.Override
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.setUnknownFields(unknownFields);
            }

            @java.lang.Override
            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.mergeUnknownFields(unknownFields);
            }


            // @@protoc_insertion_point(builder_scope:rboard.Object)
        }

        // @@protoc_insertion_point(class_scope:rboard.Object)
        private static final de.dertyp7214.rboardthememanager.proto.My.Object DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new de.dertyp7214.rboardthememanager.proto.My.Object();
        }

        public static de.dertyp7214.rboardthememanager.proto.My.Object getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<Object>
                PARSER = new com.google.protobuf.AbstractParser<Object>() {
            @java.lang.Override
            public Object parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new Object(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<Object> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<Object> getParserForType() {
            return PARSER;
        }

        @java.lang.Override
        public de.dertyp7214.rboardthememanager.proto.My.Object getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_rboard_ObjectList_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_rboard_ObjectList_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_rboard_Object_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_rboard_Object_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor
            descriptor;

    static {
        java.lang.String[] descriptorData = {
                "\n\010my.proto\022\006rboard\"-\n\nObjectList\022\037\n\007obje" +
                        "cts\030\001 \003(\0132\016.rboard.Object\"\227\001\n\006Object\022\013\n\003" +
                        "url\030\001 \001(\t\022\016\n\006author\030\002 \001(\t\022\014\n\004tags\030\003 \003(\t\022" +
                        "\016\n\006themes\030\004 \003(\t\022\014\n\004size\030\005 \001(\003\022\014\n\004date\030\006 " +
                        "\001(\003\022\014\n\004name\030\007 \001(\t\022\030\n\013description\030\010 \001(\tH\000" +
                        "\210\001\001B\016\n\014_descriptionb\006proto3"
        };
        descriptor = com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[]{
                        });
        internal_static_rboard_ObjectList_descriptor =
                getDescriptor().getMessageTypes().get(0);
        internal_static_rboard_ObjectList_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_rboard_ObjectList_descriptor,
                new java.lang.String[]{"Objects",});
        internal_static_rboard_Object_descriptor =
                getDescriptor().getMessageTypes().get(1);
        internal_static_rboard_Object_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_rboard_Object_descriptor,
                new java.lang.String[]{"Url", "Author", "Tags", "Themes", "Size", "Date", "Name", "Description", "Description",});
    }

    // @@protoc_insertion_point(outer_class_scope)
}
