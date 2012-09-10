package eltimn.model

import net.liftweb.mapper._

class Inventory extends LongKeyedMapper[Inventory] with ElasticSearchUpdater[Inventory] {
  def getSingleton = Inventory

  def primaryKeyField = id
  object id extends MappedLongIndex(this)

  def updateElasticSearch: Unit = getSingleton.updateElasticSearch(this)
}

object Inventory extends Inventory with LongKeyedMetaMapper[Inventory] with InventoryUpdater[Inventory] {

  def findAllByIdGreaterThan(n: Int): List[Inventory] =
    findAll(By_>=(id, n))
}


trait InventoryUpdater[T <: Mapper[T]] {
  self: MetaMapper[T] =>

  def findAllByIdGreaterThan(n: Int): List[T]

  def updateElasticSearch(inst: Mapper[T]): Unit = {
    ()
  }

  def updateInventory: Unit = {
    var items = List(create)
    var x = 0
    //logger.info("Starting search sync for Inventory table at %s" format Helpers.now)
    while ({items = findAllByIdGreaterThan(x); items.nonEmpty}){
      items.foreach { row => updateElasticSearch(row) }
      x = x + 1000
    }
  }
}

trait ElasticSearchUpdater[T <: Mapper[T]] {
  self: Mapper[T] =>

  def updateElasticSearch: Unit
}
