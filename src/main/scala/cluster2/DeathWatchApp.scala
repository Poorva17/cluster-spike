package cluster2

import akka.actor.ActorSelection
import akka.actor.typed.SpawnProtocol.Spawn
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed._
import akka.util.Timeout
import cluster.{Counter, MySerializable}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

case object Dead extends MySerializable

object DeathWatchActor extends App {
  implicit val system = ActorSystem(SpawnProtocol(), "test-system")
  implicit val timeout: Timeout = 1.seconds

  private val value: ActorRef[Counter.Command] = ActorRefResolver(system).resolveActorRef("akka://cluster-spike-system@127.0.0.1:2551/system/singletonProxyGlobalCounter-no-dc#-963844131")


  def behaviour: Behaviors.Receive[Dead.type ] = Behaviors.receive((ctx, msg) => {
    ctx.watchWith(value, Dead)
    msg match {
      case Dead => println("watched actor is dead")
    }
    Behaviors.same
  })
  println("incrementing")
  value ! Counter.Increment

  val z: Future[ActorRef[Dead.type]] = (system ? { x: ActorRef[ActorRef[Dead.type]] =>
    Spawn(behaviour, "deathwatch", Props.empty, x)
  })
}

