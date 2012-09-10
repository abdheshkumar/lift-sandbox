package eltimn

import org.bson.types.ObjectId
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.ObjectIdPk

package object lib {
  implicit def mongoRecordToObjectId(in: MongoRecord[_] with ObjectIdPk[_]): ObjectId = in.id.is
}
