import kotlinx.serialization.*
import kotlinx.serialization.cbor.*
import kotlinx.serialization.protobuf.*
import kotlin.test.*

@Serializable
data class OuterClass(val data: InnerClass)

@Serializable
data class InnerClass(val data: String)

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
class SerializationTest {
    @Test
    fun testProtobuf() {
        val model = InnerClass("something")

        val data = ProtoBuf.encodeToByteArray(InnerClass.serializer(), model)
        assertEquals("0a09736f6d657468696e67", data.toHexString())
    }

    @Test
    fun testProtobufNested() {
        val model = OuterClass(InnerClass("something"))

        val data = ProtoBuf.encodeToByteArray(OuterClass.serializer(), model)
        assertEquals("0a0b0a09736f6d657468696e67", data.toHexString())
    }

    @Test
    fun testCbor() {
        val model = InnerClass("something")

        val data = Cbor.encodeToByteArray(InnerClass.serializer(), model)
        assertEquals("bf646461746169736f6d657468696e67ff", data.toHexString())
    }

    @Test
    fun testCborNested() {
        val model = OuterClass(InnerClass("something"))

        val data = Cbor.encodeToByteArray(OuterClass.serializer(), model)
        assertEquals("bf6464617461bf646461746169736f6d657468696e67ffff", data.toHexString())
    }
}
