package com.fueledbysoda.todo

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler

class MainVerticle : AbstractVerticle() {

    @Throws(Exception::class)
    override fun start() {
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
            ctx.response().end("{}")
        }

        val port = System.getenv("PORT")?.toInt() ?: 8080

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port)
    }
}