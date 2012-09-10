package eltimn
package lib

import net.liftweb.mapper._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._

// 1 comment many foos

class Foo extends LongKeyedMapper[Foo] {
  def getSingleton = Foo
  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object barId extends MappedMongoField(this, Bar)
  object comment extends MappedLongForeignKey(this, Comment)
}

object Foo extends Foo with LongKeyedMetaMapper[Foo]

class Bar private () extends MongoRecord[Bar] with ObjectIdPk[Bar] {
  def meta = Bar

  object greeting extends StringField(this, 256)
}

object Bar extends Bar with MongoMetaRecord[Bar]

class Comment extends LongKeyedMapper[Comment] with OneToMany[Long, Comment] {
  def getSingleton = Comment

  def primaryKeyField = id
  object id extends MappedLongIndex(this)

  object foos extends MappedOneToMany(Foo, Foo.comment, OrderBy(Foo.id, Ascending))
}

object Comment extends Comment with LongKeyedMetaMapper[Comment]{}

