package cluster

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

trait MySerializable

object Counter {
  trait Command extends MySerializable
  case object Increment extends Command
  final case class GetValue(replyTo: ActorRef[Int]) extends Command
  case object GoodByeCounter extends Command

  def apply(): Behavior[Command] = {
    def updated(value: Int): Behavior[Command] = Behaviors.setup { ctx =>
    println("starting counter actor")
      def behavior(value: Int): Behaviors.Receive[Command] = Behaviors.receiveMessage[Command] {
        case Increment =>
          println(s" ************ actor ${ctx.system.path.address}")
          println("value = " + value)
          behavior(value + 1)
        case GetValue(replyTo) =>
          replyTo ! value
          Behaviors.same
        case GoodByeCounter =>
          // Possible async action then stop
          Behaviors.stopped
      }
      behavior(value)
    }

    updated(0)
  }
}
