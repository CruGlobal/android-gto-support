package org.ccci.gto.android.common.jsonapi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.ccci.gto.android.common.jsonapi.model.JsonApiError
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.model.ModelSimple
import org.hamcrest.MatcherAssert.assertThat
import org.json.JSONObject

private const val META_SIMPLE = """
{
  "detail": {
    "person_id": ["This person is already assigned"]
  }
}
"""

// sample error taken from https://jsonapi.org/examples/#error-objects-multiple-errors
private const val ERROR_JSONAPI_MULTIPLE1 = """
{
  "errors": [
    {
      "status": "403",
      "source": { "pointer": "/data/attributes/secretPowers" },
      "detail": "Editing secret powers is not authorized on Sundays."
    },
    {
      "status": "422",
      "source": { "pointer": "/data/attributes/volume" },
      "detail": "Volume does not, in fact, go to 11."
    },
    {
      "status": "500",
      "source": { "pointer": "/data/attributes/reputation" },
      "title": "The backend responded with an error",
      "detail": "Reputation service not responding after three requests."
    }
  ]
}
"""

class JsonApiConverterErrorsTest {
    @Test
    fun verifyToJsonSingleSimpleError() {
        val converter = JsonApiConverter.Builder().addClasses(ModelSimple::class.java).build()
        val error = simpleError()
        val json = converter.toJson(JsonApiObject.error<Any>(error))
        assertThatJson(json).node("data").isAbsent
        assertThatJson(json).node("errors").isPresent
        assertThatJson(json).node("errors").isArray.ofLength(1)
        assertThat(json, jsonPartEquals("errors[0].detail", error.detail))
        assertThat(json, jsonPartEquals("errors[0].status", "\"${error.status}\""))
        assertThatJson(json).node("errors[0].code").isEqualTo("error_code")
        assertThat(json, jsonPartEquals("errors[0].source.pointer", error.source!!.pointer))
        assertThat(json, jsonPartEquals("errors[0].meta", META_SIMPLE))
    }

    @Test
    fun verifyFromJsonSingleSimpleError() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelSimple::class.java)
            .build()
        val error = simpleError()
        val json = converter.toJson(JsonApiObject.error<Any>(error))
        val obj = converter.fromJson(json, ModelSimple::class.java)
        assertTrue(obj.hasErrors)
        assertEquals(1, obj.errors.size)
        assertEquals(error, obj.errors[0])
    }

    @Test
    fun verifyFromJsonJsonapiMultiple1() {
        val converter = JsonApiConverter.Builder().addClasses(ModelSimple::class.java).build()
        val obj = converter.fromJson(ERROR_JSONAPI_MULTIPLE1, ModelSimple::class.java)
        assertTrue(obj.hasErrors)
        assertEquals(3, obj.errors.size)
        assertEquals(
            JsonApiError(
                status = 403,
                detail = "Editing secret powers is not authorized on Sundays.",
                source = JsonApiError.Source(
                    pointer = "/data/attributes/secretPowers",
                ),
            ),
            obj.errors[0],
        )
        assertEquals(
            JsonApiError(
                status = 500,
                title = "The backend responded with an error",
                detail = "Reputation service not responding after three requests.",
                source = JsonApiError.Source(
                    pointer = "/data/attributes/reputation",
                ),
            ),
            obj.errors[2],
        )
    }

    private fun simpleError() = JsonApiError(
        status = 200,
        code = "error_code",
        title = "Title",
        detail = "Detail human readable message.",
        source = JsonApiError.Source(
            pointer = "/data/attributes/title",
        ),
        rawMeta = JSONObject(META_SIMPLE),
    )
}
