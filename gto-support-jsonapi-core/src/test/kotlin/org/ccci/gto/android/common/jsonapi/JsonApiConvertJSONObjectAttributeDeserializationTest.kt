package org.ccci.gto.android.common.jsonapi

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class JsonApiConvertJSONObjectAttributeDeserializationTest {
    // region Native Deserialization
    private val nativeConverter = JsonApiConverter.Builder().addClasses(ModelNativeAttributes::class.java).build()

    @JsonApiType(ModelNativeAttributes.TYPE)
    class ModelNativeAttributes {
        companion object {
            const val TYPE = "native"

            const val DEFAULT_STRING = "default"
            const val DEFAULT_INTEGER = 7
        }

        var string: String? = DEFAULT_STRING
        var integer: Int = DEFAULT_INTEGER
        var boxedInteger: Int? = DEFAULT_INTEGER
    }

    // region String Deserialization
    @Test
    fun `Deserialize String`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              string: "value"
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals("value", string)
        }
    }

    @Test
    fun `Deserialize String - Null`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              string: null
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertNull(string)
        }
    }

    @Test
    fun `Deserialize String - Missing`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {}
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(ModelNativeAttributes.DEFAULT_STRING, string)
        }
    }

    @Test
    fun `Deserialize String - From Integer`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              string: 1
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals("1", string)
        }
    }

    @Test
    fun `Deserialize String - From Boolean`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              string: true
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals("true", string)
        }
    }
    // endregion String Deserialization

    // region Integer Deserialization
    @Test
    fun `Deserialize Integer`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              integer: 2
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(2, integer)
        }
    }

    @Test
    fun `Deserialize Integer - Null`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              integer: null
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(ModelNativeAttributes.DEFAULT_INTEGER, integer)
        }
    }

    @Test
    fun `Deserialize Integer - From Boolean`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              integer: true
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(ModelNativeAttributes.DEFAULT_INTEGER, integer)
        }
    }

    @Test
    fun `Deserialize Integer - From Double`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              integer: 2.0
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(2, integer)
        }
    }

    @Test
    fun `Deserialize Integer - From String`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              integer: "2"
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(2, integer)
        }
    }

    @Test
    fun `Deserialize Integer - From String - Invalid`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              integer: "invalid"
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(ModelNativeAttributes.DEFAULT_INTEGER, integer)
        }
    }
    // endregion Integer Deserialization

    // region Boxed Integer Deserialization
    @Test
    fun `Deserialize Boxed Integer`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              boxedInteger: 2
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(2, boxedInteger)
        }
    }

    @Test
    fun `Deserialize Boxed Integer - Null`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              boxedInteger: null
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertNull(boxedInteger)
        }
    }

    @Test
    fun `Deserialize Boxed Integer - From Boolean`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              boxedInteger: true
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(ModelNativeAttributes.DEFAULT_INTEGER, boxedInteger)
        }
    }

    @Test
    fun `Deserialize Boxed Integer - From Double`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              boxedInteger: 2.0
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(2, boxedInteger)
        }
    }

    @Test
    fun `Deserialize Boxed Integer - From String`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              boxedInteger: "2"
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(2, boxedInteger)
        }
    }

    @Test
    fun `Deserialize Boxed Integer - From String - Invalid`() {
        val raw = """
        {
          data: {
            type: "${ModelNativeAttributes.TYPE}",
            attributes: {
              boxedInteger: "invalid"
            }
          }
        }
        """
        with(nativeConverter.fromJson(raw, ModelNativeAttributes::class.java).dataSingle!!) {
            assertEquals(ModelNativeAttributes.DEFAULT_INTEGER, boxedInteger)
        }
    }
    // endregion Boxed Integer Deserialization
    // endregion Native Deserialization
}
