package cluster

import java.net.URI

import akka.actor.typed._
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.{Cluster, ClusterSingleton, SingletonActor}

import scala.concurrent.duration.DurationDouble

object TestApp extends App {
  val system = ActorSystem(SpawnProtocol(), "test-system")
  implicit val ec = system.executionContext

  val uri = new URI("akka://cluster-spike-system/system/singletonProxyGlobalCounter-no-dc#1031729776")
 val proxy: ActorRef[Counter.Command] = ActorRefResolver(system).resolveActorRef(uri.toString)

  val a: Runnable = () => {
    println("sending ----------->")
    proxy ! Counter.Increment
  }
  system.scheduler.scheduleAtFixedRate(1.seconds, 1.seconds)(a)
}
