package eltimn.model

import net.liftweb.common._
import net.liftweb.mapper._
import net.liftweb.util.Helpers

class Inventory extends ElasticSearchUpdate[Inventory] {
  def getSingleton = Inventory
}

object Inventory extends Inventory with InventoryUpdater[Inventory] {}

trait ElasticSearchUpdate[T <: ElasticSearchUpdate[T]] extends LongKeyedMapper[T] with Loggable {
  self: T =>

  def primaryKeyField = id
  object id extends MappedLongIndex(this)

  def updateElasticSearch: Unit = {
    logger.info("Starting search sync for Inventory id %d" format id.is)
  }
}

trait InventoryUpdater[T <: ElasticSearchUpdate[T]] extends LongKeyedMetaMapper[T] with Loggable {
  self: T =>

  def updateInventory: Unit = {
    var items = List(create)
    var x = 0
    logger.info("Starting search sync for Inventory table at %s" format Helpers.now)
    while ({items = findAll(By_>=(id, x)); items.nonEmpty}){
      items.foreach { row => row.updateElasticSearch }
      x = x + 1000
    }
  }
}
