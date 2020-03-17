package cluster

import java.net.URI

import akka.actor.typed.{ActorRef, ActorRefResolver, ActorSystem, SpawnProtocol}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationDouble

object TestApp extends App {
  val system = ActorSystem(SpawnProtocol(), "test-system")
  implicit val ec: ExecutionContext = system.executionContext

  private val uri = new URI("akka://cluster-spike-system@127.0.0.1:2551/system/singletonProxyGlobalCounter-no-dc#2038879230")

  private val proxy: ActorRef[Nothing] = ActorRefResolver(system).resolveActorRef(uri.toString)

  val a: Runnable = new Runnable {
    override def run(): Unit = {
      println("sending Increment -------------> ")
      proxy.unsafeUpcast[Counter.Command] ! Counter.Increment
    }
  }
  system.scheduler.scheduleAtFixedRate(1.seconds, 1.seconds)(a)

}
