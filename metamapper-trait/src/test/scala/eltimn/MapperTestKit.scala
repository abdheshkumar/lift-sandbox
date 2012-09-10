package eltimn

import org.scalatest.{BeforeAndAfterAll, WordSpec}

import net.liftweb.common._
import net.liftweb.mapper._

trait MapperTestKit {

  def models: List[MetaMapper[_]]

  protected def setupDb(): Unit = {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = new StandardDBVendor(
        "org.h2.Driver",
        "jdbc:h2:lift_mappedmongo;AUTO_SERVER=TRUE",
        Empty, Empty
      )

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }
    Schemifier.schemify(true, Schemifier.infoF _, models:_*)
  }

  protected def teardownDb(): Unit = {
    Schemifier.destroyTables_!!(Schemifier.infoF _, models:_*)
  }
}
