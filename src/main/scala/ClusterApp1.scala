import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.pattern._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.util.Success

object ClusterApp1 extends App {
  val port = 2551
  // Override the configuration of the port
  val config = ConfigFactory.parseString(
    s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load())

  val system = ActorSystem("ClusterSystem", config)
  val cluster = Cluster(system)
  cluster.join(cluster.selfAddress)

  val dataActor = system.actorOf(Props(new DataActor))


  import DataActor._
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(5 seconds)

  // testing block
  {
    Thread.sleep(10000)
    val f = dataActor ? Get("3")
    f.onComplete({
      case Success(result) => println(result)
    })
  }

}
