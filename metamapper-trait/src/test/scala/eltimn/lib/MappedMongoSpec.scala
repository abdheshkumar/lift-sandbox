package eltimn
package lib

import net.liftweb.mapper._

class MappedMongoSpec extends MongoBaseSpec {
  def models = List(Foo, Comment)

  "MappedMongoField" should {
    "set and retrieve a MongoRecord" in {
      val bar = Bar.createRecord
        .greeting("Hola")
        .save

      val foo = Foo.create
        .barId(bar)
        .saveMe

      val barObj = foo.barId.obj

      barObj should be ('defined)

      barObj.foreach { b =>
        b.id.is should be (bar.id.is)
      }
    }
    "query using a ObjectId" in {
      val bar = Bar.createRecord
        .greeting("Hola2")
        .save

      val foo = Foo.create
        .barId(bar)
        .saveMe

      val foundFoo = Foo.find(By(Foo.barId, bar.id.is))
      foundFoo should be ('defined)

      foundFoo.foreach { f =>
        f.id.is should be (foo.id.is)
      }
    }
    "query using a MongoRecord" in {
      val bar = Bar.createRecord
        .greeting("Hola3")
        .save

      val foo = Foo.create
        .barId(bar)
        .saveMe

      val foundFoo = Foo.find(By(Foo.barId, bar))
      foundFoo should be ('defined)

      foundFoo.foreach { f =>
        f.id.is should be (foo.id.is)
      }
    }
    "query using a Mapper" in {
      val bar = Bar.createRecord
        .greeting("Hola4")
        .save

      val comment = Comment.create.saveMe

      val foo = Foo.create
        .barId(bar)
        .comment(comment)
        .saveMe

      val foundFoo = Foo.find(By(Foo.comment, comment))
      foundFoo should be ('defined)

      foundFoo.foreach { f =>
        f.id.is should be (foo.id.is)
      }

    }
  }
}

