package eltimn
package lib

import java.lang.reflect.Method
import java.sql.Types
import java.util.Date

import org.bson.types.ObjectId

import net.liftweb.common._
import net.liftweb.json._
import net.liftweb.http.js._
import net.liftweb.util._
import net.liftweb.mapper._

abstract class MappedObjectId[T <: Mapper[T]](val fieldOwner: T) extends MappedField[ObjectId, T] {
  private val data: FatLazy[ObjectId] =  FatLazy(defaultValue) // defaultValue
  private val orgData: FatLazy[ObjectId] =  FatLazy(defaultValue) // defaultValue

  def dbFieldClass = classOf[ObjectId]

  protected def real_i_set_!(value: ObjectId): ObjectId = {
    if (!data.defined_? || value != data.get) {
      data() = value
      this.dirty_?( true)
    }
    data.get
  }

  /**
   * Get the JDBC SQL Type for this field
   */
  def targetSQLType = Types.VARCHAR

  def defaultValue = ObjectId.get

  override def writePermission_? = true
  override def readPermission_? = true

  protected def i_is_! = data.get
  protected def i_was_! = orgData.get

  def asJsonValue: Box[JValue] = Full(is match {
    case null => JNull
    case oid => JString(oid.toString)
  })

  /**
   * Called after the field is saved to the database
   */
  override def doneWithSave() {
    orgData.setFrom(data)
  }

  protected def i_obscure_!(in: ObjectId) = defaultValue

  override def setFromAny(in: Any): ObjectId = {
    in match {
      case JNull => this.set(null)
      case seq: Seq[_] if !seq.isEmpty => seq.map(setFromAny).apply(0)
      case (s: ObjectId) :: _ => this.set(s)
      case s :: _ => this.setFromAny(s)
      case JString(s) if (ObjectId.isValid(s)) => this.set(new ObjectId(s))
      case null => this.set(null)
      case s: ObjectId => this.set(s)
      case Some(s: ObjectId) => this.set(s)
      case Full(s: ObjectId) => this.set(s)
      case None | Empty | Failure(_, _, _) => this.set(null)
      case o => this.set(null)
    }
  }

  def asJsExp: JsExp = JE.Str(is.toString)

  def jdbcFriendly(field: String): ObjectId = data.get

  def real_convertToJDBCFriendly(value: ObjectId): Object = value.toString

  private def wholeSet(in: ObjectId) {
    this.data() = in
    this.orgData() = in
  }

  def buildSetActualValue(accessor: Method, inst: AnyRef, columnName: String): (T, AnyRef) => Unit =
  (inst, v) => doField(inst, accessor, {case f: MappedObjectId[T] => f.wholeSet(if (v eq null) null else v.asInstanceOf[ObjectId])})

  def buildSetLongValue(accessor: Method, columnName: String): (T, Long, Boolean) => Unit =
  (inst, v, isNull) => doField(inst, accessor, {case f: MappedObjectId[T] => f.wholeSet(null)})

  def buildSetStringValue(accessor: Method, columnName: String): (T, String) => Unit =
  (inst, v) => doField(inst, accessor, {case f: MappedObjectId[T] => f.wholeSet(if ((v eq null) || !ObjectId.isValid(v)) null else new ObjectId(v))})

  def buildSetDateValue(accessor: Method, columnName: String): (T, Date) => Unit =
  (inst, v) => doField(inst, accessor, {case f: MappedObjectId[T] => f.wholeSet(null)})

  def buildSetBooleanValue(accessor: Method, columnName: String): (T, Boolean, Boolean) => Unit =
  (inst, v, isNull) => doField(inst, accessor, {case f: MappedObjectId[T] => f.wholeSet(null)})

  def fieldCreatorString(dbType: DriverType, colName: String): String = colName + " " + dbType.varcharColumnType(24) + notNullAppender()
}
