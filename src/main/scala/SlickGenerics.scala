import scala.slick.driver.H2Driver.simple._

object SlickGenerics {
    def main(args: Array[String]): Unit = {
        implicit val session = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver").createSession

        val coffees = TableQuery[Coffees]
        val sodas = TableQuery[Sodas]

        addBeverages(List(Coffee("java", 20)), coffees)
        addBeverages(List(Soda("cola", 25)), sodas)

        println(coffees.list)
        println(sodas.list)
    }

    def addBeverages[D: DrinkTypes, T <: Table[D]](beverages: List[D], table: TableQuery[T])(implicit session: Session) = {
        table.ddl.create
        table ++= beverages
    }

    class DrinkTypes[T] {}
    object DrinkTypes {
        implicit object CoffeeWitness extends DrinkTypes[Coffee]
        implicit object SodaWitness extends DrinkTypes[Soda]
    }
}

class Coffees(tag: Tag) extends Table[Coffee](tag, "COFFEES") {
    def name = column[String]("NAME", O.PrimaryKey)
    def price = column[Double]("PRICE")
    def * = (name, price) <> (Coffee.tupled, Coffee.unapply)
}
case class Coffee(name: String, price: Double)

class Sodas(tag: Tag) extends Table[Soda](tag, "SODAS") {
    def name = column[String]("NAME", O.PrimaryKey)
    def price = column[Double]("PRICE")
    def * = (name, price) <> (Soda.tupled, Soda.unapply)
}
case class Soda(name: String, price: Double)
