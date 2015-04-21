import akka.actor.{Props, ActorSystem, Actor, ActorLogging}

// Hello world to test akka

case class Greeting(who: String)

class GreetingActor extends Actor with ActorLogging {
  def receive = {
    case Greeting(who) => log.info("Hello " + who)
  }
}

object HelloWorld {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("MySystem")
    val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
    greeter ! Greeting("Charlie Parker")
  }
}

