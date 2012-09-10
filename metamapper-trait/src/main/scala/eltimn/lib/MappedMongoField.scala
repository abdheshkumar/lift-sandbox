package eltimn.lib

import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.mapper._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._

import org.bson.types.ObjectId

abstract class MappedMongoField[A <: Mapper[A], B <: MongoRecord[B] with ObjectIdPk[B]](owner: A, mongoMeta: MongoMetaRecord[B]) extends MappedObjectId(owner) {

  override def dbIndexed_? = true

  /**
    * Find the referenced object
    */
  def find: Box[B] = mongoMeta.find(this.is)

  /**
    * Get the cacheable referenced object
    */
  def obj = synchronized {
    if (!_calcedObj) {
      _calcedObj = true
      this._obj = find
    }
    _obj
  }

  def cached_? : Boolean = synchronized { _calcedObj }

  def primeObj(obj: Box[B]) = synchronized {
    _obj = obj
    _calcedObj = true
  }

  private var _obj: Box[B] = Empty
  private var _calcedObj = false

  override def set(in: ObjectId): ObjectId = synchronized {
    _calcedObj = false // invalidate the cache
    super.set(in)
  }

  /**
   * Set the value from a possible instance of the MongoRecord class
   * If v is Empty, set the value to defaultValue ("")
   * @return the Mapper containing this field
   */
  def apply(v: Box[B]): A = {
    apply(v.dmap(defaultValue)(_.id.is))
    primeObj(v)
    fieldOwner
  }

  /**
   * Set the value from an instance of the MongoRecord class
   * @return the Mapper containing this field
   */
  def apply(v: B): A = {
    apply(v.id.is)
    primeObj(Full(v))
    fieldOwner
  }
}
