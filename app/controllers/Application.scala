package com.mobileomega.controllers

import com.mobileomega.models._
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.modules.reactivemongo._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}

/*
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits.JSValueWriter
import reactivemongo.api._
import reactivemongo.api.gridfs._
import reactivemongo.api.gridfs.Implicits._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler
*/

import reactivemongo.api.gridfs._
import reactivemongo.api.gridfs.Implicits._

// Reactive Mongo imports
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

// Reactive Mongo plugin
import play.modules.reactivemongo._
//import play.modules.reactivemongo.PlayBsonImplicits._




object Application extends Controller  with MongoController {
  val db = ReactiveMongoPlugin.db
  val collection = db("images")

  val gridFS = new GridFS(db, "fs")
  gridFS.ensureIndex()

  def getImageFile(id: String) = Action {
    Async {
      // find the matching image, if any, and streams it to the client
      println("getImage : " + id)
      val docId = BSONDocument("_id" -> new BSONObjectID(id))
      val doc = gridFS.find(docId)
      serve(gridFS, doc)
    }
  }

  def index = Action { implicit request =>
		Async {
		implicit val reader = Image.ImageBSONReader;
		val query = BSONDocument("$query" -> BSONDocument())
		val found = collection.find[JSValue](query)
		found.toList.map(images => Ok(view.html.index(images)))
	}


		//	val name = "test"
		//    val qb = QueryBuilder().query(
		// 	Json.obj( "name" -> name )).sort( "created" -> SortOrder.Descending)

      //  collection.find[JsValue]( qb ).toList.map { persons =>
       // Ok(persons.foldLeft(JsArray(List()))( (obj, person) => obj ++ Json.arr(person) ))}
	
			//implicit val readFileReader = Image.ImageBSONReader


		//	val query = BSONDocument("_id" -> BSONString("*"))
			//	"$query" -> BSONDocument()
			//	)

		//
		  //  val j = Json.obj( "name" -> name )
		//	val query = QueryBuilder().query(Json.obj( "name" -> name )).sort( "created" -> SortOrder.Descending)
		//	val found = collection.find[JsValue](query)
		//	found.toList.map { images => Ok(views.html.index(images)) } 
		//}
	}
  
}