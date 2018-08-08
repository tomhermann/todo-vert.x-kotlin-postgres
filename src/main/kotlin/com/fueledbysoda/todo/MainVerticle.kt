package com.fueledbysoda.todo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import java.util.*

fun port(): Int {
    return System.getenv("PORT")?.toInt() ?: 8080
}

fun rootUrl(): String {
    return System.getenv("HEROKU_APP_NAME")?.let { "https://$it.herokuapp.com" }
            ?: "http://localhost:${port()}"
}

class MainVerticle : AbstractVerticle() {

    @Throws(Exception::class)
    override fun start() {
        val todoService = TodoService()

        val router = createRouter(todoService)

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port())
    }

    private fun createRouter(todoService: TodoService): Router {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val router = Router.router(vertx)

        router.route().handler(
                CorsHandler.create("*")
                        .allowedMethod(HttpMethod.GET)
                        .allowedMethod(HttpMethod.POST)
                        .allowedMethod(HttpMethod.OPTIONS)
                        .allowedMethod(HttpMethod.DELETE)
                        .allowedMethod(HttpMethod.PATCH)
                        .allowedHeader("X-PINGARUNNER")
                        .allowedHeader("Content-Type")
        )

        router.route().handler(BodyHandler.create())

        router.route("/").handler { ctx ->
            ctx.response().putHeader("content-type", "application/json")
            ctx.next()
        }

        router.options("/").handler { ctx ->
            ctx.response().end()
        }

        router.get("/").handler { ctx ->
            ctx.response().end(Json.encode(todoService.list()))
        }

        router.get("/:id").handler { ctx ->
            val foundItem = todoService.get(UUID.fromString(ctx.pathParam("id")))
            foundItem?.let { ctx.response().end(Json.encode(it)) } ?: ctx.fail(404)
        }

        router.patch("/:id").handler { ctx ->
            val map = mapper.readValue<Map<String, Any>>(ctx.bodyAsString)
            val updatedItem = todoService.patch(UUID.fromString(ctx.pathParam("id")), map)
            updatedItem?.let { ctx.response().end(Json.encode(it)) } ?: ctx.fail(404)
        }

        router.post("/").handler { ctx ->
            val todoItem = mapper.readValue<TodoItem>(ctx.bodyAsString)
            ctx.response().end(Json.encode(todoService.add(todoItem)))
        }

        router.delete("/").handler { ctx ->
            ctx.response().end(Json.encode(todoService.clear()))
        }

        router.delete("/:id").handler { ctx ->
            val deletedId = todoService.delete(UUID.fromString(ctx.pathParam("id")))
            deletedId?.let { ctx.response().setStatusCode(200).end() } ?: ctx.fail(404)
        }

        return router
    }
}