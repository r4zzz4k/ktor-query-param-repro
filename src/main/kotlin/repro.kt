package com.example

import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.util.KtorExperimentalAPI
import java.util.concurrent.TimeUnit
import io.ktor.client.engine.cio.CIO as ClientCIO
import io.ktor.server.cio.CIO as ServerCIO
import io.ktor.server.netty.Netty

@KtorExperimentalAPI
suspend fun main() {
	val server = embeddedServer(Netty, 8080) {
		routing {
			route("/{bar}") {
				get {
					call.respond(HttpStatusCode.OK, "${call.request.uri}\n" +
							"\tcall.parameters[\"bar\"]: ${call.parameters["bar"]}\n" +
							"\tcall.request.queryParameters[\"bar\"]: ${call.request.queryParameters["bar"]}")
				}
			}
		}
	}
	server.start(false)

	val client = HttpClient(ClientCIO)
	println(client.get<String>("http://localhost:8080/foo?bar=baz"))
	println(client.get<String>("http://localhost:8080/foo?baz=baz"))

	server.stop(10_000, 10_000, TimeUnit.MILLISECONDS)
}
